package mqcreaple.tankgame.board

import javafx.scene.image.Image

abstract class BackgroundBlock(val x: Int, val y: Int) {
    abstract val canPass: Boolean
    abstract val image: Image?

    companion object {
        fun fromChar(ch: Char, x: Int, y: Int): BackgroundBlock {
            return when(ch) {
                'D' -> Wall(true, x, y)
                'S' -> Wall(false, x, y)
                else -> Empty(x, y)
            }
        }

        fun toChar(block: BackgroundBlock): Char {
            return if(block is Empty) {
                'O'
            } else if(block is Wall) {
                if(block.canDestruct) {
                    'D'
                } else {
                    'S'
                }
            } else {
                ' '
            }
        }
    }
}