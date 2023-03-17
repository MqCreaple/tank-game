package mqcreaple.tankgame.event

import javafx.application.Platform
import mqcreaple.tankgame.game.Game
import java.util.*

class EntityRemoveEvent(val uuid: UUID): Event() {
    override fun run(game: Game) {
        game.entityMap.remove(uuid)
        Platform.runLater {
            synchronized(game.imageMap) {
                game.gui.gamePane.children.remove(game.imageMap[uuid])
                game.imageMap.remove(uuid)
            }
        }
    }
}