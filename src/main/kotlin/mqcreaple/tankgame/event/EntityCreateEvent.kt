package mqcreaple.tankgame.event

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.game.Game

class EntityCreateEvent(val entity: Entity): Event() {
    override fun run(game: Game) {
        game.entityMap[entity.uuid] = entity
        game.imageMap[entity.uuid] = ImageView(Entity::class.java.getResource(entity.imagePath)!!.toExternalForm())
        Platform.runLater {
            synchronized(game.entityMap) {
                val obj = game.imageMap[entity.uuid]!!
                obj.fitWidth = entity.width * Board.unitPixel
                obj.fitHeight = entity.height * Board.unitPixel
                AnchorPane.setLeftAnchor(obj, entity.x * Board.unitPixel)
                AnchorPane.setBottomAnchor(obj, entity.y * Board.unitPixel)
                game.gui.gamePane.children.add(game.imageMap[entity.uuid])
            }
        }
    }
}