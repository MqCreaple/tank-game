package mqcreaple.tankgame.board

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
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

    operator fun get(i: Int, j: Int) = BackgroundBlock.fromChar(background[i][j], j, i)
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

    fun getCenterPos(i: Int, j: Int) = Pair((i + 0.5) * unitPixel, (j + 0.5) * unitPixel)

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
    }
}