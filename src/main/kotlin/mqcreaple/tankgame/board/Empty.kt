package mqcreaple.tankgame.board

import javafx.scene.image.Image

class Empty(x: Int, y: Int): BackgroundBlock(x, y) {
    override val canPass: Boolean
        get() = true
    override val image: Image? = null

    override fun equals(other: Any?): Boolean {
        return other is Empty
    }
}