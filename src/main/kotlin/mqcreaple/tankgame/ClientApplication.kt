package mqcreaple.tankgame

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import mqcreaple.tankgame.controller.KeyboardController
import mqcreaple.tankgame.controller.KeyboardRemote
import mqcreaple.tankgame.entity.TankEntity
import mqcreaple.tankgame.game.ClientGame
import java.net.Socket

class ClientApplication: Application() {
    override fun start(stage: Stage) {
        println("Please give your tank a name: ")
        val name = readLine()!!
        println("Please enter server's IP address and port, separated by a single space character:")
        val line: List<String> = readLine()!!.split(' ')
        val socket = Socket(line[0], line[1].toInt())
        // load GUI
        val fxmlLoader = FXMLLoader(ClientApplication::class.java.getResource("board.fxml"))
        val scene = Scene(fxmlLoader.load())
        // display stage
        stage.title = "Tom Geng's Tank Game Client"
        stage.scene = scene
        stage.show()
        // initialize game object
        val game = ClientGame(fxmlLoader.getController(), name, socket)
        stage.onCloseRequest = EventHandler { game.gameEnd = true }
        game.keyboardRemote = KeyboardRemote(scene, game.socket)
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