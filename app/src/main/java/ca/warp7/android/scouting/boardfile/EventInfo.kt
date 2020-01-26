package ca.warp7.android.scouting.boardfile

class EventInfo (
    val eventName: String,
    val eventKey: String,
    val matchSchedule: MatchSchedule
)

val exampleEventInfo = EventInfo(
    "ONT Science Division",
    "oncmp1",
    MatchSchedule(exampleMatchSchedule)
)