package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TimedScoutingActivity
        extends AppCompatActivity {


    ActionBar actionBar;
    int section_id = 0;

    // Input fragment screens
    AutoInputs autoInputs;
    TeleInputs teleInputs;
    EndGameInputs endGameInputs;

    Match match;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_scouting);

        // Set the toolbar to be the default action bar

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);


        // Set up the action bar

        actionBar = getSupportActionBar();

        // TODO actually show the title based on what fragment is shown

        if(actionBar != null){
            actionBar.setTitle("Autonomous");
            //actionBar.setSubtitle("0:00");
        }

        // Get the match info from the intent

        Intent intent = getIntent();

        int matchNumber = intent.getIntExtra(Static.MSG_MATCH_NUMBER, -1);
        int teamNumber = intent.getIntExtra(Static.MSG_TEAM_NUMBER, -1);
        String scoutName = intent.getStringExtra(Static.MSG_SCOUT_NAME);

        match = new Match(matchNumber, teamNumber, scoutName);
        match.start();

        // TODO Default Data Values
        pushState(Static.AUTO_ROBOT_CROSS_LINE, 0);

        // Set up the fragments

        autoInputs = new AutoInputs();
        teleInputs = new TeleInputs();
        endGameInputs = new EndGameInputs();

        // Set the frame view to contain the fragments

        if(findViewById(R.id.input_frame)!= null){
            if (savedInstanceState != null) {
                return;
            }
            autoInputs.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.input_frame, autoInputs).commit();
        }
        // Add start timer code here
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_close:
                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(Static.MSG_PRINT_DATA, match.format());
                intent.putExtra(Static.MSG_ENCODE_DATA, match.encode());
                startActivity(intent);
                return true;
            case R.id.menu_undo:
                Toast.makeText(this, "Undo pressed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_prev:
                if(section_id > 0){
                    section_id -= 1;
                    updateContentSet();
                }
                return true;
            case R.id.menu_next:
                if(section_id < 2){
                    section_id += 1;
                    updateContentSet();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timed_scouting_bar, menu);
        return true;
    }


    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setTitle("Really End Match?")
                .setMessage("You will lose all data")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimedScoutingActivity.super.onBackPressed();
                    }
                }).create().show();
    }


    private void updateContentSet(){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(section_id == 0) {
            transaction.replace(R.id.input_frame, autoInputs);
            actionBar.setTitle("Autonomous");
        } else if (section_id == 1){
            transaction.replace(R.id.input_frame, teleInputs);
            actionBar.setTitle("Tele Op");
        } else if (section_id == 2){
            transaction.replace(R.id.input_frame, endGameInputs);
            actionBar.setTitle("End Game");
        }

        transaction.commit();

    }

    void pushState(int index, int value) {
        match.pushState(index, value);
    }

    void pushElapsed(int index){
        match.pushElapsed(index);
    }
}
