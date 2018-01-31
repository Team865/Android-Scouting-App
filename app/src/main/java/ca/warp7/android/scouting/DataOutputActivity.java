package ca.warp7.android.scouting;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class DataOutputActivity extends AppCompatActivity {

    TextView dataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output);

        // Set the toolbar to be the default action bar

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        // Set up the action bar

        ActionBar actionBar = getSupportActionBar();

        // TODO actually show the title based on what fragment is shown

        if (actionBar != null) {
            actionBar.setTitle("Submit Match Data");
        }

        dataView = findViewById(R.id.data_display);

        dataView.setText("Match:    23\nTeam:     865\nScouter:  Yu\nBoard:    Red 1 (2017)\nBoard #:  0x00022e09\nStart:    01-31 17:08\ndata:\n(15, 3) \n\n\ncomments:\nHello World");
    }
}
