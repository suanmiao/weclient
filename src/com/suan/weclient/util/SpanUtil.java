package com.suan.weclient.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View.OnClickListener;

public class SpanUtil {

	public static void setLinkSpan(SpannableString spannableString,
			String linkReString, OnClickListener onClickListener, int linkColor) {

		Pattern pattern = Pattern.compile(linkReString);

		Matcher matcher = pattern.matcher(spannableString.toString());

		while (matcher.find()) {

			int nowStart = matcher.start();
			int nowEnd = matcher.end();

			spannableString.setSpan(new SClickableSpan(onClickListener,
					linkColor), nowStart, nowEnd,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		}

	}
	
	public static void setBoldSpan(SpannableString spannableString,
			String linkReString) {

		Pattern pattern = Pattern.compile(linkReString);

		Matcher matcher = pattern.matcher(spannableString.toString());

		while (matcher.find()) {

			int nowStart = matcher.start();
			int nowEnd = matcher.end();
		    StyleSpan span = new StyleSpan(Typeface.BOLD);    
		    

			spannableString.setSpan(span, nowStart, nowEnd,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		}

	}

}
