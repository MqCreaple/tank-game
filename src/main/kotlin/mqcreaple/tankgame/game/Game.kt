package mqcreaple.tankgame.game

import javafx.application.Platform
import mqcreaple.tankgame.BoardController
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.event.BlockEvent
import mqcreaple.tankgame.event.EntityEvent
import mqcreaple.tankgame.event.Event
import java.time.Duration
import java.time.Instant
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * A tank game.
 * @property gui Graphic UI of this game
 * @property server if this game runs on the server side
 * @property entityList List of all entities in current game
 */
abstract class Game(val gui: BoardController, val server: Boolean) {
    var entityList: ArrayList<Entity> = ArrayList()
    var lastInstant: Instant = Instant.now()
    var lastFPS: Double = FPS
    var gameEnd: Boolean = false
    var eventQueue: ArrayDeque<Event> = ArrayDeque()
    lateinit var keyboardController: KeyboardController

    fun addEntity(entity: Entity) {
        eventQueue.add(EntityEvent(EntityEvent.Option.CREATE, entity, this))
    }

    fun removeEntity(entity: Entity) {
        eventQueue.add(EntityEvent(EntityEvent.Option.REMOVE, entity, this))
    }

    fun destroyBlock(block: BackgroundBlock) {
        eventQueue.add(BlockEvent(BlockEvent.Option.DESTROY, block, this))
    }

    fun gameMain() {
        gameInit()
        while(!gameEnd) {
            update()

            // calculate and monitor fps
            // TODO("Only Server needs to monitor FPS. Clients should wait for server to send signal each time.")
            val inst1 = Instant.now()
            val duration = Duration.between(lastInstant, inst1).toNanos()
            Thread.sleep(max(0, ((1e9 / FPS - duration) / 1e6).toLong()))
            val inst2 = Instant.now()
            val duration2 = Duration.between(lastInstant, inst2).toNanos()
            lastInstant = inst2
            lastFPS = 1e9 / duration2
            syncChanges()
        }
        gameTerminate()
    }

    /**
     * Function called when the game initializes
     */
    open fun gameInit() {}

    /**
     * Handle events.
     *
     * This function would be implemented differently for server and client.
     * - on server side, all `run` functions of every event will be called, and every event will be sent to client sockets.
     * - on client side, it will receive the event objects and call their `run` function.
     */
    abstract fun update()

    /**
     * Function called when the game terminates
     */
    open fun gameTerminate() {}

    companion object {
        // Reference fps. The real fps may be different from this value.
        const val FPS: Double = 60.0
    }

    private var changeCounter = 0

    /**
     * Synchronize changes in game objects to GUI.
     */
    private fun syncChanges() {
        Platform.runLater {
            if(changeCounter % 10 == 0) {
                // synchronize fps every one of 10 frames
                gui.fps.text = "fps: %.1f".format(lastFPS)
                changeCounter = 0
            }
            gui.gamePane.requestLayout()
            changeCounter++
        }
    }
}