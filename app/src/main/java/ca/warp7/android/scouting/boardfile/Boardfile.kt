package ca.warp7.android.scouting.boardfile

data class Boardfile(
    val version: String,
    val robotScoutTemplate: ScoutTemplate,
    val superScoutTemplate: ScoutTemplate
)