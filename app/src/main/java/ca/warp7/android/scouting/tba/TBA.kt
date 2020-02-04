package ca.warp7.android.scouting.tba

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * The Blue Alliance API
 */
class TBA(
    private val authKey: String,
    private val cacheRoot: File,
    private val cacheFirst: Boolean
) {

    private fun getTBAStringNoCache(requestURL: String): String {
        val url = URL("https://www.thebluealliance.com/api/v3$requestURL")
        val conn = url.openConnection() as HttpsURLConnection
        conn.requestMethod = "GET"
        conn.useCaches = false
        conn.setRequestProperty("X-TBA-Auth-Key", authKey)
        conn.setRequestProperty("User-Agent", "Team865")
        return conn.inputStream.bufferedReader().use { br -> br.readText() }
    }

    private fun getTBAString(requestURL: String): String {
        // Get rid of the slash
        val strippedURL = requestURL.substring(1) + ".json"

        val cacheFile = File(cacheRoot, strippedURL)

        if (cacheFirst && cacheFile.exists()) {
            return cacheFile.readText()
        }

        return try {
            val result = getTBAStringNoCache(requestURL)

            // make sure we have the parent directories made
            cacheFile.parentFile?.mkdirs()
            cacheFile.writeText(result)

            result
        } catch (e: Exception) {

            if (!cacheFirst && cacheFile.exists()) {
                cacheFile.readText()
            } else {
                throw e
            }
        }
    }

    internal fun get(requestURL: String): JSONObject {
        return JSONObject(getTBAString(requestURL))
    }

    internal fun getArray(requestURL: String): JSONArray {
        return JSONArray(getTBAString(requestURL))
    }
}