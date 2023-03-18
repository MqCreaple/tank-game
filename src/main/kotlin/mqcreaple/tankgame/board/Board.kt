package mqcreaple.tankgame.board

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import mqcreaple.tankgame.Direction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class Board(
    val width: Int,
    val height: Int,
    val background: List<CharArray>,
    val backgroundImage: List<ArrayList<ImageView?>>,
    val parentPane: Pane
    ) {
    init {
        assert(background.size == height)
        for(row in background) {
            assert(row.size == width)
        }
    }

    operator fun get(y: Int, x: Int) = BackgroundBlock.fromChar(background[y][x], x, y)
    operator fun get(x: Double, y: Double) {
        val yi = y.toInt()
        val xi = x.toInt()
        BackgroundBlock.fromChar(background[yi][xi], xi, yi)
    }

    operator fun set(y: Int, x: Int, value: BackgroundBlock) {
        background[y][x] = BackgroundBlock.toChar(value)
        backgroundImage[y][x]?.let {
            Platform.runLater { parentPane.children.remove(it) }
        }
        value.image?.let {
            backgroundImage[y][x] =  ImageView(value.image)
            AnchorPane.setLeftAnchor(backgroundImage[y][x], x * unitPixel)
            AnchorPane.setBottomAnchor(backgroundImage[y][x], y * unitPixel)
        } ?: run {
            backgroundImage[y][x] = null
        }
        backgroundImage[y][x]?.let {
            Platform.runLater { parentPane.children.add(it) }
        }
    }

    fun getCenterPos(y: Int, x: Int) = Pair((y + 0.5) * unitPixel, (x + 0.5) * unitPixel)

    fun getCoveredBlocks(x: Double, y: Double, w: Double, h: Double): List<BackgroundBlock> {
        val xLeft = max(0, floor(x).toInt())
        val xRight = min(width - 1, floor(x + w).toInt())
        val yLeft = max(0, floor(y).toInt())
        val yRight = min(height - 1, floor(y + h).toInt())
        val ret = mutableListOf<BackgroundBlock>()
        for(col in yLeft..yRight) {
            for(row in xLeft..xRight) {
                val block = BackgroundBlock.fromChar(background[col][row], row, col)
                if(block !is Empty) {
                    ret.add(block)
                }
            }
        }
        return ret
    }

    /**
     * Get the shortest path between two points.
     *
     * If there is no path between starting and ending point, return null.
     */
    fun getPath(startY: Int, startX: Int, endY: Int, endX: Int): List<Direction>? {
        data class PointWithDist(val x: Int, val y: Int, val estimateDist: Int): Comparable<PointWithDist> {
            val pos
                get() = Pair(x, y)

            override fun compareTo(other: PointWithDist): Int {
                return estimateDist.compareTo(other.estimateDist)
            }
        }

        val start = Pair(startX, startY)
        val end = Pair(endX, endY)

        val queue = PriorityQueue<PointWithDist>()
        val dirMap: Array<Array<Direction?>> = Array(height) { arrayOfNulls(width) }

        // shortest distance from starting position to a given point
        val dist: Array<Array<Int>> = Array(height) { Array(width) { Int.MAX_VALUE } }
        val visited: Array<Array<Boolean>> = Array(height) { Array(width) { false } }
        dist[startY][startX] = 0
        queue.add(PointWithDist(startX, startY, manhattanDist(start, end)))
        while(!queue.isEmpty()) {
            val current = queue.remove()
            if(visited[current.y][current.x]) {
                continue
            }
            visited[current.y][current.x] = true
            if(current.x == endX && current.y == endY) {
                // found ending point, return.
                break
            }
            for(direction in Direction.values()) {
                val nextDist = dist[current.y][current.x] + 1
                val next = PointWithDist(
                    current.x + direction.x,
                    current.y + direction.y,
                    nextDist + manhattanDist(current.x + direction.x, current.y + direction.y, endX, endY)
                )
                if(next.x < 0 || next.x >= width || next.y < 0 || next.y >= height ||
                    get(next.y, next.x) is Wall) {
                    // moves out of map bound or move into a wall, then skip the current case
                    continue
                }
                if(nextDist < dist[next.y][next.x]) {
                    dist[next.y][next.x] = nextDist
                    dirMap[next.y][next.x] = direction
                    queue.add(next)
                }
            }
        }
        // trace back path from ending point
        return if(!visited[endY][endX]) {
            null
        } else {
            val list = ArrayList<Direction>()
            var curX = endX
            var curY = endY
            while(curX != startX || curY != startY) {
                val dir = dirMap[curY][curX]!!
                list.add(dir)
                curX -= dir.x
                curY -= dir.y
            }
            list.reversed()
        }
    }

    companion object {
        var unitPixel: Double = 0.0
        fun parseString(str: String, pane: Pane): Board {
            val lines = str.split("\r\n").filter { line -> line.isNotEmpty() }.map { line -> line.toCharArray() }
            val images = lines.map { line -> line.mapTo(ArrayList()) {
                    ch -> ImageView(BackgroundBlock.fromChar(ch, 0, 0).image) as ImageView?
            } }
            val height = lines.size
            val width = lines[0].size
            return Board(width, height, lines, images, pane)
        }

        private fun manhattanDist(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
            return abs(a.first - b.first) + abs(a.second - b.second)
        }

        private fun manhattanDist(ax: Int, ay: Int, bx: Int, by: Int): Int {
            return abs(ax - bx) + abs(ay - by)
        }
    }

    override fun toString(): String {
        return background.joinToString(separator = "\r\n", transform = {array -> String(array)})
    }
}