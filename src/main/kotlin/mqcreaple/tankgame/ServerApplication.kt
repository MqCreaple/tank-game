package mqcreaple.tankgame

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.entity.TankEntity
import mqcreaple.tankgame.game.ServerGame

class ServerApplication : Application() {

    override fun start(stage: Stage) {
        // load graphic elements and set up scene
        val fxmlLoader = FXMLLoader(ServerApplication::class.java.getResource("board.fxml"))
        val scene = Scene(fxmlLoader.load())
        // display stage
        stage.title = "Tom Geng's Tank Game"
        stage.scene = scene
        stage.show()
        // add game entity
        val game = ServerGame(fxmlLoader.getController(), gamePort)
        stage.onCloseRequest = EventHandler { game.gameEnd = true }
        game.keyboardController = KeyboardController(scene)
        // start game thread
        val gameThread = Thread(game::gameMain)
        gameThread.name = "Game Thread"
        gameThread.start()
    }

    companion object {
        val gamePort: UShort = 11451u     // port to open the tank game

        @JvmStatic
        fun main(args: Array<String>) {
            launch(ServerApplication::class.java)
        }
    }
}