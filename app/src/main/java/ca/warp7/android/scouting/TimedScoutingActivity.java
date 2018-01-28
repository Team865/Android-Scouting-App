package ca.warp7.android.scouting;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class TimedScoutingActivity extends AppCompatActivity {

    ActionBar actionBar;

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

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timed_scouting_bar, menu);
        return true;
    }

    public void onBackPressed(){
        Toast.makeText(this, "Back pressed",Toast.LENGTH_SHORT).show();
    }
}
