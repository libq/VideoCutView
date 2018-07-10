package com.libq.videocutpreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.io.File;
import java.util.ArrayList;

/**
 * 描述:视频裁剪View
 *      设置裁剪区间，并且显示缩略图
 *      ps：为了更好的显示缩略图，需要输入图片的宽高比
 *      通过宽高比来计算铺满时显示的缩略图数量
 * author: libq
 * date2018/4/10 0010.
 */

public class VideoCutView extends FrameLayout {
    private LinearLayout mImageLayout = null;
    private VideoThumbnailView mThumb = null;
    private FrameLayout root=null;

    private Context mContext = null;
    private int minHeight = 100;
    private ArrayList<String> imgUrls = null;
    private double whRate = 0.618f;//缩略图宽高比

    private int mTopBottomBorderWidth = 6;
    private int mBorderColor = Color.RED;
    private int mDragAreaWidth = 40;//可拖动区域宽度
    private int mCutMinDuration = mDragAreaWidth/2+2;//最小长度//两个拖动条间的最小距离
    private int mSliderWidth = 6;//左右两个滑动块的宽度
    private boolean isDrawCursor = false;//是否需要画游标
    private int mVideoDuration;
    private ArrayList<ImageView> imageViews;

    public VideoCutView(@NonNull Context context) {
        super(context);
        initView(context,null);
    }

    public VideoCutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public VideoCutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attrs){
        imageViews = new ArrayList<>();
        mContext = context;
        //---------------------------
        if(attrs!=null){
            TypedArray tr = context.obtainStyledAttributes(attrs, R.styleable.VideoCutView);

            isDrawCursor = tr.getBoolean(R.styleable.VideoCutView_draw_cursor,isDrawCursor);
            int defTopBotmWidth =ViewUtil.px2dp(mContext,mTopBottomBorderWidth);
            mTopBottomBorderWidth = ViewUtil.dp2px(mContext,(int)tr.getDimension(R.styleable.VideoCutView_top_bottom_border_width,defTopBotmWidth));
            mBorderColor = tr.getColor(R.styleable.VideoCutView_border_color,mBorderColor);
            int defDragWidth = ViewUtil.px2dp(mContext,mDragAreaWidth);
            mDragAreaWidth = ViewUtil.dp2px(mContext,(int)tr.getDimension(R.styleable.VideoCutView_drag_area_width,defDragWidth));

            mCutMinDuration = tr.getInteger(R.styleable.VideoCutView_cut_min_duration,mCutMinDuration);
            int defSliderWidth = ViewUtil.px2dp(mContext,mSliderWidth);
            mSliderWidth = ViewUtil.dp2px(mContext,(int)tr.getDimension(R.styleable.VideoCutView_slider_width,defSliderWidth));
            mVideoDuration = tr.getInteger(R.styleable.VideoCutView_video_duration,mVideoDuration);

            tr.recycle();
        }

        //---------------------------
        View root = LayoutInflater.from(mContext).inflate(R.layout.cut_view,this);
        mThumb = (VideoThumbnailView) root.findViewById(R.id.thumb);
        mImageLayout = (LinearLayout) root.findViewById(R.id.img_list);

        mThumb.setBorderColor(mBorderColor);
        mThumb.setDragAreaWidth(mDragAreaWidth);
        mThumb.setDrawCursor(isDrawCursor);
        mThumb.setSliderWidth(mSliderWidth);
        mThumb.setTopBottomBorderWidth(mTopBottomBorderWidth);


    }

    public void isDrawCursor(boolean isDraw){
        if(mThumb!=null){
            mThumb.setDrawCursor(isDraw);
        }
    }

    /**
     * 设置图片集
     * @param urls
     */
    public void setImageUrls(ArrayList<String> urls,ImageLoadStrategyLinstener listener){
        imgUrls = urls;
        for(int i=0;i<imgUrls.size();i++){
            addThumbImage();
        }
        if(listener!=null){
            listener.onLoad(urls,imageViews);
        }
    }

    /**
     * 获取合适的图片张数
     * @return
     */
    public void getSuitImageCount(final GetImageCountCallback callback){
        if (callback!=null){
           post(new Runnable() {
               @Override
               public void run() {
                   callback.invoke(caculateThumbImageCount());
                   //设置裁剪最小距离
                   float length = ((mCutMinDuration*1f)/(mVideoDuration*1f)) * (getWidth()*1f);
                   mThumb.setMinLength(Math.round(length));
               }
           });
        }
    }


    /**
     * 设置视频时长 ms
     * @param duration
     */
    public void setVideoDuration(int duration){
        mVideoDuration = duration;
    }

    /**
     * 设置裁剪最小区间 ms
     * @param duration
     */
    public void setCutMinDuration(int duration){
        mCutMinDuration = duration;
        post(new Runnable() {
            @Override
            public void run() {
                //设置裁剪最小距离
                float length = ((mCutMinDuration*1f)/(mVideoDuration*1f)) * (getWidth()*1f);
                mThumb.setMinLength(Math.round(length));
            }
        });

    }

    /**
     * 设置宽高比
     * @param rate
     */
    public void setImageWHRate(double rate){
        whRate = rate;
    }

   /* *//**
     * 获取图片宽高比
     * @param url
     * @return
     *//*
    private double getImageWHRate(String url){
        Bitmap b =BitmapFactory.decodeFile(url);
        if(b!=null){
            double rate =(b.getWidth()*1.00)/(b.getHeight()*1.00);
            b.recycle();
            return rate;
        }
        return 1;
    }*/

    /**
     * 计算缩略图的数量
     * @return
     */
    private int caculateThumbImageCount(){
        int rootHeight = getHeight();
        int rootWidth = getWidth();
        double thumbWidth = rootHeight*1f * whRate;
        int thumbHeight = rootHeight;
        return (int)Math.ceil(rootWidth*1d/thumbWidth);
    }

    /**
     * 添加图片
     */
    private void addThumbImage(){

        ImageView iv = new ImageView(mContext);
        int thumbWidth = (int)(getHeight()*whRate);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(thumbWidth,getHeight());
        iv.setLayoutParams(lp);
        mImageLayout.addView(iv);
        imageViews.add(iv);
       /* File file = new File(url);
        Glide.with(mContext)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv);*/

    }

    public void clearAllFrame(){
        if(mImageLayout!=null){
            mImageLayout.removeAllViews();
        }
    }

    public interface GetImageCountCallback{
        void invoke(int count);
    }

    /**
     * 设置点击裁剪区域监听
     * @param listener
     */
    public void setOnTouchCutAreaListener(VideoThumbnailView.OnTouchCutAreaListener listener){
        if(listener!=null&&mThumb!=null){
            mThumb.setOnTouchCutAreaListener(listener);
        }
    }

    /**
     * 设置裁剪滑动监听
     * @param listener
     */
    public void setOnVideoPlayIntervalChangeListener(final OnVideoPlayIntervalChangeListener listener){
        if(listener!=null&&mThumb!=null){
            mThumb.setOnCutBorderScrollListener(new VideoThumbnailView.OnCutBorderScrollListener() {
                @Override
                public void onScrollBorder(int start, int end) {
                    int startTime = (int)(start*1f/(getWidth()*1f) * mVideoDuration);
                    int endTime = (int)(end*1f/(getWidth()*1f) * mVideoDuration);
                    listener.onChange(startTime,endTime);
                }
            });
        }
    }



    /**
     * 图片加载策略
     */
    public interface ImageLoadStrategyLinstener{
        void onLoad(ArrayList<String> urls,ArrayList<ImageView> ivs);
    }

    /**
     * 视频播放区间改变监听
     */
    public interface OnVideoPlayIntervalChangeListener{
        void onChange(int startTime,int endTime);
    }

    /**
     * 设置游标
     * @param x  0-100
     */
    public void setCursor(final int x){
        if(mThumb!=null){
            post(new Runnable() {
                @Override
                public void run() {
                    float cursor = mThumb.getWidth() * (float)(x*1.0f/100f);
                    mThumb.moveCursor((int)cursor);
                }
            });

        }
    }


}
