@file:Suppress("unused")

package ca.warp7.android.scouting.entry


data class TimedEntry(

    override val match: String,

    override val team: String,

    override val scout: String,

    override val board: Board,

    override val dataPoints: MutableList<DataPoint> = mutableListOf(),

    override var timestamp: Int,

    val getTime: () -> Int,

    override var comments: String = ""

) : MutableEntry {

    override fun getEncoded() = "$match:$team:${getStrippedScout()}:${board.name}:" +
            "${getHexTimestamp()}:${getEncodedData()}:${getStrippedComments()}"

    override fun add(dataPoint: DataPoint) = this.dataPoints.add(index = getNextIndex(), element = dataPoint)

    override fun undo(): DataPoint? {
        val nextIndex = getNextIndex()
        return if (nextIndex == 0) {
            null // nothing to undo
        } else {
            dataPoints.removeAt(nextIndex - 1) // undo one step
        }
    }

    override fun count(type: Int): Int {
        return dataPoints.subList(0, getNextIndex()).count { it.type == type }
    }

    override fun lastValue(type: Int): DataPoint? {
        return dataPoints.subList(0, getNextIndex()).lastOrNull { it.type == type }
    }

    override fun focused(type: Int, time: Int): Boolean {
        return dataPoints.any { it.type == type && it.time == time }
    }

    override fun focused(type: Int) = focused(type, getTime.invoke())

    private fun getHexTimestamp() = Integer.toHexString(timestamp)

    private fun getEncodedData(): String {
        val builder = StringBuilder()
        dataPoints.forEach { builder.appendDataPoint(it) }
        return builder.toString()
    }

    private fun getStrippedComments() = comments.replace("[^A-Za-z0-9 ]".toRegex(), "_")

    private fun getStrippedScout() = scout.replace("[^A-Za-z0-9]".toRegex(), "_")

    private fun getNextIndex(): Int {
        val relativeTime = getTime.invoke()
        var low = 0
        var high = dataPoints.size - 1

        while (low <= high) {
            val mid = (high + low) / 2
            val time = dataPoints[mid].time
            when {
                time < relativeTime -> low = mid
                time > relativeTime -> high = mid
                else -> return mid
            }
        }
        return 0
    }
}