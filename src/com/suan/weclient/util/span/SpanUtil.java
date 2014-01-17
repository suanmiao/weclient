package com.suan.weclient.util.span;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
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

    public static Spanned setImgSpan(String source,Context context){
        Map<String,String> faceMap = new HashMap<String, String>();
        faceMap.put("a","ic_launcher");
        faceMap.put("b","ic_launcher");
        ImageSpan imageSpan = new ImageSpan(context,faceMap);
        Spanned spanned = imageSpan.getImageSpan(source);
        return spanned;



    }

}
