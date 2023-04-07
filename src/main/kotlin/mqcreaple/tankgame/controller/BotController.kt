package mqcreaple.tankgame.controller

import javafx.application.Platform
import javafx.scene.shape.Rectangle
import mqcreaple.tankgame.Direction
import mqcreaple.tankgame.entity.ControllableEntity
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.game.Game
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class BotController(
    controlling: ControllableEntity,
    private val gameIn: Game,
    target: Entity?,
    ): Controller(controlling) {
    /**
     * Represents a segment in the calculated path.
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @param dir direction to move
     * @param dist distance to move
     */
    data class PathSegment(val x: Double, val y: Double, val dir: Direction, var dist: Double)

    var path: ArrayList<PathSegment>? = null
    var target = target
        set(value) {
            field = value
            if(value != null) {
                path = findPath(this.controlling.x, this.controlling.y, value, 0.1)
            }
        }

    constructor(controlling: ControllableEntity, gameIn: Game): this(controlling, gameIn, null)

    override fun getAction(): Action {
        return path?.let {
            if(it.isEmpty()) {
                path = null
                Action.NONE
            } else {
                val dir = it[it.size - 1].dir
                val originX = it[it.size - 1].x
                val originY = it[it.size - 1].y
                val distPassed = (controlling.x - originX) * dir.x + (controlling.y - originY) * dir.y
                if(distPassed >= it[it.size - 1].dist) {
                    it.removeLast()
                }
                if(it.isEmpty()) {
                    path = null
                    Action.NONE
                } else {
                    Action.MOVE(it[it.size - 1].dir)
                }
            }
        } ?: Action.NONE
    }

    /**
     * Use A* algorithm to find a path from a starting position to the target entity's position.
     *
     * This implementation of A* divides the continuous game board into multiple discrete grids, whose width
     * is the given `gridSize`.
     *
     * @param startX x coordinate of starting position
     * @param startY y coordinate of starting position
     * @param target target entity
     * @param gridSize width/height of searching grid
     */
    fun findPath(startX: Double, startY: Double, target: Entity, gridSize: Double): ArrayList<PathSegment>? {
        data class Pos(val x: Int, val y: Int) {
            override fun equals(other: Any?): Boolean {
                return other is Pos && other.x == x && other.y == y
            }

            override fun hashCode(): Int {
                return x.hashCode() * 31 + y.hashCode()
            }
        }
        data class PointWithDist(var dx: Int, var dy: Int, val estimatedDist: Double): Comparable<PointWithDist> {
            val dPos
                get() = Pos(dx, dy)
            val realX: Double
                get() = startX + dx * gridSize
            val realY: Double
                get() = startY + dy * gridSize
            override operator fun compareTo(other: PointWithDist): Int = estimatedDist.compareTo(other.estimatedDist)
        }

        val queue = PriorityQueue<PointWithDist>()
        val distAndDir = HashMap<Pos, Pair<Double, Direction>>() // records distance from starting point to the point
                                                                 // and the direction of last step to the point
        val visited = HashSet<Pos>()                             // contains all visited points (points whose shortest distance to starting point is determined)
        queue.add(PointWithDist(0, 0, manhattanDist(startX, startY, target.x, target.y)))
        distAndDir[Pos(0, 0)] = Pair(0.0, Direction.UP)
        var found: PointWithDist? = null
        while(!queue.isEmpty()) {
            val cur = queue.remove()
            if(visited.contains(cur.dPos)) {
                continue
            }
            visited.add(cur.dPos)
            if(cur.realX in target.xBound && cur.realY in target.yBound) {
                found = cur
                break
            }
            for(direction in Direction.values()) {
                val nextDist = distAndDir[cur.dPos]!!.first.plus(gridSize)
                val next = PointWithDist(cur.dx + direction.x, cur.dy + direction.y,
                    nextDist + manhattanDist(cur.realX, cur.realY, target.x, target.y))

                // if the next position is within the game board's bound
                if(next.realX !in 0.0..(gameIn.gui.board.widthDouble - controlling.width) ||
                        next.realY !in 0.0..(gameIn.gui.board.heightDouble - controlling.height)) {
                    continue
                }
                if(visited.contains(next.dPos)) {
                    continue
                }
                // if the controlled entity collide with blocks it cannot pass
                if(gameIn.gui.board.getCoveredBlocks(next.realX, next.realY, controlling.width, controlling.height).any { block -> !block.canPass }) {
                    continue
                }
                if(distAndDir[next.dPos]?.let { it.first < next.estimatedDist } != false) {
                    distAndDir[next.dPos] = Pair(next.estimatedDist, direction)
                    queue.add(next)
                }
            }
        }
        if(found == null) {
            // does not find any path from starting point to the target entity
            return null
        } else {
            val path = ArrayList<PathSegment>()
            val lastPoint = found
            var lastDir = distAndDir[found.dPos]!!.second
            var pathDist = 0.0
            while(lastPoint.dx != 0 || lastPoint.dy != 0) {
                lastPoint.dx -= lastDir.x
                lastPoint.dy -= lastDir.y
                pathDist += gridSize
                val newLastDir = distAndDir[found.dPos]!!.second
                if(newLastDir != lastDir) {
                    path.add(PathSegment(lastPoint.realX, lastPoint.realY, lastDir, pathDist))
                    pathDist = 0.0
                }
                lastDir = newLastDir
            }
            return path
        }
    }

    companion object {
        private fun manhattanDist(ax: Double, ay: Double, bx: Double, by: Double): Double {
            return abs(ax - bx) + abs(ay - by)
        }
    }
}