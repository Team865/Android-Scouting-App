package ca.warp7.android.scouting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity

        extends AppCompatActivity

        implements
        CompoundButton.OnCheckedChangeListener,
        TextWatcher,
        View.OnClickListener {


    private static final int MY_PERMISSIONS_REQUEST_FILES = 0;

    private EditText nameField, matchField, teamField;
    private TextView mismatchWarning;
    private CheckBox verifier;
    private Button matchStartButton;

    private Specs.Index specsIndex;

    private boolean newInterface = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up UI and event listeners

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        nameField = findViewById(R.id.name_and_initial);
        matchField = findViewById(R.id.match_number);
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
        loadFromPreferences();
        setUpSpecs();
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
                return true;

            case R.id.menu_copy_specs:

                askToCopySpecs();
                return true;

            case R.id.menu_change_interface:

                newInterface = !newInterface;

                item.setTitle(newInterface ? "Use Old Interface" : "Use New Interface");
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
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILES: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpSpecs();
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

        SharedPreferences prefs;
        prefs = this.getSharedPreferences(ID.ROOT_DOMAIN, MODE_PRIVATE);

        String name = nameField.getText().toString().replaceAll("_", "");

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ID.SAVE_SCOUT_NAME, name);

        editor.apply();

        // Start Auto Activity

        if(!Specs.hasInstance()){
            return;
        }

        Intent intent;
        intent = new Intent(this,
                newInterface ? ScoutingActivity.class : TimedScoutingActivity.class);

        intent.putExtra(ID.MSG_MATCH_NUMBER,
                Integer.parseInt(matchField.getText().toString()));

        intent.putExtra(ID.MSG_TEAM_NUMBER,
                Integer.parseInt(teamField.getText().toString()));

        intent.putExtra(ID.MSG_SCOUT_NAME, name);

        startActivity(intent);

    }


    /**
     * Set up the specs directory by copying from the asset folder
     * if the file is not already there
     */
    private void setUpSpecs() {

        File root = Specs.getSpecsRoot();
        File indexFile = new File(root, "index.json");

        if (!indexFile.exists()) {
            copySpecs();
        }

        loadIndex(indexFile);
    }

    private void askToCopySpecs() {
        new AlertDialog.Builder(this)
                .setTitle("Copy Default Metrics?")
                .setMessage("Any custom files stored at \""
                        + Specs.getSpecsRoot().getAbsolutePath()
                        + "\" will be overwritten.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copySpecs();
                    }
                })
                .create()
                .show();
    }

    private void copySpecs() {
        try {
            File root = Specs.getSpecsRoot();
            File indexFile = new File(root, "index.json");

            AssetManager am = getAssets();
            for (String fn : am.list("specs")) {

                InputStream in = am.open("specs/" + fn);
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                in.close();

                File f = new File(root, fn);
                OutputStream out = new FileOutputStream(f);
                out.write(buffer);
                out.close();
            }

            loadIndex(indexFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadIndex(File indexFile) {
        specsIndex = new Specs.Index(indexFile);

        if (!specsIndex.getNames().isEmpty()) {
            loadSpecsFromName(specsIndex.getNames().get(0));
        } else {
            getSupportActionBar().setTitle("Index File Not Found");
        }
    }

    private void loadSpecsFromName(String name) {
        if (specsIndex != null && specsIndex.getNames().contains(name)) {
            Specs specs = Specs.setInstance(specsIndex.getFileByName(name));
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setTitle("Board " + specs.getBoardName());
                ab.setSubtitle(specs.getEvent());
            }
            updateTextFieldState();
        }
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

    private void loadFromPreferences() {
        // Set up auto fill from preferences

        SharedPreferences prefs;
        prefs = this.getSharedPreferences(ID.ROOT_DOMAIN, MODE_PRIVATE);

        nameField.setText(prefs.getString(ID.SAVE_SCOUT_NAME, ""));
    }

    private boolean matchDoesExist(String m, String t) {
        return Specs.getInstance().matchIsInSchedule
                (Integer.parseInt(m) - 1, Integer.parseInt(t));
    }

    private void updateTextFieldState() {
        String n = nameField.getText().toString();
        String m = matchField.getText().toString();
        String t = teamField.getText().toString();

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
                    verifier.setTextColor(0xFF000000);

                } else {
                    mismatchWarning.setText(R.string.schedule_mismatch);
                    mismatchWarning.setVisibility(View.VISIBLE);
                    verifier.setText(R.string.verify_match_proceed);
                    verifier.setTextColor(0xFFFF0000);
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
            verifier.setTextColor(0xFF000000);
            verifier.setChecked(false);
        }
    }
}
