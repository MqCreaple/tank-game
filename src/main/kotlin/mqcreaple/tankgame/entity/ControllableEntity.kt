package mqcreaple.tankgame.entity

import javafx.scene.image.ImageView
import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.controller.Controller

abstract class ControllableEntity(
    gameIn: Game,
    imagePath: String,
    x: Double,
    y: Double,
    private val controller: Controller
) : Entity(gameIn, imagePath, x, y) {
    override fun update(gameIn: Game, board: Board) {
        val action = controller.actionAfterCoolDown
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