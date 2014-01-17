package com.suan.weclient.util;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static Bitmap roundCorner(Bitmap src, float round) {

        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        //draw white
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
//		paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect srcRect = new Rect(0, 0, width, height);
        final Rect desRect = new Rect(0, 0, width, height);

        // draw rect to canvas
        paint.setColor(Color.RED);
        canvas.drawCircle(width / 2, height / 2, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, srcRect, desRect, paint);

        // return final image
        return result;
    }

    public static Bitmap roundCornerWithBorder(Bitmap src, float round, float borderWidth) {

        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        //draw white
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
//		paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect srcRect = new Rect(0, 0, width, height);
        final Rect desRect = new Rect(0, 0, width, height);

        // draw rect to canvas
        paint.setColor(Color.RED);
        canvas.drawCircle(width / 2, height / 2, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, srcRect, desRect, paint);

        paint.setColor(Color.parseColor("#ced0c4"));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, round, paint);

        // return final image
        return result;
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Dialog createLoadingDialog(Context context,
                                             String loadingText, boolean cancelable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView outerImg = (ImageView) v.findViewById(R.id.loading_img_outer);
        ImageView innerImg = (ImageView) v.findViewById(R.id.loading_img_inner);
        // 加载动画

        Animation outerRotateAnimation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        outerRotateAnimation.setRepeatCount(-1);
        outerRotateAnimation.setDuration(1000);
        outerRotateAnimation.setInterpolator(new LinearInterpolator());
        Animation innerRotateAnimation = new RotateAnimation(360, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        innerRotateAnimation.setRepeatCount(-1);
        innerRotateAnimation.setDuration(1300);
        innerRotateAnimation.setInterpolator(new LinearInterpolator());
        // 使用ImageView显示动画
        outerImg.startAnimation(outerRotateAnimation);
        innerImg.startAnimation(innerRotateAnimation);

        TextView loadingTextView = (TextView) v.findViewById(R.id.loading_text);
        loadingTextView.setText("" + loadingText);

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(cancelable);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }


    public static float dipToPx(int dip, Resources resources) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                resources.getDisplayMetrics());
    }


    @SuppressWarnings("deprecation")
    public static Dialog createEnsureDialog(
            OnClickListener sureOnClickListener, boolean cancelVisible,
            Context context, String titleText, boolean cancelable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_ensure_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        ImageButton sureButton = (ImageButton) v
                .findViewById(R.id.dialog_ensure_button_sure);

        RelativeLayout cancelLayout = (RelativeLayout) v
                .findViewById(R.id.dialog_ensure_layout_cancel);

        sureButton.setOnClickListener(sureOnClickListener);

        TextView titleTextView = (TextView) v
                .findViewById(R.id.dialog_ensure_text_title);
        titleTextView.setText("" + titleText);

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        if (cancelVisible) {

            ImageButton cancelButton = (ImageButton) v
                    .findViewById(R.id.dialog_ensure_button_cancel);
            cancelButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    loadingDialog.dismiss();

                }
            });
        } else {
            cancelLayout.setVisibility(View.GONE);

        }
        loadingDialog.setCancelable(cancelable);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }


    public static boolean isServiceRunning(Context ctx, String filePath) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(ctx.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (filePath.equalsIgnoreCase(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
