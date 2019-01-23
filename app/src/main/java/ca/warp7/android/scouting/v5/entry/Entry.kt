package ca.warp7.android.scouting.v5.entry


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

interface Entry {
    val match: String
    val team: String
    val scout: String
    val board: Board
    val timestamp: Int
    /**
     * Get the data in an encoded string
     */
    val encoded: String
    val dataPoints: List<DataPoint>
    val comments: String

    /**
     * Gets the count of a specific data type, excluding undo
     */
    fun count(type: Int): Int

    /**
     * Gets the last recorded of a specific data type, excluding undo
     */
    fun lastValue(type: Int): DataPoint?

    /**
     * Check if a type should focus according to the current time
     */
    fun focused(type: Int): Boolean
}