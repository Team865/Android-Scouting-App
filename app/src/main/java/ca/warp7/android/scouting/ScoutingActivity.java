package ca.warp7.android.scouting;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import ca.warp7.android.scouting.abstraction.AbstractActionVibrator;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.abstraction.ScoutingTab;
import ca.warp7.android.scouting.components.QRFragment;
import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.Entry;
import ca.warp7.android.scouting.model.EntryFormatter;
import ca.warp7.android.scouting.model.ScoutingLayout;
import ca.warp7.android.scouting.model.Specs;
import ca.warp7.android.scouting.resources.ManagedPreferences;
import ca.warp7.android.scouting.widgets.CenteredControlLayout;
import ca.warp7.android.scouting.widgets.Checkbox;
import ca.warp7.android.scouting.widgets.ChoicesButton;
import ca.warp7.android.scouting.widgets.CountedInputControlLayout;
import ca.warp7.android.scouting.widgets.DurationButton;
import ca.warp7.android.scouting.widgets.LabeledControlLayout;
import ca.warp7.android.scouting.widgets.TimerButton;
import ca.warp7.android.scouting.widgets.UndefinedInputsIndicator;


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

public class ScoutingActivity
        extends AppCompatActivity
        implements ScoutingActivityListener {


    // Beta Feature Variables

    private boolean mUsingPauseBetaFeature;


    // State Variables

    private ActivityState mActivityState;

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

    private ViewGroup mNavToolbox;

    private TextView mTitleBanner;
    private TextView mTimerStatus;

    private ProgressBar mTimeProgress;
    private SeekBar mTimeSeeker;

    private TextView mStartButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mUndoSkipButton;

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

            if (mActivityState != ActivityState.SCOUTING) {
                mTimerIsRunning = false;
                return;
            }

            mTimerIsRunning = true;

            updateTimerStatusAndProgressBar();
            updateAdjacentTabStates();
            mTimer++;

            if (mTimer <= kTimerLimit) { // Check if match ended
                mTimeHandler.postDelayed(mTimerUpdater, 1000);
            } else {
                mTimerIsRunning = false;
                if (mUsingPauseBetaFeature) {
                    startActivityState(ActivityState.PAUSING);
                }
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

        outState.putSerializable(ID.INSTANCE_ACTIVITY_STATE, mActivityState);
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
        return mActivityState == ActivityState.STARTING;
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

        startActivityState(ActivityState.SCOUTING);
        updateAdjacentTabStates();

        pushStatus("Timer Started\n");

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

                if (isTimerAtCurrentTime()) {
                    attemptUndo();
                } else {
                    mTimer = calculateCurrentRelativeTime();
                    getManagedVibrator().vibrateStart();
                    mUndoSkipButton.setImageResource(R.drawable.ic_undo_ablack);
                }

                break;

            case PAUSING: // Skip button

                mTimer = calculateCurrentRelativeTime();
                startActivityState(ActivityState.SCOUTING);
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

        if (mActivityState != ActivityState.STARTING && mPreferences.shouldShowPause()) {
            builder.setNeutralButton(mUsingPauseBetaFeature ? "Hide Pause" : "Show Pause",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUsingPauseBetaFeature = !mUsingPauseBetaFeature;
                            getManagedVibrator().vibrateAction();
                            mPlayPauseButton.setVisibility(mUsingPauseBetaFeature ? View.VISIBLE : View.GONE);
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

                        // Get the comment and make sure underscore isn't used

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
     * @return The current time in seconds
     */

    private int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

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

        mToolbarTeam = findViewById(R.id.toolbar_team);
        mToolbarMatch = findViewById(R.id.toolbar_match);

        setSupportActionBar(mToolbar);

        mNavToolbox = findViewById(R.id.nav_toolbox);

        mTitleBanner = findViewById(R.id.title_banner);
        mTimerStatus = findViewById(R.id.timer_status);

        mStartButton = findViewById(R.id.start_timer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStartButton.setElevation(4);
        }

        mPlayPauseButton = findViewById(R.id.play_pause);
        mUndoSkipButton = findViewById(R.id.undo_skip);

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
                alliance.equals("R") ? kRedAllianceColour :
                        (alliance.equals("B") ? kBlueAllianceColour : kNeutralAllianceColour));

        findViewById(R.id.highlight_bar).getBackground()
                .setColorFilter(getResources()
                                .getColor(alliance.equals("R") ? R.color.colorAlmostRed :
                                        (alliance.equals("B") ? R.color.colorAlmostBlue :
                                                R.color.colorAlmostWhite)),
                        PorterDuff.Mode.MULTIPLY);

        mToolbarTeam.setTypeface(Typeface.SANS_SERIF,
                alliance.equals("R") || alliance.equals("B") ? Typeface.BOLD : Typeface.NORMAL);

        mStatusLog = new StringBuilder(); // initialize the log

        pushStatus("\n\n========LOG========");
        pushStatus("Board ID: " + mSpecs.getSpecsId());
        pushStatus("");

        // NOTE Entry uses Specs so must ensure specs instance exists
        mEntry = new Entry(matchNumber, teamNumber, scoutName, this);
    }

    /**
     * Sets up the pager with adapters and event listeners
     */

    private void setupPager() {

        mPager = findViewById(R.id.pager);

        mPagerAdapter = new ScoutingTabsPagerAdapter(getSupportFragmentManager());

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

        startActivityState(ActivityState.STARTING);
    }


    // State Updater Methods

    /**
     * Sets the current activity state and update views and timer
     *
     * @param state the activity state to start
     */

    private void startActivityState(ActivityState state) {

        if (state == ActivityState.SCOUTING &&
                (mTimerIsRunning || mTimer >= kTimerLimit)) {
            return; // Return if there is a timer running
        }

        mActivityState = state;

        switch (mActivityState) {

            case STARTING:

                setStartingNavToolbox();

                break;

            case SCOUTING:

                setScoutingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorWhite));

                getManagedVibrator().vibrateStart(); // Vibrate to signal start
                mTimerUpdater.run();

                break;

            case PAUSING:

                setPausingNavToolbox();
                setBackgroundColour(getResources().getColor(R.color.colorAlmostYellow));

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
        mTimeProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Toggles image icons and visibility for scouting state
     */

    private void setScoutingNavToolbox() {

        // mPlayPauseButton.setVisibility(View.VISIBLE);

        mPlayPauseButton.setVisibility(mUsingPauseBetaFeature ? View.VISIBLE : View.GONE);

        mUndoSkipButton.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.GONE);

        mPlayPauseButton.setImageResource(R.drawable.ic_pause_ablack);

        if (isTimerAtCurrentTime()) {
            mUndoSkipButton.setImageResource(R.drawable.ic_undo_ablack);
        } else {
            mUndoSkipButton.setImageResource(R.drawable.ic_skip_next_red);
        }

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
        mUndoSkipButton.setImageResource(R.drawable.ic_skip_next_red);

        mTimeSeeker.setVisibility(View.VISIBLE);
        mTimeProgress.setVisibility(View.GONE);
    }

    /**
     * Updates the activity's background colour
     */

    private void setBackgroundColour(int colour) {
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

        mTimerStatus.setTextColor(mTimer <= kAutonomousTime ?
                kAutonomousColour : kTeleOpColour);

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


    // Inner Class and Enum

    /**
     * Adapter that returns the proper fragment as pages are navigated
     */

    private class ScoutingTabsPagerAdapter
            extends FragmentPagerAdapter {

        ScoutingTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < mSpecs.getLayouts().size()) {
                return ScoutingInputsFragment.createInstance(position);
            } else {
                return QRFragment.createInstance();
            }
        }

        @Override
        public int getCount() {
            return mSpecs.getLayouts().size() + 1;
        }

        ScoutingTab getTabAt(int index) {
            return (ScoutingTab) instantiateItem(mPager, index);
        }


    }

    /**
     * The fragment that is shown in the biggest portion
     * of ScoutingActivity -- it manages a TableLayout that
     * contains the views from InputControls defined in Specs
     *
     * @author Team 865
     */

    public static class ScoutingInputsFragment
            extends Fragment implements ScoutingTab {


        private ScoutingActivityListener mListener;

        private TableLayout mInputTable;

        private Specs mSpecs;
        private ScoutingLayout mLayout;


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            int tabNumber = getArguments() != null ? getArguments().getInt("tab") : -1;

            mSpecs = Specs.getInstance();

            if (mSpecs == null) {
                Activity activity = getActivity();
                if (activity != null) {
                    Specs.setInstance(activity.getIntent().getStringExtra(ID.MSG_SPECS_FILE));
                    mSpecs = Specs.getInstance();
                }
            }

            mLayout = mSpecs.getLayouts().get(tabNumber);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_inputs, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mInputTable = view.findViewById(R.id.input_table);

            if (mSpecs != null) {
                layoutTable();
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            if (context instanceof ScoutingActivityListener) {
                mListener = (ScoutingActivityListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement ScoutingActivityListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        /**
         * Creates a view from its definition
         *
         * @param dc       the data constant
         * @param idIfNull the display value if control is undefined
         * @return a matching View from InputControls
         */

        private View createControlFromDataConstant(DataConstant dc, String idIfNull) {

            if (dc == null) {
                return new UndefinedInputsIndicator(getContext(), idIfNull, mListener);
            }

            switch (dc.getType()) {
                case DataConstant.TIMESTAMP:
                    //return new InputControls.TimerButton(getContext(), dc, mListener);
                    return new CountedInputControlLayout(getContext(), dc, mListener,
                            new TimerButton(getContext(), dc, mListener));

                case DataConstant.CHECKBOX:
                    return new CenteredControlLayout(getContext(), dc, mListener,
                            new Checkbox(getContext(), dc, mListener));

                case DataConstant.DURATION:
                    return new DurationButton(getContext(), dc, mListener);


                case DataConstant.RATING:

                    return new LabeledControlLayout(getContext(), dc, mListener,
                            new ca.warp7.android.scouting.widgets.SeekBar(getContext(), dc, mListener));

                case DataConstant.CHOICE:

                    return new LabeledControlLayout(getContext(), dc, mListener,
                            new ChoicesButton(getContext(), dc, mListener));

                default:
                    return new UndefinedInputsIndicator(getContext(), dc.getLabel(), mListener);
            }
        }

        /**
         * Get a specific view by its ID and its span in the table
         *
         * @return the specified view with added layout
         */

        private View createControlFromIdAndSpan(String id, int span) {
            DataConstant dc = mSpecs.getDataConstantByStringID(id);

            View view = createControlFromDataConstant(dc, id);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT);

            lp.span = span;
            lp.width = 0;

            view.setLayoutParams(lp);

            return view;
        }

        /**
         * Layouts a row in the table
         *
         * @param fieldRow an array of identifiers
         */

        private void layoutRow(String[] fieldRow) {
            TableRow tr = new TableRow(getContext());

            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

            if (fieldRow.length == 1) {
                tr.addView(createControlFromIdAndSpan(fieldRow[0], 2));

            } else {
                for (String fieldID : fieldRow) {
                    tr.addView(createControlFromIdAndSpan(fieldID, 1));
                }
            }

            mInputTable.addView(tr);
        }

        /**
         * Get the layout and create the entire table
         */

        private void layoutTable() {

            List<String[]> fields = mLayout.getFields();
            mInputTable.setWeightSum(fields.size());


            for (String[] fieldRow : fields) {
                layoutRow(fieldRow);
            }
        }

        @Override
        public void updateTabState() {

            if (mInputTable != null) {
                for (int i = 0; i < mInputTable.getChildCount(); i++) {
                    View child = mInputTable.getChildAt(i);
                    if (child instanceof TableRow) {
                        TableRow row = (TableRow) child;
                        for (int j = 0; j < row.getChildCount(); j++) {
                            View view = row.getChildAt(j);
                            if (view instanceof BaseInputControl) {
                                ((BaseInputControl) view).updateControlState();
                            }
                        }
                    }
                }
            }
        }

        /**
         * Creates an fragment instance
         *
         * @param currentTab the tab to create the instance on
         * @return the created instance
         */

        static ScoutingInputsFragment createInstance(int currentTab) {
            ScoutingInputsFragment f = new ScoutingInputsFragment();

            Bundle args = new Bundle();
            args.putInt("tab", currentTab);

            f.setArguments(args);
            return f;
        }
    }


    /**
     * Stages/states of the activity to trigger different behaviours
     */

    enum ActivityState {
        STARTING, SCOUTING, PAUSING
    }


    // Static Fields

    private static final int kTimerLimit = 150;
    private static final int kAutonomousTime = 15;
    private static final int kFadeDuration = 100;
    private static final int kTotalTimerDigits = 3;

    private static final int kBlueAllianceColour = 0xFF0000FF;
    private static final int kRedAllianceColour = 0xFFFF0000;
    private static final int kNeutralAllianceColour = 0xFFFF00FF;
    private static final int kAutonomousColour = 0xFFCC9900;
    private static final int kTeleOpColour = 0xFF006633;
}