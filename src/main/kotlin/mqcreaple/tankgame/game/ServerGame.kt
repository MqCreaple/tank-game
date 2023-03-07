package mqcreaple.tankgame.game

import mqcreaple.tankgame.BoardController
import mqcreaple.tankgame.controller.ServerKeyboardController
import mqcreaple.tankgame.entity.TankEntity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerGame(gui: BoardController, val port: UShort): Game(gui, true) {
    val serverSocket = ServerSocket(port.toInt())

    /**
     * Mapping of player name to the player information
     */
    private var playerList = HashMap<String, Player>()

    private class Player (
        val socket: Socket,
        val socketIStream: DataInputStream,
        val socketOStream: DataOutputStream,
        val controller: ServerKeyboardController,
        val tank: TankEntity
        )

    override fun gameInit() {
        println("Opened game at port $port")
        Thread {
            // This thread handles new client connection
            // TODO (potential DDOS attack)
            while(!gameEnd) {
                // try to accept a new connection
                val socket = serverSocket.accept()
                val iStream = DataInputStream(socket.getInputStream())
                val oStream = DataOutputStream(socket.getOutputStream())
                // remove all closed hosts (to free up usable names)
                for((name, player) in playerList) {
                    if(player.socket.isClosed) {
                        println("Player $name disconnected.")
                        playerList.remove(name)
                        removeEntity(player.tank)
                    }
                }
                try {
                    // try to add the new client into player list
                    val name = iStream.readUTF()
                    if(playerList.containsKey(name)) {
                        // repeated name
                        oStream.writeUTF("A player with $name already connected. Please change a name.")
                        socket.close()
                        continue
                    }
                    // Player successfully connected. Create a tank entity for the player.
                    println("player $name connected!")
                    oStream.writeUTF("Successfully connected!")
                    val controller = ServerKeyboardController(socket)
                    val tank = TankEntity(this, 1, 0.0, 0.0, controller)
                    playerList[name] = Player(socket, iStream, oStream, controller, tank)
                    addEntity(tank)  // add tank entity to game's entity list in the start of next game loop
                } catch(e: IOException) {
                    // ignore IO exception
                }
            }
        }.start()
    }

    override fun update() {
        // run every event
        while(!eventQueue.isEmpty()) {
            val event = eventQueue.removeFirst()
            event.run()
        }

        // update all entity
        synchronized(entityList) {
            for(entity in entityList) {
                entity.update(gui.board)
            }
        }

        // send events and entity updates to client sockets
        // TODO()
    }

    override fun gameTerminate() {
        for((name, player) in playerList) {
            player.socket.close()
        }
        serverSocket.close()
    }
}