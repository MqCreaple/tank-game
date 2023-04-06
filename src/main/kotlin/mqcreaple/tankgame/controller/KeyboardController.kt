package mqcreaple.tankgame.controller

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.entity.ControllableEntity
import mqcreaple.tankgame.utils.ByteOrder
import java.io.OutputStream
import java.net.Socket

class KeyboardController(controlling: ControllableEntity, scene: Scene): Controller(controlling) {
    private var keyPressed: MutableMap<KeyCode, Boolean> = keyMap.keys.associateWithTo(mutableMapOf()) { false }

    init {
        scene.onKeyPressed = EventHandler { e ->
            run {
                if(e.code in keyPressed.keys) {
                    keyPressed[e.code] = true
                }
            }
        }
        scene.onKeyReleased = EventHandler { e ->
            run {
                if(e.code in keyPressed.keys) {
                    keyPressed[e.code] = false
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