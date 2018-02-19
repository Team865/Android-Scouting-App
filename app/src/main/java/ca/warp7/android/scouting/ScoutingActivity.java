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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScoutingActivity extends AppCompatActivity {

    ActionBar actionBar;
    TextView statusBanner;

    int timer;
    Handler handler;

    Specs specs;
    int currentLayout;

    void updateStatus(String message) {
        statusBanner.setText(message);
        /*statusBanner.setBackgroundResource(R.color.colorAccent);
        statusBanner.setTextColor(getResources().getColor(android.R.color.white));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusBanner.setBackgroundResource(android.R.color.white);
                statusBanner.setTextColor(getResources()
                        .getColor(android.R.color.tab_indicator_text));
            }
        }, 500);*/
    }

    void makeLayout(){
        ArrayList<Specs.Layout> layouts = specs.getLayouts();

        if (layouts.isEmpty() || currentLayout < 0 || currentLayout >= layouts.size()) {
            return;
        }

        Specs.Layout layout = layouts.get(currentLayout);

        updateStatus(layout.getTitle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting);

        handler = new Handler();

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        actionBar = getSupportActionBar();

        statusBanner = findViewById(R.id.status_banner);

        specs = Specs.getInstance();

        if(specs == null){
            super.onBackPressed();
            return;
        }

        currentLayout = 0;
        makeLayout();

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
                tb.setTitleTextColor(0xFFDDAA33);
            }
            else if (timer <= 120) {
                tb.setTitleTextColor(0xFF008800);
            }
            else if (timer < 150){
                tb.setTitleTextColor(0xFFDDAA33);
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

            case R.id.menu_close:
                Intent intent;
                intent = new Intent(this, DataOutputActivity.class);
                intent.putExtra(Static.MSG_PRINT_DATA, "No data");
                intent.putExtra(Static.MSG_ENCODE_DATA, "_");
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
