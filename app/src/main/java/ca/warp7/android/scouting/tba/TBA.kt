package ca.warp7.android.scouting.tba

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.FileSystem
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.net.ssl.HttpsURLConnection

/**
 * The Blue Alliance API
 */
class TBA(
    private val authKey: String,
    private val cacheFile: File?
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

    private fun copyZip(strippedURL: String, result: String) {
        val zip = ZipFile(cacheFile)
        val map = HashMap<String, ByteArray>()
        zip.use {
            for (entry in it.entries()) {
                map[entry.name] = zip.getInputStream(entry).readBytes()
            }
        }
        val out = ZipOutputStream(FileOutputStream(cacheFile))
        map[strippedURL] = result.toByteArray()
        out.use {
            for (e in map.entries) {
                val entry = ZipEntry(e.key)
                out.putNextEntry(entry)
                it.write(e.value)
            }
        }
    }

    private fun getTBAString(requestURL: String): String {
        try {
            // Get rid of the slash for zip file
            val strippedURL = requestURL.substring(1)
            if (cacheFile != null) {
                val zip = ZipFile(cacheFile)
                zip.use {
                    val entry = zip.getEntry(strippedURL)
                    if (entry != null) {
                        return String(zip.getInputStream(entry).readBytes())
                    }
                }
            }
            val result = getTBAStringNoCache(requestURL)
            if (cacheFile != null) {
                copyZip(strippedURL, result)
            }
            return result
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