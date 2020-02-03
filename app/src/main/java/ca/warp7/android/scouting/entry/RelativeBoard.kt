package ca.warp7.android.scouting.entry

import ca.warp7.android.scouting.entry.Board.*

/**
 * Enumeration of boards specified relative to an alliance
 */
enum class RelativeBoard(private val valIfRed: Board, private val valIfBlue: Board) {
    A1(R1, B1),
    A2(R2, B2),
    A3(R3, B3),

    O1(B1, R1),
    O2(B2, R2),
    O3(B3, R3);

    fun relativeTo(board: Board): Board {
        return if (board.alliance == Alliance.Red) {
            valIfRed
        } else {
            valIfBlue
        }
    }
}