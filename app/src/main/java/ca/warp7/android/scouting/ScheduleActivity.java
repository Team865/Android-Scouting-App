package ca.warp7.android.scouting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.constants.RobotPosition;
import ca.warp7.android.scouting.interfaces.ScoutingScheduleItem;
import ca.warp7.android.scouting.model.MatchWithAllianceItem;
import ca.warp7.android.scouting.model.ScoutingSchedule;
import ca.warp7.android.scouting.model.Specs;
import ca.warp7.android.scouting.widgets.AllianceView;


public class ScheduleActivity extends AppCompatActivity {

    private ScoutingSchedule mScoutingSchedule;
    private ScoutingScheduleAdapter mScheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        setTitle("Match Schedule");

        Spinner spinner = findViewById(R.id.board_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.board_choices, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ListView scheduleListView = findViewById(R.id.entry_list);
        mScoutingSchedule = new ScoutingSchedule();

        try {
            mScoutingSchedule.loadFullScheduleFromCSV(
                    new File(Specs.getSpecsRoot(), "match-table.csv"));
        } catch (IOException exception) {
            onErrorDialog(exception);
        }

        mScoutingSchedule.scheduleForDisplayOnly();

        mScheduleAdapter = (new ScoutingScheduleAdapter(this,
                mScoutingSchedule.getCurrentlyScheduled()));

        scheduleListView.setAdapter(mScheduleAdapter);

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = mScoutingSchedule.getCurrentlyScheduled().get(position);
                Log.i("k", "1");
                if (item != null && item instanceof MatchWithAllianceItem) {
                    Log.i("k", "2");
                    MatchWithAllianceItem matchItem = (MatchWithAllianceItem) item;
                    if (matchItem.shouldFocus()) {
                        Log.i("k", "3");
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .create().show();
    }


    static class ScoutingScheduleAdapter extends ArrayAdapter<ScoutingScheduleItem> {

        LayoutInflater mInflater;

        ScoutingScheduleAdapter(@NonNull Context context,
                                List<ScoutingScheduleItem> scheduleItems) {
            super(context, 0, scheduleItems);
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View itemView;
            if (convertView != null && convertView instanceof LinearLayout) {
                itemView = convertView;
            } else {
                itemView = mInflater.inflate(R.layout.schedule_alliance_item, parent, false);
            }

            ScoutingScheduleItem scoutingScheduleItem = getItem(position);
            if (scoutingScheduleItem != null &&
                    scoutingScheduleItem instanceof MatchWithAllianceItem) {

                MatchWithAllianceItem matchItem =
                        (MatchWithAllianceItem) scoutingScheduleItem;
                AllianceView allianceView = itemView.findViewById(R.id.alliance_view);
                allianceView.setDataFromScheduledMatchItem(matchItem);
                allianceView.invalidate(); // Fix cached image in convert view

                TextView matchNumberView = itemView.findViewById(R.id.match_number);
                matchNumberView.setText(String.valueOf(matchItem.getMatchNumber()));
            }

            return itemView;
        }
    }


}
