package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Empty

class BlockEvent(val option: Option, val x: Int, val y: Int, val blockChar: Char): Event() {
    enum class Option {
        REPLACE, DESTROY
    }

    override fun run(game: Game) {
        when(option) {
            Option.REPLACE -> {
                game.gui.board[y, x] = BackgroundBlock.fromChar(blockChar, x, y)
            }
            Option.DESTROY -> {
                game.gui.board[y, x] = Empty(y, x)
            }
        }
    }
}