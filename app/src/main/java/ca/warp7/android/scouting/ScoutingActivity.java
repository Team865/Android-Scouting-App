package ca.warp7.android.scouting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ScoutingActivity extends AppCompatActivity {

    ActionBar actionBar;

    int timer;
    Handler handler;

    Specs specs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        actionBar = getSupportActionBar();//actionBar.setSubtitle("Autonomous");

        specs = Specs.getInstance();

        if(specs == null){
            super.onBackPressed();
            return;
        }

        handler = new Handler();
        timerUpdater.run();

    }

    Runnable timerUpdater = new Runnable() {
        @Override
        public void run() {
            //String d = String.format(Locale.CANADA,
            //"%02d:%02d", secondsSinceStart / 60, secondsSinceStart % 60);
            //actionBar.setSubtitle(d);

            Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);

            String d = String.valueOf(timer <= 15 ? 15 - timer : 150 - timer);

            actionBar.setTitle(d);

            if(timer <= 15){
                tb.setTitleTextColor(0xFFDDAA33);
            } else if (timer <= 120) {
                tb.setTitleTextColor(0xFF008800);
            } else if (timer < 150){
                tb.setTitleTextColor(0xFFDDAA33);
            } else {
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

            case R.id.menu_close:
                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(Static.MSG_PRINT_DATA, "No data");
                intent.putExtra(Static.MSG_ENCODE_DATA, "_");
                startActivity(intent);
                return true;

            case R.id.menu_undo:
                Toast.makeText(this,
                        "Undo pressed (It does nothing for now) ",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_prev:
                Toast.makeText(this,
                        "Prev pressed (It does nothing for now) ",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_next:
                Toast.makeText(this,
                        "Next pressed (It does nothing for now) ",
                        Toast.LENGTH_SHORT).show();
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
