package ca.warp7.android.scouting.boardfile

import org.json.JSONArray
import org.json.JSONObject

@Suppress("unused")
fun serializeBoardfile(bf: Boardfile): String {
    val o = JSONObject()
    o.put("version", bf.version)
    o.put("robot_scout", bf.robotScoutTemplate.toJSON())
    o.put("super_scout", bf.superScoutTemplate.toJSON())
    return o.toString()
}


private fun ScoutTemplate.toJSON(): JSONObject {
    val o = JSONObject()
    o.put("screens", JSONArray(screens.map { it.toJSON() }))
    o.put("tags", JSONArray(tags))
    return o
}

private fun TemplateScreen.toJSON(): JSONObject {
    val o = JSONObject()
    o.put("title", title)
    o.put("layout", JSONArray(layout.map { row ->
        JSONArray(row.map { field -> field.toJSON() })
    }))
    return o
}

private fun TemplateField.toJSON(): JSONObject {
    val o = JSONObject()
    o.put("name", name)
    o.put("type", type.name)
    o.put("options", JSONArray(options))
    return o
}