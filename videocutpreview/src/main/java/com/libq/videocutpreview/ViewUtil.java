package com.libq.videocutpreview;

import android.content.Context;
import android.util.TypedValue;

/**
 * describ: balabala
 * author: libq
 * date: 2018/4/17 0017.11:52
 * email:614527679@qq.com
 */

public class ViewUtil {
    public static int dp2px(Context c,int dp ){
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp,c.getResources().getDisplayMetrics()));
    }

    public static int px2dp(Context c,float pxValue){
        float scale=c.getResources().getDisplayMetrics().density;
        return Math.round(pxValue/scale+0.5f);
    }
}
