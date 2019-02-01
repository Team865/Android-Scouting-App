package ca.warp7.android.scouting.v5.entry

data class EntryItem(
    val match: String,
    val teams: List<Int>,
    val board: Board,
    val expectedTime: Int = (Math.random() * (1 shl 28)).toInt(),
    val state: EntryItemState = EntryItemState.Waiting,
    val data: Entry? = null
)