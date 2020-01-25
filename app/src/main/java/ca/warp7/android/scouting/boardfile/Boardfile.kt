package ca.warp7.android.scouting.boardfile

data class Boardfile(
    val version: String,
    val eventName: String,
    val eventKey: String,
    val matchSchedule: MatchSchedule,
    val robotScoutTemplate: ScoutTemplate,
    val superScoutTemplate: ScoutTemplate
)