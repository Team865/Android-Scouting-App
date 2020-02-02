package ca.warp7.android.scouting

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.InputType
import android.util.TypedValue
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import ca.warp7.android.scouting.ScoutingActivity.State.*
import ca.warp7.android.scouting.boardfile.Boardfile
import ca.warp7.android.scouting.boardfile.ScoutTemplate
import ca.warp7.android.scouting.boardfile.TemplateScreen
import ca.warp7.android.scouting.boardfile.createBoardfileFromAssets
import ca.warp7.android.scouting.entry.Alliance
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.entry.DataPoint
import ca.warp7.android.scouting.entry.MutableEntry
import ca.warp7.android.scouting.entry.TimedEntry
import ca.warp7.android.scouting.ui.ActionVibrator
import ca.warp7.android.scouting.ui.EntryInMatch
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

    private val handler = Handler()

    override fun vibrateAction() {
        vibrator.vibrateAction()
    }

    override fun isTimeEnabled(): Boolean {
        // this includes paused state because time is still tracked
        return activityState != WaitingToStart
    }

    override var entry: MutableEntry? = null

    override var boardfile: Boardfile? = null
    override var template: ScoutTemplate? = null

    private var startTime = 0.0
    private var relativeTimeAtPause = 0.0 // this is int to make rounding errors easier

    override fun getRelativeTime(): Double {
        return when (activityState) {
            WaitingToStart -> 0.0
            TimedScouting -> getCurrentToStartTime()
            Pausing -> relativeTimeAtPause
        }
    }

    private fun getCurrentToStartTime(): Double {
        return getCurrentTime() - startTime
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
    private var currentTab = 0

    private var entryInMatch: EntryInMatch? = null

    private fun getScreens(): List<TemplateScreen>? {
        return template?.screens
    }

    private val periodicUpdater = Runnable { periodicUpdate() }

    private fun periodicUpdate() {
        if (activityState == TimedScouting) {
            val relativeTime = getRelativeTime()
            updateActivityStatus(relativeTime.toInt())
            updateTabStates()

            // determine if the timer should stop

            if (relativeTime <= kTimerLimit) {
                handler.postDelayed(periodicUpdater, 1000)
            } else {
                startActivityState(Pausing)
            }
        }
    }

    private fun showCommentBox(entry: MutableEntry) {

        val template = template ?: return
        val tags = template.tags

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 0)

        for ((index, tag) in tags.withIndex()) {
            layout.addView(CheckBox(this).also { cb ->
                cb.text = modifyNameForDisplay(tag)
                cb.textSize = 18f
                val typeIndex = template.lookupForTag(index)
                val lastValue = entry.lastValue(typeIndex)?.value ?: 0
                cb.isChecked = lastValue != 0
                cb.setOnCheckedChangeListener { _, isChecked ->
                    vibrateAction()
                    entry.add(DataPoint(typeIndex, if (isChecked) 1 else 0, getRelativeTime()))
                }
            })
        }

        // Create the comments EditText
        val commentInput = EditText(this).apply {
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
        layout.addView(commentInput)

        // Create the alert
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.additional_info))
            .setView(layout)
            .setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                entry.comments = commentInput.text.toString()
            }
            .setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_scouting)

        startButton.setOnClickListener {
            startTime = getCurrentTime()
            entry?.timestamp = startTime.toInt()
            startActivityState(TimedScouting)
            updateTabStates()
        }

        playAndPauseImage.setOnClickListener {
            when (activityState) {
                TimedScouting -> startActivityState(Pausing)
                Pausing -> startActivityState(TimedScouting)
                else -> throw IllegalStateException("activityState incorrect")
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
            val entry = entry
            if (entry != null) {
                showCommentBox(entry)
            }
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
                        // set the paused relative time
                        relativeTimeAtPause = progress.toDouble()
                        // also set the start time because otherwise the update will fail
                        startTime = getCurrentTime() - relativeTimeAtPause
                        // show stuff in the UI
                        updateActivityStatus(progress)
                        updateTabStates()
                    }
                }
            })
        }

        boardfile = createBoardfileFromAssets(this)

        val scout = intent.getStringExtra(kScoutIntent)
        val entryInMatch = EntryInMatch.fromCSV(intent.getStringExtra(kEntryInMatchIntent))
        val match = entryInMatch.match
        val board = entryInMatch.board
        val teams = entryInMatch.teams

        val team = when (board) {
            R1 -> teams[0].toString()
            R2 -> teams[1].toString()
            R3 -> teams[2].toString()
            B1 -> teams[3].toString()
            B2 -> teams[4].toString()
            B3 -> teams[5].toString()
            RX, BX -> "ALL"
        }

        this.entryInMatch = entryInMatch
        entry = TimedEntry(match, team, scout, board, getCurrentTime().toInt()) { getRelativeTime() }

        findViewById<TextView>(R.id.toolbar_match).text = match.let {
            val split = it.split("_")
            if (split.size == 2) "M" + split[1] else it
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
        pagerAdapter = TabPagerAdapter(supportFragmentManager, getScreens()?.size ?: 0, pager)
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageSelected(position: Int) {
                currentTab = position
                updateCurrentTab()
            }
        })

        updateActivityStatus(0)
        updateCurrentTab()
        startActivityState(WaitingToStart)
    }

    override fun onPause() {
        super.onPause()
        if (activityState == TimedScouting) {
            startActivityState(Pausing)
        }
    }

    override fun onBackPressed() {
        if (activityState == WaitingToStart) {
            setResult(Activity.RESULT_CANCELED, null)
        } else {
            val entry = entry
            val eim = entryInMatch
            if (entry == null || eim == null) {
                setResult(Activity.RESULT_CANCELED, null)
                return
            }
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(
                    kEntryInMatchIntent, EntryInMatch(
                        eim.match,
                        eim.teams,
                        eim.board,
                        true,
                        eim.isScheduled,
                        entry.getEncoded()
                    ).toCSV()
                )
            })
        }
        super.onBackPressed()
    }

    private fun updateActivityStatus(matchTime: Int) {
        val time = if (matchTime <= kAutonomousTime) {
            kAutonomousTime - matchTime
        } else {
            kTimerLimit - matchTime
        }

        val status = time.toString()
        val placeholder = CharArray(kTotalTimerDigits - status.length)
        val filledStatus = String(placeholder).replace("\u0000", "0") + status
        timerStatus.text = filledStatus
        val statusColor = when {
            matchTime <= kAutonomousTime -> R.color.colorAutoYellow
            else -> R.color.colorTeleOpGreen
        }
        timerStatus.setTextColor(ContextCompat.getColor(this, statusColor))
        timeProgress.progress = matchTime
        timeSeeker.progress = matchTime
    }

    private val alphaAnimationIn: Animation = AlphaAnimation(0.0f, 1.0f).apply { duration = kFadeDuration.toLong() }
    private val alphaAnimationOut: Animation = AlphaAnimation(1.0f, 0.0f).apply { duration = kFadeDuration.toLong() }

    private fun updateCurrentTab() {
        val screens = getScreens() ?: return
        val title = if (currentTab >= 0 && currentTab < screens.size) {
            screens.get(currentTab).title
        } else if (currentTab == screens.size) {
            pagerAdapter[currentTab].updateTabState()
            getString(R.string.qr_code_tab)
        } else {
            getString(R.string.unknown_tab)
        }
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
        if (wantedState == TimedScouting && getRelativeTime() >= kTimerLimit) {
            // We are already at the end. Cannot continue to scout
            return
        }
        activityState = wantedState
        when (wantedState) {
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
                // restore the absolute time
                startTime = getCurrentTime() - relativeTimeAtPause
                periodicUpdater.run()
            }
            Pausing -> {
                playAndPauseImage.show()
                undoButton.show()
                startButton.hide()
                playAndPauseImage.setImageResource(R.drawable.ic_play_arrow_ablack)
                timeSeeker.show()
                timeProgress.hide()
                // save the relative time
                relativeTimeAtPause = getCurrentToStartTime()
            }
        }
    }
}