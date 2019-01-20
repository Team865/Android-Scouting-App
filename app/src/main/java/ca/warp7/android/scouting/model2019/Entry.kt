@file:Suppress("unused")

package ca.warp7.android.scouting.model2019

import android.util.Base64

data class Entry(
    val match: String,
    val team: String,
    val scout: String,
    val scoutPosition: ScoutPosition,
    val timestamp: Int,
    val timeSource: () -> Byte,
    val isTiming: Boolean = true
) {

    private val dataPoints: MutableList<DataPoint> = mutableListOf()
    private val hexTimestamp = Integer.toHexString(timestamp)

    var comments: String = ""

    /**
     * Get the maximum index of the datum recorded before or equal the current time,
     * or the last item in the datapoints
     */
    private val nextIndex: Int
        get() {
            if (!isTiming) return dataPoints.size
            val relTime = timeSource.invoke()
            var index = 0 // find the maximum index that is less than current time
            while (index < dataPoints.size && dataPoints[index].time <= relTime) {
                index++
            }
            return index
        }

    fun add(dataPoint: DataPoint) = dataPoints.add(index = nextIndex, element = dataPoint)

    /**
     * Performs an undo action on the data stack
     *
     * @return the data constant(metrics) of the datum being undone, or null
     * if nothing can be undone
     */
    fun undo() = nextIndex.let { if (it == 0) null else dataPoints.removeAt(it) }

    /**
     * Gets the count of a specific data type, excluding undo
     */
    fun count(dataType: Byte) = dataPoints.subList(0, nextIndex).filter { it.type == dataType }.size

    /**
     * Gets the last recorded of a specific data type, excluding undo
     */
    fun lastValue(dataType: Byte) = dataPoints.subList(0, nextIndex).lastOrNull { it.type == dataType }

    /**
     * Cleans out data that have been undone
     */
    fun focused(dataType: Byte) =
        timeSource.invoke().let { t -> dataPoints.any { it.type == dataType && it.time == t } }

    private val encodedDataPoints: String
        get() = Base64.encodeToString(dataPoints.flatMap { it.byteArray.asIterable() }.toByteArray(), Base64.DEFAULT)

    private val constrainedComments
        get() = comments
            .let { if (it.length > 63) it.substring(0..64) else it }
            .replace("[^A-Za-z0-9 ]".toRegex(), "")

    private val constrainedScout
        get() = scoutPosition.name
            .let { if (it.length > 15) it.substring(0..16) else it }
            .replace("[^A-Za-z0-9 ]".toRegex(), "")

    val encodedString: String
        get() = "$match:$team:$scout:${scoutPosition.name}:$hexTimestamp:$encodedDataPoints:$constrainedComments"
}