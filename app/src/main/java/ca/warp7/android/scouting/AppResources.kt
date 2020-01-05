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
        return try {
            resources.openRawResource(id).bufferedReader().readText()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @Suppress("DEPRECATION")
    fun getHTML(context: Context, id: Int): Spanned {
        return Html.fromHtml(getRaw(context, id))
    }
}
