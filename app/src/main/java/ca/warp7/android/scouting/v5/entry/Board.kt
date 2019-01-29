package ca.warp7.android.scouting.v5.entry

import ca.warp7.android.scouting.v5.entry.Alliance.Blue
import ca.warp7.android.scouting.v5.entry.Alliance.Red

enum class Board(val alliance: Alliance) {
    R1(Red),
    R2(Red),
    R3(Red),
    B1(Blue),
    B2(Blue),
    B3(Blue),
    RX(Red),
    BX(Blue);
}