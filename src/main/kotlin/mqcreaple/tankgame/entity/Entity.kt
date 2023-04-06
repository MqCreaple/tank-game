package mqcreaple.tankgame.entity

import javafx.scene.layout.AnchorPane
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.event.EntityCollisionEvent
import mqcreaple.tankgame.game.ServerGame
import mqcreaple.tankgame.utils.Overlap
import java.io.Serializable
import java.util.*
import kotlin.math.max
import kotlin.math.min

abstract class Entity(val imagePath: String, x: Double, y: Double): Serializable {
    constructor(imagePath: String): this(imagePath, 0.0, 0.0)

    val uuid: UUID = UUID.randomUUID()

    abstract val width: Double
    abstract val height: Double

    // x coordinate (relative to left edge)
    var x: Double = x
    fun setX(value: Double, gameIn: Game) {
        AnchorPane.setLeftAnchor(gameIn.imageMap[uuid], x * Board.unitPixel)
        x = value
    }
    // y coordinate (relative to bottom edge)
    var y: Double = y
        get() = field
    fun setY(value: Double, gameIn: Game) {
        AnchorPane.setBottomAnchor(gameIn.imageMap[uuid], y * Board.unitPixel)
        y = value
    }
    open var orientation: Direction = Direction.UP
        set(value) {
            field = value
            // TODO("Change orientation on game panel")
        }

    // center of this entity
    val xCenter
        get() = x + width / 2
    fun setXCenter(value: Double, gameIn: Game) {
        setX(value - width / 2, gameIn)
    }
    val yCenter
        get() = y + height / 2
    fun setYCenter(value: Double, gameIn: Game) {
        setY(value - height / 2, gameIn)
    }

    // boundaries of this entity
    val xBound
        get() = x..(x + width)
    val yBound
        get() = y..(y + height)

    /**
     * Try to move to a new location.
     * If the location is out of bound or unreachable, cancel the movement
     */
    fun tryMove(newX: Double, newY: Double, gameIn: Game, board: Board) {
        // detect collision with boarders
        if(newX <= 0 || newX + width >= board.width.toDouble() || newY <= 0 || newY + height >= board.height.toDouble()) {
            onCollideWithBorder(gameIn, board)
        }
        // detect collision with blocks
        val boundNewX = max(0.0, min(board.width.toDouble(), newX))
        val boundNewY = max(0.0, min(board.height.toDouble(), newY))
        val blocks = board.getCoveredBlocks(boundNewX, boundNewY, width, height)
        if(blocks.all { b -> b.canPass }) {
            setX(boundNewX, gameIn)
            setY(boundNewY, gameIn)
        }
        blocks.forEach { block -> onCollideWithBlock(block, gameIn, board) }
        // detect collision with other entities
        for((uuid, entity) in gameIn.entityMap) {
            if(entity == this) {
                continue
            }
            if(
                Overlap.interval(xBound.start, xBound.endInclusive, entity.xBound.start, entity.xBound.endInclusive) &&
                Overlap.interval(yBound.start, yBound.endInclusive, entity.yBound.start, entity.yBound.endInclusive)
            ) {
                gameIn.eventQueue.add(EntityCollisionEvent(this.uuid, entity.uuid))
            }
        }
    }

    /**
     * This function is called in every frame update
     */
    abstract fun update(gameIn: ServerGame, board: Board)

    /**
     * Event when the entity collides with a non-empty block
     */
    open fun onCollideWithBlock(block: BackgroundBlock, gameIn: Game, board: Board) {}

    /**
     * Event when the entity hits the boarder of game board
     */
    open fun onCollideWithBorder(gameIn: Game, board: Board) {}

    /**
     * Event when the entity collides with another entity
     */
    open fun onCollideWithEntity(entity: Entity, gameIn: Game) {}

    /**
     * Destruct this entity
     */
    open fun kill(gameIn: Game) {
        gameIn.scheduledRemoveEntity(this)
    }
}