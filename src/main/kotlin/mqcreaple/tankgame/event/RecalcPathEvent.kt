package mqcreaple.tankgame.event

import mqcreaple.tankgame.controller.BotController
import mqcreaple.tankgame.entity.Entity
import mqcreaple.tankgame.game.Game

class RecalcPathEvent(val controller: BotController): Event() {
    override fun run(game: Game) {
        if(controller.target != null) {
            controller.target = controller.target
        }
    }
}