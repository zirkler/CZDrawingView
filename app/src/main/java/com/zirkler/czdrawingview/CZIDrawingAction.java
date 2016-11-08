package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public interface CZIDrawingAction {
    public void touchStart(float x, float y);

    public void touchMove(float x, float y);

    public void touchUp(float x, float y);

    public void draw(Canvas canvas);

    public CZIDrawingAction createInstance(Context context, Paint paint);

    public Path getPath();

    public Paint getPaint();

    public void setPaint(Paint paint);

    public boolean isErasable();
}
