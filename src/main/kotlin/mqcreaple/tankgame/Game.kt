package mqcreaple.tankgame

import javafx.application.Platform
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.controller.ServerKeyboardController
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.event.BlockEvent
import mqcreaple.tankgame.event.EntityEvent
import mqcreaple.tankgame.event.Event
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * A tank game.
 * @property gui Graphic UI of this game
 * @property server if this game runs on the server side
 * @property entityList List of all entities in current game
 */
class Game(val gui: BoardController, val server: Boolean) {
    var entityList: ArrayList<Entity> = ArrayList()
    var lastInstant: Instant = Instant.now()
    var lastFPS: Double = FPS
    var gameEnd: Boolean = false
    var eventQueue: ArrayDeque<Event> = ArrayDeque()
    lateinit var keyboardController: KeyboardController
    lateinit var networkController: ServerKeyboardController

    fun addEntity(entity: Entity) {
        eventQueue.add(EntityEvent(EntityEvent.Option.CREATE, entity, this))
    }

    fun removeEntity(entity: Entity) {
        eventQueue.add(EntityEvent(EntityEvent.Option.REMOVE, entity, this))
    }

    fun destroyBlock(block: BackgroundBlock) {
        eventQueue.add(BlockEvent(BlockEvent.Option.DESTROY, block, this))
    }

    fun gameLoop() {
        while(!gameEnd) {
            // handle all events
            while(!eventQueue.isEmpty()) {
                val event = eventQueue.removeFirst()
                event.run()
            }
            // update all entity
            synchronized(entityList) {
                for(entity in entityList) {
                    entity.update(gui.board)
                }
            }

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
    }

    companion object {
        // Reference fps. The real fps may be different from this value.
        const val FPS: Double = 60.0
    }

    private var changeCounter = 0
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