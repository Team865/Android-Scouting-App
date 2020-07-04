package ca.warp7.android.scouting.boardfile

@Suppress("unused")
class Boardfile(
        val year: Int,
        val revision: Int,
        val robotScoutTemplate: ScoutTemplate,
        val superScoutTemplate: ScoutTemplate
)