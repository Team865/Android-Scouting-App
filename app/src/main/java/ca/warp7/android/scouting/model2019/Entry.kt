@file:Suppress("unused")

package ca.warp7.android.scouting.model2019

import android.util.Base64

/**
 * Data model for the scouting app. Strictly, it follows
 * a per-match-per-board model, but it can be mostly
 * taken as per-match-per-team. Each instance contains
 * a stack of data recorded for that particular entry,
 * which can be classified into two groups: 1) data points
 * of a singular magnitude, such as the robot's starting
 * position and its subjective driving speed; in such cases,
 * it is usually desirable to know what the last-most value
 * set to them; and 2) the time series in which each data
 * point records the occurrence of a particular action at
 * a specific time, and there may be multiple values of
 * this type that are of equal interest to the data collector,
 * such as recording whenever a game piece is picked up
 * by the robot; in these cases, it may be helpful to know
 * the the count, the parity(Start/End), duration between each
 * occurrence, or a combination of the above. In stack of data
 * are recorded according to their time of input as referenced
 * by the time tracked by the scouting interface; this means
 * that even if a data point does not track time, the stack
 * should preserve the order of input nonetheless. For the
 * purpose of usage and analysis, the implementation using
 * this class should limit one data point per second.
 *
 * @since v0.1.0 (revised 0.5.0)
 */

data class Entry(
    val match: String,
    val team: String,
    val scout: String,
    val board: Board,
    val timestamp: Int,
    val timeSource: () -> Byte,
    val data: MutableList<DataPoint> = mutableListOf(),
    val isTiming: Boolean = false,
    var comments: String = ""
) {


    private val hexTimestamp = Integer.toHexString(timestamp)
    private val encodedData: String get() = Base64.encodeToString(data.flatten().toByteArray(), Base64.DEFAULT)
    private val comments1 get() = comments.replace("[^A-Za-z0-9 ]".toRegex(), "_")
    private val scout1 get() = scout.replace("[^A-Za-z0-9 ]".toRegex(), "_")
    val encodedString: String get() = "$match:$team:$scout1:${board.name}:$hexTimestamp:$encodedData:$comments1"

    /**
     * Get the maximum index of the datum recorded before or equal the current time,
     * or the last item in the data points
     */
    private val nextIndex: Int
        get() {
            if (!isTiming) return data.size
            val relTime = timeSource.invoke()
            var index = 0
            while (index < data.size && data[index].time <= relTime) index++
            return index
        }

    /**
     * Adds a data point to the entry
     */
    fun add(dataPoint: DataPoint) = data.add(index = nextIndex, element = dataPoint)

    /**
     * Performs an undo action on the data stack
     *
     * @return the data constant(metrics) of the datum being undone, or null
     * if nothing can be undone
     */
    fun undo() = nextIndex.let { if (it == 0) null else data.removeAt(it) }

    /**
     * Gets the count of a specific data type, excluding undo
     */
    fun count(type: Byte) = data.subList(0, nextIndex).filter { it.type == type }.size

    /**
     * Gets the last recorded of a specific data type, excluding undo
     */
    fun lastValue(type: Byte) = data.subList(0, nextIndex).lastOrNull { it.type == type }

    /**
     * Cleans out data that have been undone
     */
    fun focused(type: Byte) = timeSource.invoke().let { t -> data.any { it.type == type && it.time == t } }
}