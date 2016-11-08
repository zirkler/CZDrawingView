package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;

public interface CZIDrawingAction {
    public void touchStart(float x, float y);

    public void touchMove(float x, float y);

    public void touchUp(float x, float y);

    public Path getPath();

    public void draw(Canvas canvas, Canvas cacheCanvas);

    public CZIDrawingAction createInstance(Context context);
}
