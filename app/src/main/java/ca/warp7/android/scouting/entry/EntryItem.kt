package ca.warp7.android.scouting.entry

class EntryItem(
    val match: String,
    val teams: List<Int>,
    val board: Board,
    val isComplete: Boolean,
    val isScheduled: Boolean,
    val data: String = ""
)