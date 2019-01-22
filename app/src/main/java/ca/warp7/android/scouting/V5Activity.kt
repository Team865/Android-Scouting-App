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
import ca.warp7.android.scouting.constants.Constants.kFadeDuration
import ca.warp7.android.scouting.constants.Constants.kTimerLimit
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

    private lateinit var timerStatus: TextView
    private lateinit var timeProgress: ProgressBar
    private lateinit var timeSeeker: SeekBar
    private lateinit var startButton: TextView
    private lateinit var playAndPauseImage: ImageButton
    private lateinit var undoAndNowImage: ImageButton
    private lateinit var playAndPauseView: ViewGroup
    private lateinit var undoAndNowView: ViewGroup
    private lateinit var playAndPauseText: TextView
    private lateinit var undoAndNowText: TextView
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: ScoutingTabsPagerAdapter

    private lateinit var preferences: ManagedPreferences
    private lateinit var boardfile: Boardfile

    private var activityState = ScoutingActivityState.WaitingToStart
    private var relativeTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        preferences = ManagedPreferences(this)
        boardfile = File(intent.getStringExtra(ID.IntentBoardfile)).toBoardfile()
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_scouting)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        timerStatus = findViewById(R.id.timer_status)
        startButton = findViewById(R.id.start_timer)
        startButton.elevation = 4f
        playAndPauseImage = findViewById(R.id.play_pause_image)
        undoAndNowImage = findViewById(R.id.undo_now_image)
        playAndPauseView = findViewById(R.id.play_pause_container)
        undoAndNowView = findViewById(R.id.undo_now_container)
        playAndPauseText = findViewById(R.id.play_pause_text)
        undoAndNowText = findViewById(R.id.undo_now_text)
        timeProgress = findViewById(R.id.time_progress)
        timeSeeker = findViewById(R.id.time_seeker)
        timeProgress.max = kTimerLimit
        timeProgress.progress = 0
        timeSeeker.max = kTimerLimit
        timeSeeker.progress = 0
        timeSeeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && activityState == ScoutingActivityState.Pausing) {
                    relativeTime = progress
                    // FIXME updateTimerStatusAndProgressBar()
                    // FIXME updateAdjacentTabStates()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
    }

    private val mAlphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f).apply { duration = kFadeDuration.toLong() }
    private val mAlphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f).apply { duration = kFadeDuration.toLong() }
}