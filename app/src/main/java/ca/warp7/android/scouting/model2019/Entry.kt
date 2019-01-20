@file:Suppress("unused")

package ca.warp7.android.scouting.model2019

data class Entry(
    val event: String,
    val match: Int,
    val team: Int,
    val scout: Int,
    val timeSource: () -> Byte,
    val isTiming: Boolean = true
) {
    val tags: MutableList<Byte> = mutableListOf()
    val timeStamp = timeSource.invoke()
    private val dataPoints: MutableList<DataPoint> = mutableListOf()

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

    fun push(dataPoint: DataPoint) = dataPoints.add(index = nextIndex, element = dataPoint)

    /**
     * Performs an undo action on the data stack
     *
     * @return the data constant(metrics) of the datum being undone, or null
     * if nothing can be undone
     */
    fun undo() = nextIndex.let { if (it == 0) null else dataPoints.removeAt(it)  }

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
    fun focused(dataType: Byte) = timeSource.invoke().let { time ->
        dataPoints.any { it.type == dataType && it.time == time }
    }

    override fun toString(): String {
        return "$event:$match:$team"
    }
}