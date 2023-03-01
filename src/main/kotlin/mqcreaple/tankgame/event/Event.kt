package mqcreaple.tankgame.event

import mqcreaple.tankgame.Game

abstract class Event(val game: Game) {
    abstract fun run()
}