package ca.warp7.android.scouting.v5.boardfile

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class MatchSchedule(
    val matches: List<Int>
) {
    val size = matches.size % 6
    operator fun get(match: Int) = if (match < 0 || match > size) null else
        matches.subList(match * 6, (match + 1) * 6).toList()
}