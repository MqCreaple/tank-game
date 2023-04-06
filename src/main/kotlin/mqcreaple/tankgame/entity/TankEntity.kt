package mqcreaple.tankgame.entity

import javafx.scene.image.ImageView
import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.controller.Controller

class TankEntity(player: Int, x: Double, y: Double, controllerName: String) : ControllableEntity(
    "player${player}_tank_up.png", x, y, controllerName
) {
    override val velocity: Double
        get() = 2.0
    override val width: Double
        get() = 0.8
    override val height: Double
        get() = 0.8

    override fun act(gameIn: Game, board: Board) {
        val bullet = BulletEntity(
            if(orientation.x == 0) ((xBound.start + xBound.endInclusive) / 2) else if(orientation.x > 0) xBound.endInclusive else xBound.start,
            if(orientation.y == 0) ((yBound.start + yBound.endInclusive) / 2) else if(orientation.y > 0) yBound.endInclusive else yBound.start,
            orientation.x.toDouble(),
            orientation.y.toDouble()
        )
        /*val bullet = PathFindBulletEntity(
            gameIn,
            if(orientation.x == 0) ((xBound[0] + xBound[1]) / 2) else xBound[if(orientation.x > 0) 1 else 0],
            if(orientation.y == 0) ((yBound[0] + yBound[1]) / 2) else yBound[if(orientation.y > 0) 1 else 0],
            0, 0,
            orientation.x.toDouble(),
            orientation.y.toDouble()
        )*/
        if(orientation.x == 0) {
            bullet.x -= bullet.width / 2
            if(orientation.y < 0) {
                bullet.y -= bullet.height
            }
        }
        if(orientation.y== 0) {
            bullet.y -= bullet.height / 2
            if(orientation.x < 0) {
                bullet.x -= bullet.width
            }
        }
        gameIn.scheduledAddEntity(bullet)
    }

    override fun onCollideWithEntity(entity: Entity, gameIn: Game) {
        if(entity is BulletEntity) {
            this.kill(gameIn)
            entity.kill(gameIn)
        }
    }
}