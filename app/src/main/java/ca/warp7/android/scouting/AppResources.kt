package ca.warp7.android.scouting

import android.content.Context
import android.text.Html
import android.text.Spanned
import java.io.*

/**
 * @since v0.4.2
 */

object AppResources {

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
}
