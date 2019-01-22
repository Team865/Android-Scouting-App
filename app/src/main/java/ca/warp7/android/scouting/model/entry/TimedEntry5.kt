@file:Suppress("unused")

package ca.warp7.android.scouting.model.entry

import android.util.Base64


data class TimedEntry5(
    override val match: String,
    override val team: String,
    override val scout: String,
    override val board: Board,
    override val timestamp: Int,
    override val dataPoints: MutableList<DataPoint> = mutableListOf(),
    override var comments: String = "",
    val getTime: () -> Byte,
    val isTiming: Boolean = false
) : MutableEntry {
    override val encoded get() = "$match:$team:$scout1:${board.name}:$hexTimestamp:$encodedData:$comments1"
    override fun add(dataPoint: DataPoint) = this.dataPoints.add(index = nextIndex, element = dataPoint)
    override fun undo() = nextIndex.let { if (it == 0) null else dataPoints.removeAt(it) }
    override fun count(type: Byte) = dataPoints.subList(0, nextIndex).filter { it.type == type }.size
    override fun lastValue(type: Byte) = dataPoints.subList(0, nextIndex).lastOrNull { it.type == type }
    override fun focused(type: Byte) = getTime().let { t -> dataPoints.any { it.type == type && it.time == t } }
    private val hexTimestamp = Integer.toHexString(timestamp)
    private val encodedData get() = Base64.encodeToString(dataPoints.flatten().toByteArray(), Base64.DEFAULT)
    private val comments1 get() = comments.replace("[^A-Za-z0-9 ]".toRegex(), "_")
    private val scout1 get() = scout.replace("[^A-Za-z0-9 ]".toRegex(), "_")
    private val nextIndex: Int
        get() {
            if (!isTiming) return dataPoints.size
            val relTime = getTime()
            var index = 0
            while (index < dataPoints.size && dataPoints[index].time <= relTime) index++
            return index
        }
}