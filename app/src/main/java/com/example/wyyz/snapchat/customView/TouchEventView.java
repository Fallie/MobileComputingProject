package com.example.wyyz.snapchat.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.example.wyyz.snapchat.util.TmpText;

import static com.example.wyyz.snapchat.R.drawable.confused;
import static com.example.wyyz.snapchat.R.drawable.happy1;
import static com.example.wyyz.snapchat.R.drawable.happy2;
import static com.example.wyyz.snapchat.R.drawable.love;
import static com.example.wyyz.snapchat.R.drawable.relieved;
import static com.example.wyyz.snapchat.R.drawable.sad;
import static com.example.wyyz.snapchat.R.drawable.sick;
import static com.example.wyyz.snapchat.R.drawable.winkvv;
import static com.example.wyyz.snapchat.R.drawable.winking;

/**TypeCode = 100: free-hand-draw
 * TypeCode = 200: Add-text
 * TypeCode = 300: Add emoticon
 * Created by Fallie on 06/10/2016.
 */

public class TouchEventView extends View {
    private static final String TAG = "TouchEventView";
    private Paint paint;
    private Path path = new Path();
    int type;
    Bitmap mBitmap;	  // image of emoticon
    int id;             //id the emoticon
    float mStartX;			  // x of top left corner of bitmap
    float mStartY;			  // y of top left corner of bitmap
    float mCurrentX; 		  // current x coordinate of emoticon
    float mCurrentY; 		  // current y coordinate of emoticon
    float mActionDownX;   	  // x coordinate of emoticon of an action down
    float mActionDownY;   	  // y coordinate of emoticon of an action down
    Boolean isFirstDraw = true;

    private boolean mDrawingEnabled = true;

    public TouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStartX = 500f;
        mStartY = 500f;

        mCurrentX = 500f;
        mCurrentY = 500f;


        //this.setBackgroundDrawable(new BitmapDrawable(this.getDrawingCache()));
        this. setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
    }

    public void setTypeCode(int t){
        this.type = t;
    }

    public void setFirstDraw(Boolean b){
        this.isFirstDraw = b;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(this.type == 100){
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawPath(path, paint);
            paint = null;
        }
        if(this.type == 200) {
            String testString = TmpText.textContent;
            paint = new Paint();
            paint.setStrokeWidth(5);
            paint.setTextSize(50);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.LEFT);
            Rect bounds = new Rect();
            paint.getTextBounds(testString, 0, testString.length(), bounds);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(testString,getMeasuredWidth() / 2 - bounds.width() / 2, baseline+400, paint);
            paint = null;
            TmpPhotoView.photo = getBitmapFromView(this);
        }
        if(this.type == 300){
            if(isFirstDraw){
                //canvas.drawBitmap(mBitmap, mStartX, mStartY, null);
                invalidate();
            }
            else{
                if ( mCurrentX < 0f ) mCurrentX = mStartX;

                if ( (mCurrentX + mBitmap.getWidth()) >= 960f) {
                    mCurrentX = 960f;
                }

                if ( mCurrentY < 0f ) mCurrentY = mStartY;

                if ( (mCurrentY + mBitmap.getHeight()) >= 1450f ) {
                    mCurrentY = 1450f;
                }

                if (mDrawingEnabled) {
                    canvas.drawBitmap(mBitmap, mCurrentX, mCurrentY, null);
                    //invalidate();
                }
            }

        }
    }

    public void setBitmap(int id){
        switch (id){
            case 1:
                mBitmap = BitmapFactory.decodeResource(getResources(), sad);
                break;
            case 2:
                mBitmap = BitmapFactory.decodeResource(getResources(), happy1);
                break;
            case 3:
                mBitmap = BitmapFactory.decodeResource(getResources(), happy2);
                break;
            case 4:
                mBitmap = BitmapFactory.decodeResource(getResources(), love);
                break;
            case 5:
                mBitmap = BitmapFactory.decodeResource(getResources(), confused);
                break;
            case 6:
                mBitmap = BitmapFactory.decodeResource(getResources(), relieved);
                break;
            case 7:
                mBitmap = BitmapFactory.decodeResource(getResources(), sick);
                break;
            case 8:
                mBitmap = BitmapFactory.decodeResource(getResources(), winkvv);
                break;
            case 9:
                mBitmap = BitmapFactory.decodeResource(getResources(), winking);
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(type == 100) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            TmpPhotoView.photo = getBitmapFromView(this);
            invalidate();
        }
        if(type == 300){
            if ( mDrawingEnabled ) {
                if(isFirstDraw)
                isFirstDraw = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        this.setActionDownX(this.getCurrentX());
                        this.setActionDownY(this.getCurrentY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        this.setCurrentX(event.getX());
                        this.setCurrentY(event.getY());
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        this.restoreInitialPosition();
                    default:
                        return false;
                }
                TmpPhotoView.photo = getBitmapFromView(this);
                invalidate();
            }
        }
        return true;
    }

    float getCurrentX()       { return mCurrentX; }
    void setCurrentX(float x) { mCurrentX = x; }

    float getCurrentY()       { return mCurrentY; }
    void setCurrentY(float y) { mCurrentY = y; }

    float getActionDownX()       { return mActionDownX; }
    void setActionDownX(float x) { mActionDownX = x; }

    float getActionDownY()       { return mActionDownY; }
    void setActionDownY(float y) { mActionDownY = y; }

    private void restoreInitialPosition() {
        mCurrentX = mStartX;
        mCurrentY = mStartY;
    }

    private Bitmap getBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.i(TAG, "failed getViewBitmap(" + v + ")");
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }


}