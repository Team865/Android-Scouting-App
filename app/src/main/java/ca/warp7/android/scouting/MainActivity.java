package ca.warp7.android.scouting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ca.warp7.android.scouting.constants.ID;
import ca.warp7.android.scouting.data.AppAssets;
import ca.warp7.android.scouting.model.Specs;
import ca.warp7.android.scouting.model.SpecsIndex;


public class MainActivity
        extends AppCompatActivity
        implements TextWatcher,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final int MY_PERMISSIONS_REQUEST_FILES = 0;

    private EditText scoutNameField;
    private EditText matchNumberField;
    private EditText teamNumberField;

    private TextView mismatchWarning;
    private CheckBox verifier;
    private Button matchStartButton;

    private SpecsIndex specsIndex;
    private SharedPreferences prefs;

    private String mPassedSpecsFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences(ID.ROOT, MODE_PRIVATE);
        ensurePermissions();
        setupUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupSpecs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.specs_selector_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_select_specs:
                askToSelectSpecs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        matchStartButton.setVisibility(b ? View.VISIBLE : View.INVISIBLE);

        View view = this.getCurrentFocus();

        if (view != null && verifier.isChecked()) {

            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            view.clearFocus();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupSpecs();
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        updateTextFieldState();
    }

    @Override
    public void onClick(View v) {

        String name = scoutNameField.getText().toString().replaceAll("_", "");

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ID.SAVE_SCOUT_NAME, name);

        editor.apply();

        // Start Auto Activity

        if (!Specs.hasInstance()) {
            return;
        }

        Intent intent;
        intent = new Intent(this, ScoutingActivity.class);

        intent.putExtra(ID.MSG_MATCH_NUMBER,
                Integer.parseInt(matchNumberField.getText().toString()));
        intent.putExtra(ID.MSG_TEAM_NUMBER,
                Integer.parseInt(teamNumberField.getText().toString()));
        intent.putExtra(ID.MSG_SCOUT_NAME, name);
        intent.putExtra(ID.MSG_SPECS_FILE, mPassedSpecsFile);

        startActivity(intent);

    }

    public void onLogoClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    private void ensurePermissions() {
        // Ask for File Permissions

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_FILES);
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        scoutNameField = findViewById(R.id.name_and_initial);
        matchNumberField = findViewById(R.id.match_number);
        teamNumberField = findViewById(R.id.team_number);

        mismatchWarning = findViewById(R.id.mismatch_warning);
        verifier = findViewById(R.id.verify_check);
        matchStartButton = findViewById(R.id.match_start_button);

        verifier.setOnCheckedChangeListener(this);

        scoutNameField.addTextChangedListener(this);
        matchNumberField.addTextChangedListener(this);
        teamNumberField.addTextChangedListener(this);

        matchStartButton.setOnClickListener(this);

        scoutNameField.setText(prefs.getString(ID.SAVE_SCOUT_NAME, ""));

        findViewById(R.id.team_logo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLogoClicked(null);
                return true;
            }
        });
    }

    private void setupSpecs() {

        File root = Specs.getSpecsRoot();
        File indexFile = new File(root, "index.json");

        if (!indexFile.exists()) {
            AppAssets.copyAssets(this);
        }

        loadIndex(indexFile);
    }

    private void askToSelectSpecs() {

        final String[] specs = specsIndex.getNames().toArray(new String[0]);
        new AlertDialog.Builder(this)

                .setTitle("Select your board")
                .setItems(specs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadSpecsFromName(specs[which]);
                    }
                })
                .show();
    }

    private void loadIndex(File indexFile) {
        specsIndex = new SpecsIndex(indexFile);

        ArrayList<String> names = specsIndex.getNames();

        if (!names.isEmpty()) {

            String savedName = prefs.getString(ID.SAVE_SPECS, "");

            loadSpecsFromName(names.contains(savedName) ? savedName : names.get(0));

        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Index File Not Found");
            }
        }
    }

    private void loadSpecsFromName(String name) {
        if (specsIndex != null && specsIndex.getNames().contains(name)) {
            mPassedSpecsFile = specsIndex.getFileByName(name);
            Specs specs = Specs.setInstance(mPassedSpecsFile);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setTitle("Board: " + specs.getBoardName());
                ab.setSubtitle(specs.getEvent());
            }
            updateTextFieldState();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ID.SAVE_SPECS, name);
            editor.apply();
        }
    }

    private void updateTextFieldState() {
        String n = scoutNameField.getText().toString();
        String m = matchNumberField.getText().toString();
        String t = teamNumberField.getText().toString();

        boolean n_empty = n.isEmpty();
        boolean m_empty = m.isEmpty();
        boolean t_empty = t.isEmpty();

        findViewById(R.id.name_hint)
                .setVisibility(!n_empty ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.match_hint)
                .setVisibility(!m_empty ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.team_hint)
                .setVisibility(!t_empty ? View.VISIBLE : View.INVISIBLE);

        if (!(n_empty || m_empty || t_empty)) {

            verifier.setEnabled(true);
            if (Specs.getInstance().hasMatchSchedule()) {
                if (matchDoesExist(m, t)) {

                    mismatchWarning.setVisibility(View.INVISIBLE);
                    verifier.setText(R.string.verify_match_info);
                    verifier.setTextColor(getResources().getColor(R.color.colorAlmostBlack));

                } else {
                    mismatchWarning.setText(R.string.schedule_mismatch);
                    mismatchWarning.setVisibility(View.VISIBLE);
                    verifier.setText(R.string.verify_match_proceed);
                    verifier.setTextColor(getResources().getColor(R.color.colorRed));
                    verifier.setChecked(false);

                }
            } else {
                mismatchWarning.setText(R.string.schedule_does_not_exist);
                mismatchWarning.setVisibility(View.VISIBLE);
            }

        } else {
            mismatchWarning.setVisibility(View.INVISIBLE);
            verifier.setText(R.string.verify_match_info);
            verifier.setEnabled(false);
            verifier.setTextColor(getResources().getColor(R.color.colorAlmostBlack));
            verifier.setChecked(false);
        }
    }

    private boolean matchDoesExist(String m, String t) {
        return Specs.getInstance().matchIsInSchedule
                (Integer.parseInt(m) - 1, Integer.parseInt(t));
    }
}
