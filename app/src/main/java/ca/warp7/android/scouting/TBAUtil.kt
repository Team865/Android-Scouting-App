package ca.warp7.android.scouting

import android.content.Context
import ca.warp7.android.scouting.tba.TBA
import java.io.File

/**
 * Create an instance of the blue alliance
 */
fun createCachedTBAInstance(context: Context): TBA {
    val cacheFile = File(context.cacheDir, "tba-cache.zip")
    return TBA(BuildConfig.TBA_KEY, cacheFile)
}