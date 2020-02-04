package ca.warp7.android.scouting.event

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MatchSchedule(
    val matches: List<Int>
) {
    val size = matches.size / 6

    fun getMatch(match: Int): List<Int> {
        return matches.subList(match * 6, (match + 1) * 6)
    }

    inline fun forEach(block: (matchNumber: Int, teams: List<Int>) -> Unit) {
        for (matchNumber in 0 until size) {
            block(matchNumber + 1, getMatch(matchNumber))
        }
    }
}