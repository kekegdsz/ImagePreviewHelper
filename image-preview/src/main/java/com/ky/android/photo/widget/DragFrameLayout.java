package com.ky.android.photo.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DragFrameLayout extends FrameLayout {
    private final Matrix mSuppMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];
    private static float DEFAULT_MIN_SCALE = 1.0f;
    private float mMinScale = DEFAULT_MIN_SCALE;
    // Max touches used in current gesture
    private int maxTouchCount;
    public DragFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
            case MotionEvent.ACTION_DOWN:
                maxTouchCount = event.getPointerCount();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取当前最大的手指操作数量
     *
     * @return 返回当前最大的手指操作数量
     */
    public int getMaxTouchCount() {
        return maxTouchCount;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow
                (getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    public float getMinimumScale() {
        return mMinScale;
    }
}
