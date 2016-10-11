package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.example.wyyz.snapchat.util.TmpText;

/**
 * Created by Fallie on 06/10/2016.
 */

public class DrawTextView extends View {

    public DrawTextView(Context context, AttributeSet attrs,int i) {
        super(context, attrs,i);
        this.setDrawingCacheEnabled(true);

        this.setBackgroundDrawable(new BitmapDrawable(this.getDrawingCache()));
        //this. setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
    }



    @Override
    protected void onDraw(Canvas canvas) {
        String testString = TmpText.textContent;
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(40);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(testString, 0, testString.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(testString,getMeasuredWidth() / 2 - bounds.width() / 2, baseline+400, mPaint);
        Bitmap bitmap = this.getDrawingCache(true);
        if(bitmap != null){
        TmpPhotoView.photo = Bitmap.createBitmap(bitmap.copy(bitmap.getConfig(),false));
            }
        //this.destroyDrawingCache();

    }


}