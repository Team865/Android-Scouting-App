package ca.warp7.android.scouting.ui

import ca.warp7.android.scouting.entry.Board

class EntryInMatch(
    val match: String,
    val teams: List<Int>,
    val board: Board,
    val isComplete: Boolean,
    val isScheduled: Boolean,
    val data: String = ""
)