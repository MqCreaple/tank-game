package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game
import java.io.Serializable

abstract class Event: Serializable {
    abstract fun run(game: Game)

    companion object {
        const val serialVersionUID: Long = 114514
    }
}