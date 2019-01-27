package ca.warp7.android.scouting

import android.content.Context
import android.os.Environment
import android.text.Html
import android.text.Spanned
import java.io.*
import java.util.*

/**
 * @since v0.4.2
 */

object AppResources {

    private const val kSpecsRoot = "Warp7/specs/"
    private const val kEventsRoot = "Warp7/events/"

    val specsRoot: File
        get() {
            val root = File(
                Environment.getExternalStorageDirectory(),
                kSpecsRoot
            )
            root.mkdirs()
            return root
        }

    val eventsRoot: File
        get() {
            val root = File(
                Environment.getExternalStorageDirectory(),
                kEventsRoot
            )
            root.mkdirs()
            return root
        }

    val events: List<EventInfo>
        get() {
            val root = eventsRoot
            val eventRoots = root.listFiles()
            val eventsList = ArrayList<EventInfo>()
            for (eventRoot in eventRoots) {
                if (eventRoot.isDirectory) {
                    try {
                        val eventInfo = EventInfo(eventRoot)
                        eventsList.add(eventInfo)
                    } catch (e: EventInfo.NotProperEventFormat) {
                        e.printStackTrace()
                    }

                }
            }
            return eventsList
        }

    fun copySpecsAssets(context: Context) {
        try {
            val root = specsRoot

            val assetManager = context.assets
            for (fileName in assetManager.list("specs")!!) {

                val inputStream = assetManager.open("specs/$fileName")
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                val outFile = File(root, fileName)
                val outputStream = FileOutputStream(outFile)
                outputStream.write(buffer)
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun getRaw(context: Context, id: Int): String {
        val resources = context.resources
        try {
            val br = BufferedReader(
                InputStreamReader(
                    resources.openRawResource(id)
                )
            )

            val sb = StringBuilder()
            var line: String? = br.readLine()

            while (line != null) {
                sb.append(line)
                sb.append("\n")
                line = br.readLine()
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    fun getHTML(context: Context, id: Int): Spanned {
        return Html.fromHtml(getRaw(context, id))
    }

    private fun recursiveDelete(f: File): Boolean {
        var deleted = true
        if (f.isDirectory)
            for (ff in f.listFiles())
                deleted = deleted && recursiveDelete(ff)
        return deleted && f.delete()
    }

    fun copyEventAssets(context: Context) {
        try {
            val assetManager = context.assets
            val root = eventsRoot
            val rootDirs = root.listFiles()
            for (assetEvent in assetManager.list("events")!!) {
                for (rootEventName in rootDirs) {
                    if (rootEventName.isDirectory && assetEvent == rootEventName.name) {
                        recursiveDelete(rootEventName)
                    }
                }
                val eventPath = "events/$assetEvent"
                val eventDirectory = File(root, assetEvent)
                eventDirectory.mkdir()
                for (assetEventFile in assetManager.list(eventPath)!!) {
                    val inputStream = assetManager.open("$eventPath/$assetEventFile")
                    val buffer = ByteArray(inputStream.available())
                    inputStream.read(buffer)
                    inputStream.close()
                    val outFile = File(eventDirectory, assetEventFile)
                    val outputStream = FileOutputStream(outFile)
                    outputStream.write(buffer)
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun readFile(f: File): String {
        val br = BufferedReader(FileReader(f))
        val sb = StringBuilder()

        var line: String? = br.readLine()

        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }

        br.close()
        return sb.toString()
    }
}
