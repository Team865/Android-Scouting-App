package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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

/**
 * The Scouting Activity -- responsible for navigation,
 * Setting up the interface, and receive actions from inputs
 */

public class ScoutingActivity
        extends AppCompatActivity
        implements ScoutingActivityListener {


    // Variables to store the various states of the activity

    private ActivityState mActivityState;

    private int mTimer = 0;
    private int mCurrentTab = 0;
    private int mLastRecordedTime = -1;
    private int mLastPausedTime = -1;
    private int mStartingTimestamp = 0;


    // UI elements

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mTitleBanner;
    private TextView mTimerStatus;

    private ProgressBar mTimeProgress;
    private SeekBar mTimeSeeker;

    private ImageButton mStart, mPlayPause, mUndoSkip;

    private ViewPager mPager;


    // System Services

    private Handler mTimeHandler;
    private Vibrator mVibrator;


    // App Data Model

    private Specs mSpecs;
    private Encoder mEncoder;

    private ArrayList<Specs.Layout> mLayouts;


    // Runnable Process

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
        updateLayout();

        if (savedInstanceState == null) {
            mStartingTimestamp = getCurrentTime();
        } else {
            mStartingTimestamp = savedInstanceState.getInt(ID.INSTANCE_STATE_START_TIME);
        }

        startActivityState(ActivityState.STARTING); // Start Scouting
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

            case R.id.menu_details:
                return true;

            case R.id.menu_done:
                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(ID.MSG_PRINT_DATA, mEncoder.format());
                intent.putExtra(ID.MSG_ENCODE_DATA, mEncoder.encode());
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
    public Handler getHandler() {
        return mTimeHandler;
    }

    @Override
    public Vibrator getVibrator() {
        return mVibrator;
    }

    @Override
    public Encoder getEncoder() {
        return mEncoder;
    }

    @Override
    public boolean canUpdateTime() {
        return mTimer <= kTimerLimit && mLastRecordedTime != mTimer;
    }

    @Override
    public void pushCurrentTimeAsValue(int t, int s) {
        mEncoder.push(t, mTimer, s);
        mLastRecordedTime = mTimer;
    }

    @Override
    public void pushStatus(String status) {
        mActionBar.setSubtitle(status.replace("{t}", String.valueOf(mTimer)));
    }


    // Utility Methods

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

        mTitleBanner = findViewById(R.id.title_banner);
        mTimerStatus = findViewById(R.id.timer_status);

        mStart = findViewById(R.id.start_timer);
        mPlayPause = findViewById(R.id.play_pause);
        mUndoSkip = findViewById(R.id.undo_skip);

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
     * Get values from intent(except specs) and initialize the encoder
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

        pushStatus("Scouting Started");

        mEncoder = new Encoder(matchNumber, teamNumber, scoutName);

    }

    /**
     * Sets up the pager with adapters and event listeners
     */

    private void setupPager() {

        mPager = findViewById(R.id.pager);

        PagerAdapter mPagerAdapter = new InputTabsPagerAdapter(getSupportFragmentManager());

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
                updateLayout();
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
                // Add more stuff here

                int blue = getResources().getColor(R.color.colorStartBlue);

                mToolbar.setBackgroundColor(blue);
                mPlayPause.setVisibility(View.GONE);
                mUndoSkip.setVisibility(View.GONE);

                break;

            case SCOUTING:

                if (mLastPausedTime == getCurrentTime()) {
                    // Make sure there's only one timer
                    mActivityState = ActivityState.PAUSING;
                    return;
                }

                mPlayPause.setVisibility(View.VISIBLE);
                mUndoSkip.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);

                mPlayPause.setImageResource(R.drawable.ic_pause_ablack);
                mUndoSkip.setImageResource(R.drawable.ic_undo);

                mTimeSeeker.setVisibility(View.GONE);
                mTimeProgress.setVisibility(View.VISIBLE);

                int white = getResources().getColor(R.color.colorPrimary);

                mToolbar.setBackgroundColor(white);

                mVibrator.vibrate(kStartVibration, -1); // Vibrate to signal start
                mTimerUpdater.run();

                break;

            case PAUSING:

                mLastPausedTime = getCurrentTime();

                mPlayPause.setVisibility(View.VISIBLE);
                mUndoSkip.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);

                mPlayPause.setImageResource(R.drawable.ic_play_arrow_ablack);
                mUndoSkip.setImageResource(R.drawable.ic_skip_next_ablack);

                mTimeSeeker.setVisibility(View.VISIBLE);
                mTimeProgress.setVisibility(View.GONE);

                int yellow = getResources().getColor(R.color.colorReviewYellow);

                mToolbar.setBackgroundColor(yellow);

                break;

        }
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

    private void updateLayout() {

        if (!mLayouts.isEmpty() && mCurrentTab >= 0 && mCurrentTab < mLayouts.size()) {

            setAnimatedTitleBanner(mLayouts.get(mCurrentTab).getTitle());

            if (mPager.getCurrentItem() != mCurrentTab) {
                mPager.setCurrentItem(mCurrentTab, true);
            }
        }
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

        String status = new String(new char[3 - d.length()]).replace("\0", "0") + d;

        mTimerStatus.setText(status);
        mTimerStatus.setTextColor(mTimer <= kAutonomousTime ?
                kAutonomousColour : mTimer < kTimerLimit ?
                kTeleOpColour : kFinishedColour);

        mTimeProgress.setProgress(mTimer);
        mTimeSeeker.setProgress(mTimer);
    }


    // Misc. Event Handlers

    /**
     * Handles start of entry. Only called once
     */

    public void onStartScouting(View view) {
        mStartingTimestamp = getCurrentTime();
        startActivityState(ActivityState.SCOUTING);
        pushStatus("Timer Started");
    }

    /**
     * Event Handler for the play/pause button, which updates the activity state
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
                Specs.DataConstant dc = mEncoder.undo();
                if (dc == null) {
                    pushStatus("Nothing can be undone");
                } else {
                    pushStatus("Undo \'" + dc.getLabel() + "\'");
                    mVibrator.vibrate(20);
                }
                break;

            case PAUSING: // Skip button

                mTimer = (getCurrentTime() - mStartingTimestamp) % (kTimerLimit + 1);

                startActivityState(ActivityState.SCOUTING);

                break;
        }
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

    static final int kBlueAllianceColour = 0xFF0000FF;
    static final int kRedAllianceColour = 0xFFFF0000;
    static final int kNeutralAllianceColour = 0xFF808080;
    static final int kAutonomousColour = 0xFFCC9900;
    static final int kTeleOpColour = 0xFF006633;
    static final int kFinishedColour = 0xFFFF0000;

    static final long[] kStartVibration = new long[]{0, 30, 30, 30};
}