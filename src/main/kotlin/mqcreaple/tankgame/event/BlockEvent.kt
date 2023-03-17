package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Empty

class BlockEvent(val option: Option, val block: BackgroundBlock): Event() {
    enum class Option {
        REPLACE, DESTROY
    }

    override fun run(game: Game) {
        when(option) {
            Option.REPLACE -> {
                game.gui.board[block.y, block.x] = block
            }
            Option.DESTROY -> {
                game.gui.board[block.y, block.x] = Empty(block.y, block.x)
            }
        }
    }
}