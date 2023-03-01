package mqcreaple.tankgame.event

import mqcreaple.tankgame.Game
import mqcreaple.tankgame.entity.Entity

class EntityCollisionEvent(game: Game, val collider: Entity, val collided: Entity): Event(game) {
    override fun run() {
        collided.onCollideWithEntity(collider)
    }
}