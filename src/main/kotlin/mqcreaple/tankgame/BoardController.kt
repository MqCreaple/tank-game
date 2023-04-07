package mqcreaple.tankgame

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import mqcreaple.tankgame.board.Board
import java.io.File
import kotlin.math.*

class BoardController {
    lateinit var board: Board

    @FXML
    private lateinit var version: String

    @FXML
    private lateinit var masterPane: HBox

    @FXML
    lateinit var statusBar: VBox

    @FXML
    lateinit var gamePane: AnchorPane

    @FXML
    lateinit var fps: Label

    val paths: ArrayList<Line> = ArrayList()

    @FXML
    fun initialize() {
        initialize(File("board.txt").readText())
    }

    fun initialize(boardStr: String) {
        board = Board.parseString(boardStr, gamePane)

        Board.unitPixel = min(gamePane.prefWidth / board.width, gamePane.prefHeight / board.height)
        for(i in 0 until board.height) {
            for(j in 0 until board.width) {
                board.backgroundImage[i][j]?.let {
                    it.fitWidth = Board.unitPixel
                    it.fitHeight = Board.unitPixel
                    AnchorPane.setBottomAnchor(it, i * Board.unitPixel)
                    AnchorPane.setLeftAnchor(it, j * Board.unitPixel)
                    gamePane.children.add(it)
                }
            }
        }
    }

    fun clearPath() {
        paths.forEach { gamePane.children.remove(it) }
        paths.clear()
    }

    fun addPath(startX: Double, startY: Double, endX: Double, endY: Double) {
        val line = Line(
            startX * Board.unitPixel,
            (board.height - startY) * Board.unitPixel,
            endX * Board.unitPixel,
            (board.height - endY) * Board.unitPixel
        )
        line.stroke = Color.RED
        line.strokeWidth = 2.0
        gamePane.children.add(line)
        paths.add(line)
    }
}