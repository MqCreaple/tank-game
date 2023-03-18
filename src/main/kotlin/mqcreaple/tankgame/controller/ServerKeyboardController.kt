package mqcreaple.tankgame.controller

import javafx.scene.input.KeyCode
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.utils.ByteOrder
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class ServerKeyboardController(clientSocket: Socket): Controller() {
    private var keyPressed: MutableMap<KeyCode, Boolean> = KeyboardController.keyMap.keys.associateWithTo(mutableMapOf()) { false }
    val controllerThread: Thread = Thread {
        var disconnected = false
        while(!disconnected) {
            val socketStream = DataInputStream(clientSocket.getInputStream())
            val buffer = byteArrayOf(0, 0, 0, 0, 0)
            var length = 0
            do {
                try {
                    length = socketStream.read(buffer, 0, buffer.size)
                    if(length == 4) {
                        length = socketStream.read(buffer, length, 1)
                    }
                    if(length == -1) {
                        break
                    }
                    val keyCode = ByteOrder.fromNetOrdInt(buffer)
                    val action = (buffer[4] == 0x00.toByte())
                    for(k in keyPressed.keys) {
                        if(k.code == keyCode) {
                            keyPressed[k] = action
                            break
                        }
                    }
                } catch(e: SocketException) {
                    // Player disconnected. Signal the server's network monitor thread to remove the player.
                    clientSocket.close()
                    disconnected = true
                    break
                }
            } while(length != 0)
        }
    }

    init {
        controllerThread.start()
    }

    override fun getAction(): Action {
        for((k, v) in keyMap) {
            if(keyPressed[k] == true) {
                return v
            }
        }
        return Action.NONE
    }

    companion object {
        val keyMap = mapOf(
            KeyCode.W to Action.MOVE(Direction.UP),
            KeyCode.A to Action.MOVE(Direction.LEFT),
            KeyCode.S to Action.MOVE(Direction.DOWN),
            KeyCode.D to Action.MOVE(Direction.RIGHT),
            KeyCode.SPACE to Action.ACT
        )
    }
}