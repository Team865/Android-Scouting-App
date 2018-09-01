package ca.warp7.android.scouting.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.model.MatchWithAllianceItem;
import ca.warp7.android.scouting.model.RobotPosition;

public class AllianceView extends View {

    static final float kPad = 24;
    static final int kRadius = 16;

    Paint mAlmostRedPaint = new Paint();
    Paint mAlmostBluePaint = new Paint();
    Paint mAlmostBlackTextPaint = new Paint();
    Paint mGrayTextPaint = new Paint();
    Paint mRedTextPaint = new Paint();
    Paint mBlueTextPaint = new Paint();
    Paint mRedBoldTextPaint = new Paint();
    Paint mBlueBoldTextPaint = new Paint();

    private float mMinimumWidth;

    private String mR1 = "Red 1";
    private String mR2 = "Red 2";
    private String mR3 = "Red 3";
    private String mB1 = "Blue 1";
    private String mB2 = "Blue 2";
    private String mB3 = "Blue 3";

    private int mFocusPosition = RobotPosition.RED1;
    private boolean mShouldFocus = false;


    public AllianceView(Context context) {
        super(context);
        initPaints();
    }

    public AllianceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void setTextPaintStyle(Paint paint, boolean bold) {
        paint.setTextSize(32);
        paint.setAntiAlias(true);
        paint.setTypeface(bold ? Typeface.create(Typeface.SANS_SERIF,
                Typeface.BOLD) : Typeface.SANS_SERIF);
    }

    private void initPaints() {
        mAlmostRedPaint.setColor(getResources().getColor(R.color.colorAlmostRed));
        mAlmostBluePaint.setColor(getResources().getColor(R.color.colorAlmostBlue));

        mAlmostBlackTextPaint.setColor(getResources().getColor(R.color.colorAlmostBlack));
        mGrayTextPaint.setColor(getResources().getColor(R.color.colorGray));
        mRedTextPaint.setColor(getResources().getColor(R.color.colorRed));
        mBlueTextPaint.setColor(getResources().getColor(R.color.colorBlue));
        mRedBoldTextPaint.setColor(getResources().getColor(R.color.colorRed));
        mBlueBoldTextPaint.setColor(getResources().getColor(R.color.colorBlue));

        setTextPaintStyle(mAlmostBlackTextPaint, false);
        setTextPaintStyle(mGrayTextPaint, false);
        setTextPaintStyle(mRedTextPaint, false);
        setTextPaintStyle(mBlueTextPaint, false);
        setTextPaintStyle(mRedBoldTextPaint, true);
        setTextPaintStyle(mBlueBoldTextPaint, true);

        mMinimumWidth = mRedBoldTextPaint.measureText("8888");
    }

    public void setDataFromScheduledMatchItem(MatchWithAllianceItem matchItem) {
        mR1 = String.valueOf(matchItem.getTeamAt(0));
        mR2 = String.valueOf(matchItem.getTeamAt(1));
        mR3 = String.valueOf(matchItem.getTeamAt(2));
        mB1 = String.valueOf(matchItem.getTeamAt(3));
        mB2 = String.valueOf(matchItem.getTeamAt(4));
        mB3 = String.valueOf(matchItem.getTeamAt(5));
        mFocusPosition = matchItem.getFocusPosition();
        mShouldFocus = matchItem.shouldFocus();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = getMeasuredWidth();
        final int h = getMeasuredHeight();
        canvas.drawRect(0, kRadius, w, h / 2, mAlmostRedPaint);
        canvas.drawRect(kRadius, 0, w - kRadius, kRadius, mAlmostRedPaint);
        canvas.drawCircle(kRadius, kRadius, kRadius, mAlmostRedPaint);
        canvas.drawCircle(w - kRadius, kRadius, kRadius, mAlmostRedPaint);
        canvas.drawRect(0, h / 2, w, h - kRadius, mAlmostBluePaint);
        canvas.drawRect(kRadius, h - kRadius, w - kRadius, h, mAlmostBluePaint);
        canvas.drawCircle(kRadius, h - kRadius, kRadius, mAlmostBluePaint);
        canvas.drawCircle(w - kRadius, h - kRadius, kRadius, mAlmostBluePaint);
        canvas.drawLine(w / 3, 0, w / 3, h, mGrayTextPaint);
        canvas.drawLine(w / 3 * 2, 0, w / 3 * 2, h, mGrayTextPaint);
        canvas.drawLine(0, h / 2, w, h / 2, mGrayTextPaint);
        Paint R1Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED1 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint R2Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED2 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint R3Paint = mShouldFocus ? (mFocusPosition == RobotPosition.RED3 ?
                mRedBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B1Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE1 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B2Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE2 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        Paint B3Paint = mShouldFocus ? (mFocusPosition == RobotPosition.BLUE3 ?
                mBlueBoldTextPaint : mGrayTextPaint) : mAlmostBlackTextPaint;
        int fromBottom = 10;
        canvas.drawText(mR1, (w / 3 - R1Paint
                .measureText(mR1)) / 2, h / 2 - fromBottom, R1Paint);
        canvas.drawText(mR2, w / 3 + (w / 3 - R2Paint
                .measureText(mR2)) / 2, h / 2 - fromBottom, R2Paint);
        canvas.drawText(mR3, w / 3 * 2 + (w / 3 - R3Paint
                .measureText(mR3)) / 2, h / 2 - fromBottom, R3Paint);
        canvas.drawText(mB1, (w / 3 - B1Paint
                .measureText(mB1)) / 2, h - fromBottom, B1Paint);
        canvas.drawText(mB2, w / 3 + (w / 3 - B2Paint
                .measureText(mB2)) / 2, h - fromBottom, B2Paint);
        canvas.drawText(mB3, w / 3 * 2 + (w / 3 - B3Paint
                .measureText(mB3)) / 2, h - fromBottom, B3Paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension((int) (mMinimumWidth * 3 + kPad * 6), 84);
    }
}
