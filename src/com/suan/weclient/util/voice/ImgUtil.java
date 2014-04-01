package com.suan.weclient.util.voice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suan.weclient.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by lhk on 3/19/14.
 */
public class ImgUtil {

    public interface OnImgPrepareListener {
        public void onPrepareFinished(ImgPrepareResuleHolder imgPrepareResuleHolder);
    }

    public static class ImgPrepareResuleHolder {
        Bitmap bitmap;
        String filePath;

        public ImgPrepareResuleHolder(Bitmap bitmap, String filePath) {
            this.bitmap = bitmap;
            this.filePath = filePath;
        }

        public Bitmap getBitmap(){
            return bitmap;
        }

        public String getFilePath(){
            return filePath;
        }
    }


    public static void prepareBitmap(final String filePath, final OnImgPrepareListener onImgPrepareListener) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                onImgPrepareListener.onPrepareFinished((ImgPrepareResuleHolder) msg.obj);

            }
        };

        new Thread() {
            public void run() {


                Bitmap bitmap = null;

                Message message = new Message();

                try {
                    bitmap = BitmapFactory.decodeFile(filePath);
                    message.obj = new ImgPrepareResuleHolder(bitmap, filePath);

                    if (bitmap.getByteCount() > 2000000) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeFile(filePath, options);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        String tempFilePath = Util.getFilePath(System.currentTimeMillis() + "IMG.png");
                        FileOutputStream fos = new FileOutputStream(tempFilePath);
                        fos.write(bitmapdata);
                        fos.close();
                        message.obj = new ImgPrepareResuleHolder(bitmap, tempFilePath);

                    }

                } catch (Exception e) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeFile(filePath, options);
                    message.obj = new ImgPrepareResuleHolder(bitmap, filePath);

                }

                loadHandler.sendMessage(message);
            }
        }.start();


    }


}
