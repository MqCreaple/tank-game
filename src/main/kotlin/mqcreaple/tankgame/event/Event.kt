package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game

abstract class Event(val game: Game) {
    abstract fun run()

    /**
     * Convert an event into a stream of bytes.
     */
    // abstract fun serialize(): ByteArray
}