package mqcreaple.tankgame.event

class Timer(val event: Event, val timeMillis: Long, var nextTime: Long): Comparable<Timer> {
    override fun compareTo(other: Timer): Int {
        return nextTime.compareTo(other.nextTime)
    }
}