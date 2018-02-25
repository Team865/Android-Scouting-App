package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ScoutingActivity
        extends AppCompatActivity
        implements InputsFragment.InputsFragmentListener {

    Handler handler;
    Vibrator vibrator;

    ActionBar actionBar;
    TextView statusBanner;
    TextView statusTimer;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    int timer;
    int currentTab = 0;

    Specs specs;
    Encoder encoder;

    ArrayList<Specs.Layout> layouts;

    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);


    Runnable timerUpdater = new Runnable() {
        @Override
        public void run() {

            String d = (timer <= 15? "Ⓐ" : timer <= 120 ? "Ⓣ" : "Ⓔ") + " "
                    + String.valueOf(timer <= 15 ? 15 - timer : 150 - timer);

            statusTimer.setText(d);
            statusTimer.setTextColor(timer <= 15 ?
                    0xFFCC9900 : (timer <= 120 ?
                    0xFF006633 : (timer < 150 ?
                    0xFFFF9900 : 0xFFFF0000)));

            timer++;

            if (timer <= specs.getTimer()){
                handler.postDelayed(timerUpdater, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        specs = Specs.getInstance();

        if(specs == null){
            super.onBackPressed();
            return;
        }

        layouts = specs.getLayouts();

        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupUI();
        setupEncoder();
        setupPager();

        updateLayout();

        vibrator.vibrate(new long[]{0, 35, 30, 35}, -1);
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

            case R.id.menu_done:
                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(ID.MSG_PRINT_DATA, encoder.format());
                intent.putExtra(ID.MSG_ENCODE_DATA, encoder.encode());
                startActivity(intent);
                return true;

            case R.id.menu_undo:
                Toast.makeText(this,
                        "Undo pressed (It does nothing) ",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_prev:
                if (currentTab > 0){
                    currentTab--;
                    updateLayout();
                }
                return true;

            case R.id.menu_next:
                if (currentTab < specs.getLayouts().size() - 1){
                    currentTab++;
                    updateLayout();
                }
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
        return handler;
    }

    @Override
    public Vibrator getVibrator() {
        return vibrator;
    }

    void updateStatus(final String status){

        in.setDuration(125);
        out.setDuration(125);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                statusBanner.setText(status);
                statusBanner.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if(!statusBanner.getText().toString().isEmpty()){
            statusBanner.startAnimation(out);
        } else {
            statusBanner.setText(status);
        }

    }

    private void setupUI(){
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_scouting);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.drawable.ic_close);
        myToolBar.setNavigationContentDescription(R.string.menu_close);
        setSupportActionBar(myToolBar);

        actionBar = getSupportActionBar();

        statusBanner = findViewById(R.id.status_banner);
        statusTimer = findViewById(R.id.status_timer);

        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setupEncoder(){
        Intent intent = getIntent();

        int matchNumber = intent.getIntExtra(ID.MSG_MATCH_NUMBER, -1);
        int teamNumber = intent.getIntExtra(ID.MSG_TEAM_NUMBER, -1);
        String scoutName = intent.getStringExtra(ID.MSG_SCOUT_NAME);

        encoder = new Encoder(matchNumber, teamNumber, scoutName);

        for(int i = 0; i < 20; i++){
            encoder.push((int) (Math.random() * 22), (int) (Math.random() * 150));
        }
    }

    private void setupPager(){

        pager = findViewById(R.id.pager);

        pagerAdapter = new InputTabsPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
                updateLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateLayout(){

        if (!layouts.isEmpty() && currentTab >= 0 && currentTab < layouts.size()) {

            Specs.Layout layout = layouts.get(currentTab);

            updateStatus(layout.getTitle());

            if (pager.getCurrentItem() != currentTab) {
                pager.setCurrentItem(currentTab, true);
            }
        }
    }


    private class InputTabsPagerAdapter extends FragmentStatePagerAdapter{

        InputTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return new InputsFragment();
        }

        @Override
        public int getCount() {
            return specs.getLayouts().size();
        }
    }


    /**
     * A singleton manager to keep track of compile-time views by assigning ids
     */
    static class ViewIdManager{
        private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

        private static int generateViewId() {

            if (Build.VERSION.SDK_INT < 17) {
                for (;;) {
                    final int result = sNextGeneratedId.get();

                    int newValue = result + 1;
                    if (newValue > 0x00FFFFFF)
                        newValue = 1;
                    if (sNextGeneratedId.compareAndSet(result, newValue)) {
                        return result;
                    }
                }
            } else {
                return View.generateViewId();
            }

        }

        private static ViewIdManager viewIdManager;

        static ViewIdManager getInstance(){
            if(viewIdManager == null){
                viewIdManager = new ViewIdManager();
            }
            return viewIdManager;
        }

        private HashMap<Integer, String> idMap;

        @SuppressWarnings("unchecked")
        ViewIdManager() {
            idMap = new HashMap();
        }

        int createViewId(String retrievalId){
            int viewId = ViewIdManager.generateViewId();

            idMap.put(viewId, retrievalId);

            return viewId;
        }

        String getRetrievalId(int viewId){
            if(idMap.containsKey(viewId)){
                return idMap.get(viewId);
            }
            return "";
        }

        void clear(){
            idMap.clear();
        }
    }

}
