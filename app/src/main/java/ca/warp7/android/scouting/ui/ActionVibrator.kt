package ca.warp7.android.scouting.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator

/**
 * @since v0.4.1
 */

class ActionVibrator(
    context: Context,
    private val mVibrationOn: Boolean
)  {

    private val actual = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    @Suppress("DEPRECATION")
    fun vibrateStart() {
        if (mVibrationOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                actual.vibrate(VibrationEffect.createWaveform(kStartVibration, -1))
            } else {
                actual.vibrate(kStartVibration, -1)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun vibrateAction() {
        if (mVibrationOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                actual.vibrate(VibrationEffect.createOneShot(kActionEffectVibration, DEFAULT_AMPLITUDE))
            } else {
                actual.vibrate(kActionEffectVibration)
            }
        }
    }

    companion object {
        private val kStartVibration = longArrayOf(0, 20, 30, 20)
        private const val kActionEffectVibration = 30L
    }
}
