package mqcreaple.tankgame.entity

import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.game.ServerGame
import kotlin.math.hypot

class PathFindBulletEntity(gameIn: Game, x: Double, y: Double, targetX: Int, targetY: Int, defaultDirX: Double, defaultDirY: Double):
    BulletEntity(gameIn, x, y, defaultDirX, defaultDirY) {
    override val width: Double
        get() = 0.1
    override val height: Double
        get() = 0.1
    private val dirX: Double
    private val dirY: Double

    init {
        val magnitude = hypot(defaultDirX, defaultDirY)
        this.dirX = defaultDirX * velocity / magnitude
        this.dirY = defaultDirY * velocity / magnitude
    }

    var path = gameIn.gui.board.getPath(y.toInt(), x.toInt(), targetY, targetX)
    private var curAt = 0

    override fun update(gameIn: ServerGame, board: Board) {
        path?.let {
            // there is a path from start to end, then follow the path
            val newX = x + it[curAt].x * velocity/ gameIn.lastFPS
            val newY = y + it[curAt].y * velocity / gameIn.lastFPS
            if((newX.toInt() != x.toInt() || newX.toInt() != (x + this.width).toInt()) && (newX.toInt() == (newX + this.width).toInt()) ||
                (newY.toInt() != y.toInt() || newY.toInt() != (y + this.height).toInt()) && (newY.toInt() == (newY + this.height).toInt())) {
                curAt++
                if(curAt >= it.size) {
                    // arrives at destination, then stop path finding and move in straight line
                    path = null
                }
            }
            tryMove(newX, newY, gameIn, board)
        } ?: run {
            // no path exists from start to end, then follow default direction
            super.update(gameIn, board)
        }
    }
}