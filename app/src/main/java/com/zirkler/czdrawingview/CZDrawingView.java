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

    // The CLICK_CANCEL_DISTANCE describes how far the user have to move away
    // his finger after a touch_down event to cancel the click. This value is in fixel.
    public static int CLICK_CANCEL_DISTANCE = 20;


    private Bitmap mScribble, mBitmapImage;
    private Canvas mCacheCanvas;
    private RectF mCurrentRect;
    private int mViewWidth;
    private int mViewHeight;
    private CZIDrawingAction mCurrentDrawingAction;
    private List<CZIDrawingAction> mDrawnStuff;
    private List<CZIDrawingAction> mUndoneStuff;
    private CZIDrawingAction touchedItem;
    private OnItemClickCallback mClickCallback;
    private float touchDownSourceY = -1;
    private float touchDownSourceX = -1;

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
        mCurrentDrawingAction = new CZDrawingActionFreehand(getContext(), null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // initially clear the canvas
        canvas.drawColor(Color.WHITE);
        mCacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Draw existing drawings, including easin
        for (int i = 0; i < mDrawnStuff.size(); i++) {
            CZIDrawingAction drawingAction = mDrawnStuff.get(i);
            if (drawingAction.isErasable()) {
                drawingAction.draw(mCacheCanvas);
            }
        }

        // Draw the current (not yet finished) path.
        if (mCurrentDrawingAction != null) {
            mCurrentDrawingAction.draw(mCacheCanvas);
        }

        // Draw non-erasable stuff
        for (int i = 0; i < mDrawnStuff.size(); i++) {
            CZIDrawingAction drawingAction = mDrawnStuff.get(i);
            if (!drawingAction.isErasable()) {
                drawingAction.draw(mCacheCanvas);
            }
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

        // TOUCH DOWN
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchDownSourceX = x;
            touchDownSourceY = y;

            // check if any drawn item got touched, starting from the end to respect drawn order
            for (int i = mDrawnStuff.size() - 1; i >= 0 ; i--) {
                CZIDrawingAction item = mDrawnStuff.get(i);
                if (item.checkBounds(x, y)) {
                    touchedItem = item;
                    break;
                }
            }

            if (touchedItem != null) {
                // user touched item
            } else {
                // Normal touch_down, user just started drawing
                mCurrentDrawingAction.touchStart(x, y);
                invalidate();
            }
        }

        // TOUCH MOVE
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (touchedItem != null) {
                // user touched item and then moved
                // if user moved away from its touched item, he likes to draw, with the touched item as origin
                if (!touchedItem.checkBounds(x, y)) {
                    mCurrentDrawingAction.touchStart(touchDownSourceX, touchDownSourceY);
                    mCurrentDrawingAction.touchMove(x, y);
                    touchedItem = null;
                }
            } else {
                // Normal movement, user is currently drawing
                mCurrentDrawingAction.touchMove(x, y);
            }
            invalidate();
        }

        // TOUCH UP
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (touchedItem != null) {
                // Check if user canceled his click by moving away from the click source
                if (touchedItem.checkBounds(x, y)) {
                    if (mClickCallback != null) {
                        mClickCallback.onItemClicked(touchedItem);
                    }
                }
            } else {
                // Normal touch up, user just ended his drawing
                touch_up(x, y);
            }
            touchedItem = null;
        }

        return true;
    }

    private void touch_up(float x, float y) {
        if (x != -1 && y != -1) {
            mCurrentDrawingAction.touchUp(x, y);

            // If we have drawn after a undo happened, clear the items from the undone list,
            // so we don't mix up the undo/redo functionality.
            if (mUndoneStuff.size() > 0) {
                mUndoneStuff.clear();
            }

            // The current drawingAction finished, add it to the drawn stuff list
            mDrawnStuff.add(mCurrentDrawingAction);

            // So the current drawingAction finished, when the user touches the drawingView the next time,
            // we start drawing with a new instance of the same drawingAction.
            mCurrentDrawingAction = mCurrentDrawingAction.createInstance(getContext(), mCurrentDrawingAction.getPaint());
        }
        invalidate();
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

    public OnItemClickCallback getmClickCallback() {
        return mClickCallback;
    }

    public void setClickCallback(OnItemClickCallback mClickCallback) {
        this.mClickCallback = mClickCallback;
    }

    public interface OnItemClickCallback {
        public void onItemClicked(CZIDrawingAction item);
    }
}