package mqcreaple.tankgame

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.game.ClientGame
import java.net.Socket

class ClientApplication: Application() {
    override fun start(stage: Stage) {
        // load GUI
        val fxmlLoader = FXMLLoader(ClientApplication::class.java.getResource("board.fxml"))
        val scene = Scene(fxmlLoader.load())
        // display stage
        stage.title = "Tom Geng's Tank Game Client"
        stage.scene = scene
        stage.show()
        // initialize game object
        val game = ClientGame(fxmlLoader.getController())
        stage.onCloseRequest = EventHandler { game.gameEnd = true }
        game.keyboardController = KeyboardController(scene, game.socket)
        val gameThread = Thread(game::gameMain)
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