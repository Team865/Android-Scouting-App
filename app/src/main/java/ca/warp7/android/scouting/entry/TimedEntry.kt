@file:Suppress("unused")

package ca.warp7.android.scouting.entry


data class TimedEntry(

    override val match: String,

    override val team: String,

    override val scout: String,

    override val board: Board,

    override var timestamp: Int,

    val getTime: () -> Double

) : MutableEntry {

    override val dataPoints: MutableList<DataPoint> = mutableListOf()

    override var comments: String = ""

    override fun getEncoded(): String {
        return "$match:$team:${getStrippedScout()}:${board.name}:" +
                "${Integer.toHexString(timestamp)}:${getEncodedData()}:${getStrippedComments()}"
    }

    override fun add(dataPoint: DataPoint) {
        dataPoints.add(getNextIndex(), dataPoint)
    }

    override fun undo(): DataPoint? {
        val nextIndex = getNextIndex()
        return if (nextIndex == 0) {
            null
        } else {
            dataPoints.removeAt(nextIndex - 1)
        }
    }

    override fun count(type: Int): Int {
        val nextIndex = getNextIndex()
        var count = 0
        for (i in 0 until nextIndex) {
            if (dataPoints[i].type == type) count++
        }
        return count
    }

    override fun lastValue(type: Int): DataPoint? {
        val nextIndex = getNextIndex()
        for (i in nextIndex downTo 0) {
            val dp = dataPoints[i]
            if (dp.type == type) {
                return dp
            }
        }
        return null
    }

    override fun focused(type: Int, time: Double): Boolean {
        val relativeTime = getTime.invoke()
        var low = 0
        var high = dataPoints.size - 1

        while (low <= high) {
            val mid = (high + low) / 2
            val midPoint = dataPoints[mid]
            when {
                (relativeTime - midPoint.time) > 0.5 -> low = mid
                (midPoint.time - relativeTime) > 0.5 -> high = mid
                midPoint.type == type -> return true
            }
        }
        return false
    }

    override fun focused(type: Int) = focused(type, getTime.invoke())

    private fun getEncodedData(): String {
        val builder = StringBuilder()
        for (dataPoint in dataPoints) {
            builder.appendDataPoint(dataPoint)
        }
        return builder.toString()
    }

    private fun getStrippedComments(): String {
        return comments.replace("[^A-Za-z0-9 ]".toRegex(), "_")
    }

    private fun getStrippedScout(): String {
        return scout.replace("[^A-Za-z0-9]".toRegex(), "_")
    }

    private fun getNextIndex(): Int {
        val relativeTime = getTime.invoke()
        var low = 0
        var high = dataPoints.size - 1

        while (low <= high) {
            val mid = (high + low) / 2
            val midTime = dataPoints[mid].time
            when {
                midTime < relativeTime -> low = mid
                midTime > relativeTime -> high = mid
                else -> return mid
            }
        }
        return 0
    }
}