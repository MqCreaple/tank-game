package mqcreaple.tankgame.controller

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.utils.ByteOrder
import java.io.OutputStream
import java.net.Socket

class KeyboardController(scene: Scene, socket: Socket?): Controller() {
    constructor(scene: Scene): this(scene, null)

    private val stream: OutputStream? = socket?.getOutputStream()
    private var keyPressed: MutableMap<KeyCode, Boolean> = keyMap.keys.associateWithTo(mutableMapOf()) { false }

    init {
        scene.onKeyPressed = EventHandler { e ->
            run {
                if(e.code in keyPressed.keys) {
                    keyPressed[e.code] = true
                    stream?.let {
                        // protocol: first write the key code integer (in network byte order), then write 0x00 for key down or 0x01 for key up
                        stream.write(ByteOrder.toNetOrd(e.code.code))
                        stream.write(0x00)
                    }
                }
            }
        }
        scene.onKeyReleased = EventHandler { e ->
            run {
                if(e.code in keyPressed.keys) {
                    keyPressed[e.code] = false
                    stream?.let {
                        stream.write(ByteOrder.toNetOrd(e.code.code))
                        stream.write(0x01)
                    }
                }
            }
        }
    }

    override fun getAction(): Action {
        for((k, v) in keyPressed) {
            if(v) {
                return keyMap[k]!!
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