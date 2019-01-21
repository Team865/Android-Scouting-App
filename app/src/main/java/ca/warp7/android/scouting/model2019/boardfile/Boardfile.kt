package ca.warp7.android.scouting.model2019.boardfile

data class Boardfile(
    val eventName: String,
    val eventKey: String,
    val matchSchedule: MatchSchedule,
    val robotScoutTemplate: ScoutTemplate,
    val superScoutTemplate: ScoutTemplate
)