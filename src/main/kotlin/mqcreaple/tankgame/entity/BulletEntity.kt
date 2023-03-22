package mqcreaple.tankgame.entity

import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.board.Wall
import mqcreaple.tankgame.game.ServerGame
import kotlin.math.hypot

open class BulletEntity(gameIn: Game, x: Double, y: Double, dirX: Double, dirY: Double):
    Entity(gameIn, "enemy_bullet.png", x, y) {

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

    override fun update(gameIn: ServerGame, board: Board) {
        tryMove(x + dirX / gameIn.lastFPS, y + dirY / gameIn.lastFPS, gameIn, board)
    }

    override fun onCollideWithBlock(block: BackgroundBlock, gameIn: Game, board: Board) {
        super.onCollideWithBlock(block, gameIn, board)
        if(block is Wall && block.canDestruct) {
            gameIn.scheduledDestroyBlock(block)
        }
        this.kill(gameIn)
    }

    override fun onCollideWithBorder(gameIn: Game, board: Board) {
        super.onCollideWithBorder(gameIn, board)
        this.kill(gameIn)
    }

    val velocity: Double
        get() = 5.0
}