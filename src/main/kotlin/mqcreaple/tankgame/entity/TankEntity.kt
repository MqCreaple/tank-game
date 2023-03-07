package mqcreaple.tankgame.entity

import javafx.scene.image.ImageView
import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.controller.Controller

class TankEntity(gameIn: Game, val player: Int, x: Double, y: Double, controller: Controller) : ControllableEntity(
    gameIn, ImageView(TankEntity::class.java.getResource("player${player}_tank_up.png")!!.toExternalForm()), x, y, controller
) {
    override val velocity: Double
        get() = 2.0
    override val width: Double
        get() = 0.8
    override val height: Double
        get() = 0.8

    override fun act(gameIn: Game, board: Board) {
        val bullet = BulletEntity(
            gameIn,
            if(orientation.x == 0) ((xBound[0] + xBound[1]) / 2) else xBound[if(orientation.x > 0) 1 else 0],
            if(orientation.y == 0) ((yBound[0] + yBound[1]) / 2) else yBound[if(orientation.y > 0) 1 else 0],
            orientation.x.toDouble(),
            orientation.y.toDouble()
        )
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
        gameIn.addEntity(bullet)
    }

    override fun onCollideWithEntity(entity: Entity) {
        if(entity is BulletEntity) {
            this.kill()
            entity.kill()
        }
    }
}