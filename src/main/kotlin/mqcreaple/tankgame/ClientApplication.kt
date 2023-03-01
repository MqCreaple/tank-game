package mqcreaple.tankgame

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import mqcreaple.tankgame.controller.KeyboardController
import java.net.Socket

class ClientApplication: Application() {
    override fun start(stage: Stage) {
        println("Please enter an IP address and port number, separated with a space")
        val line: List<String> = readLine()!!.split(' ')
        val socket = Socket(line[0], line[1].toInt())
        val fxmlLoader = FXMLLoader(ClientApplication::class.java.getResource("board.fxml"))
        val scene = Scene(fxmlLoader.load())
        // display stage
        stage.title = "Tom Geng's Tank Game Client"
        stage.scene = scene
        stage.show()
        // initialize game object
        val game = fxmlLoader.getController<BoardController>().game
        stage.onCloseRequest = EventHandler { game.gameEnd = true }
        game.keyboardController = KeyboardController(scene, socket)
        val gameThread = Thread(game::gameLoop)
        gameThread.name = "Game Thread"
        gameThread.start()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ClientApplication::class.java)
        }
    }
}