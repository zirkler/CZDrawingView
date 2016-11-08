package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class CZDrawingActionFreehand implements CZIDrawingAction {

    Path mPath;
    Context mContext;
    Paint mRedPaint;
    float mX;
    float mY;

    public CZDrawingActionFreehand(Context context) {
        mContext = context;
        mRedPaint = new Paint();
        mPath = new Path();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setColor(Color.RED);
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeJoin(Paint.Join.ROUND);
        mRedPaint.setStrokeCap(Paint.Cap.ROUND);
        mRedPaint.setStrokeWidth(CZDrawingView.dip2pixel(mContext, 5));
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
    public void draw(Canvas canvas, Canvas cacheCanvas) {
        cacheCanvas.drawPath(mPath, mRedPaint);
    }

    @Override
    public CZIDrawingAction createInstance(Context context) {
        return new CZDrawingActionFreehand(context);
    }
}
