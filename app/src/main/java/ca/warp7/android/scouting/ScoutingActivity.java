package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


/*
CODE ORGANIZED BY FOLLOWING SECTIONS

1. State Variables
2. UI elements
3. System Services
4. Data Model Variables
5. Timer Process
6. Animation Objects
7. Activity Methods
8. ScoutingActivityListener methods
9. Misc. Event Handlers
10.Utility Methods
11.Initialization Methods
12.State Updater Methods
13.Inner Class and Enum
14.Static Fields

 */


/**
 * <p>The Scouting Activity -- responsible for navigation,
 * Setting up the interface, and receive actions from inputs.</p>
 *
 * <p>
 * @see InputsFragment
 * @see ScoutingActivityListener
 * @see Entry
 * </p>
 *
 * @author Team 865
 */

public class ScoutingActivity
        extends AppCompatActivity
        implements ScoutingActivityListener {


    // State Variables

    private ActivityState mActivityState;

    private int mTimer = 0;
    private int mCurrentTab = 0;
    private int mLastRecordedTime = -1;
    private int mLastPausedTime = -1;
    private int mStartingTimestamp = 0;


    // UI elements

    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private ConstraintLayout mNavToolbox;

    private TextView mTitleBanner;
    private TextView mTimerStatus;

    private ProgressBar mTimeProgress;
    private SeekBar mTimeSeeker;

    private TextView mStartButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mUndoSkipButton;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    // System Services

    private Handler mTimeHandler;
    private Vibrator mVibrator;


    // Data Model Variables

    private Specs mSpecs;
    private Entry mEntry;
    private ArrayList<Specs.Layout> mLayouts;

    private StringBuilder mStatusLog;


    // Timer Process

    private Runnable mTimerUpdater = new Runnable() {
        @Override
        public void run() {

            if (mActivityState != ActivityState.SCOUTING) {
                return; // Check if activity is paused
            }

            updateTimerStatusAndSeeker();

            mTimer++;

            if (mTimer <= kTimerLimit) { // Check if match ended
                mTimeHandler.postDelayed(mTimerUpdater, 1000);
            }
        }
    };


    // Animation Objects

    private final Animation animate_in = new AlphaAnimation(0.0f, 1.0f);
    private final Animation animate_out = new AlphaAnimation(1.0f, 0.0f);


    // Activity Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mTimeHandler = new Handler();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupSpecs();
        setupUI();
        setupNavigationSliders();
        setupValuesFromIntent();
        setupPager();

        updateTimerStatusAndSeeker();
        updateCurrentTab();

        if (savedInstanceState == null) {
            mStartingTimestamp = getCurrentTime();
        } else {
            mStartingTimestamp = savedInstanceState.getInt(ID.INSTANCE_STATE_START_TIME);
        }

        mEntry.setStartingTimestamp(mStartingTimestamp);

        startActivityState(ActivityState.STARTING);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ID.INSTANCE_STATE_START_TIME, mStartingTimestamp);
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

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_details: // Info button

                new AlertDialog.Builder(this)
                        .setTitle(mActionBar.getTitle() + " (" + mSpecs.getBoardName() + ")")
                        .setMessage(mStatusLog.toString())
                        .create()
                        .show(); // Show the log in a dialog

                return true;

            case R.id.menu_done: // Check mark button

                mEntry.clean(); // Remove the undoes

                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(ID.MSG_PRINT_DATA, EntryFormatter.formatReport(mEntry));
                intent.putExtra(ID.MSG_ENCODE_DATA, EntryFormatter.formatEncode(mEntry));
                startActivity(intent);

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


    // ScoutingActivityListener methods

    @Override
    public int getCurrentRelativeTime() {
        return mTimer;
    }

    @Override
    public Handler getHandler() {
        return mTimeHandler;
    }

    @Override
    public Vibrator getVibrator() {
        return mVibrator;
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
        return mActivityState == ActivityState.STARTING;
    }

    @Override
    public void pushCurrentTimeAsValue(int type, int state_flag) {
        mEntry.push(type, mTimer, state_flag);
        mLastRecordedTime = mTimer;
    }

    @Override
    public void pushStatus(String status) {
        // mActionBar.setSubtitle(status.replace("{t}", String.valueOf(mTimer)));
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

        startActivityState(ActivityState.SCOUTING);
        updateTabInputStates();

        pushStatus("Timer Started");

    }

    /**
     * Event Handler for the play/pause button,
     * which updates the activity state
     */

    public void onPlayPauseClicked(View view) {

        switch (mActivityState) {

            case SCOUTING: // Pause button
                startActivityState(ActivityState.PAUSING);
                break;

            case PAUSING: // Play button
                startActivityState(ActivityState.SCOUTING);
                break;
        }
    }

    /**
     * Event Handler for the undo/skip button
     */

    public void onUndoSkipClicked(View view) {

        switch (mActivityState) {

            case SCOUTING: // Undo button
                Specs.DataConstant dc = mEntry.undo();
                if (dc == null) {
                    pushStatus("Nothing can be undone");
                } else {
                    pushStatus("Undo \'" + dc.getLabel() + "\'");
                    mVibrator.vibrate(20);
                    updateTabInputStates();
                }
                break;

            case PAUSING: // Skip button

                mTimer = (getCurrentTime() - mStartingTimestamp) % (kTimerLimit + 1);

                startActivityState(ActivityState.SCOUTING);

                break;
        }
    }


    // Utility Methods

    /**
     * @return The current time in seconds
     */
    private int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }


    // Initialization Methods

    /**
     * Set up fields from specs
     */

    private void setupSpecs() {

        mSpecs = Specs.getInstance();

        if (mSpecs == null) { // Fixes singlet not loaded issue
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

        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setNavigationContentDescription(R.string.menu_close);

        setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();

        mNavToolbox = findViewById(R.id.nav_toolbox);

        mTitleBanner = findViewById(R.id.title_banner);
        mTimerStatus = findViewById(R.id.timer_status);

        mStartButton = findViewById(R.id.start_timer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStartButton.setElevation(4);
        }

        mPlayPauseButton = findViewById(R.id.play_pause);
        mUndoSkipButton = findViewById(R.id.undo_skip);

        String alliance = mSpecs.getAlliance();

        mToolbar.setTitleTextColor(
                alliance.equals("R") ? kRedAllianceColour :
                        (alliance.equals("B") ? kBlueAllianceColour : kNeutralAllianceColour));

        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.colorAlmostBlack));

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
                if (fromUser && mActivityState == ActivityState.PAUSING) {
                    mTimer = progress;
                    updateTimerStatusAndSeeker();
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

        String a = mSpecs.getAlliance();

        if (a.equals("R") || a.equals("B")) {
            mActionBar.setTitle("Q" + matchNumber + " — " + teamNumber);
        } else {
            mActionBar.setTitle(mSpecs.getBoardName());
        }

        mStatusLog = new StringBuilder(); // initialize the log
        pushStatus("Scouting Started");

        // NOTE Entry uses Specs so must ensure specs instance exists
        mEntry = new Entry(matchNumber, teamNumber, scoutName, this);
    }

    /**
     * Sets up the pager with adapters and event listeners
     */

    private void setupPager() {

        mPager = findViewById(R.id.pager);

        mPagerAdapter = new InputTabsPagerAdapter(getSupportFragmentManager());

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


    // State Updater Methods

    /**
     * Sets the current activity state and update views and timer
     *
     * @param state the activity state to start
     */

    private void startActivityState(ActivityState state) {

        mActivityState = state;

        switch (mActivityState) {

            case STARTING:

                setStartingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorStartBlue));

                break;

            case SCOUTING:

                // Make sure there's only one timer
                if (mLastPausedTime == getCurrentTime()) {
                    mActivityState = ActivityState.PAUSING;
                    return;
                }

                setScoutingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorWhite));

                mVibrator.vibrate(kStartVibration, -1); // Vibrate to signal start
                mTimerUpdater.run();

                break;

            case PAUSING:

                mLastPausedTime = getCurrentTime();

                setPausingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorReviewYellow));

                break;

        }
    }

    /**
     * Hide the navigation buttons on start
     */

    private void setStartingNavToolbox() {

        mPlayPauseButton.setVisibility(View.GONE);
        mUndoSkipButton.setVisibility(View.GONE);

        mTimeSeeker.setVisibility(View.GONE);
        mTimeProgress.setVisibility(View.GONE);
    }

    /**
     * Toggles image icons and visibility for scouting state
     */

    private void setScoutingNavToolbox() {

        mPlayPauseButton.setVisibility(View.VISIBLE);
        mUndoSkipButton.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mPlayPauseButton.setImageResource(R.drawable.ic_pause_ablack);
        mUndoSkipButton.setImageResource(R.drawable.ic_undo);

        mTimeSeeker.setVisibility(View.GONE);
        mTimeProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Toggles image icons and visibility for pausing state
     */

    private void setPausingNavToolbox() {

        mPlayPauseButton.setVisibility(View.VISIBLE);
        mUndoSkipButton.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_ablack);
        mUndoSkipButton.setImageResource(R.drawable.ic_skip_next_ablack);

        mTimeSeeker.setVisibility(View.VISIBLE);
        mTimeProgress.setVisibility(View.GONE);
    }


    /**
     * Updates the activity's background colour
     */

    private void setBackgroundColour(int colour) {
        mToolbar.setBackgroundColor(colour);
        mNavToolbox.setBackgroundColor(colour);
        mPager.setBackgroundColor(colour);
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

        if (!mLayouts.isEmpty() && mCurrentTab >= 0 && mCurrentTab < mLayouts.size()) {

            setAnimatedTitleBanner(mLayouts.get(mCurrentTab).getTitle());

            if (mPager.getCurrentItem() != mCurrentTab) {
                mPager.setCurrentItem(mCurrentTab, true);
            }
        }
    }

    /**
     * Updates the state on the views on the page to match undo
     * and navigation. Current implementation calls the PageAdapter
     * to destroy all instantiated tabs and recreate them (also in
     * InputTabsPagerAdapter). Plans to upgrade to getting the specific
     * fragments and update them (in InputsFragment)
     */

    private void updateTabInputStates() {
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Reflect the value of mTimer on the timer view and seek bars
     */

    private void updateTimerStatusAndSeeker() {

        String d;

        int time = mTimer <= kAutonomousTime ? kAutonomousTime - mTimer : kTimerLimit - mTimer;

        if (mTimer < kTimerLimit) {
            d = String.valueOf(time);
            mTimerStatus.setTypeface(null, Typeface.NORMAL);
        } else {
            d = "FIN";
            mTimerStatus.setTypeface(null, Typeface.BOLD);
        }

        char[] placeholder = new char[kTotalTimerDigits - d.length()];
        String status = new String(placeholder).replace("\0", "0") + d;

        mTimerStatus.setText(status);

        mTimerStatus.setTextColor(mTimer <= kAutonomousTime ?
                kAutonomousColour : mTimer < kTimerLimit ?
                kTeleOpColour : kFinishedColour);

        mTimeProgress.setProgress(mTimer);
        mTimeSeeker.setProgress(mTimer);
    }


    // Inner Class and Enum

    /**
     * Adapter that returns the proper fragment as pages are navigated
     */

    private class InputTabsPagerAdapter
            extends FragmentPagerAdapter {

        InputTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return InputsFragment.createInstance(position);
        }

        @Override
        public int getCount() {
            return mSpecs.getLayouts().size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            // Recreate all fragments when notifyDataSetChanged is called
            return POSITION_NONE;
        }

        @SuppressWarnings("unused")
        InputsFragment getFragment(int index) {
        /* Fragments are cached in Adapter, so instantiateItem will
           return the cached one (if any) or a new instance if necessary.
         */
            return (InputsFragment) instantiateItem(mPager, index);
        }

    }


    /**
     * Stages/states of the activity to trigger different behaviours
     */

    enum ActivityState {
        STARTING, SCOUTING, PAUSING
    }


    // Static Fields

    static final int kTimerLimit = 150;
    static final int kAutonomousTime = 15;
    static final int kFadeDuration = 100;
    static final int kTotalTimerDigits = 3;

    static final int kBlueAllianceColour = 0xFF0000FF;
    static final int kRedAllianceColour = 0xFFFF0000;
    static final int kNeutralAllianceColour = 0xFF808080;
    static final int kAutonomousColour = 0xFFCC9900;
    static final int kTeleOpColour = 0xFF006633;
    static final int kFinishedColour = 0xFFFF0000;

    static final long[] kStartVibration = new long[]{0, 20, 30, 20};
}