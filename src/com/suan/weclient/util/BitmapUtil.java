package com.suan.weclient.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by lhk on 3/14/14.
 */
public class BitmapUtil {

    public static Bitmap decodeBitmap(InputStream in){
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(in);
        }catch (Exception e){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap =  BitmapFactory.decodeStream(in,null,options);

        }

        return bitmap;

    }



}
