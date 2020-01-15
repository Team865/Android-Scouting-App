package ca.warp7.android.scouting.entry

import ca.warp7.android.scouting.entry.Alliance.Blue
import ca.warp7.android.scouting.entry.Alliance.Red

enum class Board(val alliance: Alliance, val displayName: String) {
    R1(Red, "Red 1"),
    R2(Red, "Red 2"),
    R3(Red, "Red 3"),
    B1(Blue, "Blue 1"),
    B2(Blue, "Blue 2"),
    B3(Blue, "Blue 3"),
    RX(Red, "Red Super Scout"),
    BX(Blue, "Blue Super Scout");
}