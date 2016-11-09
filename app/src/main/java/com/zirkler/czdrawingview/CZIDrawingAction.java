package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;

public interface CZIDrawingAction {
    public void touchStart(float x, float y);

    public void touchMove(float x, float y);

    public void touchUp(float x, float y);

    public void draw(Canvas canvas);

    public CZIDrawingAction createInstance(Context context, CZPaint paint);

    public CZPath getPath();

    public CZPaint getPaint();

    public void setPaint(CZPaint paint);

    public boolean isErasable();

    public boolean checkBounds(float x, float y);

}
