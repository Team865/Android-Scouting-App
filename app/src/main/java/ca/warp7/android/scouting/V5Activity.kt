@file:Suppress("unused")

package ca.warp7.android.scouting

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
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
import ca.warp7.android.scouting.constants.Constants.*
import ca.warp7.android.scouting.res.ManagedPreferences
import ca.warp7.android.scouting.v5.boardfile.Boardfile
import ca.warp7.android.scouting.v5.boardfile.toBoardfile
import ca.warp7.android.scouting.v5.entry.Board
import ca.warp7.android.scouting.v5.entry.Board.*
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

    private lateinit var match: String
    private lateinit var team: String
    private lateinit var scout: String
    private lateinit var board: Board

    private var activityState = ScoutingActivityState.WaitingToStart
    private var relativeTime = 0
    private var timerIsCountingUp = false
    private var timerIsRunning = false
    private var currentTab = 0
    private var startingTimestamp = 0
    private var lastRecordedTime = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_scouting)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
        pager = findViewById(R.id.pager)

        timeProgress.max = kTimerLimit
        timeProgress.progress = 0
        timeSeeker.max = kTimerLimit
        timeSeeker.progress = 0
        timeSeeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && activityState == ScoutingActivityState.Pausing) {
                    relativeTime = progress
                    // FIXME updateStatus()
                    // FIXME updateAdjacentTabStates()
                }
            }
        })

        preferences = ManagedPreferences(this)
        boardfile = File(intent.getStringExtra(IntentKey.Boardfile)).toBoardfile()
        match = intent.getStringExtra(IntentKey.Match)
        team = intent.getStringExtra(IntentKey.Team)
        scout = intent.getStringExtra(IntentKey.Scout)
        board = intent.getSerializableExtra(IntentKey.Board) as Board

        findViewById<TextView>(R.id.toolbar_match).text = match
        findViewById<TextView>(R.id.toolbar_team).also {
            it.text = when (board) {
                RS, BS -> "ALL"
                else -> team
            }
            it.setTextColor(
                ContextCompat.getColor(
                    this, when (board) {
                        R1, R2, R3, RS -> R.color.colorRed
                        B1, B2, B3, BS -> R.color.colorBlue
                    }
                )
            )
            it.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // FIXME mEntry = Entry(match, team, scoutName, this)
        pagerAdapter = ScoutingTabsPagerAdapter(supportFragmentManager, 0, pager) // FIXME size
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                // FIXME mCurrentTab = position
                // FIXME updateCurrentTab()
            }
        })

    }

    /**
     * Reflect the value of mTimer on the timer view and seek bars
     */
    private fun updateStatus() {
        val time = if (timerIsCountingUp) {
            timerStatus.setTypeface(null, Typeface.BOLD)
            relativeTime
        } else {
            timerStatus.setTypeface(null, Typeface.NORMAL)
            if (relativeTime <= kAutonomousTime) kAutonomousTime - relativeTime else kTimerLimit - relativeTime
        }

        val status = time.toString()
        val placeholder = CharArray(kTotalTimerDigits - status.length)
        val filledStatus = String(placeholder).replace("\u0000", "0") + status
        timerStatus.text = filledStatus
        timerStatus.setTextColor(
            ContextCompat.getColor(
                this, when {
                    relativeTime <= kAutonomousTime -> R.color.colorAutoYellow
                    else -> R.color.colorTeleOpGreen
                }
            )
        )
        timeProgress.progress = relativeTime
        timeSeeker.progress = relativeTime
    }

    private val mAlphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f).apply { duration = kFadeDuration.toLong() }
    private val mAlphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f).apply { duration = kFadeDuration.toLong() }
}