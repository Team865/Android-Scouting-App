package ca.warp7.android.scouting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.warp7.android.scouting.components.ScoutingScheduleAdapter;
import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.constants.RobotPosition;
import ca.warp7.android.scouting.model.MatchWithAllianceItem;
import ca.warp7.android.scouting.model.ScoutingSchedule;
import ca.warp7.android.scouting.res.AppResources;
import ca.warp7.android.scouting.res.EventInfo;

/**
 * @since v0.4.2
 */

public class ScheduleActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_FILES = 0;

    private ScoutingSchedule mScoutingSchedule;
    private ScoutingScheduleAdapter mScheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        setTitle("Match Schedule");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_FILES);
        }

        final List<EventInfo> events = AppResources.getEvents();

        List<String> names = new ArrayList<>();
        for (EventInfo event : events) {
            names.add(event.getEventName());
        }

        new AlertDialog.Builder(this).setTitle("Select Event")
                .setItems(names.toArray(new String[0]), (dialog, which) -> createScreen(events.get(which)))
                .create().show();


    }

    private void createScreen(EventInfo selectedEvent) {
        Spinner spinner = findViewById(R.id.board_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.board_choices, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ListView scheduleListView = findViewById(R.id.entry_list);
        mScoutingSchedule = new ScoutingSchedule();

        try {
            mScoutingSchedule.loadFullScheduleFromCSV(selectedEvent.getMatchTableRoot());
        } catch (IOException exception) {
            onErrorDialog(exception);
        }

        mScoutingSchedule.scheduleForDisplayOnly();

        mScheduleAdapter = (new ScoutingScheduleAdapter(this,
                mScoutingSchedule.getCurrentlyScheduled()));

        scheduleListView.setAdapter(mScheduleAdapter);

        scheduleListView.setOnItemClickListener((parent, view, position, id) -> {
            Object item = mScoutingSchedule.getCurrentlyScheduled().get(position);
            if (item instanceof MatchWithAllianceItem) {
                MatchWithAllianceItem matchItem = (MatchWithAllianceItem) item;
                if (matchItem.shouldFocus()) {
                    int team = matchItem.getTeamAtPosition(matchItem.getFocusPosition());
                    int match = matchItem.getMatchNumber();
                    Intent intent;
                    intent = new Intent(ScheduleActivity.this, ScoutingActivity.class);

                    intent.putExtra(ID.MSG_MATCH_NUMBER, match);
                    intent.putExtra(ID.MSG_TEAM_NUMBER, team);
                    intent.putExtra(ID.MSG_SCOUT_NAME, "hi");
                    intent.putExtra(ID.MSG_SPECS_FILE, "");

                    startActivity(intent);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mScoutingSchedule.scheduleForDisplayOnly();
                        break;
                    case 1:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.RED1);
                        break;
                    case 2:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.RED2);
                        break;
                    case 3:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.RED3);
                        break;
                    case 4:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.BLUE1);
                        break;
                    case 5:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.BLUE2);
                        break;
                    case 6:
                        mScoutingSchedule.scheduleAllAtRobotPosition(RobotPosition.BLUE3);
                        break;
                }
                mScheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onErrorDialog(Exception exception) {
        exception.printStackTrace();
        new AlertDialog.Builder(this)
                .setTitle("An error occurred")
                .setMessage(exception.toString())
                .setPositiveButton("OK", (dialog, which) -> onBackPressed())
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permission", "granted");
                }
            }
        }
    }
}
