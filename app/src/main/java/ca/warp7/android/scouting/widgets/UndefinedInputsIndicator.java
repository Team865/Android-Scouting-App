package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.abstraction.ScoutingActivityListener;

/**
 * Creates a placeholder button that shows definition errors
 * @since v0.2.0
 */

public class UndefinedInputsIndicator
        extends AppCompatButton
        implements View.OnClickListener {

    ScoutingActivityListener listener;

    public UndefinedInputsIndicator(Context context) {
        super(context);
    }

    public UndefinedInputsIndicator(Context context, String text,
                                    ScoutingActivityListener listener) {
        super(context);
        setOnClickListener(this);
        setTextSize(18);
        setText(text);
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        setTextColor(getResources().getColor(R.color.colorWhite));
        getBackground().setColorFilter(
                getResources().getColor(android.R.color.black), PorterDuff.Mode.MULTIPLY);
        listener.getManagedVibrator().vibrateAction();
        listener.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTextColor(getResources().getColor(android.R.color.black));
                getBackground().clearColorFilter();
            }
        }, 1000);
    }
}
