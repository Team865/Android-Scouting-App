package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TimedScoutingActivity extends AppCompatActivity {

    ActionBar actionBar;
    int section_id;

    // Input screens

    AutoInputs ai;
    TeleInputs ti;
    EndGameInputs ei;

    CountDownTimer cdt;


    private void exitAction(){
        new AlertDialog.Builder(this)
                .setTitle("Really End Match?")
                .setMessage("You will lose all entered data")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cdt.cancel();
                        TimedScoutingActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_scouting);

        // Set the toolbar to be the default action bar

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);


        // Set up the action bar

        actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setTitle("Autonomous");
            actionBar.setSubtitle("0:00");
        }

        // Set up the fragments

        ai = new AutoInputs();
        ti = new TeleInputs();
        ei = new EndGameInputs();

        section_id = 0;

        if(findViewById(R.id.input_frame)!= null){
            if (savedInstanceState != null) {
                return;
            }
            ai.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.input_frame, ai).commit();
        }


        // Timer code, ignore for now
        /*cdt = new CountDownTimer(150000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

                long s = (150000 - millisUntilFinished) / 1000;

                long r = s % 60;

                getSupportActionBar().setSubtitle(s / 60 + ":" + (r < 10 ? "0":"") + r);
            }

            @Override
            public void onFinish() {

            }
        };

        cdt.start();*/


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timed_scouting_bar, menu);
        return true;
    }

    public void onBackPressed(){
        exitAction();
    }

    private void updateContentSet(){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(section_id == 0) {
            transaction.replace(R.id.input_frame, ai);
            actionBar.setTitle("Autonomous");
        } else if (section_id == 1){
            transaction.replace(R.id.input_frame, ti);
            actionBar.setTitle("Tele-Op");
        } else if (section_id == 2){
            transaction.replace(R.id.input_frame, ei);
            actionBar.setTitle("Endgame");
        }

        transaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_close:
                exitAction();
                return true;
            case R.id.menu_undo:
                Toast.makeText(this, "Undo pressed",Toast.LENGTH_SHORT).show();
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
}
