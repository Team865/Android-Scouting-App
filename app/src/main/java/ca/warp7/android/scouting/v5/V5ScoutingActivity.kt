package ca.warp7.android.scouting.v5

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import ca.warp7.android.scouting.Constants.*
import ca.warp7.android.scouting.ManagedPreferences
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.ScoutingActivityState.*
import ca.warp7.android.scouting.v5.boardfile.Boardfile
import ca.warp7.android.scouting.v5.boardfile.ScoutTemplate
import ca.warp7.android.scouting.v5.boardfile.exampleBoardfile
import ca.warp7.android.scouting.v5.entry.Alliance
import ca.warp7.android.scouting.v5.entry.Board
import ca.warp7.android.scouting.v5.entry.Board.BX
import ca.warp7.android.scouting.v5.entry.Board.RX
import ca.warp7.android.scouting.v5.entry.MutableEntry
import ca.warp7.android.scouting.v5.entry.V5TimedEntry
import ca.warp7.android.scouting.v5.ui.V5TabsPagerAdapter

class V5ScoutingActivity : AppCompatActivity(), BaseScoutingActivity {

    override fun updateTabStates() {
        if (currentTab != 0) pagerAdapter[currentTab - 1].updateTabState()
        pagerAdapter[currentTab].updateTabState()
        if (currentTab != pagerAdapter.count - 1) pagerAdapter[currentTab + 1].updateTabState()
    }

    override lateinit var handler: Handler
    override val actionVibrator get() = preferences.vibrator
    override var entry: MutableEntry? = null
    override val timeEnabled get() = activityState != WaitingToStart
    override var boardfile: Boardfile? = null
    override var template: ScoutTemplate? = null
    override var relativeTime = 0

    private lateinit var timerStatus: TextView
    private lateinit var timeProgress: ProgressBar
    private lateinit var timeSeeker: SeekBar
    private lateinit var startButton: TextView
    private lateinit var playAndPauseImage: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: V5TabsPagerAdapter
    private lateinit var preferences: ManagedPreferences
    private lateinit var match: String
    private lateinit var team: String
    private lateinit var scout: String
    private lateinit var board: Board

    private var activityState = WaitingToStart
    private var timerIsCountingUp = false
    private var timerIsRunning = false
    private var currentTab = 0
    private var startingTimestamp = 0

    private val screens get() = template?.screens
    private val timedUpdater = Runnable {
        if (activityState != TimedScouting) timerIsRunning = false else {
            timerIsRunning = true
            updateActivityStatus()
            updateTabStates()
            relativeTime++
            if (relativeTime <= kTimerLimit) postTimerUpdate()
            else {
                timerIsRunning = false
                startActivityState(Pausing)
            }
        }
    }

    private fun postTimerUpdate() {
        handler.postDelayed(timedUpdater, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_v5_scouting)
        timerStatus = findViewById(R.id.timer_status)
        startButton = findViewById(R.id.start_timer)
        playAndPauseImage = findViewById(R.id.play_pause_image)
        undoButton = findViewById(R.id.undo_now_image)
        timeProgress = findViewById(R.id.time_progress)
        timeSeeker = findViewById(R.id.time_seeker)
        pager = findViewById(R.id.pager)
        startButton.setOnClickListener {
            startingTimestamp = currentTime
            entry?.timestamp = startingTimestamp
            startActivityState(TimedScouting)
            updateTabStates()
        }
        playAndPauseImage.setOnClickListener {
            when (activityState) {
                TimedScouting -> startActivityState(Pausing)
                Pausing -> startActivityState(TimedScouting)
                else -> Unit
            }
        }
        undoButton.setOnClickListener {
            entry?.apply {
                val dataPoint = undo()
                dataPoint?.also {
                    actionVibrator.vibrateAction()
                    updateTabStates()
                }
            }
        }
        findViewById<ImageButton>(R.id.v5_comment_button).setOnClickListener {
            entry?.also {
                val input = EditText(this).apply {
                    inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                            InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    setText(it.comments)
                    setSelection(it.comments.length)
                    gravity = Gravity.CENTER
                    setHint(R.string.comments_hint)
                }
                AlertDialog.Builder(this)
                    .setTitle(R.string.edit_comments)
                    .setView(input)
                    .setPositiveButton("OK") { _, _ -> it.comments = input.text.toString() }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .create()
                    .apply { window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) }.show()
            }
        }
        findViewById<TextView>(R.id.title_banner).setOnClickListener {
            timerIsCountingUp = !timerIsCountingUp
            updateActivityStatus()
        }
        timeProgress.max = kTimerLimit
        timeProgress.progress = 0
        timeSeeker.max = kTimerLimit
        timeSeeker.progress = 0
        timeSeeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && activityState == Pausing) {
                    relativeTime = progress
                    updateActivityStatus()
                    updateTabStates()
                }
            }
        })
        preferences = ManagedPreferences(this)
        boardfile = exampleBoardfile
        match = intent.getStringExtra(ScoutingIntentKey.kMatch)
        team = intent.getStringExtra(ScoutingIntentKey.kTeam)
        scout = intent.getStringExtra(ScoutingIntentKey.kScout)
        board = intent.getSerializableExtra(ScoutingIntentKey.kBoard) as Board
        findViewById<TextView>(R.id.toolbar_match).text = match.let {
            val split = it.split("_")
            if (split.size == 2) split[1] else it
        }
        findViewById<TextView>(R.id.toolbar_team).also {
            it.text = team
        }
        findViewById<TextView>(R.id.toolbar_board).also {
            it.text = board.name
            val color = when (board.alliance) {
                Alliance.Red -> R.color.colorRed
                Alliance.Blue -> R.color.colorBlue
            }
            it.setTextColor(ContextCompat.getColor(this, color))
        }
        template = when (board) {
            RX, BX -> boardfile?.superScoutTemplate
            else -> boardfile?.robotScoutTemplate
        }
        pagerAdapter = V5TabsPagerAdapter(supportFragmentManager, screens?.size ?: 0, pager)
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageSelected(position: Int) {
                currentTab = position
                updateCurrentTab()
            }
        })
        updateActivityStatus()
        updateCurrentTab()
        entry = V5TimedEntry(match, team, scout, board, mutableListOf(), currentTime, { relativeTime }, isTiming = true)
        startActivityState(WaitingToStart)
    }

    override fun onBackPressed() {
        when (activityState) {
            WaitingToStart -> setResult(Activity.RESULT_CANCELED, null)
            else -> setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(ScoutingIntentKey.kResult, entry?.encoded)
                putExtra(ScoutingIntentKey.kMatch, match)
                putExtra(ScoutingIntentKey.kBoard, board)
                putExtra(ScoutingIntentKey.kTeam, team)
            })
        }
        super.onBackPressed()
    }

    private fun updateActivityStatus() {
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
        val statusColor = when {
            relativeTime <= kAutonomousTime -> R.color.colorAutoYellow
            else -> R.color.colorTeleOpGreen
        }
        timerStatus.setTextColor(ContextCompat.getColor(this, statusColor))
        timeProgress.progress = relativeTime
        timeSeeker.progress = relativeTime
    }

    private val alphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f).apply { duration = kFadeDuration.toLong() }
    private val alphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f).apply { duration = kFadeDuration.toLong() }

    private fun updateCurrentTab() {
        val title = if (currentTab >= 0 && currentTab < screens?.size ?: -1) {
            screens?.get(currentTab)?.title ?: "Unknown"
        } else if (currentTab == screens?.size ?: -1) {
            pagerAdapter[currentTab].updateTabState()
            "QR Code"
        } else "Unknown"
        val titleBanner = findViewById<TextView>(R.id.title_banner)
        if (!titleBanner.text.toString().isEmpty()) {
            alphaAnimationOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) = Unit
                override fun onAnimationRepeat(animation: Animation) = Unit
                override fun onAnimationEnd(animation: Animation) {
                    titleBanner.text = title
                    titleBanner.startAnimation(alphaAnimationIn)
                }
            })
            titleBanner.startAnimation(alphaAnimationOut)
        } else {
            titleBanner.text = title
            titleBanner.startAnimation(alphaAnimationIn)
        }
        if (pager.currentItem != currentTab) {
            pager.setCurrentItem(currentTab, true)
        }
    }

    private fun startActivityState(state: ScoutingActivityState) {
        if (state == TimedScouting && (timerIsRunning || relativeTime >= kTimerLimit)) return
        activityState = state
        when (activityState) {
            WaitingToStart -> {
                playAndPauseImage.hide()
                undoButton.hide()
                timeSeeker.hide()
                timeProgress.show()
            }
            TimedScouting -> {
                playAndPauseImage.show()
                undoButton.show()
                startButton.hide()
                timeSeeker.hide()
                timeProgress.show()
                playAndPauseImage.setImageResource(R.drawable.ic_pause_ablack)
                actionVibrator.vibrateStart()
                timedUpdater.run()
            }
            Pausing -> {
                playAndPauseImage.show()
                undoButton.show()
                startButton.hide()
                playAndPauseImage.setImageResource(R.drawable.ic_play_arrow_ablack)
                timeSeeker.show()
                timeProgress.hide()
            }
        }
    }
}