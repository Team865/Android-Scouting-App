package ca.warp7.android.scouting.ui

import ca.warp7.android.scouting.entry.Board

class EntryInMatch(
    val match: String,
    val teams: List<Int>,
    val board: Board,
    val isComplete: Boolean,
    val isScheduled: Boolean,
    val data: String = ""
) {

    fun toCSV(): String {
        val cols = ArrayList<Any>()
        cols.add(match)
        cols.addAll(teams)
        cols.add(board)
        cols.add(isComplete)
        cols.add(isScheduled)
        cols.add(data)
        return cols.joinToString(",")
    }

    companion object {
        fun fromCSV(s: String): EntryInMatch {
            val sp = s.split(",")
            return EntryInMatch(
                sp[0],
                sp.subList(1, 7).map { it.toInt() },
                Board.valueOf(sp[7]),
                sp[8].toBoolean(),
                sp[9].toBoolean(),
                sp[10]
            )
        }
    }
}