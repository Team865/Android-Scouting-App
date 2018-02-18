package ca.warp7.android.scouting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {


    private static final int MY_PERMISSIONS_REQUEST_FILES = 0;

    private EditText nameField, matchField, teamField;
    private TextView mismatchWarning, matchHint, teamHint;
    private CheckBox verifier;
    private Button matchStartButton;

    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up UI and event listeners

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        nameField = findViewById(R.id.name_and_initial);
        matchHint = findViewById(R.id.match_hint);
        matchField = findViewById(R.id.match_number);
        teamHint = findViewById(R.id.team_hint);
        teamField = findViewById(R.id.team_number);
        mismatchWarning = findViewById(R.id.mismatch_warning);
        verifier = findViewById(R.id.verify_check);
        matchStartButton = findViewById(R.id.match_start_button);

        verifier.setOnCheckedChangeListener(this);

        nameField.addTextChangedListener(this);
        matchField.addTextChangedListener(this);
        teamField.addTextChangedListener(this);

        matchStartButton.setOnClickListener(this);

        // Set up miscellaneous tasks

        ensurePermissions();
        initBoard();
        loadFromPreferences();
        setUpSpecs();
    }

    /**
     * Set up the specs directory by copying from the asset folder
     * if the file is not already there
     */
    private void setUpSpecs(){

        File root = Specs.getSpecsRoot();
        File indexFile = new File(Specs.getSpecsRoot(), "specs/index.json");

        if (!indexFile.exists()){
            try{
                AssetManager am = getAssets();
                for(String fn : am.list("specs")){

                    InputStream in = am.open("specs/" + fn);
                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    in.close();

                    File f = new File(root, fn);
                    OutputStream out = new FileOutputStream(f);
                    out.write(buffer);
                    out.close();

                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void ensurePermissions(){
        // Ask for File Permissions

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_FILES);
        }
    }

    private void loadFromPreferences(){
        // Set up auto fill from preferences

        SharedPreferences prefs;
        prefs = this.getSharedPreferences(Static.ROOT_DOMAIN, MODE_PRIVATE);

        nameField.setText(prefs.getString(Static.SAVE_SCOUT_NAME, ""));
    }

    private boolean matchDoesExist(String m, String t) {
        return board.matchDoesExist(Integer.parseInt(m) - 1, Integer.parseInt(t));
    }

    private void initBoard(){
        //Create the Board object

        board = new Board();

        // Set up the action bar

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Board " + board.getBoardName());
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

            if (matchDoesExist(m, t)) {

                mismatchWarning.setVisibility(View.INVISIBLE);
                verifier.setText(R.string.verify_match_info);
                verifier.setTextColor(0xFF000000);
                //matchStartButton.setTextColor(getResources().getColor(R.color.colorAccent));

            } else {

                mismatchWarning.setVisibility(View.VISIBLE);
                verifier.setText(R.string.verify_match_proceed);
                verifier.setTextColor(0xFFFF0000);
                //matchStartButton.setTextColor(0xFFFF0000);
                verifier.setChecked(false);

            }

        } else {
            mismatchWarning.setVisibility(View.INVISIBLE);
            verifier.setText(R.string.verify_match_info);
            verifier.setEnabled(false);
            verifier.setTextColor(0xFF000000);
            //matchStartButton.setTextColor(getResources().getColor(R.color.colorAccent));
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
}
