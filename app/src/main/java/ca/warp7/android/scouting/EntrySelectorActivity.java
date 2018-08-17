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

public class EntrySelectorActivity extends AppCompatActivity {

    static class MatchTableAdapter extends ArrayAdapter<ManagedData.MatchInfo> {

        LayoutInflater mInflater;

        MatchTableAdapter(@NonNull Context context, List<ManagedData.MatchInfo> matches) {
            super(context, 0, matches);
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

            ManagedData.MatchInfo matchInfo = getItem(position);

            if (matchInfo != null) {
                Widgets.AllianceView allianceView = itemView.findViewById(R.id.alliance_view);
                allianceView.setAllianceFromMatchInfo(matchInfo);
                allianceView.setNoRobotFocused();
                TextView matchNumber = itemView.findViewById(R.id.match_number);
                matchNumber.setText(String.valueOf(matchInfo.getMatchNumber()));
            }
            return itemView;
        }
    }

    ManagedData.MatchTable mMatchTable;
    ListView mEntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_selector);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        setTitle("Entry Selector");
        Spinner spinner = findViewById(R.id.board_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.board_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mEntryList = findViewById(R.id.entry_list);
        try {
            mMatchTable = new ManagedData.MatchTable();
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("An error occurred")
                    .setMessage(e.toString())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .create().show();
        }
        mEntryList.setAdapter(new MatchTableAdapter(this, mMatchTable.getMatches()));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mEntryList.setAdapter(new ArrayAdapter<>(EntrySelectorActivity.this,
//                        android.R.layout.simple_list_item_1,
//                        mMatchTable.getTeamsArrayForBoard(position)));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
}
