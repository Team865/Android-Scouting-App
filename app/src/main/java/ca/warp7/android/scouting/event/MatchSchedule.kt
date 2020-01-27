package ca.warp7.android.scouting.event

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class MatchSchedule(
    val matches: List<Int>
) {
    val size = matches.size / 6

    operator fun get(match: Int) = if (match < 0 || match > size) null else
        matches.subList(match * 6, (match + 1) * 6)

    fun forEach(block: (matchNumber: Int, teams: List<Int>) -> Unit) =
        (0 until size).forEach { this[it]?.apply { block(it + 1, this) } }
}