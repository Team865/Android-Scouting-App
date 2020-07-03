package ca.warp7.android.scouting.ui.field

import android.graphics.*
import android.os.Build
import androidx.annotation.ColorInt

fun colorFilter(@ColorInt color: Int): ColorFilter {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        BlendModeColorFilter(color, BlendMode.SRC)
    } else {
        PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
    }
}