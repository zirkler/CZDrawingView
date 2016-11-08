package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class CZDrawingActionMeasurementLine implements CZIDrawingAction {

    Context mContext;
    Paint mRedPaint;
    float mXStart;
    float mYStart;
    float mXEnd;
    float mYEnd;
    Path mPath;
    private Paint mTextPaint;

    public CZDrawingActionMeasurementLine(Context context) {
        mContext = context;
        mRedPaint = new Paint();
        mPath = new Path();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setColor(Color.CYAN);
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeJoin(Paint.Join.ROUND);
        mRedPaint.setStrokeCap(Paint.Cap.ROUND);
        mRedPaint.setStrokeWidth(CZDrawingView.dip2pixel(mContext, 5));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(60); // TODO: respect display density
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
    public void draw(Canvas canvas, Canvas cacheCanvas) {
        // draw the line
        cacheCanvas.drawPath(mPath, mRedPaint);

        // draw the text
        String text = "130cm";
        float textWidth = mTextPaint.measureText(text, 0, text.length());
        float textPosX = (mXStart + mXEnd) / 2;
        textPosX = (textPosX - (textWidth / 2));
        float textPosY = (mYStart + mYEnd) / 2;
        cacheCanvas.drawText(text, textPosX, textPosY, mTextPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context) {
        return new CZDrawingActionMeasurementLine(context);
    }
}
