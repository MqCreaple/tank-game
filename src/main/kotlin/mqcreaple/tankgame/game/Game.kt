package mqcreaple.tankgame.game

import javafx.application.Platform
import javafx.scene.image.ImageView
import mqcreaple.tankgame.BoardController
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.event.BlockEvent
import mqcreaple.tankgame.event.EntityCreateEvent
import mqcreaple.tankgame.event.EntityRemoveEvent
import mqcreaple.tankgame.event.Event
import java.io.Serializable
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max

/**
 * A tank game.
 * @property gui Graphic UI of this game
 * @property server if this game runs on the server side
 * @property entityMap Mapping from entity's UUID to the entity object
 */
abstract class Game(val gui: BoardController, val server: Boolean) {
    var entityMap: MutableMap<UUID, Entity> = mutableMapOf()
    var imageMap: MutableMap<UUID, ImageView> = mutableMapOf()
    var lastInstant: Instant = Instant.now()
    var lastFPS: Double = FPS
    var gameEnd: Boolean = false
    var eventQueue: ArrayDeque<Event> = ArrayDeque()

    fun scheduledAddEntity(entity: Entity) {
        synchronized(eventQueue) {
            eventQueue.add(EntityCreateEvent(entity))
        }
    }

    fun scheduledRemoveEntity(entity: Entity) {
        synchronized(eventQueue) {
            eventQueue.add(EntityRemoveEvent(entity.uuid))
        }
    }

    fun scheduledDestroyBlock(block: BackgroundBlock) {
        synchronized(eventQueue) {
            eventQueue.add(BlockEvent(BlockEvent.Option.DESTROY, block.x, block.y, BackgroundBlock.toChar(block)))
        }
    }

    fun gameMain() {
        gameInit()
        while(!gameEnd) {
            update()

            // calculate and monitor fps
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

    data class EntityPosition(val uuid: UUID, val x: Double, val y: Double): Serializable {
        companion object {
            const val serialVersionUID: Long = 114515
        }
    }
}