package com.hl.rollingbaby.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 *将波浪的纵坐标增加一段高度，即在3道波浪下绘制一个正方形
 */
class Solid extends View {

    private static final String TAG = "Solid";
    private boolean isDrawing = false;

    private Paint aboveWavePaint;
    private Paint blowWavePaint;
    private Paint onWavePaint;

    public Solid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        setLayoutParams(params);
    }

    public void setAboveWavePaint(Paint aboveWavePaint) {
        this.aboveWavePaint = aboveWavePaint;
    }

    public void setBlowWavePaint(Paint blowWavePaint) {
        this.blowWavePaint = blowWavePaint;
    }

    public void setOnWavePaint(Paint onWavePaint) {
        this.onWavePaint = onWavePaint;
    }

    public void setDrawStation(boolean isDrawing) {
        this.isDrawing = isDrawing;
        invalidate();
        requestLayout();
    }

    /**
     * 绘制3条波浪下的正方形以其提升高度
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        if (isDrawing) {
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), blowWavePaint);
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), aboveWavePaint);
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), onWavePaint);
        }
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


}
