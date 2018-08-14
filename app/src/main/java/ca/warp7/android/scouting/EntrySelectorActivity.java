package ca.warp7.android.scouting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.IOException;

public class EntrySelectorActivity extends AppCompatActivity {

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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEntryList.setAdapter(new ArrayAdapter<>(EntrySelectorActivity.this,
                        android.R.layout.simple_list_item_1,
                        mMatchTable.getTeamsArrayForBoard(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
