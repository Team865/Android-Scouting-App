package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoutingActivity
        extends AppCompatActivity{


    private static class ViewIdManager{
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
    }

    ActionBar actionBar;
    TextView statusBanner;
    TextView statusTimer;
    TableLayout inputTable;

    int timer;

    Handler handler;
    Vibrator vibrator;

    Specs specs;
    int currentLayout;

    Encoder encoder;


    View.OnTouchListener buttonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();

            Button b = (Button) v;

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    b.setTextColor(0xFFFFFFFF);
                    b.getBackground().setColorFilter(
                            getResources().getColor(R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
                    break;
                case MotionEvent.ACTION_UP:
                    b.setTextColor(getResources().getColor(R.color.colorAccent));
                    b.getBackground().clearColorFilter();
                    vibrator.vibrate(30);
                    break;
            }

            return true;
        }
    };

    View.OnClickListener buttonListener2 = new View.OnClickListener() {
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
    };

    Runnable timerUpdater = new Runnable() {
        @Override
        public void run() {

            String d = "⏱ " + String.valueOf(timer <= 15 ? 15 - timer : 150 - timer);

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


    Button createLayoutButton(String text){
        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(20);
        //button.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        button.setTextColor(getResources().getColor(R.color.colorAccent));

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);

        layoutParams.width = 0;

        button.setLayoutParams(layoutParams);
        button.setOnClickListener(buttonListener2);

        return button;
    }

    TableRow createLayoutRow(){
        TableRow tableRow = new TableRow(this);

        tableRow.setGravity(Gravity.CENTER);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

        return tableRow;
    }

    void layoutInputTable(Specs.Layout layout){
        inputTable.removeAllViews();


        for(int i = 0; i < 4; i++){
            TableRow tr = createLayoutRow();

            tr.addView(createLayoutButton(layout.getTitle()));
            tr.addView(createLayoutButton("Hi"));

            inputTable.addView(tr);
        }
    }

    void updateStatus(String message) {
        statusBanner.setText(message);
    }

    void makeLayout(){
        ArrayList<Specs.Layout> layouts = specs.getLayouts();

        if (layouts.isEmpty() || currentLayout < 0 || currentLayout >= layouts.size()) {
            return;
        }

        Specs.Layout layout = layouts.get(currentLayout);

        updateStatus(layout.getTitle());

        layoutInputTable(layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_scouting);

        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(myToolBar);

        actionBar = getSupportActionBar();

        statusBanner = findViewById(R.id.status_banner);
        statusTimer = findViewById(R.id.status_timer);

        actionBar.setDisplayShowTitleEnabled(false);


        inputTable = findViewById(R.id.input_table);
        inputTable.setGravity(Gravity.CENTER);

        specs = Specs.getInstance();

        if(specs == null){
            super.onBackPressed();
            return;
        }

        currentLayout = 0;
        makeLayout();

        Intent intent = getIntent();
        int matchNumber = intent.getIntExtra(ID.MSG_MATCH_NUMBER, -1);
        int teamNumber = intent.getIntExtra(ID.MSG_TEAM_NUMBER, -1);
        String scoutName = intent.getStringExtra(ID.MSG_SCOUT_NAME);

        encoder = new Encoder(matchNumber, teamNumber, scoutName);
        encoder.push(1,3);
        encoder.push(19,3);

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
                if (currentLayout > 0){
                    currentLayout--;
                    makeLayout();
                }
                return true;

            case R.id.menu_next:
                if (currentLayout < specs.getLayouts().size() - 1){
                    currentLayout++;
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
}
