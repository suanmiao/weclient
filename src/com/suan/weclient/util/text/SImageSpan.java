package com.suan.weclient.util.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.umeng.analytics.k;

import java.util.Map;
import java.util.Set;

/**
 * Created by lhk on 1/3/14.
 */
public class SImageSpan extends android.text.style.ImageSpan{
    private String key;

    public SImageSpan(Drawable drawable,int verticalAligment,String key) {
        super(drawable, verticalAligment);
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
