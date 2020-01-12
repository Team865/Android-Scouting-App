@file:Suppress("unused", "NOTHING_TO_INLINE")

package ca.warp7.android.scouting.tba

import org.json.JSONArray
import org.json.JSONObject

internal fun JSONObject.int(key: String) = optInt(key)
internal fun JSONObject.string(key: String) = optString(key)
internal fun JSONObject.double(key: String) = optDouble(key)
internal fun JSONObject.boolean(key: String) = optBoolean(key)
internal fun JSONObject.obj(key: String) = optJSONObject(key)


internal inline fun <T> JSONArray.mapToList(func: (JSONObject) -> T): List<T> {
    val result = ArrayList<T>()
    for (i in 0 until length()) result.add(func(get(i) as JSONObject))
    return result
}

internal inline fun <T> JSONObject.mapValues(func: (Any?) -> T): Map<String, T> {
    val map = HashMap<String, T>()
    for (key in keys()) {
        map[key] = func(get(key))
    }
    return map
}

internal fun JSONObject.genericArray(key: String): JSONArray? {
    return getJSONArray(key)
}

internal fun JSONObject.stringList(key: String): List<String>? {
    return optJSONArray(key)?.map { it as String }
}

internal fun JSONObject.doubleList(key: String): List<Double>? {
    return optJSONArray(key)?.map { it as Double }
}

internal fun JSONObject.intList(key: String): List<Int>? {
    return optJSONArray(key)?.map { it as Int }
}

internal fun JSONObject.booleanList(key: String): List<Boolean>? {
    return optJSONArray(key)?.map { it as Boolean }
}

internal fun JSONObject.objList(key: String): List<JSONObject>? {
    return optJSONArray(key)?.map { it as JSONObject }
}

internal inline fun <T> JSONArray.map(func: (Any?) -> T): List<T> {
    val list = ArrayList<T>()
    var i = 0
    val len = length()
    while (i < len) {
        list.add(func(get(i)))
        i++
    }
    return list
}