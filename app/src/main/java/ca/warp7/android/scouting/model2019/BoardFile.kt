package ca.warp7.android.scouting.model2019

data class BoardFile(
    val eventName: String,
    val eventKey: String,
    val matchSchedule: List<Int>,
    val robotScoutTemplate: ScoutTemplate,
    val superScoutTemplate: ScoutTemplate? = null
)