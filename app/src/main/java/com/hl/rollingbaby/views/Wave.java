package com.hl.rollingbaby.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.hl.rollingbaby.R;

/**
 * 音乐播放界面的背景波浪，实际为几条不同的正弦波，频率、峰值、相位、透明度均不同
 * y=Asin(ωx+φ)+k
 */
class Wave extends View {

    private static final String TAG = "Wave";
    private boolean isDrawing = false;

    //3种不同的峰值
    private final int WAVE_HEIGHT_LARGE = 60;
    private final int WAVE_HEIGHT_MIDDLE = 40;
    private final int WAVE_HEIGHT_LITTLE = 20;

    //3种不同的波长
    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    //3种不同的频率
    private final float WAVE_HZ_FAST = 0.15f;
    private final float WAVE_HZ_NORMAL = 0.1f;
    private final float WAVE_HZ_SLOW = 0.05f;

    //3种不同的透明度
    public final int DEFAULT_ABOVE_WAVE_ALPHA = 70;
    public final int DEFAULT_ON_WAVE_ALPHA = 60;
    public final int DEFAULT_BLOW_WAVE_ALPHA = 50;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();
    private Path mOnWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();
    private Paint mOnWavePaint = new Paint();

    //3种不同的颜色
    private int mAboveWaveColor;
    private int mBlowWaveColor;
    private int mOnWaveColor;

    private float mWaveMultiple;    //相乘系数
    private float mWaveLength;      //波长
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    // 波浪动画
    private float mAboveOffset;
    private float mBlowOffset;
    private float mOnOffset;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDrawStation(boolean isDrawing) {
        this.isDrawing = isDrawing;
        invalidate();     //通知系统界面发生改变
        requestLayout(); //刷新界面
    }

    @Override
    public void draw(Canvas canvas) {
        if (isDrawing) {
            //绘制3条不同的波浪
            canvas.drawPath(mAboveWavePath, mAboveWavePaint);
            canvas.drawPath(mOnWavePath, mOnWavePaint);
            canvas.drawPath(mBlowWavePath, mBlowWavePaint);
        }
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public void setOnWaveColor(int onWaveColor) {
        this.mOnWaveColor = onWaveColor;
    }

    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
    }

    public Paint getOnWavePaint() {
        return mOnWavePaint;
    }

    /**
     * 初始化波浪大小
     * @param waveMultiple 相乘系数
     * @param waveHeight   高度
     * @param waveHz        频率
     */
    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        mAboveOffset = mWaveHeight * 0.7f;
        mOnOffset = mWaveHeight * 0.6f;
        mBlowOffset = mWaveHeight * 0.2f;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
        setLayoutParams(params);
    }

    /**
     * 初始化画笔
     */
    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mOnWavePaint.setColor(mOnWaveColor);
        mOnWavePaint.setAlpha(DEFAULT_ON_WAVE_ALPHA);
        mOnWavePaint.setStyle(Paint.Style.FILL);
        mOnWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case WaveView.MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HEIGHT_LARGE;
            case WaveView.MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HZ_FAST;
            case WaveView.MIDDLE:
                return WAVE_HZ_NORMAL;
            case WaveView.LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    /**
     * calculate wave track
     * 计算波浪路径
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();
        mOnWavePath.reset();

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);//slowest,lowest,longest;最慢、最低、最长的波
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (0.6 * mWaveHeight * Math.sin(0.5 * omega * x + mAboveOffset) + mWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mOnWavePath.moveTo(left, bottom);//fastest,2ed high;最快、第二高的波
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (0.8 * mWaveHeight * Math.sin(0.8 * omega * x + mOnOffset) + mWaveHeight);
            mOnWavePath.lineTo(x, y);
        }
        mOnWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);//highest,2ed fast;最高、第二快的波
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(0.6 * omega * x + mBlowOffset) + mWaveHeight);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveLength == 0) {
                startWave();
            }
        }
    }

    /**
     * 开始绘制波浪
     */
    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom();
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }

        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }

        if (mOnOffset > Float.MAX_VALUE - 100) {
            mOnOffset = 0;
        } else {
            mOnOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);   //计算延时
            }
        }
    }

}
