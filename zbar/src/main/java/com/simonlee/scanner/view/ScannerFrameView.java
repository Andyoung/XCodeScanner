package com.simonlee.scanner.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.simonlee.scanner.R;

/**
 * @author Simon Lee
 * @e-mail jmlixiaomeng@163.com
 */

@SuppressWarnings("unused")
public final class ScannerFrameView extends View {

    /**
     * 扫描框宽占比（相对父容器的宽）
     */
    private float mFrameWidthRatio;

    /**
     * 扫描框高宽比（高/宽）
     */
    private float mFrameHWRatio;

    /**
     * 边框是否显示
     */
    private boolean isFrameLineVisible;

    /**
     * 边框颜色
     */
    private int mFrameLineColor;

    /**
     * 边框宽度
     */
    private int mFrameLineWidth;

    /**
     * 边角是否显示
     */
    private boolean isFrameCornerVisible;

    /**
     * 边角长度;
     * 与{@link #mFrameCornerLengthRatio}不兼容，以{@link #isMeasureFrameCornerLengthByRatio}判断
     */
    private int mFrameCornerLength;

    /**
     * 边角长占比（相对View本身的宽度）;
     * 与{@link #mFrameCornerLength}不兼容，以{@link #isMeasureFrameCornerLengthByRatio}判断
     */
    private float mFrameCornerLengthRatio;

    /**
     * 边角长度是否以“长占比”为准;
     * true for {@link #mFrameCornerLengthRatio};
     * false for {@link #mFrameCornerLength}
     */
    private boolean isMeasureFrameCornerLengthByRatio = false;

    /**
     * 边角宽度
     */
    private int mFrameCornerWidth;

    /**
     * 边角颜色
     */
    private int mFrameCornerColor;

    /**
     * 扫描线是否显示
     */
    private boolean isScanLineVisible;

    /**
     * 扫描线移动方向
     */
    private int mScanLineDirection;

    /**
     * 扫描线长度Padding
     * 与{@link #mScanLineLengthRatio}不兼容，以{@link #isMeasureScanLineLengthByRatio}判断
     */
    private int mScanLineLengthPadding;

    /**
     * 扫描线长占比（相对View本身的宽/高，依方向而定）
     * 与{@link #mScanLineLengthPadding}不兼容，以{@link #isMeasureScanLineLengthByRatio}判断
     */
    private float mScanLineLengthRatio;

    /**
     * 扫描线长度是否以“长占比”为准;
     * true for {@link #mScanLineLengthRatio};
     * false for {@link #mScanLineLengthPadding}
     */
    private boolean isMeasureScanLineLengthByRatio = false;

    /**
     * 扫描线宽度
     */
    private int mScanLineWidth;

    /**
     * 扫描线颜色
     */
    private int mScanLineColor;

    /**
     * 扫描周期（单位毫秒）
     */
    private int mScanCycle;

    /**
     * 扫描线移动轨迹起点
     */
    private int mTrailStart;

    /**
     * 扫描线移动轨迹终点
     */
    private int mTrailEnd;

    /**
     * 扫描线当前移动偏移量
     */
    private int mCurMoveOffset;

    /**
     * 扫描线移动轨迹与边框的Margin（相对边框的内边界）
     */
    private static final int TRAIL_MARGIN_FRAME = 5;

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 扫描动画
     */
    private ValueAnimator mScanAnimator;

    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_BOTTOM = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;

    /**
     * LayoutParams宽度
     */
    private int mLayoutWidth;

    /**
     * LayoutParams高度
     */
    private int mLayoutHeight;

    public ScannerFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ScannerFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        float density = getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScannerFrameView);

        this.isFrameLineVisible = typedArray.getBoolean(R.styleable.ScannerFrameView_frameLine_visible, true);
        this.mFrameLineWidth = typedArray.getDimensionPixelSize(R.styleable.ScannerFrameView_frameLine_width, (int) (density));
        this.mFrameLineColor = typedArray.getColor(R.styleable.ScannerFrameView_frameLine_color, Color.WHITE);

        this.mLayoutWidth = typedArray.getLayoutDimension(R.styleable.ScannerFrameView_android_layout_width, 0);//-2wrap_content -1match_parent
        this.mLayoutHeight = typedArray.getLayoutDimension(R.styleable.ScannerFrameView_android_layout_height, 0);//-2wrap_content -1match_parent

        this.mFrameWidthRatio = typedArray.getFloat(R.styleable.ScannerFrameView_frame_widthRatio, 0.7F);
        if (mFrameWidthRatio > 1) {
            mFrameWidthRatio = 1;
        }
        this.mFrameHWRatio = typedArray.getFloat(R.styleable.ScannerFrameView_frame_hwRatio, 1.0F);

        this.isFrameCornerVisible = typedArray.getBoolean(R.styleable.ScannerFrameView_frameCorner_visible, true);
        this.mFrameCornerLength = typedArray.getDimensionPixelSize(R.styleable.ScannerFrameView_frameCorner_length, 0);
        if (mFrameCornerLength <= 0) {
            this.isMeasureFrameCornerLengthByRatio = true;
            this.mFrameCornerLengthRatio = typedArray.getFloat(R.styleable.ScannerFrameView_frameCorner_lengthRatio, 0.1F);
        }
        this.mFrameCornerWidth = typedArray.getDimensionPixelSize(R.styleable.ScannerFrameView_frameCorner_width, (int) (3 * density));
        this.mFrameCornerColor = typedArray.getColor(R.styleable.ScannerFrameView_frameCorner_color, Color.BLUE);

        this.isScanLineVisible = typedArray.getBoolean(R.styleable.ScannerFrameView_scanLine_visible, true);
        this.mScanLineDirection = typedArray.getInt(R.styleable.ScannerFrameView_scanLine_direction, DIRECTION_BOTTOM);
        this.mScanLineLengthPadding = typedArray.getDimensionPixelSize(R.styleable.ScannerFrameView_scanLine_lengthPadding, -1);
        if (mScanLineLengthPadding < 0) {
            this.isMeasureScanLineLengthByRatio = true;
            this.mScanLineLengthRatio = typedArray.getFloat(R.styleable.ScannerFrameView_scanLine_lengthRatio, 0.98F);
        }
        this.mScanLineWidth = typedArray.getDimensionPixelSize(R.styleable.ScannerFrameView_scanLine_width, (int) (2 * density));
        this.mScanLineColor = typedArray.getColor(R.styleable.ScannerFrameView_scanLine_color, Color.BLUE);
        this.mScanCycle = typedArray.getInt(R.styleable.ScannerFrameView_scan_cycle, 1500);
        typedArray.recycle();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        mLayoutWidth = params.width;
        mLayoutHeight = params.width;
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mFrameWidthRatio > 0 && mLayoutWidth == ViewGroup.LayoutParams.WRAP_CONTENT && widthMode != MeasureSpec.EXACTLY) {
            widthSize = (int) (widthSize * mFrameWidthRatio);
        }
        if (mFrameHWRatio > 0 && mLayoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT && heightMode != MeasureSpec.EXACTLY) {
            heightSize = (int) (widthSize * mFrameHWRatio);
        }
        setMeasuredDimension(widthSize, heightSize);
        measureFrame();
    }

    /**
     * 计算扫描框各参数
     */
    public void measureFrame() {
        if (isFrameCornerVisible) {
            measureFrameCornerLength();//计算边角长度
        }
        if (isScanLineVisible) {
            boolean isVerticalDirection = mScanLineDirection != DIRECTION_LEFT && mScanLineDirection != DIRECTION_RIGHT;//是否为垂直上下扫描
            measureScanLineLengthPadding(isVerticalDirection);//计算扫描线长度Padding
            updateScanAnimator(isVerticalDirection);
        }
    }

    private void measureFrameCornerLength() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        if (isMeasureFrameCornerLengthByRatio) {//按比例计算边角长度(以自身宽度为准)
            mFrameCornerLength = (int) (measuredWidth * mFrameCornerLengthRatio);
        }
        int maxLength = Math.min(measuredWidth, measuredHeight);
        if (maxLength < mFrameCornerLength * 2) {//限定边角长度最大值不超过宽和高的1/2
            mFrameCornerLength = maxLength / 2;
        }
    }

    private void measureScanLineLengthPadding(boolean isVerticalDirection) {
        int maxLength = isVerticalDirection ? getMeasuredWidth() : getMeasuredHeight();//根据移动方向得到最大长度
        if (maxLength <= 0) {
            return;
        }
        if (isMeasureScanLineLengthByRatio) {//按比例计算长度
            mScanLineLengthPadding = (int) ((maxLength * (1 - mScanLineLengthRatio)) / 2);
        }
        if (mScanLineLengthPadding < 0) {//扫描线最大长度为maxLength，因此padding最小为0
            mScanLineLengthPadding = 0;
        }
    }

    private void updateScanAnimator(boolean isVerticalDirection) {
        if (mScanCycle <= 0 || mScanLineWidth <= 0) {
            cancelScanAnimator();
        } else {
            int maxDistance = isVerticalDirection ? getMeasuredHeight() : getMeasuredWidth();//根据移动方向得到最大移动距离
            int trailStart = TRAIL_MARGIN_FRAME + mFrameLineWidth;
            int trailEnd = maxDistance - trailStart - mScanLineWidth;
            if (trailStart < trailEnd) {
                startScanAnimator(trailStart, trailEnd);
            } else {
                cancelScanAnimator();
            }
        }
    }

    private void startScanAnimator(int start, int end) {
        mTrailStart = start;
        mTrailEnd = end;
        if (mScanAnimator == null) {
            mScanAnimator = new ValueAnimator();
            mScanAnimator.setIntValues(start, end);
            mScanAnimator.setDuration(mScanCycle);
            mScanAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mScanAnimator.setInterpolator(new LinearInterpolator());
            mScanAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurMoveOffset = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mScanAnimator.start();
        } else {
            mScanAnimator.setIntValues(start, end);
        }
    }

    private void cancelScanAnimator() {
        mTrailStart = 0;
        mTrailEnd = 0;
        if (mScanAnimator != null) {
            mScanAnimator.removeAllUpdateListeners();
            mScanAnimator.cancel();
            mScanAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = Math.min(getMeasuredWidth(), canvas.getWidth());
        int height = Math.min(getMeasuredHeight(), canvas.getHeight());

        //画扫描框的四条边线
        if (isFrameLineVisible && mFrameLineWidth > 0) {
            mPaint.setColor(mFrameLineColor);
            canvas.drawRect(0, 0, width, mFrameLineWidth, mPaint);//上边线
            canvas.drawRect(0, 0, mFrameLineWidth, height, mPaint);//左边线
            canvas.drawRect(width - mFrameLineWidth, 0, width, height, mPaint);//右边线
            canvas.drawRect(0, height - mFrameLineWidth, width, height, mPaint);//下边线
        }

        //画扫描框四个边角，共八个部分
        if (isFrameCornerVisible && mFrameCornerWidth > 0 && mFrameCornerLength > 0) {
            mPaint.setColor(mFrameCornerColor);
            canvas.drawRect(0, 0, mFrameCornerLength, mFrameCornerWidth, mPaint);//左上角横
            canvas.drawRect(0, 0, mFrameCornerWidth, mFrameCornerLength, mPaint);//左上角竖
            canvas.drawRect(0, height - mFrameCornerWidth, mFrameCornerLength, height, mPaint);//左下角横
            canvas.drawRect(0, height - mFrameCornerLength, mFrameCornerWidth, height, mPaint);//左下角竖
            canvas.drawRect(width - mFrameCornerLength, 0, width, mFrameCornerWidth, mPaint);//右上角横
            canvas.drawRect(width - mFrameCornerLength, height - mFrameCornerWidth, width, height, mPaint);//右下角横
            canvas.drawRect(width - mFrameCornerWidth, 0, width, mFrameCornerLength, mPaint);//右上角竖
            canvas.drawRect(width - mFrameCornerWidth, height - mFrameCornerLength, width, height, mPaint);//右下角竖
        }

        //画扫描线
        if (isScanLineVisible && mScanLineWidth > 0 && mTrailStart < mTrailEnd && mCurMoveOffset >= mTrailStart && mCurMoveOffset <= mTrailEnd) {
            mPaint.setColor(mScanLineColor);
            // 根据扫描方向，当前偏移量，确定位置绘制扫描线
            switch (mScanLineDirection) {
                case DIRECTION_TOP: {//从下往上
                    canvas.drawRect(mScanLineLengthPadding, height - mCurMoveOffset - mScanLineWidth, width - mScanLineLengthPadding, height - mCurMoveOffset, mPaint);
                    break;
                }
                case DIRECTION_BOTTOM: {//从上往下
                    canvas.drawRect(mScanLineLengthPadding, mCurMoveOffset, width - mScanLineLengthPadding, mCurMoveOffset + mScanLineWidth, mPaint);
                    break;
                }
                case DIRECTION_LEFT: {//从右往左
                    canvas.drawRect(mCurMoveOffset, mScanLineLengthPadding, mCurMoveOffset + mScanLineWidth, height - mScanLineLengthPadding, mPaint);
                    break;
                }
                case DIRECTION_RIGHT: {//从左往右
                    canvas.drawRect(width - mCurMoveOffset - mScanLineWidth, mScanLineLengthPadding, width - mCurMoveOffset, height - mScanLineLengthPadding, mPaint);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelScanAnimator();
        super.onDetachedFromWindow();
    }

    /**
     * 设置扫描宽占比（相对父容器的宽）
     *
     * @param frameWidthRatio 宽占比
     */
    public void setFrameWidthRatio(float frameWidthRatio) {
        if (frameWidthRatio > 1) {
            frameWidthRatio = 1;
        }
        this.mFrameWidthRatio = frameWidthRatio;
    }

    /**
     * 设置扫描框高宽比（高/宽）
     *
     * @param frameHWRatio 高宽比(height/width)
     */
    public void setFrameHWRatio(float frameHWRatio) {
        this.mFrameHWRatio = frameHWRatio;
    }

    /**
     * 设置是否显示边框
     *
     * @param frameLineVisible true:显示，false:隐藏
     */
    public void setFrameLineVisible(boolean frameLineVisible) {
        this.isFrameLineVisible = frameLineVisible;
    }

    /**
     * 设置边框宽度
     *
     * @param frameLineWidth 边框宽度，单位pixel
     */
    public void setFrameLineWidth(int frameLineWidth) {
        this.mFrameLineWidth = frameLineWidth;
    }

    /**
     * 设置边框颜色
     *
     * @param frameLineColor 十六进制色值
     */
    public void setFrameLineColor(@ColorInt int frameLineColor) {
        this.mFrameLineColor = frameLineColor;
    }

    /**
     * 设置是否显示边角
     *
     * @param frameCornerVisible true:显示，false:隐藏
     */
    public void setFrameCornerVisible(boolean frameCornerVisible) {
        this.isFrameCornerVisible = frameCornerVisible;
    }

    /**
     * 设置边角长度
     *
     * @param frameCornerLength 边角长度，单位pixel
     */
    public void setFrameCornerLength(int frameCornerLength) {
        this.isMeasureFrameCornerLengthByRatio = false;
        this.mFrameCornerLength = frameCornerLength;
    }

    /**
     * 设置边角长占比（相对View本身的宽）
     *
     * @param frameCornerLengthRatio 边角长占比
     */
    public void setframeCornerLengthRatio(float frameCornerLengthRatio) {
        this.isMeasureFrameCornerLengthByRatio = true;
        this.mFrameCornerLengthRatio = frameCornerLengthRatio;
    }

    /**
     * 设置边角宽度
     *
     * @param frameCornerWidth 边角宽度，单位pixel
     */
    public void setFrameCornerWidth(int frameCornerWidth) {
        this.mFrameCornerWidth = frameCornerWidth;
    }

    /**
     * 设置边角颜色
     *
     * @param frameCornerColor 十六进制色值
     */
    public void setFrameCornerColor(@ColorInt int frameCornerColor) {
        this.mFrameCornerColor = frameCornerColor;
    }

    /**
     * 设置是否显示扫描线
     *
     * @param scanLineVisible true:显示，false:隐藏
     */
    public void setScanLineVisible(boolean scanLineVisible) {
        this.isScanLineVisible = scanLineVisible;
    }

    /**
     * 设置扫描线移动方向
     *
     * @param scanLineDirection DIRECTION_TOP:从下往上
     *                          DIRECTION_BOTTOM:从上往下
     *                          DIRECTION_LEFT:从右往左
     *                          DIRECTION_RIGHT:从左往右
     */
    public void setScanLineDirection(int scanLineDirection) {
        switch (scanLineDirection) {
            case DIRECTION_TOP:
            case DIRECTION_BOTTOM:
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT: {
                this.mScanLineDirection = scanLineDirection;
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 设置扫描线长度Padding
     *
     * @param scanLineLengthPadding 扫描线长度Padding，单位pixel
     */
    public void setScanLineLengthPadding(int scanLineLengthPadding) {
        this.isMeasureScanLineLengthByRatio = false;
        this.mScanLineLengthPadding = scanLineLengthPadding;
    }

    /**
     * 设置扫描线长占比（相对扫描框的宽/高，依扫描方向而定）
     *
     * @param scanLineLengthRatio 扫描线长占比
     */
    public void setScanLineLengthRatio(float scanLineLengthRatio) {
        this.isMeasureScanLineLengthByRatio = true;
        this.mScanLineLengthRatio = scanLineLengthRatio;
    }

    /**
     * 设置扫描线宽度
     *
     * @param scanLineWidth 扫描线宽度，单位pixel
     */
    public void setScanLineWidth(int scanLineWidth) {
        this.mScanLineWidth = scanLineWidth;
    }

    /**
     * 设置扫描线颜色
     *
     * @param scanLineColor 十六进制色值
     */
    public void setScanLineColor(@ColorInt int scanLineColor) {
        this.mScanLineColor = scanLineColor;
    }

    /**
     * 设置扫描线移动周期
     *
     * @param scanLineCycle 周期，单位msec
     */
    public void setScanLineCycle(int scanLineCycle) {
        this.mScanCycle = scanLineCycle;
        if (mScanAnimator != null) {
            long curPlayTime = mScanAnimator.getCurrentPlayTime();
            long curDuration = mScanAnimator.getDuration();
            long newPlayTime = curPlayTime / curDuration * scanLineCycle;
            mScanAnimator.setDuration(scanLineCycle);
            mScanAnimator.setCurrentPlayTime(newPlayTime);
        }
    }

}
