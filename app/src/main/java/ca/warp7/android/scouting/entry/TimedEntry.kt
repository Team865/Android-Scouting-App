@file:Suppress("unused")

package ca.warp7.android.scouting.entry

import kotlin.math.abs


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
        for (i in nextIndex - 1 downTo 0) {
            val dp = dataPoints[i]
            if (dp.type == type) {
                return dp
            }
        }
        return null
    }

    override fun isFocused(type: Int, time: Double): Boolean {

        return dataPoints.any { it.type == type && abs(time - it.time) < 0.5 }

        /*if (dataPoints.isEmpty()) {
            return false
        }

        var low = 0
        var high = dataPoints.size - 1

        while (low != high) {
            val mid = (high + low) / 2
            val midPoint = dataPoints[mid]

            when {
                (time - midPoint.time) > 0.5 -> low = mid
                (midPoint.time - time) > 0.5 -> high = mid
                midPoint.type == type -> return true
            }
        }
        return false*/
    }

    override fun isFocused(type: Int): Boolean {
        return isFocused(type, getTime.invoke())
    }

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
        return getNextIndex(getTime.invoke())
    }

    internal fun getNextIndex(currentTime: Double): Int {

        if (dataPoints.isEmpty()) {
            return 0
        }

        // Fix: Don't use <= because we want the last of data points with first time
        if (currentTime < dataPoints.first().time) {
            return 1
        }

        if (currentTime >= dataPoints.last().time) {
            return dataPoints.size
        }

        var low = 0
        var high = dataPoints.size - 1

        // To get the element that we want, use a binary search algorithm
        // instead of iterating over a for-loop. A binary search is O(log(n))
        // whereas searching using a loop is O(n).

        while (low != high) {
            val mid = (low + high) / 2
            if (dataPoints[mid].time <= currentTime) {
                low = mid + 1
            } else {
                high = mid
            }
        }

        return low
    }
}