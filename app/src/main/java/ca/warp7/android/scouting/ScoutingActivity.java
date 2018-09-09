package ca.warp7.android.scouting;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import ca.warp7.android.scouting.abstraction.AbstractActionVibrator;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.components.ScoutingInputsFragment;
import ca.warp7.android.scouting.components.ScoutingTabsPagerAdapter;
import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.constants.ScoutingState;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.Entry;
import ca.warp7.android.scouting.model.EntryFormatter;
import ca.warp7.android.scouting.model.ScoutingLayout;
import ca.warp7.android.scouting.model.Specs;
import ca.warp7.android.scouting.resources.ManagedPreferences;
import ca.warp7.android.scouting.wrappers.ScoutingActivityWrapper;

import static ca.warp7.android.scouting.constants.Constants.kAutonomousTime;
import static ca.warp7.android.scouting.constants.Constants.kFadeDuration;
import static ca.warp7.android.scouting.constants.Constants.kTimerLimit;
import static ca.warp7.android.scouting.constants.Constants.kTotalTimerDigits;


/**
 * <p>The Scouting Activity -- A generic activity to collect data
 * for an Entry based on a set Specs. It controls all sub-components
 * of the activity and implements a listener that can be used for
 * callback. It is responsible to manage the activity's state and
 * lifecycle, setting up components in the interface, receive
 * events from action buttons for navigation and commands, and
 * keeps track of an Entry object which stores the data </p>
 * <p>
 * <p>
 *
 * @author Team 865
 * @see ScoutingInputsFragment
 * @see ScoutingActivityListener
 * @see Entry
 * @since v0.2
 * </p>
 */

public class ScoutingActivity extends ScoutingActivityWrapper {


    // Beta Feature Variables

    private boolean mUsingPauseBetaFeature;


    // State Variables

    private int mActivityState;

    private boolean mTimerIsCountingUp;
    private boolean mTimerIsRunning;

    private int mTimer = 0;
    private int mCurrentTab = 0;

    private int mStartingTimestamp = 0;
    private int mLastRecordedTime = -1;


    // UI elements (see layout file)

    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar mToolbar;

    private TextView mToolbarTeam;
    private TextView mToolbarMatch;

    private TextView mTitleBanner;
    private TextView mTimerStatus;

    private ProgressBar mTimeProgress;
    private SeekBar mTimeSeeker;

    private TextView mStartButton;
    private ImageButton mPlayAndPauseImage;
    private ImageButton mUndoAndNowImage;
    private ViewGroup mPlayAndPauseView;
    private ViewGroup mUndoAndNowView;
    private TextView mPlayAndPauseText;
    private TextView mUndoAndNowText;

    private ViewPager mPager;
    private ScoutingTabsPagerAdapter mPagerAdapter;


    // System Services

    private Handler mTimeHandler;
    private ManagedPreferences mPreferences;


    // Data Model Variables

    private Specs mSpecs;
    private Entry mEntry;
    private List<ScoutingLayout> mLayouts;
    private StringBuilder mStatusLog;


    // Timer Process

    private Runnable mTimerUpdater = new Runnable() {
        @Override
        public void run() {

            if (mActivityState != ScoutingState.SCOUTING) {
                mTimerIsRunning = false;
                return;
            }

            mTimerIsRunning = true;

            updateTimerStatusAndProgressBar();
            updateAdjacentTabStates();
            mTimer++;

            if (mTimer <= kTimerLimit) {
                mTimeHandler.postDelayed(mTimerUpdater, 1000);
            } else {
                mTimerIsRunning = false;
                if (mUsingPauseBetaFeature) {
                    startActivityState(ScoutingState.PAUSING);
                }
            }
        }
    };


    // Activity Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mTimeHandler = new Handler();
        mPreferences = new ManagedPreferences(this);

        setupSpecs();
        setupUI();
        setupNavigationSliders();
        setupValuesFromIntent();
        setupPager();

        updateTimerStatusAndProgressBar();
        updateCurrentTab();

        initStates(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(ID.INSTANCE_TIMER, mTimer);
        outState.putInt(ID.INSTANCE_START_TIME, mStartingTimestamp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scouting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_flags:
                onCommentsAndFlags();
                return true;

            case R.id.menu_logs:
                onShowEntryLogs();
                return true;

            case R.id.menu_qr:
                //onShowQRCode();
                mCurrentTab = mLayouts.size();
                updateCurrentTab();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_confirmation)
                .setMessage(R.string.exit_confirmation_body)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScoutingActivity.super.onBackPressed();
                    }
                })
                .create()
                .show();
    }


    // ScoutingActivityListener methods (documented there)

    @Override
    public int getCurrentRelativeTime() {
        return mTimer;
    }

    @Override
    public Handler getHandler() {
        return mTimeHandler;
    }

    @Override
    public AbstractActionVibrator getManagedVibrator() {
        return mPreferences.getVibrator();
    }

    @Override
    public Entry getEntry() {
        return mEntry;
    }

    @Override
    public boolean timeIsRecordable() {
        return mTimer <= kTimerLimit && mLastRecordedTime != mTimer;
    }

    @Override
    public boolean timedInputsShouldDisable() {
        return mActivityState == ScoutingState.STARTING;
    }

    @Override
    public boolean dataShouldFocus(int dataType) {
        return mEntry.isFocused(dataType);
    }

    @Override
    public void pushCurrentTimeAsValue(int type, int state_flag) {
        mEntry.push(type, mTimer, state_flag);
        mLastRecordedTime = mTimer;
    }

    @Override
    public void pushStatus(String status) {
        mStatusLog.append(status.replace("{t}", String.valueOf(mTimer)));
        mStatusLog.append("\n");
    }


    // Misc. Event Handlers

    /**
     * Handles start of entry. Only called once
     */

    public void onStartScouting(View view) {
        mStartingTimestamp = getCurrentTime();
        mEntry.setStartingTimestamp(mStartingTimestamp);
        startActivityState(ScoutingState.SCOUTING);
        updateAdjacentTabStates();
        pushStatus("Timer Started\n");
    }

    /**
     * Event Handler for the play/pause button,
     * which updates the activity state
     */

    public void onPlayPauseClicked(View view) {

        switch (mActivityState) {

            case ScoutingState.SCOUTING: // Pause button
                startActivityState(ScoutingState.PAUSING);
                break;

            case ScoutingState.PAUSING: // Play button
                startActivityState(ScoutingState.SCOUTING);
                break;
        }
    }

    /**
     * Event Handler for the undo/skip button
     */

    public void onUndoSkipClicked(View view) {

        switch (mActivityState) {

            case ScoutingState.SCOUTING: // Undo button

                if (isTimerAtCurrentTime()) {
                    attemptUndo();
                } else {
                    mTimer = calculateCurrentRelativeTime();
                    getManagedVibrator().vibrateStart();
                    mUndoAndNowImage.setImageResource(R.drawable.ic_undo_ablack);
                    mUndoAndNowText.setText(R.string.btn_undo);
                }
                break;

            case ScoutingState.PAUSING: // Now button

                mTimer = calculateCurrentRelativeTime();
                startActivityState(ScoutingState.SCOUTING);
                break;
        }
    }

    /**
     * Toggles the format of the timer, which is shown
     */

    public void onStatusTimerClicked(View view) {
        mTimerIsCountingUp = !mTimerIsCountingUp;
        updateTimerStatusAndProgressBar();
    }

    /**
     * Shows the entry log
     */

    private void onShowEntryLogs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Entry Report/Log")
                .setMessage(EntryFormatter.formatReport(mEntry) + mStatusLog.toString())
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (mActivityState != ScoutingState.STARTING && mPreferences.shouldShowPause()) {
            builder.setNeutralButton(mUsingPauseBetaFeature ? "Hide Pause" : "Show Pause",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUsingPauseBetaFeature = !mUsingPauseBetaFeature;
                            getManagedVibrator().vibrateAction();
                            mPlayAndPauseView.setVisibility(mUsingPauseBetaFeature ? View.VISIBLE : View.GONE);
                        }
                    });
        }
        builder.create().show();
    }

    /**
     * Opens a comments dialog, flags in the future
     */

    private void onCommentsAndFlags() {

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        input.setText(mEntry.getComments());
        input.setSelection(mEntry.getComments().length());
        input.setGravity(Gravity.CENTER);
        input.setHint(R.string.comments_hint);

        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle(R.string.edit_comments)
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEntry.setComments(input.getText().toString()
                                .replaceAll("_", ""));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        Window window = dialog.getWindow();

        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }


    // Utility Methods

    /**
     * Calculates the relative time based on
     * the current time and the starting timestamp
     */
    private int calculateCurrentRelativeTime() {
        return (getCurrentTime() - mStartingTimestamp) % (kTimerLimit + 1);
    }

    /**
     * Calculates whether the counting timer is in approximation with the current time
     */

    private boolean isTimerAtCurrentTime() {
        return Math.abs(mTimer - calculateCurrentRelativeTime()) <= 1;
    }


    // Initialization Methods

    /**
     * Set up fields from specs
     */

    private void setupSpecs() {

        mSpecs = Specs.getInstance();
        if (mSpecs == null) {
            Specs.setInstance(getIntent().getStringExtra(ID.MSG_SPECS_FILE));
            mSpecs = Specs.getInstance();
        }
        mLayouts = mSpecs.getLayouts();
    }

    /**
     * Set misc view fields
     */

    private void setupUI() {

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_scouting);

        mToolbar = findViewById(R.id.my_toolbar);

        mToolbarTeam = findViewById(R.id.toolbar_team);
        mToolbarMatch = findViewById(R.id.toolbar_match);

        setSupportActionBar(mToolbar);

        mTitleBanner = findViewById(R.id.title_banner);
        mTimerStatus = findViewById(R.id.timer_status);

        mStartButton = findViewById(R.id.start_timer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStartButton.setElevation(4);
        }

        mPlayAndPauseImage = findViewById(R.id.play_pause_image);
        mUndoAndNowImage = findViewById(R.id.undo_now_image);
        mPlayAndPauseView = findViewById(R.id.play_pause_container);
        mUndoAndNowView = findViewById(R.id.undo_now_container);
        mPlayAndPauseText = findViewById(R.id.play_pause_text);
        mUndoAndNowText = findViewById(R.id.undo_now_text);

        animate_in.setDuration(kFadeDuration);
        animate_out.setDuration(kFadeDuration);
    }

    /**
     * Set up the progress/seek bars
     */

    private void setupNavigationSliders() {

        mTimeProgress = findViewById(R.id.time_progress);
        mTimeSeeker = findViewById(R.id.time_seeker);

        mTimeProgress.setMax(kTimerLimit);
        mTimeProgress.setProgress(0);

        mTimeSeeker.setMax(kTimerLimit);
        mTimeSeeker.setProgress(0);

        mTimeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mActivityState == ScoutingState.PAUSING) {
                    mTimer = progress;
                    updateTimerStatusAndProgressBar();
                    updateAdjacentTabStates();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Get values from intent(except specs) and initialize the entry model
     */

    private void setupValuesFromIntent() {

        Intent intent = getIntent();

        int matchNumber = intent.getIntExtra(ID.MSG_MATCH_NUMBER, -1);
        int teamNumber = intent.getIntExtra(ID.MSG_TEAM_NUMBER, -1);
        String scoutName = intent.getStringExtra(ID.MSG_SCOUT_NAME);

        String alliance = mSpecs.getAlliance();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mToolbarTeam.setText(alliance.equals("R") || alliance.equals("B") ?
                String.valueOf(teamNumber) : mSpecs.getBoardName());

        String m = "" + matchNumber;
        mToolbarMatch.setText(m);
        mToolbarTeam.setTextColor(
                getResources().getColor(alliance.equals("R") ? R.color.colorRed :
                        (alliance.equals("B") ? R.color.colorBlue :
                                R.color.colorPurple)));

        findViewById(R.id.highlight_bar).getBackground()
                .setColorFilter(getResources()
                                .getColor(alliance.equals("R") ? R.color.colorAlmostRed :
                                        (alliance.equals("B") ? R.color.colorAlmostBlue :
                                                R.color.colorAlmostWhite)),
                        PorterDuff.Mode.MULTIPLY);

        mToolbarTeam.setTypeface(Typeface.SANS_SERIF,
                alliance.equals("R") || alliance.equals("B") ? Typeface.BOLD : Typeface.NORMAL);

        mStatusLog = new StringBuilder();

        pushStatus("\n\n========LOG========");
        pushStatus("Board ID: " + mSpecs.getSpecsId());
        pushStatus("");

        mEntry = new Entry(matchNumber, teamNumber, scoutName, this);
    }

    /**
     * Sets up the pager with adapters and event listeners
     */

    private void setupPager() {

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScoutingTabsPagerAdapter(
                getSupportFragmentManager(), mSpecs.getLayouts().size(), mPager);

        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTab = position;
                updateCurrentTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * Initializes states or restore it from savedInstanceState
     */

    private void initStates(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            mStartingTimestamp = getCurrentTime();
        } else {
            mStartingTimestamp = savedInstanceState.getInt(ID.INSTANCE_START_TIME);
        }

        mEntry.setStartingTimestamp(mStartingTimestamp);

        startActivityState(ScoutingState.STARTING);
    }


    // State Updater Methods

    /**
     * Sets the current activity state and update views and timer
     *
     * @param state the activity state to start
     */

    private void startActivityState(int state) {

        if (state == ScoutingState.SCOUTING && (mTimerIsRunning || mTimer >= kTimerLimit)) {
            return;
        }

        mActivityState = state;

        switch (mActivityState) {
            case ScoutingState.STARTING:
                setStartingNavToolbox();
                break;

            case ScoutingState.SCOUTING:
                setScoutingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorWhite));
                getManagedVibrator().vibrateStart();
                mTimerUpdater.run();
                break;

            case ScoutingState.PAUSING:
                setPausingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorAlmostYellow));
                break;
        }
    }

    /**
     * Hide the navigation buttons on start
     */

    private void setStartingNavToolbox() {
        mPlayAndPauseView.setVisibility(View.GONE);
        mUndoAndNowView.setVisibility(View.GONE);
        mTimeSeeker.setVisibility(View.GONE);
        mTimeProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Toggles image icons and visibility for scouting state
     */

    private void setScoutingNavToolbox() {

        mPlayAndPauseView.setVisibility(mUsingPauseBetaFeature ? View.VISIBLE : View.GONE);

        mUndoAndNowView.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mPlayAndPauseImage.setImageResource(R.drawable.ic_pause_ablack);
        mPlayAndPauseText.setText(R.string.btn_pause);

        if (isTimerAtCurrentTime()) {
            mUndoAndNowImage.setImageResource(R.drawable.ic_undo_ablack);
            mUndoAndNowText.setText(R.string.btn_undo);
        } else {
            mUndoAndNowImage.setImageResource(R.drawable.ic_skip_next_red);
            mUndoAndNowText.setText(R.string.btn_now);
        }

        mTimeSeeker.setVisibility(View.GONE);
        mTimeProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Toggles image icons and visibility for pausing state
     */

    private void setPausingNavToolbox() {

        mPlayAndPauseView.setVisibility(View.VISIBLE);
        mUndoAndNowView.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mPlayAndPauseImage.setImageResource(R.drawable.ic_play_arrow_ablack);
        mPlayAndPauseText.setText(R.string.btn_resume);
        mUndoAndNowImage.setImageResource(R.drawable.ic_skip_next_red);
        mUndoAndNowText.setText(R.string.btn_now);

        mTimeSeeker.setVisibility(View.VISIBLE);
        mTimeProgress.setVisibility(View.GONE);
    }

    /**
     * Updates the activity's background colour
     */

    private void setBackgroundColour(int colour) {
        findViewById(android.R.id.content).setBackgroundColor(colour);
    }

    /**
     * Change the Title Banner with a fade in/fade out animation
     *
     * @param title the title to change
     */

    private void setAnimatedTitleBanner(final String title) {

        if (!mTitleBanner.getText().toString().isEmpty()) {

            animate_out.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTitleBanner.setText(title);
                    mTitleBanner.startAnimation(animate_in);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mTitleBanner.startAnimation(animate_out);

        } else {
            mTitleBanner.setText(title);
            mTitleBanner.startAnimation(animate_in);
        }

    }

    /**
     * Updates the current tab as well as the title banner
     */

    private void updateCurrentTab() {

        String title = "";

        if (mCurrentTab >= 0 && mCurrentTab < mLayouts.size()) {
            title = mLayouts.get(mCurrentTab).getTitle();
        } else if (mCurrentTab == mLayouts.size()) {
            title = "QR Code";
            mPagerAdapter.getTabAt(mCurrentTab).updateTabState();
        }

        setAnimatedTitleBanner(title);

        if (mPager.getCurrentItem() != mCurrentTab) {
            mPager.setCurrentItem(mCurrentTab, true);
        }
    }

    /**
     * Updates the state on the views on the page to match undo
     * and navigation.
     */

    private void updateAdjacentTabStates() {

        if (mCurrentTab != 0) {
            mPagerAdapter.getTabAt(mCurrentTab - 1).updateTabState();
        }

        mPagerAdapter.getTabAt(mCurrentTab).updateTabState();

        if (mCurrentTab != mPagerAdapter.getCount() - 1) {
            mPagerAdapter.getTabAt(mCurrentTab + 1).updateTabState();
        }
    }

    /**
     * Reflect the value of mTimer on the timer view and seek bars
     */

    private void updateTimerStatusAndProgressBar() {

        String status;
        int time;

        if (mTimerIsCountingUp) {
            time = mTimer;
            mTimerStatus.setTypeface(null, Typeface.BOLD);
        } else {
            time = mTimer <= kAutonomousTime ? kAutonomousTime - mTimer : kTimerLimit - mTimer;
            mTimerStatus.setTypeface(null, Typeface.NORMAL);
        }

        status = String.valueOf(time);

        char[] placeholder = new char[kTotalTimerDigits - status.length()];
        String filled_status = new String(placeholder).replace("\0", "0") + status;

        mTimerStatus.setText(filled_status);

        mTimerStatus.setTextColor(getResources().getColor(mTimer <= kAutonomousTime ?
                R.color.colorAutoYellow :
                R.color.colorTeleOpGreen));

        mTimeProgress.setProgress(mTimer);
        mTimeSeeker.setProgress(mTimer);
    }

    /**
     * Attempts to undo the previous action and vibrates
     * if the undo has been successful
     */

    private void attemptUndo() {
        DataConstant dc = mEntry.undo();
        if (dc == null) {
            pushStatus("Cannot Undo @" + mTimer + "s");
        } else {
            pushStatus("Undo \'" + dc.getLabel() + "\'");
            getManagedVibrator().vibrateAction();
            updateAdjacentTabStates();
        }
    }
}