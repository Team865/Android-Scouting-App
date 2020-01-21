package ca.warp7.android.scouting

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.TypedValue
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import ca.warp7.android.scouting.ScoutingActivity.State.*
import ca.warp7.android.scouting.boardfile.Boardfile
import ca.warp7.android.scouting.boardfile.ScoutTemplate
import ca.warp7.android.scouting.boardfile.exampleBoardfile
import ca.warp7.android.scouting.entry.Alliance
import ca.warp7.android.scouting.entry.Board
import ca.warp7.android.scouting.entry.Board.BX
import ca.warp7.android.scouting.entry.Board.RX
import ca.warp7.android.scouting.entry.MutableEntry
import ca.warp7.android.scouting.entry.TimedEntry
import ca.warp7.android.scouting.ui.TabPagerAdapter

class ScoutingActivity : AppCompatActivity(), BaseScoutingActivity {

    enum class State {
        WaitingToStart, TimedScouting, Pausing
    }

    override fun updateTabStates() {
        if (currentTab != 0) pagerAdapter[currentTab - 1].updateTabState()
        pagerAdapter[currentTab].updateTabState()
        if (currentTab != pagerAdapter.count - 1) pagerAdapter[currentTab + 1].updateTabState()
    }

    private fun getCurrentTime(): Double {
        return System.currentTimeMillis() / 1000.0
    }

    private val vibrator by lazy {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        ActionVibrator(
            this, sharedPreferences.getBoolean(this.getString(R.string.pref_use_vibration_key), true)
        )
    }

    private lateinit var handler: Handler

    override fun vibrateAction() {
        vibrator.vibrateAction()
    }

    override var entry: MutableEntry? = null
    override val timeEnabled get() = activityState != WaitingToStart
    override var boardfile: Boardfile? = null
    override var template: ScoutTemplate? = null

    override fun getRelativeTime(): Double {
        return matchTime
    }

    private val timerStatus: TextView get() = findViewById(R.id.timer_status)
    private val timeProgress: ProgressBar get() = findViewById(R.id.time_progress)
    private val timeSeeker: SeekBar get() = findViewById(R.id.time_seeker)
    private val startButton: TextView get() = findViewById(R.id.start_timer)
    private val playAndPauseImage: ImageButton get() = findViewById(R.id.play_pause_image)
    private val undoButton: ImageButton get() = findViewById(R.id.undo_now_image)
    private val pager: ViewPager get() = findViewById(R.id.pager)

    private lateinit var pagerAdapter: TabPagerAdapter

    private var activityState = WaitingToStart
    private var timerIsCountingUp = false
    private var timerIsRunning = false
    private var currentTab = 0

    var matchTime = 0.0
    // keep track for dt calculations
    private var lastTime = 0.0

    private val screens get() = template?.screens

    private val timedUpdater = Runnable {
        if (activityState != TimedScouting) {
            timerIsRunning = false
        } else {
            timerIsRunning = true
            updateActivityStatus()
            updateTabStates()

            // Calculate the time relative to the start of the match, and
            // determine if the timer should stop

            val time = getCurrentTime()
            val dt = time - lastTime
            lastTime = time

            matchTime += dt
            if (matchTime <= kTimerLimit) {
                postTimerUpdate()
            } else {
                timerIsRunning = false
                startActivityState(Pausing)
            }
        }
    }

    private fun postTimerUpdate() {
        handler.postDelayed(timedUpdater, 1000)
    }

    private fun showCommentBox(entry: MutableEntry) {
        // Create the comments EditText
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            setText(entry.comments)
            setSelection(entry.comments.length)
            compoundDrawablePadding = 16
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setHint(R.string.comments_hint)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_ablack_small, 0, 0, 0)
        }

        val layout = LinearLayout(this)
        layout.addView(input)
        layout.setPadding(16, 8, 16, 0)

        // Create the alert
        AlertDialog.Builder(this)
            .setTitle(R.string.edit_comments)
            .setView(layout)
            .setPositiveButton("OK") { _, _ -> entry.comments = input.text.toString() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()
            .apply { window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_scouting)

        startButton.setOnClickListener {
            entry?.timestamp = getCurrentTime().toInt()
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
                    vibrateAction()
                    updateTabStates()
                }
            }
        }

        findViewById<ImageButton>(R.id.comment_button).setOnClickListener {
            entry?.also { showCommentBox(it) }
        }
        findViewById<TextView>(R.id.title_banner).setOnClickListener {
            timerIsCountingUp = !timerIsCountingUp
            updateActivityStatus()
        }

        timeProgress.apply {
            max = kTimerLimit
            progress = 0
        }
        timeSeeker.apply {
            max = kTimerLimit
            progress = 0
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser && activityState == Pausing) {
                        matchTime = progress.toDouble()
                        updateActivityStatus()
                        updateTabStates()
                    }
                }
            })
        }

        boardfile = exampleBoardfile
        val match = intent.getStringExtra(ScoutingIntentKey.kMatch)
        val team = intent.getStringExtra(ScoutingIntentKey.kTeam)
        val scout = intent.getStringExtra(ScoutingIntentKey.kScout)
        val board = intent.getSerializableExtra(ScoutingIntentKey.kBoard) as Board

        entry = TimedEntry(match, team, scout, board, getCurrentTime().toInt()) { matchTime }

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

        val pager = pager
        pagerAdapter = TabPagerAdapter(supportFragmentManager, screens?.size ?: 0, pager)
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
        startActivityState(WaitingToStart)
    }

    override fun onBackPressed() {
        if (activityState == WaitingToStart) {
            setResult(Activity.RESULT_CANCELED, null)
        } else {
            val entry = entry
            if (entry == null) {
                setResult(Activity.RESULT_CANCELED, null)
                return
            }
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(ScoutingIntentKey.kResult, entry.getEncoded())
                putExtra(ScoutingIntentKey.kMatch, entry.match)
                putExtra(ScoutingIntentKey.kBoard, entry.board)
                putExtra(ScoutingIntentKey.kTeam, entry.team)
            })
        }
        super.onBackPressed()
    }

    private fun updateActivityStatus() {
        val time = if (timerIsCountingUp) {
            timerStatus.setTypeface(null, Typeface.BOLD)
            matchTime
        } else {
            timerStatus.setTypeface(null, Typeface.NORMAL)
            if (matchTime <= kAutonomousTime) kAutonomousTime - matchTime else kTimerLimit - matchTime
        }
        val status = time.toInt().toString()
        val placeholder = CharArray(kTotalTimerDigits - status.length)
        val filledStatus = String(placeholder).replace("\u0000", "0") + status
        timerStatus.text = filledStatus
        val statusColor = when {
            matchTime <= kAutonomousTime -> R.color.colorAutoYellow
            else -> R.color.colorTeleOpGreen
        }
        timerStatus.setTextColor(ContextCompat.getColor(this, statusColor))
        timeProgress.progress = matchTime.toInt()
        timeSeeker.progress = matchTime.toInt()
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
        if (titleBanner.text.toString().isNotEmpty()) {
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

    private fun startActivityState(wantedState: State) {
        if (wantedState == TimedScouting && (timerIsRunning || matchTime >= kTimerLimit)) {
            return
        }
        activityState = wantedState
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
                vibrator.vibrateStart()
                // need to reset the time so that dt doesn't get messed up
                lastTime = getCurrentTime()
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