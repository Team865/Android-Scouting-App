package ca.warp7.android.scouting.boardfile

class TemplateField(
    val name: String,
    val type: FieldType,
    val options: List<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateField

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}