package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.BaseInputControl;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.model.DataConstant;

/**
 * A button that gives the user a list of options to choose
 *
 * @since v0.2.0
 */

public class ChoicesButton
        extends AppCompatTextView
        implements BaseInputControl,
        View.OnClickListener {

    private DataConstant dc;
    private ScoutingActivityListener listener;

    private int lastWhich = 0;

    public ChoicesButton(Context context) {
        super(context);
    }

    public ChoicesButton(Context context,
                         DataConstant dc,
                         ScoutingActivityListener listener) {
        super(context);
        this.dc = dc;
        this.listener = listener;

        setOnClickListener(this);

        setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        setAllCaps(false);
        setTextSize(18);

        setTypeface(Typeface.SANS_SERIF);
        setGravity(Gravity.CENTER);

        updateControlState();
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle(dc.getLabel())
                .setItems(dc.getChoices(), (dialog, which) -> {
                    if (which != lastWhich) {
                        lastWhich = which;
                        setText(dc.getChoices()[which]);
                        listener.getManagedVibrator().vibrateAction();
                        listener.getEntry().push(dc.getIndex(), which, 1);
                        listener.pushStatus(dc.getLabel() + " <" + getText() + ">");
                    }
                }).show();
    }

    @Override
    public void updateControlState() {
        lastWhich = listener.getEntry().getLastValue(dc.getIndex());
        setText(dc.getChoices()[lastWhich]);
    }
}
