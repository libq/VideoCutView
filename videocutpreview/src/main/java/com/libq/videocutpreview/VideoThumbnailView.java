package com.libq.videocutpreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * describ:绘制两头都可以移动的view
 * author:libq
 * date:2018.4.9
 */
public class VideoThumbnailView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private Paint mCursorPaint;
    private Paint mSliderPaint;
    private boolean canDrawCursor = true;
    private int mCursorX;
    private int mCursorWidth = 6;

    private Rect sliderLeftRectf;//左边滑块区域
    private Rect sliderRightRectf;//右边滑块
    private OnCutBorderScrollListener onCutBorderScrollListener;
    private OnTouchCutAreaListener onTouchCutAreaListener;
    private Context mContext;

    private int mTopBottomBorderWidth = 6;
    private int mBorderColor = Color.RED;
    private int mDragAreaWidth = 40;//可拖动区域宽度
    private int minLength = mDragAreaWidth/2+2;//最小长度//两个拖动条间的最小距离
    private int mSliderWidth = 6;//左右两个滑动块的宽度
    private boolean isDrawCursor = false;//是否需要画游标

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setTopBottomBorderWidth(int topBottomBorderWidth) {
        this.mTopBottomBorderWidth = topBottomBorderWidth;
    }


    public void setDragAreaWidth(int dragAreaWidth) {
        this.mDragAreaWidth = dragAreaWidth;
    }

    public void setSliderWidth(int sliderWidth) {
        this.mSliderWidth = sliderWidth;
    }

    public void setDrawCursor(boolean drawCursor) {
        isDrawCursor = drawCursor;
    }

    public VideoThumbnailView(Context context) {
        super(context);
        init(context);
    }

    public VideoThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int cursorWidth = 3;
    private void init(Context context) {
        mContext = context;
        mCursorPaint = new Paint();
        mCursorPaint.setAntiAlias(true);
        mCursorPaint.setStrokeWidth(cursorWidth);
        mCursorPaint.setColor(Color.RED);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mTopBottomBorderWidth);

        mSliderPaint = new Paint();
        mSliderPaint.setAntiAlias(true);
        mSliderPaint.setStrokeCap(Paint.Cap.SQUARE);
        mSliderPaint.setStyle(Paint.Style.FILL);
        mSliderPaint.setColor(mBorderColor);

    }


    /**
     * 设置边框颜色
     * @param borderColor
     */
    public void setBorderColor(int borderColor){
        mBorderColor = borderColor;
    }
    /**
     * 设置最小间隔
     * @param minPx
     */
    public void setMinLength(int minPx){
        if(mWidth>0 && minPx > mWidth){
            minPx = mWidth;
        }
        this.minLength = minPx;
    }

    public interface OnCutBorderScrollListener {
        void onScrollBorder(int start, int end);
    }

    public void setOnCutBorderScrollListener(OnCutBorderScrollListener listener){
        this.onCutBorderScrollListener = listener;
    }

    public void setOnTouchCutAreaListener(OnTouchCutAreaListener listener){
        onTouchCutAreaListener = listener;
    }

    /**
     * 点击裁剪区域监听
     */
    public interface OnTouchCutAreaListener {
        void onTouchUp();
        void onTouchDown();
    }

    public float getLeftInterval(){
        return sliderLeftRectf.left;
    }

    public float getRightInterval(){
        return sliderRightRectf.right;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            sliderLeftRectf = new Rect();
            sliderLeftRectf.left = 0;
            sliderLeftRectf.top = 0;
            sliderLeftRectf.right = mSliderWidth;
            sliderLeftRectf.bottom = mHeight;

            sliderRightRectf = new Rect();
            sliderRightRectf.left = mWidth - mSliderWidth;
            sliderRightRectf.top = 0;
            sliderRightRectf.right = mWidth;
            sliderRightRectf.bottom = mHeight;
        }
    }

    private float downX;
    private boolean scrollLeft;
    private boolean scrollRight;
    private int mScrollStartPosition,mScrollEndPosition;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        move(event);
        return scrollLeft || scrollRight;
    }

    boolean scrollChange;
    private boolean move(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canDrawCursor = false;

                downX = event.getX();
                if (downX > sliderLeftRectf.left- mDragAreaWidth/2 && downX < sliderLeftRectf.right+ mDragAreaWidth/2) {
                    scrollRight = false;
                    scrollLeft = true;
                }
                if (downX > sliderRightRectf.left- mDragAreaWidth/2 && downX < sliderRightRectf.right+ mDragAreaWidth /2) {
                    scrollLeft = false;
                    scrollRight = true;
                }
                if ((downX > sliderLeftRectf.left- mDragAreaWidth/2 && downX < sliderLeftRectf.right+ mDragAreaWidth /2)||
                        (downX > sliderRightRectf.left- mDragAreaWidth /2 && downX < sliderRightRectf.right+ mDragAreaWidth /2)) {
                    if(onTouchCutAreaListener !=null){
                        onTouchCutAreaListener.onTouchDown();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                canDrawCursor = false;
                float moveX = event.getX();

                float scrollX = moveX - downX;

                if (scrollLeft) {
                    sliderLeftRectf.left = sliderLeftRectf.left + (int)scrollX;
                    sliderLeftRectf.right = sliderLeftRectf.right + (int)scrollX;

                    if(sliderLeftRectf.left < 0){
                        sliderLeftRectf.left = 0;
                        sliderLeftRectf.right = mSliderWidth;
                    }
                   /* if(sliderLeftRectf.left > sliderRightRectf.right- minLength){
                        sliderLeftRectf.left = sliderRightRectf.right- minLength;
                        sliderLeftRectf.right = sliderLeftRectf.left+ mSliderWidth;
                    }*/
                    if(sliderLeftRectf.right > sliderRightRectf.left- minLength){
                        sliderLeftRectf.right = sliderRightRectf.left- minLength;
                        sliderLeftRectf.left = sliderLeftRectf.right - mSliderWidth;
                    }
                    scrollChange = true;
                    invalidate();
                } else if (scrollRight) {
                    sliderRightRectf.left = sliderRightRectf.left + (int)scrollX;
                    sliderRightRectf.right = sliderRightRectf.right + (int)scrollX;

                    if(sliderRightRectf.right > mWidth){
                        sliderRightRectf.right = mWidth;
                        sliderRightRectf.left = sliderRightRectf.right- mSliderWidth;
                    }
                    if(sliderRightRectf.left < sliderLeftRectf.left+ minLength){
                        sliderRightRectf.left = sliderLeftRectf.left+ minLength;
                        sliderRightRectf.right = sliderRightRectf.left + mSliderWidth;
                    }
                    scrollChange = true;
                    invalidate();
                }

                /*if(onCutBorderScrollListener != null){
                    onCutBorderScrollListener.onScrollBorder(sliderLeftRectf.left, sliderRightRectf.right);
                }*/
                mScrollStartPosition = sliderLeftRectf.left;
                mScrollEndPosition = sliderRightRectf.right;

                downX = moveX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                downX = 0;
                scrollLeft = false;
                scrollRight = false;
                if(scrollChange && onCutBorderScrollListener != null){
                    onCutBorderScrollListener.onScrollBorder(mScrollStartPosition,mScrollEndPosition);
                }
                if(onTouchCutAreaListener !=null){
                    onTouchCutAreaListener.onTouchUp();
                }
                scrollChange = false;
                canDrawCursor = true;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isDrawCursor) {
            if(canDrawCursor){
                canvas.drawRect(mCursorX,sliderLeftRectf.top-mTopBottomBorderWidth,mCursorX+mCursorWidth,sliderLeftRectf.bottom+mTopBottomBorderWidth,mCursorPaint);
               // canvas.drawLine(mCursorX, sliderLeftRectf.top-mTopBottomBorderWidth,mCursorX, sliderLeftRectf.bottom+mTopBottomBorderWidth,mCursorPaint);
            }
        }
        mPaint.setColor(mBorderColor);

        drawSlider(canvas, sliderLeftRectf);

        drawSlider(canvas,sliderRightRectf);

        canvas.drawLine(sliderLeftRectf.left, 0, sliderRightRectf.right, 0, mPaint);
        canvas.drawLine(sliderLeftRectf.left, mHeight, sliderRightRectf.right, mHeight, mPaint);

        mPaint.setColor(Color.parseColor("#99313133"));

        RectF rectF3 = new RectF();
        rectF3.left = 0;
        rectF3.top = 0;
        rectF3.right = sliderLeftRectf.left;
        rectF3.bottom = mHeight;
        canvas.drawRect(rectF3, mPaint);

        RectF rectF4 = new RectF();
        rectF4.left = sliderRightRectf.right;
        rectF4.top = 0;
        rectF4.right = mWidth;
        rectF4.bottom = mHeight;
        canvas.drawRect(rectF4, mPaint);



    }

    /**
     * 移动游标
     * @param x
     */
    public void moveCursor(int x){
        if (isDrawCursor) {
            mCursorX = x;
            invalidate();
        }
    }

    private void drawSlider(Canvas canvas,Rect rect){
        canvas.drawRect(rect,mSliderPaint);
    }
}