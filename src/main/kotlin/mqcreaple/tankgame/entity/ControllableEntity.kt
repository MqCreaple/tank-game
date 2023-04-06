package mqcreaple.tankgame.entity

import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.controller.Controller
import mqcreaple.tankgame.game.ServerGame

abstract class ControllableEntity(
    imagePath: String,
    x: Double,
    y: Double,
    private val controllerName: String
) : Entity(imagePath, x, y) {
    override fun update(gameIn: ServerGame, board: Board) {
        val action = gameIn.getPlayer(controllerName)!!.controller.actionAfterCoolDown
        if(action is Controller.Action.MOVE) {
            val newX = x + velocity / gameIn.lastFPS * action.dir.x
            val newY = y + velocity / gameIn.lastFPS * action.dir.y
            tryMove(newX, newY, gameIn, board)
            orientation = action.dir
        }
        if(action == Controller.Action.ACT) {
            act(gameIn, board)
        }
    }

    open fun act(gameIn: Game, board: Board) {}

    abstract val velocity: Double
}