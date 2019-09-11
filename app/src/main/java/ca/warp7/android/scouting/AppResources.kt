package ca.warp7.android.scouting

import android.content.Context
import android.os.Environment
import android.text.Html
import android.text.Spanned
import java.io.*

/**
 * @since v0.4.2
 */

object AppResources {

    private const val kSpecsRoot = "Warp7/specs/"

    val v4SpecsRoot: File
        get() {
            val root = File(
                Environment.getExternalStorageDirectory(),
                kSpecsRoot
            )
            root.mkdirs()
            return root
        }

    fun copySpecsAssets(context: Context) {
        try {
            val root = v4SpecsRoot

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

    @Suppress("DEPRECATION")
    fun getHTML(context: Context, id: Int): Spanned {
        return Html.fromHtml(getRaw(context, id))
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
