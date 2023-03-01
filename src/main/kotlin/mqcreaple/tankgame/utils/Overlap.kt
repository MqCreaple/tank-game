package mqcreaple.tankgame.utils

class Overlap {
    companion object {
        /**
         * checks if two intervals overlaps
         * @param l1 left bound of first interval
         * @param r1 right bound of first interval, always assume r1 > l1
         * @param l2 left bound of second interval
         * @param r2 right bound of second interval, always assume r2 > l2
         */
        fun interval(l1: Double, r1: Double, l2: Double, r2: Double): Boolean {
            return (l2 < r1) && (l1 < r2)
        }
    }
}