package ca.warp7.android.scouting;


import android.Manifest;
import android.content.Context;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {


    private static final int MY_PERMISSIONS_REQUEST_FILES = 0;
    private EditText nameField, matchField, teamField;
    private ToggleButton allianceToggle;
    private TextView mismatchWarning, matchHint, teamHint;
    private CheckBox verifier;
    private Button matchStartButton;

    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar to be the default action bar

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        // Ask for File Permissions

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_FILES);
        }

        initBoard();

        // Get the rest of the UI

        nameField = (EditText) findViewById(R.id.name_and_initial);
        matchHint = (TextView) findViewById(R.id.match_hint);
        matchField = (EditText) findViewById(R.id.match_number);
        teamHint = (TextView) findViewById(R.id.team_hint);
        teamField = (EditText) findViewById(R.id.team_number);
        allianceToggle = (ToggleButton) findViewById(R.id.alliance_toggle);
        mismatchWarning = (TextView) findViewById(R.id.mismatch_warning);
        verifier = (CheckBox) findViewById(R.id.verify_check);
        matchStartButton = (Button) findViewById(R.id.match_start_button);

        // Set up auto fill from preferences

        SharedPreferences prefs;
        prefs = this.getSharedPreferences(Static.ROOT_DOMAIN, MODE_PRIVATE);

        nameField.setText(prefs.getString(Static.SAVE_SCOUT_NAME, ""));

        // Ensure input UI states

        verifier.setChecked(false);
        verifier.setEnabled(false);
        mismatchWarning.setVisibility(View.INVISIBLE);
        matchStartButton.setVisibility(View.INVISIBLE);

        // Add Event Listeners

        verifier.setOnCheckedChangeListener(this);
        nameField.addTextChangedListener(this);
        matchField.addTextChangedListener(this);
        teamField.addTextChangedListener(this);
        matchStartButton.setOnClickListener(this);

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
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBoard();
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

        String n = nameField.getText().toString();
        String m = matchField.getText().toString();
        String t = teamField.getText().toString();

        boolean n_empty = n.isEmpty();
        boolean m_empty = m.isEmpty();
        boolean t_empty = t.isEmpty();

        if (!m_empty) {
            matchHint.setVisibility(View.VISIBLE);
        } else {
            matchHint.setVisibility(View.INVISIBLE);
        }

        if (!t_empty) {
            teamHint.setVisibility(View.VISIBLE);
        } else {
            teamHint.setVisibility(View.INVISIBLE);
        }

        if (!(n_empty || m_empty || t_empty)) {

            verifier.setEnabled(true);

            if (doesMatchExist(m, t)) {

                mismatchWarning.setVisibility(View.INVISIBLE);
                verifier.setText(R.string.verify_match_info);
                verifier.setTextColor(0xFF000000);
                matchStartButton.setTextColor(getResources().getColor(R.color.colorAccent));

            } else {

                mismatchWarning.setVisibility(View.VISIBLE);
                verifier.setText(R.string.verify_match_proceed);
                verifier.setTextColor(0xFFFF0000);
                matchStartButton.setTextColor(0xFFFF0000);
                verifier.setChecked(false);

            }

        } else {
            mismatchWarning.setVisibility(View.INVISIBLE);
            verifier.setText(R.string.verify_match_info);
            verifier.setEnabled(false);
            verifier.setTextColor(0xFF000000);
            matchStartButton.setTextColor(getResources().getColor(R.color.colorAccent));
            verifier.setChecked(false);
        }

    }

    @Override
    public void onClick(View v) {

        SharedPreferences prefs;
        prefs = this.getSharedPreferences(Static.ROOT_DOMAIN, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Static.SAVE_SCOUT_NAME, nameField.getText().toString());

        editor.apply();

        // Start Auto Activity

        Intent intent;
        intent = new Intent(this, TimedScoutingActivity.class);

        intent.putExtra(Static.MSG_MATCH_NUMBER,
                Integer.parseInt(matchField.getText().toString()));

        intent.putExtra(Static.MSG_TEAM_NUMBER,
                Integer.parseInt(teamField.getText().toString()));

        intent.putExtra(Static.MSG_SCOUT_NAME,
                nameField.getText().toString().replaceAll("_", ""));

        startActivity(intent);

    }


    private boolean doesMatchExist(String m, String t) {
        return board.matchDoesExist(Integer.parseInt(m) - 1, Integer.parseInt(t));
    }

    private void initBoard(){
        //Create the Board object

        board = new Board();

        // Set up the action bar

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Board " + board.getBoardName());
            actionBar.setSubtitle(R.string.app_version_text);
        }
    }
}
