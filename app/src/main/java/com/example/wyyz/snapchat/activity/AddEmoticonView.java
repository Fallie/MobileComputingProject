package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.wyyz.snapchat.util.TmpPhotoView;

import static com.example.wyyz.snapchat.R.drawable.confused;
import static com.example.wyyz.snapchat.R.drawable.happy1;
import static com.example.wyyz.snapchat.R.drawable.happy2;
import static com.example.wyyz.snapchat.R.drawable.love;
import static com.example.wyyz.snapchat.R.drawable.relieved;
import static com.example.wyyz.snapchat.R.drawable.sad;
import static com.example.wyyz.snapchat.R.drawable.sick;
import static com.example.wyyz.snapchat.R.drawable.wink;
import static com.example.wyyz.snapchat.R.drawable.winking;

/**
 * Created by Fallie on 06/10/2016.
 */

public class AddEmoticonView extends View {


    Bitmap mBitmap;	  // image of emoticon
    float mStartX;			  // x of top left corner of bitmap
    float mStartY;			  // y of top left corner of bitmap
    float mCurrentX; 		  // current x coordinate of emoticon
    float mCurrentY; 		  // current y coordinate of emoticon
    float mActionDownX;   	  // x coordinate of emoticon of an action down
    float mActionDownY;   	  // y coordinate of emoticon of an action down

    private boolean mDrawingEnabled = true;


    public AddEmoticonView(Context context, AttributeSet atrs,int id) {
        super(context,atrs,id);
        this.setDrawingCacheEnabled(true);

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
                mBitmap = BitmapFactory.decodeResource(getResources(), wink);
                break;
            case 9:
                mBitmap = BitmapFactory.decodeResource(getResources(), winking);
                break;
            default:
                break;
        }


        mStartX = 500f;
        mStartY = 500f;
        mCurrentX = 500f;
        mCurrentY = 500f;

        this.setBackgroundDrawable(new BitmapDrawable(this.getDrawingCache()));
        //this. setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
    }
    float getCurrentX()       { return mCurrentX; }
    void setCurrentX(float x) { mCurrentX = x; }

    float getCurrentY()       { return mCurrentY; }
    void setCurrentY(float y) { mCurrentY = y; }

    float getActionDownX()       { return mActionDownX; }
    void setActionDownX(float x) { mActionDownX = x; }

    float getActionDownY()       { return mActionDownY; }
    void setActionDownY(float y) { mActionDownY = y; }



    public void draw(Canvas canvas){
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

            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if ( mDrawingEnabled ) {
            handleOnTouchEvent(event);
            return true;
        }
        else
            return true;
    }


    public void restoreInitialPositions() {
        restoreInitialPosition();
    }

    public void enableDrawing() {
        mDrawingEnabled = true;
    }

    public void disableDrawing() {
        mDrawingEnabled = false;
    }

    void handleOnTouchEvent(MotionEvent me) {
        final float me_x = me.getX();
        final float me_y = me.getY();

        final int action = me.getAction();
        switch ( action ) {
            case MotionEvent.ACTION_DOWN:
                this.setActionDownX(this.getCurrentX());
                this.setActionDownY(this.getCurrentY());

                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                this.setCurrentX(me.getX());
                this.setCurrentY(me.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                this.restoreInitialPosition();
                break;
        }
        Bitmap bitmap = this.getDrawingCache(true);
        if(bitmap != null){
            TmpPhotoView.photo = Bitmap.createBitmap(bitmap.copy(bitmap.getConfig(),false));
            //this.destroyDrawingCache();
        }
    }

    void restoreInitialPosition() {
        mCurrentX = mStartX;
        mCurrentY = mStartY;
    }

}
