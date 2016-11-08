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

    public CZDrawingActionEraser(Context context) {
        mContext = context;
        mPath = new Path();
        mEraserPaint = new Paint();
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setStrokeWidth(40);
        mEraserPaint.setAlpha(0xFF);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
    public void draw(Canvas canvas, Canvas cacheCanvas) {
        cacheCanvas.drawPath(mPath, mEraserPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context) {
        return new CZDrawingActionEraser(context);
    }
}
