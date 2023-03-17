package mqcreaple.tankgame.event

import mqcreaple.tankgame.game.Game
import mqcreaple.tankgame.entity.Entity
import java.util.*

class EntityCollisionEvent(val collider: UUID, val collided: UUID): Event() {
    override fun run(game: Game) {
        game.entityMap[collider]?.let {
            game.entityMap[collided]?.onCollideWithEntity(it, game)
        }
    }
}