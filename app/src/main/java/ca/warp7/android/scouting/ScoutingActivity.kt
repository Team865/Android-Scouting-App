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
import ca.warp7.android.scouting.boardfile.ScoutTemplate
import ca.warp7.android.scouting.boardfile.createBoardfileFromAssets
import ca.warp7.android.scouting.entry.*
import ca.warp7.android.scouting.entry.Board.*
import ca.warp7.android.scouting.ui.ActionVibrator
import ca.warp7.android.scouting.ui.EntryInMatch
import ca.warp7.android.scouting.ui.TabPagerAdapter

class ScoutingActivity : AppCompatActivity(), BaseScoutingActivity {

    override fun vibrateAction() {
        vibrator.vibrateAction()
    }

    override fun isTimeEnabled(): Boolean {
        // this includes paused state because time is still tracked
        return activityState != WaitingToStart
    }

    override var entry: MutableEntry? = null
    override var template: ScoutTemplate? = null

    override fun getRelativeTime(): Double {
        return when (activityState) {
            WaitingToStart -> 0.0
            TimedScouting -> getCurrentToStartTime()
            Pausing -> relativeTimeAtPause
        }
    }

    @Suppress("RemoveRedundantQualifierName")
    override fun modifyName(name: String): String {
        val eim = entryInMatch
        var varName = name
        if (eim != null && eim.teams.size > 5) {
            val boards = Board.values()
            for (i in 0 until 6) {
                varName = varName.replace(boards[i].name, eim.teams[i].toString())
            }
            val relBoards = RelativeBoard.values()
            for (i in 0 until 6) {
                val relBoard = relBoards[i]
                val team = eim.teams[relBoard.relativeTo(eim.board).ordinal].toString()
                varName = varName.replace(relBoard.name, team)
            }
        }
        return modifyNameForDisplay(varName)
    }

    enum class State {
        WaitingToStart, TimedScouting, Pausing
    }

    private var startTime = 0.0
    private var relativeTimeAtPause = 0.0 // this is int to make rounding errors easier

    private fun getCurrentTime(): Double {
        return System.currentTimeMillis() / 1000.0
    }

    private fun getCurrentToStartTime(): Double {
        return getCurrentTime() - startTime
    }

    private val vibrator by lazy {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        ActionVibrator(
            this, sharedPreferences.getBoolean(this.getString(R.string.pref_use_vibration_key), true)
        )
    }

    private val timerStatus: TextView get() = findViewById(R.id.timer_status)
    private val timeProgress: ProgressBar get() = findViewById(R.id.time_progress)
    private val timeSeeker: SeekBar get() = findViewById(R.id.time_seeker)
    private val startButton: TextView get() = findViewById(R.id.start_timer)
    private val playAndPauseImage: ImageButton get() = findViewById(R.id.play_pause_image)
    private val undoButton: ImageButton get() = findViewById(R.id.undo_now_image)
    private val pager: ViewPager get() = findViewById(R.id.pager)

    private var pagerAdapter: TabPagerAdapter? = null

    private var activityState = WaitingToStart
    private var currentTab = 0

    private var entryInMatch: EntryInMatch? = null

    private val handler = Handler()
    private val periodicUpdater = Runnable { periodicUpdate() }

    /**
     * Updates the timer once every second
     */
    private fun periodicUpdate() {
        if (activityState == TimedScouting) {
            val relativeTime = getRelativeTime()
            updateActivityStatus(relativeTime.toInt())
            updateAdjacentTabState()

            // determine if the timer should stop

            if (relativeTime <= kTimerLimit) {
                handler.postDelayed(periodicUpdater, 1000)
            } else {
                startActivityState(Pausing)
            }
        }
    }

    /**
     * Update all adjacent tabs
     */
    private fun updateAdjacentTabState() {
        val pagerAdapter = pagerAdapter ?: return
        if (currentTab != 0) {
            pagerAdapter[currentTab - 1].updateTabState()
        }
        pagerAdapter[currentTab].updateTabState()
        if (currentTab != pagerAdapter.count - 1) {
            pagerAdapter[currentTab + 1].updateTabState()
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
                cb.text = modifyName(tag)
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
            updateAdjacentTabState()
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
                    updateAdjacentTabState()
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
                        updateAdjacentTabState()
                    }
                }
            })
        }

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

        // special entry team string to encode match schedule with super scouts
        val entryTeam = when (board) {
            RX -> teams.joinToString("_")
            // invert the list order for the blue alliance
            BX -> (teams.subList(3, 6) + teams.subList(0, 3)).joinToString("_")
            else -> team
        }

        this.entryInMatch = entryInMatch
        entry = TimedEntry(match, entryTeam, scout, board, getCurrentTime().toInt()) { getRelativeTime() }

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

        val boardFile = createBoardfileFromAssets(this)
        val template = when (board) {
            RX, BX -> boardFile.superScoutTemplate
            else -> boardFile.robotScoutTemplate
        }
        this.template = template

        val pager = pager
        pagerAdapter = TabPagerAdapter(supportFragmentManager, template.screens.size, pager)
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
        val entry = entry
        val eim = entryInMatch

        if (entry == null || eim == null || entry.dataPoints.isEmpty()) {
            setResult(Activity.RESULT_CANCELED, null)
        } else {
            val resultData = EntryInMatch(
                eim.match,
                eim.teams,
                eim.board,
                true,
                eim.isScheduled,
                entry.getEncoded()
            ).toCSV()

            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(kEntryInMatchIntent, resultData)
            )
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
        val screens = template!!.screens

        // get the correct title of the tab
        val title = if (currentTab >= 0 && currentTab < screens.size) {
            screens[currentTab].title
        } else if (currentTab == screens.size) {
            getString(R.string.qr_code_tab)
        } else {
            getString(R.string.unknown_tab)
        }

        // update the tab
        if (currentTab == screens.size) {
            pagerAdapter!![currentTab].updateTabState()
        }

        val titleBanner = findViewById<TextView>(R.id.title_banner)

        // animate the title banner
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