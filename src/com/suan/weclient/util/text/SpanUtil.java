package com.suan.weclient.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.Util;
import com.umeng.analytics.a.n;

public class SpanUtil {

    private static HashMap<String, String> faceMap;

    public static HashMap<String, String> getFaceMap() {

        if (faceMap == null) {
            faceMap = new HashMap<String, String>();


            faceMap.put("/::)", "f023");
            faceMap.put("/::~", "f_static_040");
            faceMap.put("/::B", "f019");
            faceMap.put("/::|", "f091");
            faceMap.put("/:8-)", "f_static_021");
            faceMap.put("/::<", "f_static_009");
            faceMap.put("/::$", "f_static_020");

            faceMap.put("/::X", "f_static_011");
            faceMap.put("/::Z", "f_static_035");
            faceMap.put("/::-|", "f_static_026");
            faceMap.put("/::@", "f_static_024");
            faceMap.put("/::P", "f001");
            faceMap.put("/::D", "f000");

            faceMap.put("/::O", "f033");
            faceMap.put("/::(", "f032");
            faceMap.put("/::+", "f012");
            faceMap.put("/:--b", "f020");
            faceMap.put("/::Q", "f013");
            faceMap.put("/::T", "f022");

            faceMap.put("/:,@P", "f003");
            faceMap.put("/:,@-D", "f018");
            faceMap.put("/::d", "f030");
            faceMap.put("/:,@o", "f031");
            faceMap.put("/::g", "f081");
            faceMap.put("/:|-)", "f082");
            faceMap.put("/::!", "f_static_026");

            faceMap.put("/::L", "f002");
            faceMap.put("/::&gt;", "f_static_037");
            faceMap.put("/::,@", "f_static_050");
            faceMap.put("/:,@f", "f_static_042");
            faceMap.put("/::-S", "f_static_083");
            faceMap.put("/:?", "f_static_034");
            faceMap.put("/:,@x", "f_static_011");

            faceMap.put("/:,@@", "f_static_049");
            faceMap.put("/::8", "f_static_013");
            faceMap.put("/:,@!", "f039");
            faceMap.put("/:!!!", "f078");
            faceMap.put("/:xx", "f_static_005");
            faceMap.put("/:bye", "f_static_004");
            faceMap.put("/:wipe", "f_static_006");
            faceMap.put("/:dig", "f085");
            faceMap.put("/:handclap", "f086");
            faceMap.put("/:&amp;-(", "f_static_087");
            faceMap.put("/:B-)", "f_static_046");
            faceMap.put("/:&lt;@", "f_static_088");
            faceMap.put("/:@&gt;", "f088");

            faceMap.put("/::-O", "f_static_089");
            faceMap.put("/:&gt;-|", "f_static_048");
            faceMap.put("/:P-(", "f_static_014");
            faceMap.put("/::&#39;|", "f_static_090");
            faceMap.put("/:X-)", "f_static_041");
            faceMap.put("/::*", "f_static_036");
            faceMap.put("/:@x", "f_static_091");
//            faceMap.put("", "f");

        }

        return faceMap;
    }

    public static String getUnspannedContentString(EditText editText) {
        Editable editable = editText.getEditableText();
        String contentString = editable.toString();
        SImageSpan[] sImageSpans = editable.getSpans(0, editable.length(), SImageSpan.class);
        int nowDeleteLength = 0;
        for (SImageSpan nowSpan : sImageSpans) {
            int start = editable.getSpanStart(nowSpan);
            int end = editable.getSpanEnd(nowSpan);
            String unspannedPart = nowSpan.getKey();
            String target = contentString.substring(start - nowDeleteLength, end - nowDeleteLength);

            contentString.replace(target, unspannedPart);
            nowDeleteLength += target.length() - unspannedPart.length();

        }

        return contentString;

    }


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


    public static void setHtmlSpanAndImgSpan(TextView textView, String source,
                                             Context context) {

        int hyperColor = Color.parseColor("#079CDD");

        source = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(source);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(source);


        //find and analyse tags
        Pattern pattern = Pattern.compile("<[^>]*>[^<|>]*<[^>]*>");

        Matcher matcher = pattern.matcher(source);

        int nowDeleteLength = 0;

        while (matcher.find()) {

            int nowStart = matcher.start();
            int nowEnd = matcher.end();

            String foundString = matcher.group();
            String url = getTargetUrlFromTag(foundString);
            String name = getContentFromTag(foundString);

            spannableStringBuilder.setSpan(new HrefClickableSpan(url, context,hyperColor ), nowStart - nowDeleteLength, nowEnd - nowDeleteLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.replace(nowStart - nowDeleteLength, nowEnd - nowDeleteLength, name);
            nowDeleteLength += foundString.length() - name.length();

        }


        //find and analyse tags

        source = spannableStringBuilder.toString();
        nowDeleteLength = 0;

        pattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");


        matcher = pattern.matcher(source);


        while (matcher.find()) {

            int nowStart = matcher.start();
            int nowEnd = matcher.end();

            String foundString = matcher.group();
            String url = foundString;

            spannableStringBuilder.setSpan(new HrefClickableSpan(url, context, hyperColor), nowStart - nowDeleteLength, nowEnd - nowDeleteLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        //find and analyse expressions

        //reset source and delete length
        source = spannableStringBuilder.toString();
        nowDeleteLength = 0;
        Set<String> keys = getFaceMap().keySet();
        for (String key : keys) {
            key = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(key);
            int indexStart = 0;
            while (source.indexOf(key, indexStart) > -1) {
                int start = source.indexOf(key, indexStart);
                int end = start + key.length();
                SImageSpan sImageSpan = getImgSpan(context, textView.getTextSize(), key);
                if(sImageSpan!=null){

                    spannableStringBuilder.setSpan(sImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    Log.e("null span","key"+key);
                }
                indexStart = start + 1;
            }

        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);


    }


    public static SImageSpan getImgSpan(Context context, float textSize, String key) {
        Drawable drawable = null;
        String sourceName = context.getPackageName() + ":drawable/"
                + getFaceMap().get(key);
        int id = context.getResources().getIdentifier(sourceName, null, null);
        if (id != 0) {
            drawable = context.getResources().getDrawable(id);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            }
        }


        int faceSize = (int) (Util.dipToPx((int) textSize, context.getResources()) * 1.2);

        if(drawable==null){
            return null;

        }
        int heightCutOff = (drawable.getIntrinsicHeight() - (int) faceSize) / 2;
        int widthCutOff = (drawable.getIntrinsicWidth() - (int) faceSize) / 2;

        if (faceSize > drawable.getIntrinsicHeight() || faceSize > drawable.getIntrinsicWidth()) {

            heightCutOff = 0;
            widthCutOff = 0;

        }

        drawable.setBounds(widthCutOff, heightCutOff, drawable.getIntrinsicWidth() - 2 * widthCutOff, drawable.getIntrinsicHeight() - 2 * heightCutOff);

        return new SImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM, key);
    }


    private static String getContentFromTag(String tagString) {

        String url = tagString;
        Pattern pattern = Pattern.compile(">([^<]*)<");

        Matcher matcher = pattern.matcher(tagString);
        while (matcher.find()) {
            url = matcher.group(1);
        }

        return url;

    }

    private static String getTargetUrlFromTag(String tagString) {

        String url = tagString;
        Pattern pattern = Pattern.compile("href=\\s*\"([^\"]*)");

        Matcher matcher = pattern.matcher(tagString);
        while (matcher.find()) {
            url = matcher.group(1);
        }

        return url;
    }


}
