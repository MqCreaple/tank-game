package mqcreaple.tankgame.game

import mqcreaple.tankgame.BoardController
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.NullPointerException
import java.net.Socket

class ClientGame(gui: BoardController): Game(gui, false) {
    val name: String
    val socket: Socket
    val socketIStream: DataInputStream
    val socketOStream: DataOutputStream

    init {
        println("Please give your tank a name: ")
        name = readLine()!!
        println("Please enter server's IP address and port, separated by a single space character:")
        val line: List<String> = readLine()!!.split(' ')
        socket = Socket(line[0], line[1].toInt())
        socketIStream = DataInputStream(socket.getInputStream())
        socketOStream = DataOutputStream(socket.getOutputStream())
        socketOStream.writeUTF(name)
        val reply = socketIStream.readUTF()
        println(reply)
    }

    override fun update() {
        // TODO("Not yet implemented")
    }
}