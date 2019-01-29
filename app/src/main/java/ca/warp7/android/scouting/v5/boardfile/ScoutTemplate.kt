package ca.warp7.android.scouting.v5.boardfile

data class ScoutTemplate(val screens: List<TemplateScreen>, val tags: List<String>) {
    private val indices = screens.map { it.fields.flatten() }.flatten()
    fun lookup(templateField: TemplateField): Int = indices.indexOf(templateField) + 1
    fun lookup(name: String): Int = indices.indexOfFirst { it.name == name } + 1
}