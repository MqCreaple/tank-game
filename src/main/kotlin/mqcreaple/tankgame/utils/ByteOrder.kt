package mqcreaple.tankgame.utils

class ByteOrder {
    companion object {
        /**
         * Convert a integer to network byte order bytes
         */
        fun toNetOrd(a: Int): ByteArray = byteArrayOf(
            (a shr 24).toByte(),
            (a shr 16).toByte(),
            (a shr 8).toByte(),
            a.toByte()
        )

        fun fromNetOrd(arr: ByteArray): Int = (arr[0].toInt() shl 24) +
                (arr[1].toInt() shl 16) +
                (arr[2].toInt() shl 8) +
                arr[3].toInt()
    }
}