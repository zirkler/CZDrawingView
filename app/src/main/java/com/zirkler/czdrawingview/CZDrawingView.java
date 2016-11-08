package com.zirkler.czdrawingview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class CZDrawingView extends ImageView implements View.OnTouchListener {

    private static final String TAG = CZDrawingView.class.getSimpleName();
    private Bitmap mScribble, mBitmapImage;
    private Canvas mCacheCanvas;
    private RectF mCurrentRect;
    private int mViewWidth;
    private int mViewHeight;
    private CZIDrawingAction mCurrentDrawingAction;
    private List<CZIDrawingAction> mDrawnStuff;
    private List<CZIDrawingAction> mUndoneStuff;

    public CZDrawingView(Context context) {
        super(context);
        setup();
    }

    public CZDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CZDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        return dest;
    }

    public static int dip2pixel(Context context, float n){
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
        return value;
    }

    public void setup() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mDrawnStuff = new ArrayList<>();
        mUndoneStuff = new ArrayList<>();
        mCurrentDrawingAction = new CZDrawingActionFreehand(getContext());
    }

    private void touch_start(float x, float y) {
        mCurrentDrawingAction.touchStart(x, y);
        invalidate();
    }

    private void touch_move(float x, float y) {
        mCurrentDrawingAction.touchMove(x, y);
        invalidate();
    }

    private void touch_up(float x, float y) {
        if (x != -1 && y != -1) {
            mCurrentDrawingAction.touchUp(x, y);

            // If we have drawn after a undo happened, clear the items from the undone list,
            // so we don't mix up the undo/redo functionality.
            if (mUndoneStuff.size() > 0) {
                mUndoneStuff.clear();
            }
            mDrawnStuff.add(mCurrentDrawingAction);
            mCurrentDrawingAction = mCurrentDrawingAction.createInstance(getContext());
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // initially clear the canvas
        canvas.drawColor(Color.WHITE);
        mCacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Draw existing drawings
        for (int i = 0; i < mDrawnStuff.size(); i++) {
            CZIDrawingAction drawingAction = mDrawnStuff.get(i);
            drawingAction.draw(canvas, mCacheCanvas);
        }

        // Draw the current (not yet finished) path.
        if (mCurrentDrawingAction != null) {
            mCurrentDrawingAction.draw(canvas, mCacheCanvas);
        }

        if (mBitmapImage != null) {
            canvas.drawBitmap(mBitmapImage, null, mCurrentRect, null);
        }

        canvas.drawBitmap(mScribble, 0, 0, null);
    }

    public void undo() {
        if (mDrawnStuff.size() > 0) {
            CZIDrawingAction undoItem = mDrawnStuff.get(mDrawnStuff.size() - 1);
            mUndoneStuff.add(undoItem);
            mDrawnStuff.remove(undoItem);
            invalidate();
        }
    }

    public void redo() {
        if (mUndoneStuff.size() > 0) {
            CZIDrawingAction redoItem = mUndoneStuff.get(mUndoneStuff.size() - 1);
            mDrawnStuff.add(redoItem);
            mUndoneStuff.remove(redoItem);
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up(x, y);
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCurrentRect = new RectF(0, 0, w, h);
        mScribble = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCacheCanvas = new Canvas(mScribble);
    }

    @Override
    public void setImageBitmap(Bitmap bmp) {
        if (bmp.getWidth() > bmp.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }

        this.mBitmapImage = scaleCenterCrop(bmp, mViewHeight, mViewWidth);
        invalidate();
    }

    public CZIDrawingAction getCurrentDrawingAction() {
        return mCurrentDrawingAction;
    }

    public void setCurrentDrawingAction(CZIDrawingAction mCurrentDrawingAction) {
        this.mCurrentDrawingAction = mCurrentDrawingAction;
    }
}