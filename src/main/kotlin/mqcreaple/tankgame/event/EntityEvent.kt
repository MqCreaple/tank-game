package mqcreaple.tankgame.event

import javafx.application.Platform
import mqcreaple.tankgame.Game
import mqcreaple.tankgame.entity.Entity

class EntityEvent(val option: Option, val entity: Entity, game: Game): Event(game) {
    enum class Option {
        CREATE, REMOVE
    }

    override fun run() {
        when(option) {
            Option.CREATE -> {
                game.entityList.add(entity)
                Platform.runLater {
                    synchronized(game.entityList) {
                        game.gui.gamePane.children.add(entity.guiNode)
                    }
                }
            }
            Option.REMOVE -> {
                game.entityList.remove(entity)
                Platform.runLater {
                    synchronized(game.entityList) {
                        game.gui.gamePane.children.remove(entity.guiNode)
                    }
                }
            }
        }
    }
}