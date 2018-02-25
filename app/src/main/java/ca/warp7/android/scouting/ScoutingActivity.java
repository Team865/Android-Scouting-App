package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ScoutingActivity
        extends AppCompatActivity
        implements InputsFragment.OnInputReceivedListener{

    Handler handler;
    Vibrator vibrator;

    ActionBar actionBar;
    TextView statusBanner;
    TextView statusTimer;

    ViewPager container;

    int timer;
    int currentTab = 0;

    Specs specs;
    Encoder encoder;

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

        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupUI();
        setupEncoder();
        makeLayout();

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

            case R.id.menu_close:
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
                    makeLayout();
                }
                return true;

            case R.id.menu_next:
                if (currentTab < specs.getLayouts().size() - 1){
                    currentTab++;
                    makeLayout();
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


    TableRow.LayoutParams createCellParams(){
        return new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);
    }

    TableLayout.LayoutParams createRowParams(){
        return new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
    }

    Button createLayoutButton(String text){
        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(20);
        //button.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        button.setTextColor(getResources().getColor(R.color.colorAccent));

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                createCellParams());

        layoutParams.width = 0;

        button.setLayoutParams(layoutParams);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Button b = (Button) v;

                b.setTextColor(0xFFFFFFFF);
                b.getBackground().setColorFilter(
                        getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);

                vibrator.vibrate(30);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setTextColor(getResources().getColor(R.color.colorAccent));
                        b.getBackground().clearColorFilter();
                    }
                }, 1000);
            }
        });

        return button;
    }

    TableRow createLayoutRow(){
        TableRow tableRow = new TableRow(this);

        tableRow.setGravity(Gravity.CENTER);

        tableRow.setLayoutParams(createRowParams());

        return tableRow;
    }



    void updateStatus(final String status){

        in.setDuration(100);
        out.setDuration(100);

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

    private void makeLayout(){
        ArrayList<Specs.Layout> layouts = specs.getLayouts();

        if (layouts.isEmpty() || currentTab < 0 || currentTab >= layouts.size()) {
            return;
        }

        Specs.Layout layout = layouts.get(currentTab);

        updateStatus(layout.getTitle());
    }

    private void setupUI(){
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_scouting);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.drawable.ic_close);
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
