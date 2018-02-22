package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScoutingActivity extends AppCompatActivity {

    ActionBar actionBar;
    TextView statusBanner;
    TableLayout inputTable;

    int timer;
    Handler handler;

    Specs specs;
    int currentLayout;

    Encoder encoder;

    void updateStatus(String message) {
        statusBanner.setText(message);
    }


    Button createLayoutButton(String text){
        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(24);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);

        layoutParams.width = 0;

        button.setLayoutParams(layoutParams);

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

    void makeLayout(){
        ArrayList<Specs.Layout> layouts = specs.getLayouts();

        if (layouts.isEmpty() || currentLayout < 0 || currentLayout >= layouts.size()) {
            return;
        }

        Specs.Layout layout = layouts.get(currentLayout);

        updateStatus(layout.getTitle());

        layoutInputTable(layout);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting);

        handler = new Handler();

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(myToolBar);

        actionBar = getSupportActionBar();

        statusBanner = findViewById(R.id.status_banner);
        inputTable = (TableLayout) findViewById(R.id.input_table);

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
        encoder.push(18,3);

        timerUpdater.run();

    }

    Runnable timerUpdater = new Runnable() {
        @Override
        public void run() {
            //String d = String.format(Locale.CANADA,
            //"%02d:%02d", secondsSinceStart / 60, secondsSinceStart % 60);
            //actionBar.setSubtitle(d);

            Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);

            String d = "⏱ " + String.valueOf(timer <= 15 ? 15 - timer : 150 - timer);

            actionBar.setTitle(d);

            if(timer <= 15){
                tb.setTitleTextColor(0xFFCC9900);
            }
            else if (timer <= 120) {
                tb.setTitleTextColor(0xFF009933);
            }
            else if (timer < 150){
                tb.setTitleTextColor(0xFFFF9900);
            }
            else {
                tb.setTitleTextColor(0xFFFF0000);
            }

            timer++;

            if (timer <= specs.getTimer()){
                handler.postDelayed(timerUpdater, 1000);
            }
        }
    };

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
