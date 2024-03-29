package mqcreaple.tankgame.game

import mqcreaple.tankgame.BoardController
import mqcreaple.tankgame.controller.BotController
import mqcreaple.tankgame.controller.Controller
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.controller.ServerKeyboardController
import mqcreaple.tankgame.entity.TankEntity
import mqcreaple.tankgame.event.EntityCreateEvent
import mqcreaple.tankgame.event.Event
import mqcreaple.tankgame.event.RecalcPathEvent
import mqcreaple.tankgame.event.Timer
import java.io.DataInputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerGame(gui: BoardController, val port: UShort): Game(gui, true) {
    class Player (
        val socket: Socket?,
        val socketIStream: DataInputStream?,
        val socketOStream: ObjectOutputStream?,
        val controller: Controller,
        val tank: TankEntity
    )

    val serverSocket = ServerSocket(port.toInt())
    lateinit var keyboardController: KeyboardController

    /**
     * Mapping of player name to the player information
     */
    private var playerList = HashMap<String, Player>()
    fun getPlayer(name: String) = playerList[name]

    var timers = PriorityQueue<Timer>()

    override fun gameInit() {
        println("Opened game at port $port")
        Thread {
            // This thread handles new client connection
            // TODO (potential DDOS attack)
            while(!gameEnd) {
                // try to accept a new connection
                val socket = serverSocket.accept()
                val iStream = DataInputStream(socket.getInputStream())
                val oStream = ObjectOutputStream(socket.getOutputStream())
                // remove all closed hosts (to free up usable names)
                synchronized(playerList) {
                    for((name, player) in playerList) {
                        player.socket?.let {
                            if(player.socket.isClosed) {
                                println("Player $name disconnected.")
                                playerList.remove(name)
                                scheduledRemoveEntity(player.tank)
                                if(player.controller is ServerKeyboardController) {
                                    player.controller.controllerThread.join()
                                }
                            }
                        }
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
                    oStream.flush()
                    val tank = TankEntity(1, 0.0, 0.0, name)
                    val controller = ServerKeyboardController(tank, socket)
                    playerList[name] = Player(socket, iStream, oStream, controller, tank)
                    scheduledAddEntity(tank)  // add tank entity to game's entity list in the start of next game loop
                    synchronized(this.gui) {
                        oStream.writeUTF(gui.board.toString()) // write current game board to the stream
                    }
                    oStream.flush()
                    synchronized(this.entityMap) {
                        // write all existing entities through socket to the client
                        for((_, entity) in entityMap) {
                            oStream.writeObject(EntityCreateEvent(entity))
                        }
                        oStream.flush()
                    }
                    (playerList["default"]!!.controller as BotController).target = tank
                } catch(e: IOException) {
                    // ignore IO exception
                }
            }
        }.start()
        // add default player (controlled by keyboard controller)
        val defaultTank = TankEntity(2, 0.5, 0.5, "default")
        scheduledAddEntity(defaultTank)
        val botController = BotController(defaultTank, this)
        playerList["default"] = Player(
            null, null, null, botController, defaultTank
        )
        // add a timer that recalculate the bot's path
        timers.add(Timer(RecalcPathEvent(botController), 1000, System.currentTimeMillis() + 1000))
    }

    override fun update() {
        // send events and entity updates to client sockets
        synchronized(playerList) {
            val nameToRemove = ArrayList<String>()
            for((name, client) in playerList) {
                client.socket?.let {
                    if(client.socket.isClosed) {
                        // player disconnected, then remove its data from player list
                        nameToRemove.add(name)
                    } else {
                        // player is still connected, then write all events and entity positions to the remote player
                        synchronized(eventQueue) {
                            for(event in eventQueue) {
                                client.socketOStream!!.writeObject(event)
                            }
                        }
                        synchronized(entityMap) {
                            for((uuid, entity) in entityMap) {
                                client.socketOStream!!.writeObject(EntityPosition(uuid, entity.x, entity.y))
                            }
                        }
                    }
                }
            }
            // remove all names in the scheduled list
            for(name in nameToRemove) {
                println("player $name disconnected.")
                scheduledRemoveEntity(playerList[name]!!.tank)
                playerList.remove(name)
            }
        }

        // check timeout of timers
        synchronized(timers) {
            while(!timers.isEmpty() && timers.peek().nextTime <= System.currentTimeMillis()) {
                val timer = timers.remove()
                eventQueue.add(timer.event)
                timers.add(Timer(timer.event, timer.timeMillis, timer.nextTime + timer.timeMillis))
            }
        }

        // run every event
        synchronized(eventQueue) {
            while(!eventQueue.isEmpty()) {
                val event = eventQueue.removeFirst()
                event.run(this)
            }
        }

        // update all entity
        synchronized(entityMap) {
            for((_, entity) in entityMap) {
                entity.update(this, gui.board)
            }
        }
    }

    override fun gameTerminate() {
        synchronized(playerList) {
            // disconnect all remote players
            for((name, player) in playerList) {
                player.socket?.let {
                    player.socket.close()
                }
            }
        }
        serverSocket.close()
    }

    fun addTimer(event: Event, timeMillis: Long) {
        synchronized(timers) {
            timers.add(Timer(event, timeMillis, System.currentTimeMillis() + timeMillis))
        }
    }
}