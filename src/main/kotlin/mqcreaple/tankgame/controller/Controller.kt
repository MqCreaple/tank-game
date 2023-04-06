package mqcreaple.tankgame.controller

import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.entity.ControllableEntity
import java.time.Duration
import java.time.Instant

abstract class Controller(val controlling: ControllableEntity) {
    sealed class Action {
        abstract val coolDown: Double     // in milliseconds
        abstract val ordinal: Int

        object NONE: Action() {
            override val coolDown: Double
                get() = 0.0
            override val ordinal: Int
                get() = 0
        }

        object ACT: Action() {
            override val coolDown: Double
                get() = 200.0
            override val ordinal: Int
                get() = 1
        }

        data class MOVE(val dir: Direction) : Action() {
            override val coolDown: Double
                get() = 0.0
            override val ordinal: Int
                get() = 2
        }

        companion object {
            val size: Int
                get() = 3
        }
    }

    private val lastAction = Array<Instant>(Action.size) { Instant.now() }

    val actionAfterCoolDown: Action
        get() {
            val action = getAction()
            val now = Instant.now()
            if(Duration.between(lastAction[action.ordinal], now).toMillis() < action.coolDown) {
                return Action.NONE
            }
            lastAction[action.ordinal] = now
            return action
        }

    protected abstract fun getAction(): Action
}