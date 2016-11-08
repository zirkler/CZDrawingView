package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class CZDrawingActionFreehand implements CZIDrawingAction {

    Path mPath;
    Context mContext;
    Paint mPaint;
    float mX;
    float mY;

    public CZDrawingActionFreehand(Context context, Paint paint) {
        mContext = context;
        mPath = new Path();

        // If there isn't a paint provided, create a default paint.
        if (paint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(CZDrawingView.dip2pixel(mContext, 5));
        } else {
            mPaint = paint;
        }
    }


    @Override
    public void touchStart(float x, float y) {
        mX = x;
        mY = y;
        mPath.moveTo(x, y);
    }

    @Override
    public void touchMove(float x, float y) {
        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        mX = x;
        mY = y;
    }

    @Override
    public void touchUp(float x, float y) {
        mPath.lineTo(x, y);
    }

    @Override
    public Path getPath() {
        return mPath;
    }

    @Override
    public Paint getPaint() {
        return null;
    }

    @Override
    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    @Override
    public boolean isErasable() {
        return true;
    }

    @Override
    public boolean checkBounds(float x, float y) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context, Paint paint) {
        return new CZDrawingActionFreehand(context, paint);
    }
}
