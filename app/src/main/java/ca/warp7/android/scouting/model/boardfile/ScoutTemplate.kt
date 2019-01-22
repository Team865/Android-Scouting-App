package ca.warp7.android.scouting.model.boardfile

data class ScoutTemplate(val screens: List<TemplateScreen>, val tags: List<String>) {
    val indices = screens.map { it.fields.flatten() }.flatten()
}