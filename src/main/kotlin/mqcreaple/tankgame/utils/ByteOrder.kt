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

        fun toNetOrd(a: Short): ByteArray = byteArrayOf(
            (a.toInt() shr 8).toByte(),
            a.toByte()
        )

        fun fromNetOrdShort(arr: ByteArray): Short = ((arr[0].toInt() shl 8) + arr[1]).toShort()
        fun fromNetOrdShort(arr: List<Byte>): Short = ((arr[0].toInt() shl 8) + arr[1]).toShort()

        fun fromNetOrdInt(arr: ByteArray): Int = (arr[0].toInt() shl 24) +
                (arr[1].toInt() shl 16) +
                (arr[2].toInt() shl 8) +
                arr[3].toInt()
        fun fromNetOrdInt(arr: List<Byte>): Int = (arr[0].toInt() shl 24) +
                (arr[1].toInt() shl 16) +
                (arr[2].toInt() shl 8) +
                arr[3].toInt()
    }
}