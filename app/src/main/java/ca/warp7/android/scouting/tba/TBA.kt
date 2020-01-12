package ca.warp7.android.scouting.tba

import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * The Blue Alliance API
 */
class TBA(
        private val authKey: String,
        private val userAgent: String = "KnotBook"
) {

    private fun getTBAString(requestURL: String): String {
        try {
            val url = URL("https://www.thebluealliance.com/api/v3$requestURL")
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.useCaches = false
            conn.setRequestProperty("X-TBA-Auth-Key", authKey)
            conn.setRequestProperty("User-Agent", userAgent)
            return conn.inputStream.bufferedReader().use { br -> br.readText() }
        } catch (e: Throwable) {
            throw e
        }
    }

    internal fun get(requestURL: String): JSONObject {
        return JSONObject(getTBAString(requestURL))
    }

    internal fun getArray(requestURL: String): JSONArray {
        return JSONArray(getTBAString(requestURL))
    }
}