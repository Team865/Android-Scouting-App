package ca.warp7.android.scouting;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EntrySelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_selector);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        setTitle("Entry Selector");

        ListView lv = findViewById(R.id.entry_list);
        Spinner spinner = findViewById(R.id.board_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.board_choices, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        try {
            File mtf = new File(Specs.getSpecsRoot(), "match-table.csv");
            String mt_content = readFile(mtf);
            String[] split = mt_content.split("\n");
            ArrayAdapter aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, split);
            lv.setAdapter(aa);

        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("An error occurred")
                    .setMessage(e.toString())
                    .create()
                    .show();
        }
    }

    private static String readFile(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();

        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }

        br.close();
        return sb.toString();
    }
}
