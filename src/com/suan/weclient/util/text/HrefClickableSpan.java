package com.suan.weclient.util.text;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class HrefClickableSpan extends ClickableSpan {

    private int mLinkColor;
    private Context context;
    private String url;


    public HrefClickableSpan(String url, Context context, int linkColor) {
        this.url = url;
        this.context = context;
        mLinkColor = linkColor;

    }


    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mLinkColor); // 设置链接的文本颜色
        ds.setUnderlineText(false); // 去掉下划线
        ds.setTypeface(Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD));
    }

    @Override
    public void onClick(View widget) {
        // TODO Auto-generated method stub

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

}
