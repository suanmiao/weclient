package com.suan.weclient.util.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

public class SClickableSpan extends ClickableSpan {

	private OnClickListener mOnClickListener;
	private int mLinkColor;
	

	public SClickableSpan(OnClickListener onClickListener,int linkColor) {
		mOnClickListener = onClickListener;
		mLinkColor = linkColor;

	}
	

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(mLinkColor); // 设置链接的文本颜色
		ds.setUnderlineText(false); // 去掉下划线
	}

	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		mOnClickListener.onClick(widget);

	}

}
