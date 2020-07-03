package ca.warp7.android.scouting.boardfile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

@Suppress("unused")
fun createBoardfileFromAssets(context: Context): Boardfile {
    val assets = context.assets
    val json = assets.open("Boardfile.json").reader().readText()
    return deserializeBoardfile(json)
}

fun deserializeBoardfile(json: String): Boardfile {
    val o = JSONObject(json)
    return Boardfile(
            year = o.getInt("year"),
            revision = o.getInt("revision"),
            robotScoutTemplate = o.getJSONObject("robot_scout").toTemplate(),
            superScoutTemplate = o.getJSONObject("super_scout").toTemplate()
    )
}

internal inline fun <T> JSONArray.mapToList(func: (Any?) -> T): List<T> {
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
            this,
            getString("name"),
            FieldType.valueOf(getString("type"))
    )
}