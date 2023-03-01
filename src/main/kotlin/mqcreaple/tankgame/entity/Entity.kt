package mqcreaple.tankgame.entity

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.transform.Rotate
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.Game
import mqcreaple.tankgame.board.BackgroundBlock
import mqcreaple.tankgame.board.Board
import mqcreaple.tankgame.event.EntityCollisionEvent
import mqcreaple.tankgame.utils.Overlap
import kotlin.math.max
import kotlin.math.min

abstract class Entity(var gameIn: Game, var guiNode: ImageView, x: Double, y: Double) {
    constructor(gameIn: Game, guiNode: ImageView): this(gameIn, guiNode, 0.0, 0.0)

    abstract val width: Double
    abstract val height: Double

    init {
        guiNode.fitWidth = width * Board.unitPixel
        guiNode.fitHeight = height * Board.unitPixel
        AnchorPane.setLeftAnchor(guiNode, x * Board.unitPixel)
        AnchorPane.setBottomAnchor(guiNode, y * Board.unitPixel)
    }

    // x coordinate (relative to left edge)
    var x: Double = x
        set(value) {
            AnchorPane.setLeftAnchor(guiNode, x * Board.unitPixel)
            field = value
        }
    // y coordinate (relative to bottom edge)
    var y: Double = y
        set(value) {
            AnchorPane.setBottomAnchor(guiNode, y * Board.unitPixel)
            field = value
        }
    open var orientation: Direction = Direction.UP
        set(value) {
            field = value
            // TODO("Change orientation on game panel")
        }

    // boundaries of this entity
    val xBound
        get() = arrayOf(x, x + width)
    val yBound
        get() = arrayOf(y, y + height)

    /**
     * Try to move to a new location.
     * If the location is out of bound or unreachable, cancel the movement
     */
    fun tryMove(newX: Double, newY: Double, board: Board) {
        // detect collision with boarders
        if(newX <= 0 || newX + width >= board.width.toDouble() || newY <= 0 || newY + height >= board.height.toDouble()) {
            onCollideWithBorder(board)
        }
        // detect collision with blocks
        val boundNewX = max(0.0, min(board.width.toDouble(), newX))
        val boundNewY = max(0.0, min(board.height.toDouble(), newY))
        val blocks = board.getCoveredBlocks(boundNewX, boundNewY, width, height)
        if(blocks.all { b -> b.canPass }) {
            x = boundNewX
            y = boundNewY
        }
        blocks.forEach { block -> onCollideWithBlock(block, board) }
        // detect collision with other entities
        for(entity in gameIn.entityList) {
            if(entity == this) {
                continue
            }
            if(
                Overlap.interval(xBound[0], xBound[1], entity.xBound[0], entity.xBound[1]) &&
                Overlap.interval(yBound[0], yBound[1], entity.yBound[0], entity.yBound[1])
            ) {
                gameIn.eventQueue.add(EntityCollisionEvent(gameIn, this, entity))
            }
        }
    }

    /**
     * This function is called in every frame update
     */
    abstract fun update(board: Board)

    /**
     * Event when the entity collides with a non-empty block
     */
    open fun onCollideWithBlock(block: BackgroundBlock, board: Board) {}

    /**
     * Event when the entity hits the boarder of game board
     */
    open fun onCollideWithBorder(board: Board) {}

    /**
     * Event when the entity collides with another entity
     */
    open fun onCollideWithEntity(entity: Entity) {}

    /**
     * Destruct this entity
     */
    open fun kill() {
        gameIn.removeEntity(this)
    }
}