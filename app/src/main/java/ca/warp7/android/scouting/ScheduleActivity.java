package ca.warp7.android.scouting;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    static class ScoutingScheduleAdapter extends ArrayAdapter<ManagedData.ScoutingScheduleItem> {

        LayoutInflater mInflater;

        ScoutingScheduleAdapter(@NonNull Context context,
                                List<ManagedData.ScoutingScheduleItem> scheduleItems) {
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
                itemView = mInflater.inflate(R.layout.entry_list_item, parent, false);
            }

            ManagedData.ScoutingScheduleItem scoutingScheduleItem = getItem(position);
            if (scoutingScheduleItem != null &&
                    scoutingScheduleItem instanceof ManagedData.MatchWithAllianceItem) {

                ManagedData.MatchWithAllianceItem matchItem =
                        (ManagedData.MatchWithAllianceItem) scoutingScheduleItem;
                Widgets.AllianceView allianceView = itemView.findViewById(R.id.alliance_view);
                allianceView.setAllianceFromScheduledMatchItem(matchItem);
                allianceView.setNoRobotFocused();
                TextView matchNumberView = itemView.findViewById(R.id.match_number);
                matchNumberView.setText(String.valueOf(matchItem.getMatchNumber()));
            }

            return itemView;
        }
    }


    ManagedData.ScoutingSchedule mScoutingSchedule;
    ListView mScheduleListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        setTitle("Match Schedule");

        Spinner spinner = findViewById(R.id.board_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.board_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mScheduleListView = findViewById(R.id.entry_list);
        mScoutingSchedule = new ManagedData.ScoutingSchedule();

        try {
            mScoutingSchedule.loadFullScheduleFromMatchTableCSV();
        } catch (IOException exception) {
            onErrorDialog(exception);
        }

        mScoutingSchedule.scheduleForDisplayOnly();

        mScheduleListView.setAdapter(new ScoutingScheduleAdapter(this,
                mScoutingSchedule.getCurrentlyScheduled()));

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mScheduleListView.setAdapter(new ArrayAdapter<>(ScheduleActivity.this,
//                        android.R.layout.simple_list_item_1,
//                        mScoutingSchedule.getTeamsArrayForBoard(position)));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
}
