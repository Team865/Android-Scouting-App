@file:Suppress("unused")

package ca.warp7.android.scouting

import ca.warp7.android.scouting.boardfile.ScoutTemplate
import ca.warp7.android.scouting.entry.MutableEntry

interface BaseScoutingActivity {

    /**
     * @return the number of seconds since the start of the entry
     */
    fun getRelativeTime(): Double

    /**
     * Vibrate to indicate an action
     */
    fun vibrateAction()

    /**
     * whether the activity is in a state not accepting timed data
     */
    fun isTimeEnabled(): Boolean

    /**
     * The entry model object tracking data history
     */
    val entry: MutableEntry?

    /**
     * Scout template
     */
    val template: ScoutTemplate?

    /**
     * Replaces the template name with the display name
     */
    fun modifyName(name: String): String
}