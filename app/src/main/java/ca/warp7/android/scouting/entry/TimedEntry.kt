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

    override fun undo() = getNextIndex().let { if (it == 0) null else dataPoints.removeAt(it - 1) }

    override fun count(type: Int) = dataPoints.subList(0, getNextIndex()).count { it.type == type }

    override fun lastValue(type: Int) = dataPoints.subList(0, getNextIndex()).lastOrNull { it.type == type }

    override fun focused(type: Int, time: Int) = dataPoints.any { it.type == type && it.time == time }

    override fun focused(type: Int) = focused(type, getTime())

    private fun getHexTimestamp() = Integer.toHexString(timestamp)

    private fun getEncodedData(): String {
        val builder = StringBuilder()
        dataPoints.forEach { builder.appendDataPoint(it) }
        return builder.toString()
    }

    private fun getStrippedComments() = comments.replace("[^A-Za-z0-9 ]".toRegex(), "_")

    private fun getStrippedScout() = scout.replace("[^A-Za-z0-9]".toRegex(), "_")

    private fun getNextIndex(): Int {
        val relTime = getTime()
        var index = 0
        while (index < dataPoints.size && dataPoints[index].time <= relTime) index++
        return index
    }
}