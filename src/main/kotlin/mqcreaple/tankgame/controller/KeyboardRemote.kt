package mqcreaple.tankgame.controller

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import mqcreaple.tankgame.utils.ByteOrder
import java.io.OutputStream
import java.net.Socket

class KeyboardRemote(scene: Scene, socket: Socket) {
    private val stream: OutputStream = socket.getOutputStream()

    init {
        scene.onKeyPressed = EventHandler { e ->
            run {
                if(e.code in KeyboardController.keyMap.keys) {
                    // protocol: first write the key code integer (in network byte order), then write 0x00 for key down or 0x01 for key up
                    stream.write(ByteOrder.toNetOrd(e.code.code))
                    stream.write(0x00)
                }
            }
        }
        scene.onKeyReleased = EventHandler { e ->
            run {
                if(e.code in KeyboardController.keyMap.keys) {
                    stream.write(ByteOrder.toNetOrd(e.code.code))
                    stream.write(0x01)
                }
            }
        }
    }
}