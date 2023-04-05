package mqcreaple.tankgame.game

import mqcreaple.tankgame.BoardController
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.event.Event
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.net.Socket

class ClientGame(gui: BoardController, name: String, val socket: Socket): Game(gui, false) {
    val socketIStream: ObjectInputStream
    val socketOStream: DataOutputStream

    init {
        socketIStream = ObjectInputStream(socket.getInputStream())
        socketOStream = DataOutputStream(socket.getOutputStream())
        socketOStream.writeUTF(name)
        val reply = socketIStream.readUTF()
        println(reply)
        if(socket.isConnected) {
            val map = socketIStream.readUTF()
            gui.gamePane.children.clear()
            gui.initialize(map)
        }
    }

    override fun update() {
        // get all events in this game loop from remote server
        while(true) {
            try {
                val obj = socketIStream.readObject()
                if(obj is Event) {
                    obj.run(this)
                } else if(obj is EntityPosition) {
                    entityMap[obj.uuid]?.let {
                        it.setX(obj.x, this)
                        it.setY(obj.y, this)
                    }
                }
            } catch(_: Exception) {
                break
            }
        }
    }
}