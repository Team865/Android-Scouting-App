package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class ScoutingActivity
        extends AppCompatActivity
        implements InputControls.ActivityListener {

    Handler mTimeHandler;
    Vibrator mVibrator;

    ActionBar mActionBar;
    TextView mTitleBanner;
    TextView mTimerStatus;
    ProgressBar mTimeProgress;
    SeekBar mTimeSeeker;

    ViewPager mPager;
    PagerAdapter mPagerAdapter;

    int mTimer = 0;
    int mCurrentTab = 0;
    int mLastRecordedTime = -1;

    Specs mSpecs;
    Encoder mEncoder;

    ArrayList<Specs.Layout> mLayouts;

    final Animation animate_in = new AlphaAnimation(0.0f, 1.0f);
    final Animation animate_out = new AlphaAnimation(1.0f, 0.0f);


    Runnable timerUpdater = new Runnable() {
        @Override
        public void run() {

            updateTimerStatusAndSeeker();

            if (mTimer <= mSpecs.getTimer()) {
                mTimeHandler.postDelayed(timerUpdater, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpecs = Specs.getInstance();

        if (mSpecs == null) {
            super.onBackPressed();
            return;
        }

        mLayouts = mSpecs.getLayouts();

        mTimeHandler = new Handler();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupUI();
        setupValuesFromIntent();
        setupPager();

        updateLayout();

        mVibrator.vibrate(new long[]{0, 35, 30, 35}, -1);
        timerUpdater.run();
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

            case R.id.menu_undo:
                Specs.DataConstant dc = mEncoder.undo();
                if (dc == null) {
                    pushStatus("Nothing can be undone");
                } else {
                    pushStatus("Undo \'" + dc.getLabel() + "\'");
                    mVibrator.vibrate(20);
                }
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
        return mTimer <= mSpecs.getTimer() && mLastRecordedTime != mTimer;
    }

    @Override
    public void pushCurrentTimeAsValue(int t, int s) {
        mEncoder.push(t, mTimer, s);
        mLastRecordedTime = mTimer;
    }

    @Override
    public void pushStatus(String status) {
        //mActionBar.setSubtitle(status.replace("{t}", String.valueOf(mTimer)));
    }

    private void setupUI(){
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_scouting);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.drawable.ic_close);
        myToolBar.setNavigationContentDescription(R.string.menu_close);
        setSupportActionBar(myToolBar);

        mActionBar = getSupportActionBar();

        mTitleBanner = findViewById(R.id.title_banner);
        mTimerStatus = findViewById(R.id.timer_status);

        mTimeProgress = findViewById(R.id.time_progress);
        mTimeSeeker = findViewById(R.id.time_seeker);

        int timer_max = mSpecs.getTimer();

        mTimeProgress.setMax(timer_max);
        mTimeProgress.setProgress(0);

        mTimeSeeker.setMax(timer_max);
        mTimeSeeker.setProgress(0);

        // mTimeSeeker.setVisibility(View.VISIBLE);

        String a = mSpecs.getAlliance();

        myToolBar.setTitleTextColor(
                a.equals("R") ? 0xFFFF0000 : a.equals("B") ? 0xFF0000FF : 0xFF808080);

        myToolBar.setSubtitleTextColor(getResources().getColor(R.color.colorAlmostBlack));
    }

    private void setupValuesFromIntent() {
        Intent intent = getIntent();

        int matchNumber = intent.getIntExtra(ID.MSG_MATCH_NUMBER, -1);
        int teamNumber = intent.getIntExtra(ID.MSG_TEAM_NUMBER, -1);
        String scoutName = intent.getStringExtra(ID.MSG_SCOUT_NAME);

        String a = mSpecs.getAlliance();

        if (a.equals("R") || a.equals("B")) {
            mActionBar.setTitle("Match " + matchNumber + " — " + teamNumber);
        } else {
            mActionBar.setTitle(mSpecs.getBoardName());
        }

        // mActionBar.setSubtitle("Match " + matchNumber + " Started");

        mEncoder = new Encoder(matchNumber, teamNumber, scoutName);

    }

    private void setupPager(){

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
                updateLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setAnimatedTitleBanner(final String title) {

        animate_in.setDuration(125);
        animate_out.setDuration(125);

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

        if (!mTitleBanner.getText().toString().isEmpty()) {
            mTitleBanner.startAnimation(animate_out);
        } else {
            mTitleBanner.setText(title);
        }

    }

    private void updateLayout(){

        if (!mLayouts.isEmpty() && mCurrentTab >= 0 && mCurrentTab < mLayouts.size()) {

            Specs.Layout layout = mLayouts.get(mCurrentTab);

            setAnimatedTitleBanner(layout.getTitle());

            if (mPager.getCurrentItem() != mCurrentTab) {
                mPager.setCurrentItem(mCurrentTab, true);
            }
        }
    }

    private void updateTimerStatusAndSeeker() {
        String d = String.valueOf(mTimer <= 15 ? 15 - mTimer : 150 - mTimer);

        //d = (mTimer <= 15? "Ⓐ" : mTimer <= 120 ? "Ⓣ" : "Ⓔ") + " " + d;

        mTimerStatus.setText(d);
        mTimerStatus.setTextColor(mTimer <= 15 ?
                0xFFCC9900 : (mTimer <= 120 ?
                0xFF006633 : (mTimer < 150 ?
                0xFFFF9900 : 0xFFFF0000)));

        mTimeProgress.setProgress(mTimer);
        mTimeSeeker.setProgress(mTimer);

        mTimer++;
    }


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

}
