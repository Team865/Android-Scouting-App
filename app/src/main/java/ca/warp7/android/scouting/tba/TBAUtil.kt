package ca.warp7.android.scouting.tba

import android.content.Context
import ca.warp7.android.scouting.BuildConfig
import java.io.File

/**
 * Create an instance of the blue alliance
 */
fun createCachedTBAInstance(context: Context, cacheFirst: Boolean): TBA {
    val cacheFile = File(context.cacheDir, "tba-cache")
    return TBA(BuildConfig.TBA_KEY, cacheFile, cacheFirst)
}