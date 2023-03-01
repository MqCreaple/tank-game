package mqcreaple.tankgame.board

import javafx.scene.image.Image

class Wall(val canDestruct: Boolean, x: Int, y: Int): BackgroundBlock(x, y) {
    override val canPass: Boolean
        get() = false
    override val image: Image = if(canDestruct) {
        Image(Wall::class.java.getResource("break_brick.jpg")!!.toExternalForm())
    } else {
        Image(Wall::class.java.getResource("solid_brick.jpg")!!.toExternalForm())
    }

    override fun equals(other: Any?): Boolean {
        if(other is Wall) {
            return other.canDestruct == canDestruct
        }
        return false
    }
}