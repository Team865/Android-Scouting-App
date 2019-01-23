package ca.warp7.android.scouting

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import ca.warp7.android.scouting.ScoutingActivityState.*
import ca.warp7.android.scouting.components.V5TabsPagerAdapter
import ca.warp7.android.scouting.constants.Constants.*
import ca.warp7.android.scouting.res.ManagedPreferences
import ca.warp7.android.scouting.v5.boardfile.Boardfile
import ca.warp7.android.scouting.v5.boardfile.ScoutTemplate
import ca.warp7.android.scouting.v5.boardfile.toBoardfile
import ca.warp7.android.scouting.v5.entry.Board
import ca.warp7.android.scouting.v5.entry.Board.*
import ca.warp7.android.scouting.v5.entry.MutableEntry
import ca.warp7.android.scouting.v5.entry.V5TimedEntry
import java.io.File

abstract class V5Activity : AppCompatActivity(), ScoutingActivityBase {

    override lateinit var handler: Handler
    override val actionVibrator get() = preferences.vibrator
    override val isSecondLimit get() = relativeTime > kTimerLimit || relativeTime == lastRecordedTime
    override var entry: MutableEntry? = null
    override val timeEnabled get() = activityState != WaitingToStart

    override fun feedSecondLimit() {
        lastRecordedTime = relativeTime
    }

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
    private lateinit var pagerAdapter: V5TabsPagerAdapter

    private lateinit var preferences: ManagedPreferences
    private lateinit var boardfile: Boardfile
    private lateinit var template: ScoutTemplate
    private val screens get() = template.screens

    private lateinit var match: String
    private lateinit var team: String
    private lateinit var scout: String
    private lateinit var board: Board

    private var activityState = WaitingToStart
    private var relativeTime: Byte = 0
    private var lastRecordedTime: Byte = 0
    private var timerIsCountingUp = false
    private var timerIsRunning = false
    private var currentTab = 0
    private var startingTimestamp = 0

    private var usingPauseBetaFeature: Boolean = false

    /**
     * Calculates the relative time based on
     * the current time and the starting timestamp
     */
    private val calculateRelativeTime get() = Math.min(currentTime - startingTimestamp, kTimerLimit).toByte()

    /**
     * Calculates whether the counting timer is in approximation with the current time
     */
    private val relativeTimeMatchesCurrentTime: Boolean get() = Math.abs(relativeTime - calculateRelativeTime) <= 1

    private val timedUpdater = Runnable {
        if (activityState != TimedScouting) {
            timerIsRunning = false
        } else {
            timerIsRunning = true
            updateActivityStatus()
            updateTabStates()
            relativeTime++
            if (relativeTime <= kTimerLimit) postTimerUpdate()
            else {
                timerIsRunning = false
                if (usingPauseBetaFeature) startActivityState(Pausing)
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
        setContentView(R.layout.activity_scouting)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        timerStatus = findViewById(R.id.timer_status)
        startButton = findViewById(R.id.start_timer)
        playAndPauseImage = findViewById(R.id.play_pause_image)
        undoAndNowImage = findViewById(R.id.undo_now_image)
        playAndPauseView = findViewById(R.id.play_pause_container)
        undoAndNowView = findViewById(R.id.undo_now_container)
        playAndPauseText = findViewById(R.id.play_pause_text)
        undoAndNowText = findViewById(R.id.undo_now_text)
        timeProgress = findViewById(R.id.time_progress)
        timeSeeker = findViewById(R.id.time_seeker)
        pager = findViewById(R.id.pager)

        startButton.setOnClickListener {
            startingTimestamp = currentTime
            entry?.timestamp = startingTimestamp
            startActivityState(TimedScouting)
            updateTabStates()
        }

        playAndPauseView.setOnClickListener {
            when (activityState) {
                TimedScouting -> startActivityState(Pausing)
                Pausing -> startActivityState(TimedScouting)
                else -> Unit
            }
        }

        undoAndNowView.setOnClickListener {
            when (activityState) {
                TimedScouting -> if (relativeTimeMatchesCurrentTime) {
                    entry?.apply {
                        val dataPoint = undo()
                        dataPoint?.also {
                            actionVibrator.vibrateAction()
                            updateTabStates()
                        }
                    }
                } else {
                    relativeTime = calculateRelativeTime
                    actionVibrator.vibrateStart()
                    undoAndNowImage.setImageResource(R.drawable.ic_undo_ablack)
                    undoAndNowText.setText(R.string.btn_undo)
                }
                Pausing -> {
                    relativeTime = calculateRelativeTime
                    startActivityState(TimedScouting)
                }
                else -> Unit
            }
        }

        timerStatus.setOnClickListener {
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
                    relativeTime = progress.toByte()
                    updateActivityStatus()
                    updateTabStates()
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

        template = when (board) {
            RS, BS -> boardfile.superScoutTemplate
            else -> boardfile.robotScoutTemplate
        }

        pagerAdapter = V5TabsPagerAdapter(supportFragmentManager, screens.size, pager)
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

        entry = V5TimedEntry(
            match = match,
            team = team,
            scout = scout,
            board = board,
            dataPoints = mutableListOf(),
            timestamp = currentTime,
            getTime = { relativeTime })

        startActivityState(WaitingToStart)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_confirmation)
            .setMessage(R.string.exit_confirmation_body)
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes) { _, _ -> super.onBackPressed() }
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scouting_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_flags -> {
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
                    .apply {
                        if (activityState != WaitingToStart && preferences.shouldShowPause()) {
                            setNeutralButton(if (usingPauseBetaFeature) "Hide Pause" else "Show Pause") { _, _ ->
                                usingPauseBetaFeature = !usingPauseBetaFeature
                                actionVibrator.vibrateAction()
                                if (usingPauseBetaFeature) {
                                    playAndPauseView.show()
                                } else {
                                    playAndPauseView.hide()
                                }
                            }
                        }
                    }
                    .create()
                    .apply {
                        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                        show()
                    }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Reflect the value of mTimer on the timer view and seek bars
     */
    private fun updateActivityStatus() {
        val time = if (timerIsCountingUp) {
            timerStatus.setTypeface(null, Typeface.BOLD)
            relativeTime.toInt()
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
        timeProgress.progress = relativeTime.toInt()
        timeSeeker.progress = relativeTime.toInt()
    }

    private val alphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f).apply { duration = kFadeDuration.toLong() }
    private val alphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f).apply { duration = kFadeDuration.toLong() }

    /**
     * Updates the current tab as well as the title banner
     */
    private fun updateCurrentTab() {
        val title = if (currentTab >= 0 && currentTab < screens.size) {
            screens[currentTab].title
        } else if (currentTab == screens.size) {
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

    /**
     * Updates the state on the views on the page to match undo
     * and navigation.
     */
    private fun updateTabStates() {
        if (currentTab != 0) pagerAdapter[currentTab - 1].updateTabState()
        pagerAdapter[currentTab].updateTabState()
        if (currentTab != pagerAdapter.count - 1) pagerAdapter[currentTab + 1].updateTabState()
    }

    /**
     * Sets the current activity state and update views and timer
     *
     * @param state the activity state to start
     */
    private fun startActivityState(state: ScoutingActivityState) {
        if (state == TimedScouting && (timerIsRunning || relativeTime >= kTimerLimit)) return
        activityState = state
        when (activityState) {
            WaitingToStart -> {
                playAndPauseView.hide()
                undoAndNowView.hide()
                timeSeeker.hide()
                timeProgress.hide()
            }
            TimedScouting -> {
                if (usingPauseBetaFeature) playAndPauseView.show() else playAndPauseView.hide()
                undoAndNowView.show()
                startButton.hide()
                timeSeeker.hide()
                timeProgress.show()
                playAndPauseImage.setImageResource(R.drawable.ic_pause_ablack)
                playAndPauseText.setText(R.string.btn_pause)
                if (relativeTimeMatchesCurrentTime) {
                    undoAndNowImage.setImageResource(R.drawable.ic_undo_ablack)
                    undoAndNowText.setText(R.string.btn_undo)
                } else {
                    undoAndNowImage.setImageResource(R.drawable.ic_skip_next_red)
                    undoAndNowText.setText(R.string.btn_now)
                }
                findViewById<View>(android.R.id.content)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                actionVibrator.vibrateStart()
                timedUpdater.run()
            }
            Pausing -> {
                playAndPauseView.show()
                undoAndNowView.show()
                startButton.hide()
                playAndPauseImage.setImageResource(R.drawable.ic_play_arrow_ablack)
                playAndPauseText.setText(R.string.btn_resume)
                undoAndNowImage.setImageResource(R.drawable.ic_skip_next_red)
                undoAndNowText.setText(R.string.btn_now)
                timeSeeker.show()
                timeProgress.hide()
                findViewById<View>(android.R.id.content)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAlmostYellow))
            }
        }
    }
}

