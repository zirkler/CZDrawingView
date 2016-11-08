package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class CZDrawingActionMeasurementLine implements CZIDrawingAction {

    Context mContext;
    float mXStart = -1;
    float mYStart = -1;
    float mXEnd = -1;
    float mYEnd = -1;
    RectF mRect;
    private Path mPath;
    private Paint mPaint;
    private Paint mTextPaint;
    private View.OnClickListener mOnClickListener;

    public CZDrawingActionMeasurementLine(Context context, Paint paint) {
        mContext = context;
        mPath = new Path();

        // If there isn't a paint provided, create a default paint.
        if (paint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.CYAN);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(CZDrawingView.dip2pixel(mContext, 5));

        } else {
            mPaint = paint;
        }

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(CZDrawingView.dip2pixel(mContext, 15));
    }

    @Override
    public void touchStart(float x, float y) {
        mXStart = x;
        mYStart = y;
        mXEnd = x;
        mYEnd = y;
        mPath = new Path();
        mPath.moveTo(x, y);
    }

    @Override
    public void touchMove(float x, float y) {
        mXEnd = x;
        mYEnd = y;
        mPath = new Path();
        mPath.moveTo(mXStart, mYStart);
        mPath.lineTo(x, y);
    }

    @Override
    public void touchUp(float x, float y) {
        mXEnd = x;
        mYEnd = y;
        mPath = new Path();
        mPath.moveTo(mXStart, mYStart);
        mPath.lineTo(x, y);
    }

    @Override
    public Path getPath() {
        return mPath;
    }

    @Override
    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    @Override
    public boolean isErasable() {
        return false;
    }

    @Override
    public boolean checkBounds(float x, float y) {
        if (mRect == null) {
            return false;
        }

        if (mRect.contains(x, y)) {
            return true;
        }

        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        // draw the line
        canvas.drawPath(mPath, mPaint);

        // only draw rectangle and text when user already made some movement
        if (mXEnd != -1) {
            // draw text and rectangle in the middle of the line
            String text = "130cm";
            float textWidth = mTextPaint.measureText(text, 0, text.length());
            float textHeight = mTextPaint.getTextSize();
            float textPosX = (mXStart + mXEnd) / 2;
            textPosX = (textPosX - (textWidth / 2));
            float textPosY = (mYStart + mYEnd) / 2;


            // draw a rectangle around the text
            mRect = new RectF(textPosX,
                    (textPosY - textHeight) + 8, // just some manual adjustment to center the rect around the text
                    textPosX + textWidth,
                    textPosY);
            canvas.drawRect(mRect, mPaint);

            canvas.drawText(text, textPosX, textPosY, mTextPaint);
        }
    }

    @Override
    public CZIDrawingAction createInstance(Context context, Paint paint) {
        return new CZDrawingActionMeasurementLine(context, paint);
    }
}
