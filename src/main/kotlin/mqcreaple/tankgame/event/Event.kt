package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game
import java.io.Serializable

abstract class Event: Serializable {
    val serialVersionUID = 114514L

    abstract fun run(game: Game)
}