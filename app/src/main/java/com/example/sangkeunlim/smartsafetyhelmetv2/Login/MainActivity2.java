package com.example.sangkeunlim.smartsafetyhelmetv2.Login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;


/**
 * Created by donggun on 2018-03-20.
 */

public class MainActivity2 extends View {
    private final static int FPS = 33;
    private Bitmap mBitmap;
    private Rect mRect;
    private int mPosition;
    private int mDistanceLimit;
    private int mMaxSize;
    private boolean isMovingLeft;
    private android.os.Handler mHandler;

    public MainActivity2(Context context) {
        this(context, null);
    }

    public MainActivity2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializedBackground();
    }

    private void initializedBackground() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.harmonious_family);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isMovingLeft) {
                    mPosition--;
                    if (mPosition < -mDistanceLimit) {
                        mPosition = -mDistanceLimit;
                        isMovingLeft = false;
                    }
                } else {
                    mPosition++;
                    if (mPosition > 0) {
                        mPosition = 0;
                        isMovingLeft = true;
                    }
                }
                mRect.left = mPosition;
                mRect.right = mMaxSize + mPosition;
                invalidate();
                mHandler.sendEmptyMessageDelayed(0, FPS);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0,FPS);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if(mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxSize = w > h ? w : h;
        mRect = new Rect(0,0,mMaxSize, mMaxSize);
        mPosition = 0;
        mDistanceLimit = w > h ? w - h : h - w;
        isMovingLeft = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isInEditMode()) {
            return;
        }
        if(mBitmap != null && mRect != null) {
            canvas.drawBitmap(mBitmap, null, mRect, null);
        }
    }
}
