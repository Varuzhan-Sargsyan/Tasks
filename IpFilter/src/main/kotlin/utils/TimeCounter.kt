package utils

class TimeCounter {
    private var started: Long = 0
    private var ended: Long = 0

    private fun durationInMilliseconds() = ended - started

    fun start() : TimeCounter {
        started = System.currentTimeMillis()
        return this
    }
    
    fun end() {
        ended = System.currentTimeMillis()
    }

    override fun toString(): String {
        val hours = durationInMilliseconds() / (1000 * 60 * 60)
        val minutes = durationInMilliseconds() % (1000 * 60 * 60) / (1000 * 60)
        val seconds = durationInMilliseconds() % (1000 * 60) / 1000
        val milliseconds = durationInMilliseconds() % 1000

        return StringBuilder().apply {
            if (hours != 0L) append(" $hours hours")
            if (minutes != 0L) append(" $minutes minutes")
            if (seconds != 0L) append(" $seconds seconds")
            append(" $milliseconds milliseconds\n")
        }.toString()
    }

}