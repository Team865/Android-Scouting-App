package ca.warp7.android.scouting.boardfile

class ScoutTemplate(val screens: List<TemplateScreen>, val tags: List<String>) {
    private val indices = screens.map { it.layout.flatten() }.flatten()

    fun lookup(templateField: TemplateField): Int {
        return indices.indexOf(templateField) + 1
    }

    fun lookup(name: String): Int {
        return indices.indexOfFirst { it.name == name } + 1
    }
}