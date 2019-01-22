@file:Suppress("unused")

package ca.warp7.android.scouting

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import ca.warp7.android.scouting.abstraction.AbstractActionVibrator
import ca.warp7.android.scouting.model.entry.MutableEntry
import ca.warp7.android.scouting.res.ManagedPreferences

abstract class V5Activity : AppCompatActivity(), ScoutingActivityBase {

    override val currentTime get() = (System.currentTimeMillis() / 1000).toInt()

    override lateinit var handler: Handler

    override val actionVibrator: AbstractActionVibrator? = null
    override val isSecondLimit: Boolean = false
    override var entry: MutableEntry? = null
    override var timeEnabled: Boolean = true

    lateinit var preferences: ManagedPreferences

    val mAlphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f)
    val mAlphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        preferences = ManagedPreferences(this)
    }
}