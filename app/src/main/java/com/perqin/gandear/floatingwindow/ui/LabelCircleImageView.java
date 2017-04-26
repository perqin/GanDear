package com.perqin.gandear.floatingwindow.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.perqin.gandear.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public class LabelCircleImageView extends CircleImageView {
    private String mLabel;
    private Paint mLabelFillPaint;
    private float mLabelTextSize;

    public LabelCircleImageView(Context context) {
        super(context);

        init();
    }

    public LabelCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelCircleImageView, 0, 0);
        mLabel = ta.getString(R.styleable.LabelCircleImageView_label);
        mLabelTextSize = ta.getDimension(R.styleable.LabelCircleImageView_labelTextSize, 42);

        ta.recycle();

        init();
    }

    public void setLabel(String label) {
        mLabel = label;
        invalidate();
    }

    private void init() {
        mLabelFillPaint = new Paint();

        setup();
    }

    private void setup() {
        mLabelFillPaint.setTextAlign(Paint.Align.CENTER);
        mLabelFillPaint.setColor(Color.WHITE);
        mLabelFillPaint.setStyle(Paint.Style.FILL);
        mLabelFillPaint.setTextSize(mLabelTextSize);
        mLabelFillPaint.setShadowLayer(18, 0, 0, Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLabel != null) {
            Paint.FontMetrics fontMetrics = mLabelFillPaint.getFontMetrics();
            final float lineHeight = fontMetrics.bottom - fontMetrics.top;
            final float baseline = fontMetrics.bottom;
            if (mLabel.length() < 3) {
                // No line break
                canvas.drawText(mLabel, getWidth() - lineHeight, (getHeight() - lineHeight / 2) - baseline, mLabelFillPaint);
            } else {
                canvas.drawText(mLabel.substring(0, 2), getWidth() - lineHeight, getHeight() - lineHeight - baseline, mLabelFillPaint);
                canvas.drawText(mLabel.substring(2), getWidth() - lineHeight, getHeight() - baseline, mLabelFillPaint);
            }
        }
    }
}
