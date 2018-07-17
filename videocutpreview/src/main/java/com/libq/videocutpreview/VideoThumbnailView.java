package com.libq.videocutpreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
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
    private int mCursorWidth = 10;
    private int interHeight;

    private Rect sliderLeftRectf;//左边滑块区域
    private Rect sliderRightRectf;//右边滑块
    //
    private OnCutBorderScrollListener onCutBorderScrollListener;
    private OnTouchCutAreaListener onTouchCutAreaListener;
    private Context mContext;

    private int mTopBottomBorderWidth = 6;
    private int mBorderColor = Color.RED;
    private int mDragAreaWidth = 40;//可拖动区域宽度
    private int minLength = mDragAreaWidth/2;//最小长度//两个拖动条间的最小距离
    private int mSliderWidth = 28;//左右两个滑动块的宽度
    private int topSpace = 10;

    private RectF leftInterRect;
    private RectF rightInterRect;//右边拖动条内部
    private Paint sliderInterPaint;
    private int sliderInterHeight;
    private int sliderInterWidth=mSliderWidth/5;
    private OnScrollCursorListener onScrollCursorListener;

    private boolean isDrawCursor = false;
    private RectF cursorRect;

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setTopBottomBorderWidth(int topBottomBorderWidth) {
        this.mTopBottomBorderWidth = topBottomBorderWidth;
    }

    public void setOnScrollCursorListener(OnScrollCursorListener onScrollCursorListener) {
        this.onScrollCursorListener = onScrollCursorListener;
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

//    private int cursorWidth = 6;
    private void init(Context context) {
        mContext = context;
        mCursorPaint = new Paint();
        mCursorPaint.setAntiAlias(true);
        mCursorPaint.setStrokeWidth(mCursorWidth);
        mCursorPaint.setColor(Color.WHITE);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mTopBottomBorderWidth);

        mSliderPaint = new Paint();
        mSliderPaint.setAntiAlias(true);
        mSliderPaint.setStrokeCap(Paint.Cap.SQUARE);
        mSliderPaint.setStyle(Paint.Style.FILL);
        mSliderPaint.setColor(Color.RED);

        sliderInterPaint = new Paint();
        sliderInterPaint.setAntiAlias(true);
        sliderInterPaint.setStrokeCap(Paint.Cap.ROUND);
        sliderInterPaint.setStyle(Paint.Style.FILL);
        sliderInterPaint.setColor(Color.WHITE);


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

    /**
     * 裁剪的左边界
     * @return
     */
    public float getLeftInterval(){
        return sliderLeftRectf.right;
    }

    /**
     * 裁剪的右边界
     * @return
     */
    public float getRightInterval(){
        return sliderRightRectf.left;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();
            interHeight = mHeight/4;

            sliderLeftRectf = new Rect();
            sliderLeftRectf.left = 0;
            sliderLeftRectf.top = topSpace;
            sliderLeftRectf.right = mSliderWidth;
            sliderLeftRectf.bottom = mHeight-topSpace;

            sliderRightRectf = new Rect();
            sliderRightRectf.left = mWidth - mSliderWidth;
            sliderRightRectf.top = topSpace;
            sliderRightRectf.right = mWidth;
            sliderRightRectf.bottom = mHeight-topSpace;

            leftInterRect = new RectF();
            leftInterRect.left = sliderLeftRectf.left + mSliderWidth/2 - sliderInterWidth/2;
            leftInterRect.right =sliderLeftRectf.left + mSliderWidth/2 + sliderInterWidth/2;
            leftInterRect.top = sliderLeftRectf.top + interHeight;
            leftInterRect.bottom = sliderLeftRectf.bottom - interHeight;

            rightInterRect = new RectF();
            rightInterRect.left = sliderRightRectf.left + mSliderWidth/2 - sliderInterWidth/2;
            rightInterRect.right =sliderRightRectf.left + mSliderWidth/2 + sliderInterWidth/2;
            rightInterRect.top = sliderRightRectf.top + interHeight;
            rightInterRect.bottom = sliderRightRectf.bottom - interHeight;

            cursorRect = new RectF();
            cursorRect.left = 0;
            cursorRect.top = 0;
            cursorRect.right = mCursorWidth;
            cursorRect.bottom = mHeight;
        }
    }

    private float downX;
    private boolean scrollLeft;
    private boolean scrollRight;
    private int mScrollStartPosition,mScrollEndPosition;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        move(event);
        return scrollLeft || scrollRight||dragCursor;
    }

    boolean scrollChange;
    boolean dragCursor;
    private boolean move(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                downX = event.getX();
                if (downX > sliderLeftRectf.left && downX < sliderLeftRectf.right) {
                    scrollRight = false;
                    scrollLeft = true;
                }
                if (downX > sliderRightRectf.left && downX < sliderRightRectf.right) {
                    scrollLeft = false;
                    scrollRight = true;
                }
                if ((downX > sliderLeftRectf.left && downX < sliderLeftRectf.right)||
                        (downX > sliderRightRectf.left && downX < sliderRightRectf.right)) {
                    if(onTouchCutAreaListener !=null){
                        onTouchCutAreaListener.onTouchDown();
                    }
                }

                //如果点击游标区域
                if(downX > cursorRect.left &&downX < cursorRect.right){
                    dragCursor = true;
                    scrollLeft = false;
                    scrollRight = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:

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

                    leftInterRect.left = sliderLeftRectf.left + mSliderWidth/2 - sliderInterWidth/2;
                    leftInterRect.right =sliderLeftRectf.left + mSliderWidth/2 + sliderInterWidth/2;
                    leftInterRect.top = sliderLeftRectf.top + interHeight;
                    leftInterRect.bottom = sliderLeftRectf.bottom - interHeight;

                    invalidate();
                    scrollChange = true;

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

                    rightInterRect.left = sliderRightRectf.left + mSliderWidth/2 - sliderInterWidth/2;
                    rightInterRect.right =sliderRightRectf.left + mSliderWidth/2 + sliderInterWidth/2;
                    rightInterRect.top = sliderRightRectf.top + interHeight;
                    rightInterRect.bottom = sliderRightRectf.bottom - interHeight;

                    invalidate();
                    scrollChange = true;
                }
                if(dragCursor){
                    mCursorX +=  scrollX;
                    Log.e("xxxx","xxx"+mCursorX);
                    cursorRect.left = mCursorX;
                    cursorRect.right = cursorRect.left+mCursorWidth;
                    if(onScrollCursorListener!=null){
                        onScrollCursorListener.onScroll((mCursorX*1f)/(1f*mWidth));
                    }
                    invalidate();
                }

                mScrollStartPosition = sliderLeftRectf.right;
                mScrollEndPosition = sliderRightRectf.left;

                downX = moveX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if(dragCursor&&onScrollCursorListener!=null){
                    onScrollCursorListener.onScrollEnd((mCursorX*1f)/(1f*mWidth));
                }

                if(scrollChange && onCutBorderScrollListener != null){
                    onCutBorderScrollListener.onScrollBorder(mScrollStartPosition,mScrollEndPosition);
                }
                if(onTouchCutAreaListener !=null){
                    onTouchCutAreaListener.onTouchUp();
                }
                scrollChange = false;
                downX = 0;
                scrollLeft = false;
                scrollRight = false;
                dragCursor = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        drawSlider(canvas, sliderLeftRectf);
        //内部白条
        canvas.drawRoundRect(leftInterRect,sliderInterWidth*1f/2.0f,sliderInterWidth*1f/2.0f,sliderInterPaint);

        drawSlider(canvas,sliderRightRectf);
        //内部白条
        canvas.drawRoundRect(rightInterRect,sliderInterWidth/2,sliderInterWidth/2,sliderInterPaint);

        mPaint.setColor(Color.RED);
        canvas.drawLine(sliderLeftRectf.left, topSpace, sliderRightRectf.right, topSpace, mPaint);
        canvas.drawLine(sliderLeftRectf.left, mHeight-topSpace, sliderRightRectf.right, mHeight-topSpace, mPaint);

        mPaint.setColor(Color.parseColor("#99313133"));

        //裁剪边界左边阴影
        RectF rectF3 = new RectF();
        rectF3.left = mSliderWidth;
        rectF3.top = topSpace;
        rectF3.right = sliderLeftRectf.left;
        rectF3.bottom = mHeight-topSpace;
        canvas.drawRect(rectF3, mPaint);

        RectF rectF4 = new RectF();
        rectF4.left = sliderRightRectf.right;
        rectF4.top = topSpace;
        rectF4.right = mWidth-mSliderWidth;
        rectF4.bottom = mHeight-topSpace;
        canvas.drawRect(rectF4, mPaint);

        if (isDrawCursor) {
            canvas.drawRoundRect(cursorRect,mCursorWidth/2,mCursorWidth/2,mCursorPaint);
        }


    }

    /**
     * 移动游标
     * @param x
     */
    public void setCursor(int x){
        if (isDrawCursor) {
            if(!dragCursor)
            {
                mCursorX = x;
                cursorRect.left = mCursorX;
                cursorRect.right = cursorRect.left + mCursorWidth;
                postInvalidate();
            }
        }
    }

    private void drawSlider(Canvas canvas,Rect rect){
        canvas.drawRect(rect,mSliderPaint);
    }

    public interface OnScrollCursorListener{
        /**
         * 滑动游标
         * @param value
         */
        void onScroll(float value);

        /**
         * 滑动结束
         * @param value
         */
        void onScrollEnd(float value);
    }
}
