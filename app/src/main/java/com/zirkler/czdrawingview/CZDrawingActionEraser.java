package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CZDrawingActionEraser implements CZIDrawingAction {

    Path mPath;
    Context mContext;
    Paint mEraserPaint;
    float mX;
    float mY;

    public CZDrawingActionEraser(Context context, Paint paint) {
        mContext = context;
        mPath = new Path();

        if (paint == null) {
            mEraserPaint = new Paint();
            mEraserPaint.setStyle(Paint.Style.STROKE);
            mEraserPaint.setAntiAlias(true);
            mEraserPaint.setStrokeWidth(40);
            mEraserPaint.setAlpha(0xFF);
            mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            mEraserPaint = paint;
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
        mPath.lineTo(mX, mY);
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public Paint getPaint() {
        return mEraserPaint;
    }

    @Override
    public void setPaint(Paint paint) {
        mEraserPaint = paint;
    }

    @Override
    public boolean isErasable() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mEraserPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context, Paint paint) {
        return new CZDrawingActionEraser(context, paint);
    }
}
