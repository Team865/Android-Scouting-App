package ca.warp7.android.scouting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.SANS_SERIF;

public class Widgets {

    @SuppressWarnings("unused")
    static class AllianceView extends View {

        static final float kPad = 36;

        Paint mAlmostRedPaint = new Paint();
        Paint mAlmostBluePaint = new Paint();
        Paint mGrayTextPaint = new Paint();
        Paint mRedTextPaint = new Paint();
        Paint mBlueTextPaint = new Paint();
        Paint mRedFocusedTextPaint = new Paint();
        Paint mBlueFocusedTextPaint = new Paint();

        float min_width;

        private String mR1Team = "1";
        private String mR2Team = "33";
        private String mR3Team = "865";
        private String mB1Team = "4917";
        private String mB2Team = "2056";
        private String mB3Team = "254";

        private ManagedData.RobotPosition mFocusedRobotPosition = ManagedData.RobotPosition.RED1;
        private boolean mShouldFocusARobot = false;


        public AllianceView(Context context) {
            super(context);
            initPaints();
        }

        public AllianceView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initPaints();
        }

        private void initPaints() {
            mAlmostRedPaint.setColor(getResources().getColor(R.color.colorAlmostRed));
            mAlmostBluePaint.setColor(getResources().getColor(R.color.colorAlmostBlue));
            mGrayTextPaint.setColor(getResources().getColor(R.color.colorGray));
            mGrayTextPaint.setTextSize(36);
            mGrayTextPaint.setTypeface(Typeface.SANS_SERIF);
            mGrayTextPaint.setAntiAlias(true);
            mRedTextPaint.setColor(getResources().getColor(R.color.colorRed));
            mRedTextPaint.setTextSize(36);
            mRedTextPaint.setAntiAlias(true);
            mBlueTextPaint.setColor(getResources().getColor(R.color.colorBlue));
            mBlueTextPaint.setTextSize(36);
            mBlueTextPaint.setAntiAlias(true);
            mRedFocusedTextPaint.setColor(getResources().getColor(R.color.colorRed));
            mRedFocusedTextPaint.setTypeface(Typeface.create(SANS_SERIF, BOLD));
            mRedFocusedTextPaint.setTextSize(36);
            mRedFocusedTextPaint.setAntiAlias(true);
            mBlueFocusedTextPaint.setColor(getResources().getColor(R.color.colorBlue));
            mBlueFocusedTextPaint.setTypeface(Typeface.create(SANS_SERIF, BOLD));
            mBlueFocusedTextPaint.setTextSize(36);
            mBlueFocusedTextPaint.setAntiAlias(true);

            min_width = mRedFocusedTextPaint.measureText("8888");
            setFocusedRobotPosition(ManagedData.RobotPosition.BLUE3);
        }

        public void setFocusedRobotPosition(ManagedData.RobotPosition position) {
            mFocusedRobotPosition = position;
            mShouldFocusARobot = true;
        }

        public void setNoRobotFocused() {
            mShouldFocusARobot = false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int radius = 16;
            canvas.drawRect(0, radius, width, height / 2, mAlmostRedPaint);
            canvas.drawRect(radius, 0, width - radius, radius, mAlmostRedPaint);
            canvas.drawCircle(radius, radius, radius, mAlmostRedPaint);
            canvas.drawCircle(width - radius, radius, radius, mAlmostRedPaint);
            canvas.drawRect(0, height / 2, width, height - radius, mAlmostBluePaint);
            canvas.drawRect(radius, height - radius, width - radius, height, mAlmostBluePaint);
            canvas.drawCircle(radius, height - radius, radius, mAlmostBluePaint);
            canvas.drawCircle(width - radius, height - radius, radius, mAlmostBluePaint);
            canvas.drawLine(width / 3, 0, width / 3, height, mGrayTextPaint);
            canvas.drawLine(width / 3 * 2, 0, width / 3 * 2, height, mGrayTextPaint);
            canvas.drawLine(0, height / 2, width, height / 2, mGrayTextPaint);
            Paint R1Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED1 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint R2Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED2 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint R3Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.RED3 ?
                    mRedFocusedTextPaint : mGrayTextPaint) : mRedTextPaint;
            Paint B1Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE1 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            Paint B2Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE2 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            Paint B3Paint = mShouldFocusARobot ? (mFocusedRobotPosition ==
                    ManagedData.RobotPosition.BLUE3 ?
                    mBlueFocusedTextPaint : mGrayTextPaint) : mBlueTextPaint;
            canvas.drawText(mR1Team, (width / 3 - R1Paint
                    .measureText(mR1Team)) / 2, height / 2 - 16, R1Paint);
            canvas.drawText(mR2Team, width / 3 + (width / 3 - R2Paint
                    .measureText(mR2Team)) / 2, height / 2 - 16, R2Paint);
            canvas.drawText(mR3Team, width / 3 * 2 + (width / 3 - R3Paint
                    .measureText(mR3Team)) / 2, height / 2 - 16, R3Paint);
            canvas.drawText(mB1Team, (width / 3 - B1Paint
                    .measureText(mB1Team)) / 2, height - 16, B1Paint);
            canvas.drawText(mB2Team, width / 3 + (width / 3 - B2Paint
                    .measureText(mB2Team)) / 2, height - 16, B2Paint);
            canvas.drawText(mB3Team, width / 3 * 2 + (width / 3 - B3Paint
                    .measureText(mB3Team)) / 2, height - 16, B3Paint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension((int) (min_width * 3 + kPad * 6), 120);
        }
    }
}
