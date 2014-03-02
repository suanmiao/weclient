package com.suan.weclient.util;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.util.net.WechatManager;

import org.apache.http.HeaderIterator;

import java.io.FilePermission;
import java.util.ArrayList;
import java.util.List;

public class Util {


    /*
     * about pop dialog
     */

    private static TextView popTitleTextView;

    private static TextView popContentTextView;
    public static EditText popContentEditText;
    private static TextView popTextAmountTextView;

    private static EditText loginUserEdit, loginPwdEdit;

    private static Button copyBitAddrButton, goToPayButton;

    private static Button popCancelButton, popSureButton;


    private static Dialog popDialog;
    private static Dialog replyDialog;

    public static Bitmap roundCorner(Bitmap src, int width) {

        src = Bitmap.createScaledBitmap(src, width, width, false);

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, width, Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        //draw white
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        // config rectangle for embedding
        final Rect srcRect = new Rect(0, 0, width, width);
        final Rect desRect = new Rect(0, 0, width, width);

        // draw rect to canvas
        paint.setColor(Color.RED);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, srcRect, desRect, paint);

        // return final image
        return result;
    }

    public static Bitmap roundCornerWithBorder(Bitmap src, int width, float borderWidth, int borderColor) {

        src = Bitmap.createScaledBitmap(src, width, width, false);

        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, width, Config.ARGB_8888);

        // set canvas for painting
        Canvas canvas = new Canvas(result);
        //draw white
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        // config rectangle for embedding
        final Rect srcRect = new Rect(0, 0, width, width);
        final Rect desRect = new Rect(0, 0, width, width);

        // draw rect to canvas
        paint.setColor(Color.RED);
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, srcRect, desRect, paint);

        paint.setColor(borderColor);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, width / 2, width, paint);

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
                                             String loadingText, int dialogCancelType) {

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

        switch (dialogCancelType) {
            case WechatManager.DIALOG_POP_CANCELABLE:

                loadingDialog.setCancelable(true);// 不可以用“返回键”取消
                break;

            case WechatManager.DIALOG_POP_NOT_CANCELABLE:

                loadingDialog.setCancelable(false);
                break;
        }

        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }

    public static Dialog createDevReplyDialog(Context context, String title,
                                              String content, OnClickListener sureClickListener,
                                              OnClickListener cancelClickListener) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_dev_reply_layout,
                null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_dev_reply_text_title);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_dev_reply_button_reply);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_dev_reply_button_o);

        popContentTextView = (TextView) dialogView
                .findViewById(R.id.dialog_dev_reply_text_content);
        popContentTextView.setText(content);

        popTitleTextView.setText(title);
        popSureButton.setOnClickListener(sureClickListener);
        popCancelButton.setOnClickListener(cancelClickListener);

        replyDialog = new Dialog(context, R.style.dialog);

        replyDialog.setContentView(dialogView);

        return replyDialog;

    }


    public static Dialog createSaveUsDialog(final Context context, String title) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_save_us_layout,
                null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_save_us_text_title);

        copyBitAddrButton = (Button)dialogView.findViewById(R.id.dialog_save_us_button_copy_bit_addr);
        goToPayButton = (Button)dialogView.findViewById(R.id.dialog_save_us_button_go_to_pay);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_save_us_button_sure);

        popContentTextView = (TextView) dialogView
                .findViewById(R.id.dialog_dev_reply_text_content);


        popTitleTextView.setText(title);

        copyBitAddrButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(context.getResources().getString(R.string.bit_adder));
                Toast.makeText(context,context.getResources().getString(R.string.has_copyed),Toast.LENGTH_SHORT).show();

            }
        });

        goToPayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://me.alipay.com/suanmiao";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        popSureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    popDialog.dismiss();

                } catch (Exception e) {

                }

            }
        });

        popDialog = new Dialog(context, R.style.dialog);

        popDialog.setContentView(dialogView);

        return popDialog;

    }









    public static Dialog createContactUsDialog(final Context context, String title) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_contact_us_layout,
                null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_contact_us_text_title);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_contact_us_button_send_mail);


        popTitleTextView.setText(title);

        popSureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getResources().getString(R.string.xiaohong_mail),
                            context.getResources().getString(R.string.xiaoshou_mail),
                            context.getResources().getString(R.string.my_mail)});
                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.mail_title));
                    intent.putExtra(Intent.EXTRA_TEXT, "");

                    context.startActivity(Intent.createChooser(intent, "Send Email"));

                    popDialog.dismiss();

                } catch (Exception e) {

                }

            }
        });

        popDialog = new Dialog(context, R.style.dialog);

        popDialog.setContentView(dialogView);

        return popDialog;

    }





    public static Dialog createLoginDialog(Context context, String title,
                                           OnClickListener sureClickListener,
                                           OnClickListener cancelClickListener) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_login_layout,
                null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_login_text_title);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_login_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_login_button_cancel);


        loginUserEdit = (EditText) dialogView.findViewById(R.id.dialog_login_edit_user_id);
        loginPwdEdit = (EditText) dialogView.findViewById(R.id.dialog_login_edit_pass_word);

        popTitleTextView.setText(title);
        popSureButton.setOnClickListener(sureClickListener);
        popCancelButton.setOnClickListener(cancelClickListener);

        popDialog = new Dialog(context, R.style.dialog);

        popDialog.setContentView(dialogView);

        return popDialog;

    }


    public static Dialog createFeedbackDialog(Context context, OnClickListener sureClickListener, OnClickListener cancelClickListener) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_feedback_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_feedback_text_title);

        popContentEditText = (EditText) dialogView
                .findViewById(R.id.dialog_feedback_edit_text);
        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_feedback_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_feedback_button_cancel);

        popTextAmountTextView = (TextView) dialogView
                .findViewById(R.id.dialog_feedback_text_num);
        popTextAmountTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popContentEditText.setText("");

            }
        });

        popContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                popTextAmountTextView.setTextColor(Color.rgb(0, 0, 0));
                popTextAmountTextView.setText(popContentEditText.getText()
                        .length() + " x");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        popTitleTextView.setText("反馈");

        popSureButton.setOnClickListener(sureClickListener);

        popCancelButton.setOnClickListener(cancelClickListener);

        replyDialog = new Dialog(context, R.style.dialog);

        replyDialog.setContentView(dialogView);
        return replyDialog;
    }


    public static float dipToPx(int dip, Resources resources) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                resources.getDisplayMetrics());
    }

    public static int getScreenHeight(Resources resources) {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        return displayMetrics.heightPixels;
    }


    @SuppressWarnings("deprecation")
    public static Dialog createEnsureDialog(
            OnClickListener sureOnClickListener, boolean cancelVisible,
            Context context, String titleText, String contentText, boolean cancelable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_ensure_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        Button sureButton = (Button) v
                .findViewById(R.id.dialog_ensure_button_sure);

        RelativeLayout cancelLayout = (RelativeLayout) v
                .findViewById(R.id.dialog_ensure_layout_cancel);

        sureButton.setOnClickListener(sureOnClickListener);

        TextView titleTextView = (TextView) v
                .findViewById(R.id.dialog_ensure_text_title);
        titleTextView.setText("" + titleText);

        TextView contentTextView = (TextView) v.findViewById(R.id.dialog_ensure_text_content);
        contentTextView.setText("" + contentText);

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        if (cancelVisible) {

            Button cancelButton = (Button) v
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


    public static Dialog createReplyDialog(Context context,String nickname,boolean lastReplyCanceled,String canceledReplyContent
            ,OnClickListener sureClickListener,OnClickListener cancelClickListener){


        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_edit_text_title);

        popContentEditText = (EditText) dialogView
                .findViewById(R.id.dialog_edit_edit_text);
        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_edit_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_edit_button_cancel);

        popContentTextView = (TextView) dialogView
                .findViewById(R.id.dialog_edit_text_num);
        popContentTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popContentEditText.setText("");

            }
        });

        if (lastReplyCanceled) {
            popContentEditText.setText(canceledReplyContent);
        }

        popContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                int remainTextAmount = 140 - s.length();
                if (remainTextAmount >= 0) {
                    popContentTextView.setTextColor(Color.rgb(0, 0, 0));
                } else {
                    popContentTextView.setTextColor(Color.RED);
                }
                popContentTextView.setText(remainTextAmount + " x");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        popTitleTextView.setText(context.getResources().getString(R.string.reply) + ":"
                + nickname);

        popSureButton.setOnClickListener(sureClickListener);

        popCancelButton.setOnClickListener(cancelClickListener);

        popDialog = new Dialog(context, R.style.dialog);

        popDialog.setContentView(dialogView);

        return popDialog;


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


    public static String getShortString(String source, int contentLength, int dotsLength) {
        if (source.length() <= contentLength) {
            return source;
        }
        String dotsString = "";
        for (int i = 0; i < dotsLength; i++) {
            dotsString += ".";

        }
        return source.replace(source.substring(contentLength, source.length()), dotsString);

    }


    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getRandomFloat(int bit) {
        String result = "0.";
        for (int i = 0; i < bit; i++) {
            int nowInt = (int) (10 * Math.random());
            if (nowInt == 10) {
                nowInt = 0;
            }
            result += nowInt;
        }

        return result;
    }
}
