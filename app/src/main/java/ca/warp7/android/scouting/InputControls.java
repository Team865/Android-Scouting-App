package ca.warp7.android.scouting;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import ca.warp7.android.scouting.model.BaseInputControl;
import ca.warp7.android.scouting.model.ChildInputControl;
import ca.warp7.android.scouting.model.DataConstant;
import ca.warp7.android.scouting.model.ParentInputControlListener;
import ca.warp7.android.scouting.model.ScoutingActivityListener;

/**
 * Includes a set of controls for input used in the inputs fragment
 * that implement custom behaviours for scouting purposes.
 *
 * @author Team 865
 */

class InputControls {

    /**
     * A Base button for other buttons to extend onto
     */

    static abstract class BaseButton
            extends AppCompatButton
            implements BaseInputControl,
            ChildInputControl,
            View.OnClickListener {

        DataConstant dc;
        ScoutingActivityListener listener;
        ParentInputControlListener parentInputControlListener;
        View parentSupportView;

        BaseButton(Context context) {
            super(context);
        }

        BaseButton(Context context,
                   DataConstant dc,
                   ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(18);
            setLines(2);
            setTypeface(Typeface.SANS_SERIF);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStateListAnimator(null);
                setElevation(4);
            }
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public void setParentListener(ParentInputControlListener listener) {
            parentInputControlListener = listener;
            parentSupportView = listener.getSupportView();
        }
    }

    /**
     * A button that records time as it is pressed
     */

    static class TimerButton
            extends BaseButton {

        int counter;

        public TimerButton(Context context) {
            super(context);
        }

        public TimerButton(Context context,
                           DataConstant dc,
                           ScoutingActivityListener listener) {
            super(context, dc, listener);

            setText(dc.getLabel().replace(" ", "\n"));
            updateControlState();
        }

        @Override
        public void onClick(View v) {
            if (listener.timeIsRecordable()) {

                listener.getManagedVibrator().vibrateAction();

                listener.pushCurrentTimeAsValue(dc.getIndex(), 1);
                updateControlState();
                listener.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateControlState();
                    }
                }, 1000);

                listener.pushStatus(dc.getLabel() + " - {t}s");
            } else {
                listener.pushStatus("Time Conflict @" + listener.getCurrentRelativeTime() + "s");
            }
        }

        @Override
        public void setParentListener(ParentInputControlListener listener) {
            super.setParentListener(listener);
            updateCounterView(false);
        }

        @Override
        public void updateControlState() {

            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            } else {

                counter = listener.getEntry().getCount(dc.getIndex());
                if (listener.dataShouldFocus(dc.getIndex())) {
                    updateCounterView(true);
                    setTextColor(getResources().getColor(R.color.colorWhite));
                    getBackground().setColorFilter(
                            getResources().getColor(R.color.colorAccent),
                            PorterDuff.Mode.MULTIPLY);
                } else {
                    getBackground().clearColorFilter();
                    setTextColor(getResources().getColor(R.color.colorAccent));
                    updateCounterView(false);
                }
                setEnabled(true);
            }
        }

        private void updateCounterView(boolean white) {
            if (parentSupportView instanceof TextView) {
                TextView counterView = (TextView) parentSupportView;
                counterView.setText(String.valueOf(counter));

                if (white) {
                    counterView.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    counterView.setTextColor(getResources().getColor(R.color.colorAlmostBlack));
                }
            }
        }
    }

    /**
     * A button that measures duration,
     * equivalent in function as a ToggleButton.
     * It records the time of when the button is
     */

    static class DurationButton
            extends BaseButton {

        boolean isOn;

        public DurationButton(Context context) {
            super(context);
        }

        public DurationButton(Context context,
                              DataConstant dc,
                              ScoutingActivityListener listener) {
            super(context, dc, listener);
            updateControlState();
        }

        @Override
        public void onClick(View v) {
            if (listener.timeIsRecordable()) {
                isOn = !isOn;
                listener.pushCurrentTimeAsValue(dc.getIndex(), isOn ? 1 : 0);
                listener.pushStatus(getText().toString() + " - {t}s");

                updateLooks();
                listener.getManagedVibrator().vibrateAction();

            } else {
                listener.pushStatus("Time Conflict @" + listener.getCurrentRelativeTime() + "s");
            }
        }

        @Override
        public void updateControlState() {
            isOn = listener.getEntry().getCount(dc.getIndex()) % 2 != 0;

            updateLooks();

            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            } else {
                setEnabled(true);
            }
        }

        void updateLooks() {
            if (isOn) {
                setTextColor(getResources().getColor(R.color.colorWhite));
                setText(dc.getLabelOn());
                getBackground().setColorFilter(
                        getResources().getColor(R.color.colorRed),
                        PorterDuff.Mode.MULTIPLY);
            } else {
                setTextColor(getResources().getColor(R.color.colorLightGreen));
                setText(dc.getLabel());
                getBackground().clearColorFilter();
            }
        }

    }

    /**
     * A button that gives the user a list of options to choose
     */

    static class ChoicesButton
            extends AppCompatTextView
            implements BaseInputControl,
            View.OnClickListener {

        DataConstant dc;
        ScoutingActivityListener listener;

        int lastWhich = 0;

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

            setTextColor(getResources().getColor(R.color.colorAccent));

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
                    .setItems(dc.getChoices(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which != lastWhich) {
                                lastWhich = which;
                                setText(dc.getChoices()[which]);
                                listener.getManagedVibrator().vibrateAction();
                                listener.getEntry().push(dc.getIndex(), which, 1);
                                listener.pushStatus(dc.getLabel() + " <" + getText() + ">");
                            }
                        }
                    }).show();
        }

        @Override
        public void updateControlState() {
            lastWhich = listener.getEntry().getLastValue(dc.getIndex());
            setText(dc.getChoices()[lastWhich]);
        }
    }

    /**
     * A checkbox that gives true or false values
     */

    static class Checkbox
            extends AppCompatCheckBox
            implements BaseInputControl,
            View.OnClickListener {

        DataConstant dc;
        ScoutingActivityListener listener;

        public Checkbox(Context context) {
            super(context);
        }

        public Checkbox(Context context,
                        DataConstant dc,
                        ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnClickListener(this);

            setAllCaps(false);
            setTextSize(18);
            setLines(2);

            setTypeface(Typeface.SANS_SERIF);

            setText(dc.getLabel());

            updateControlState();
        }

        @Override
        public void onClick(View v) {
            listener.getManagedVibrator().vibrateAction();
            listener.getEntry().push(dc.getIndex(), isChecked() ? 1 : 0, 1);
            listener.pushStatus(getText().toString() + " - " + (isChecked() ? "On" : "Off"));
        }

        @Override
        public void updateControlState() {
            // Temporary Fix before board format changed
            if (listener.timedInputsShouldDisable()) {
                setEnabled(false);
                setTextColor(getResources().getColor(R.color.colorGray));
            } else {
                setEnabled(true);
                setTextColor(getResources().getColor(R.color.colorAccent));
            }
            setChecked(listener.getEntry().getCount(dc.getIndex()) % 2 != 0);
        }
    }

    /**
     * Creates a ratings bar based on the maximum value specified
     */

    static class SeekBar
            extends AppCompatSeekBar
            implements BaseInputControl,
            AppCompatSeekBar.OnSeekBarChangeListener {


        DataConstant dc;
        ScoutingActivityListener listener;

        int lastProgress;

        public SeekBar(Context context) {
            super(context);
        }

        public SeekBar(Context context,
                       DataConstant dc,
                       ScoutingActivityListener listener) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOnSeekBarChangeListener(this);
            setBackgroundColor(0);
            setMax(dc.getMax());
            updateControlState();
        }

        @Override
        public void onProgressChanged(android.widget.SeekBar seekBar,
                                      int progress,
                                      boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {

            if (getProgress() != lastProgress) {
                listener.getManagedVibrator().vibrateAction();
                lastProgress = getProgress();

                listener.getEntry().push(dc.getIndex(), lastProgress, 1);
                listener.pushStatus(dc.getLabel() + " - " + lastProgress + "/" + dc.getMax());
            }
        }

        @Override
        public void updateControlState() {
            lastProgress = listener.getEntry().getLastValue(dc.getIndex());
            setProgress(lastProgress);
        }
    }

    /**
     * Creates a box container for a label and another control
     */

    static class LabeledControlLayout
            extends LinearLayout
            implements BaseInputControl {

        DataConstant dc;
        ScoutingActivityListener listener;

        View subControl;

        public LabeledControlLayout(Context context) {
            super(context);
        }

        public LabeledControlLayout(Context context,
                                    DataConstant dc,
                                    ScoutingActivityListener listener,
                                    View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            setOrientation(VERTICAL);

            // Set the background of the view

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new Button(getContext()).getBackground());
            } else {
                setBackgroundResource(android.R.drawable.btn_default);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(4);
            }

            TableRow.LayoutParams childLayout = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f);

            // Add the views

            TextView label = new TextView(context);
            label.setTextColor(getResources().getColor(R.color.colorAlmostBlack));

            label.setText(dc.getLabel());
            label.setGravity(Gravity.CENTER);
            label.setTextSize(15);

            label.setLayoutParams(childLayout);
            addView(label);


            control.setLayoutParams(childLayout);
            addView(control);
            subControl = control;
        }

        @Override
        public void updateControlState() {
            if (subControl instanceof BaseInputControl) {
                ((BaseInputControl) subControl).updateControlState();
            }
        }
    }

    /**
     * Creates a box container that centers the control inside it
     */

    static class CenteredControlLayout
            extends ConstraintLayout
            implements BaseInputControl,
            View.OnClickListener {

        DataConstant dc;
        ScoutingActivityListener listener;

        View subControl;

        public CenteredControlLayout(Context context) {
            super(context);
        }

        public CenteredControlLayout(Context context,
                                     DataConstant dc,
                                     ScoutingActivityListener listener,
                                     View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            // Set the background of the view

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(new Button(getContext()).getBackground());
            } else {
                setBackgroundResource(android.R.drawable.btn_default);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(4);
            }

            ConstraintLayout.LayoutParams childLayout;

            childLayout = new ConstraintLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            childLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            childLayout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

            control.setLayoutParams(childLayout);

            addView(control);
            setOnClickListener(this);

            subControl = control;
            updateControlState();
        }

        @Override
        public void onClick(View v) {
            if (!listener.timedInputsShouldDisable()
                    && subControl instanceof View.OnClickListener) {
                subControl.performClick();
            }
        }

        @Override
        public void updateControlState() {
            setEnabled(!listener.timedInputsShouldDisable());
            if (subControl instanceof BaseInputControl) {
                ((BaseInputControl) subControl).updateControlState();
            }
        }
    }

    /**
     * A counter for the buttons
     */

    static class CountedInputControlLayout
            extends FrameLayout
            implements BaseInputControl,
            ParentInputControlListener {

        DataConstant dc;
        ScoutingActivityListener listener;

        TextView counter;
        View subControl;

        public CountedInputControlLayout(@NonNull Context context) {
            super(context);
        }

        public CountedInputControlLayout(Context context,
                                         DataConstant dc,
                                         ScoutingActivityListener listener,
                                         View control) {
            super(context);
            this.dc = dc;
            this.listener = listener;

            addView(control);

            counter = new TextView(context);
            counter.setTextSize(15);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                counter.setElevation(10);
            }

            LayoutParams childLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            childLayout.leftMargin = 24;
            childLayout.topMargin = 16;

            counter.setLayoutParams(childLayout);

            addView(counter);

            if (control instanceof ChildInputControl) {
                ((ChildInputControl) control).setParentListener(this);
            }

            subControl = control;

        }

        @Override
        public View getSupportView() {
            return counter;
        }

        @Override
        public void updateControlState() {
            if (subControl instanceof BaseInputControl) {
                ((BaseInputControl) subControl).updateControlState();
            }
        }
    }

}
