package ca.warp7.android.scouting.boardfile

import org.json.JSONArray
import org.json.JSONObject

@Suppress("unused")
fun deserializeBoardfile(json: String): Boardfile {
    val o = JSONObject(json)
    return Boardfile(
        version = o.getString("version"),
        eventName = "",
        eventKey = "",
        matchSchedule = MatchSchedule(listOf()),
        robotScoutTemplate = o.getJSONObject("robot_scout").toTemplate(),
        superScoutTemplate = o.getJSONObject("super_scout").toTemplate()
    )
}

private inline fun <T> JSONArray.mapToList(func: (Any?) -> T): List<T> {
    val list = ArrayList<T>()
    var i = 0
    val len = length()
    while (i < len) {
        list.add(func(get(i)))
        i++
    }
    return list
}

private fun JSONObject.toTemplate(): ScoutTemplate {
    return ScoutTemplate(
        getJSONArray("screens").mapToList { (it as JSONObject).toScreen() },
        getJSONArray("tags").mapToList { it as String }
    )
}

private fun JSONObject.toScreen(): TemplateScreen {
    return TemplateScreen(
        getString("title"),
        getJSONArray("layout").mapToList { row ->
            (row as JSONArray).mapToList { field ->
                (field as JSONObject).toField()
            }
        }
    )
}

private fun JSONObject.toField(): TemplateField {
    return TemplateField(
        getString("name"),
        FieldType.valueOf(getString("type")),
        getJSONArray("options").mapToList { it as String }
    )
}