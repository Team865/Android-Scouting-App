package ca.warp7.android.scouting;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DataOutputActivity extends AppCompatActivity {

    TextView dataView;

    private String print;
    private String encoded;
    private String comments = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output);

        // Set the toolbar to be the default action bar

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        myToolBar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(myToolBar);

        // Set up the action bar

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.data_title);
        }


        Intent intent = getIntent();

        print = intent.getStringExtra(Static.MSG_PRINT_DATA);
        encoded = getIntent().getStringExtra(Static.MSG_ENCODE_DATA);

        dataView = findViewById(R.id.data_display);
        dataView.setText(print);

    }

    public void onSendQRClicked(View view) {

        try {
            Intent intent = new Intent();
            intent.setAction("com.google.zxing.client.android.ENCODE");
            intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
            intent.putExtra("ENCODE_DATA", encoded + comments);
            startActivity(intent);

        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }

    }

    public void onCommentClicked(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        input.setText(comments);

        builder.setView(input);

        builder.setTitle("Edit Comments");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the comment and make sure underscore isn't used
                comments = input.getText().toString().replaceAll("_", "");
                if (comments.isEmpty()) {
                    dataView.setText(print);
                } else {
                    String newPrint = print + "\nComments:\n\n" + comments;
                    dataView.setText(newPrint);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
