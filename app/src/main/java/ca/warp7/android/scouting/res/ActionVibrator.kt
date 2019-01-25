package ca.warp7.android.scouting.res

import android.content.Context
import android.os.Vibrator
import ca.warp7.android.scouting.AbstractActionVibrator

/**
 * @since v0.4.1
 */

class ActionVibrator(context: Context,
                     private val mVibrationOn: Boolean) : AbstractActionVibrator {

    private val actual = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    override fun vibrateStart() {
        if (mVibrationOn) {
            actual.vibrate(kStartVibration, -1)
        }
    }

    override fun vibrateAction() {
        if (mVibrationOn) {
            actual.vibrate(kActionEffectVibration.toLong())
        }
    }

    companion object {
        private val kStartVibration = longArrayOf(0, 20, 30, 20)
        private const val kActionEffectVibration = 30
    }
}
