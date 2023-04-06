package mqcreaple.tankgame.controller

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
    target: UUID?,
    ): Controller(controlling) {
    var path: ArrayList<Pair<Direction, Double>>? = null
    var target = target
        set(value) {
            field = value
            if(value != null) {
                path = gameIn.entityMap[value]?.let { findPath(this.controlling.x, this.controlling.y, it, 0.1) }
            }
        }

    constructor(controlling: ControllableEntity, gameIn: Game): this(controlling, gameIn, null)

    override fun getAction(): Action {
        return if(path == null) {
            Action.NONE
        } else {
            TODO("Move along path.")
        }
    }

    /**
     * Use A* algorithm to find a path from a starting position to the target entity's position.
     *
     * This implementation of A* divides the continuous game board into multiple discrete grids, whose width
     * is the given `gridSize`.
     *
     * @param startX x coordinate of starting position
     * @param startY y coordinate of starting position
     * @param target
     */
    fun findPath(startX: Double, startY: Double, target: Entity, gridSize: Double): ArrayList<Pair<Direction, Double>>? {
        data class PointWithDist(var dx: Int, var dy: Int, val estimatedDist: Double) {
            val dPos
                get() = Pair(dx, dy)
            val realX: Double
                get() = startX + dx * gridSize
            val realY: Double
                get() = startY + dx * gridSize
            operator fun compareTo(other: PointWithDist): Int = estimatedDist.compareTo(other.estimatedDist)
        }

        val queue = PriorityQueue<PointWithDist>()
        val distAndDir = HashMap<Pair<Int, Int>, Pair<Double, Direction>>() // records distance from starting point to the point
                                                                            // and the direction of last step to the point
        val visited = HashSet<Pair<Int, Int>>()                             // contains all visited points (points whose shortest distance to starting point is determined)
        queue.add(PointWithDist(0, 0, manhattanDist(startX, startY, target.x, target.y)))
        distAndDir[Pair(0, 0)] = Pair(0.0, Direction.UP)
        visited.add(Pair(0, 0))
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
                // if the controlled entity collide with blocks it cannot pass
                if(gameIn.gui.board.getCoveredBlocks(next.realX, next.realY, controlling.width, controlling.height).any { block -> !block.canPass }) {
                    continue
                }
                if(distAndDir[Pair(next.dx, next.dy)]?.let { it.first < next.estimatedDist } != false) {
                    distAndDir[Pair(next.dx, next.dy)] = Pair(next.estimatedDist, direction)
                    queue.add(next)
                }
            }
        }
        if(found == null) {
            // does not find any path from starting point to the target entity
            return null
        } else {
            val path = ArrayList<Pair<Direction, Double>>()
            val lastPoint = found
            var lastDir = distAndDir[Pair(found.dx, found.dy)]!!.second
            var pathDist = 0.0
            while(lastPoint.dx != 0 || lastPoint.dy != 0) {
                lastPoint.dx -= lastDir.x
                lastPoint.dy -= lastDir.y
                pathDist += gridSize
                val newLastDir = distAndDir[Pair(found.dx, found.dy)]!!.second
                if(newLastDir != lastDir) {
                    path.add(Pair(lastDir, pathDist))
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