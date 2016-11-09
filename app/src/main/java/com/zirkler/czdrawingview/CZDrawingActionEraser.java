package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CZDrawingActionEraser implements CZIDrawingAction {

    CZPath mPath;
    Context mContext;
    CZPaint mEraserPaint;
    float mX;
    float mY;

    public CZDrawingActionEraser(Context context, CZPaint paint) {
        mContext = context;
        mPath = new CZPath();

        if (paint == null) {
            mEraserPaint = new CZPaint();
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
    public CZPath getPath() {
        return null;
    }

    @Override
    public CZPaint getPaint() {
        return mEraserPaint;
    }

    @Override
    public void setPaint(CZPaint paint) {
        mEraserPaint = paint;
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
        canvas.drawPath(mPath, mEraserPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context, CZPaint paint) {
        return new CZDrawingActionEraser(context, paint);
    }
}
