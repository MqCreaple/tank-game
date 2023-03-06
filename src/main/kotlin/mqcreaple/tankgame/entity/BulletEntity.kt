package mqcreaple.tankgame.entity

import javafx.scene.image.ImageView
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.board.Empty
import mqcreaple.tankgame.board.Wall
import kotlin.math.hypot

class BulletEntity(gameIn: Game, x: Double, y: Double, dirX: Double, dirY: Double):
    Entity(gameIn, ImageView(BulletEntity::class.java.getResource("enemy_bullet.png")!!.toExternalForm()), x, y) {

    override val width: Double
        get() = 0.1
    override val height: Double
        get() = 0.1
    private val dirX: Double
    private val dirY: Double

    init {
        val magnitude = hypot(dirX, dirY)
        this.dirX = dirX * velocity / magnitude
        this.dirY = dirY * velocity / magnitude
    }

    override fun update(board: Board) {
        tryMove(x + dirX / gameIn.lastFPS, y + dirY / gameIn.lastFPS, board)
    }

    override fun onCollideWithBlock(block: BackgroundBlock, board: Board) {
        super.onCollideWithBlock(block, board)
        if(block is Wall) {
            if(block.canDestruct) {
                gameIn.destroyBlock(block)
            }
        }
        this.kill()
    }

    override fun onCollideWithBorder(board: Board) {
        super.onCollideWithBorder(board)
        this.kill()
    }

    val velocity: Double
        get() = 5.0
}