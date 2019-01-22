@file:Suppress("unused")

package ca.warp7.android.scouting

import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import ca.warp7.android.scouting.abstraction.AbstractActionVibrator
import ca.warp7.android.scouting.components.ScoutingTabsPagerAdapter
import ca.warp7.android.scouting.constants.ID
import ca.warp7.android.scouting.res.ManagedPreferences
import ca.warp7.android.scouting.v5.boardfile.Boardfile
import ca.warp7.android.scouting.v5.boardfile.toBoardfile
import ca.warp7.android.scouting.v5.entry.MutableEntry
import java.io.File

abstract class V5Activity : AppCompatActivity(), ScoutingActivityBase {

    override lateinit var handler: Handler
    override val actionVibrator: AbstractActionVibrator get() = preferences.vibrator
    override val isSecondLimit: Boolean = false
    override var entry: MutableEntry? = null
    override var timeEnabled: Boolean = true

    private lateinit var mTimerStatus: TextView
    private lateinit var mTimeProgress: ProgressBar
    private lateinit var mTimeSeeker: SeekBar
    private lateinit var mStartButton: TextView
    private lateinit var mPlayAndPauseImage: ImageButton
    private lateinit var mUndoAndNowImage: ImageButton
    private lateinit var mPlayAndPauseView: ViewGroup
    private lateinit var mUndoAndNowView: ViewGroup
    private lateinit var mPlayAndPauseText: TextView
    private lateinit var mUndoAndNowText: TextView
    private lateinit var mPager: ViewPager
    private lateinit var mPagerAdapter: ScoutingTabsPagerAdapter

    private lateinit var preferences: ManagedPreferences
    private lateinit var boardfile: Boardfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        preferences = ManagedPreferences(this)
        boardfile = File(intent.getStringExtra(ID.IntentBoardfile)).toBoardfile()
    }

    val mAlphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f)
    val mAlphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f)
}