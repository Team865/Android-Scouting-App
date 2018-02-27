package ca.warp7.android.scouting;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class DataOutputActivity
        extends AppCompatActivity {

    TextView dataView;
    ImageView qrView;
    Button swapButton;

    private String print;
    private String encoded;
    private String comments = "";

    private boolean qrShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.data_title);
        }

        Intent intent = getIntent();

        print = intent.getStringExtra(ID.MSG_PRINT_DATA);
        encoded = getIntent().getStringExtra(ID.MSG_ENCODE_DATA);

        dataView = findViewById(R.id.data_display);
        dataView.setText(print);

        qrView = findViewById(R.id.qr_image);
        qrView.setVisibility(View.INVISIBLE);

        swapButton = findViewById(R.id.swap_qr_report_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_comment:
                showCommentsDialog();
                return true;

            case R.id.menu_send:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, encoded + comments);
                intent.setType("text/plain");

                startActivity(Intent.createChooser(intent, "Send encoded data to:"));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSendQRClicked(View view) {

        if(qrShown){

            qrShown = false;
            swapButton.setText(R.string.send_show_qr);
            qrView.setVisibility(View.INVISIBLE);
            findViewById(R.id.scroll).setVisibility(View.VISIBLE);

        }else {

            qrShown = true;
            swapButton.setText(R.string.send_show_report);

            makeQR();

            qrView.setVisibility(View.VISIBLE);
            findViewById(R.id.scroll).setVisibility(View.INVISIBLE);
        }

    }


    private String getFullEncode() {
        return encoded + comments;
    }

    Bitmap encodeAsBitmap() throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(
                    getFullEncode(),
                    BarcodeFormat.QR_CODE,
                    qrView.getWidth(),
                    qrView.getHeight(),
                    null);

        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, qrView.getWidth(), 0, 0, w, h);
        return bitmap;
    }

    private void makeQR(){
        try {
            qrView.setImageBitmap(encodeAsBitmap());
        } catch (WriterException e){
            e.printStackTrace();
        }
    }

    private void showCommentsDialog() {

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        input.setText(comments);

        new AlertDialog.Builder(this)

                .setView(input)
                .setTitle("Edit Comments")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Get the comment and make sure underscore isn't used

                        comments = input.getText().toString()
                                .replaceAll("_", "");

                        if (comments.isEmpty()) {
                            dataView.setText(print);
                        } else {
                            String newPrint = print + "\nComments:\n\n" + comments;
                            dataView.setText(newPrint);
                        }

                        makeQR();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }
}
