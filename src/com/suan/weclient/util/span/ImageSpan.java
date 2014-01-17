package com.suan.weclient.util.span;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;

import java.util.Map;
import java.util.Set;

/**
 * Created by lhk on 1/3/14.
 */
public class ImageSpan {

    /**
     * the map of face.
     */
    private Map<String, String> faceMap;
    private Context context;

    public ImageSpan(Context context, Map<String, String> faceMap){
        this.context = context;
        this.faceMap = faceMap;
    }

    /**
     * get the image by the given key
     */
    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            String sourceName = context.getPackageName() + ":drawable/"
                    + source;
            int id = context.getResources().getIdentifier(sourceName, null, null);
            if (id != 0) {
                drawable = context.getResources().getDrawable(id);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                }
            }
            return drawable;
        }
    };

    /**
     * return a {@link Spanned} with image
     * @param text
     * @return
     */
    public Spanned getImageSpan(CharSequence text){
        String cs = text.toString();
        if (faceMap != null) {
            Set<String> keys = faceMap.keySet();
            for (String key : keys) {
                if (cs.contains(key)) {
                    cs = cs.replace(key, "<img src='" + faceMap.get(key) + "'>");
                }
            }
        }
        return Html.fromHtml(cs, imageGetter, null);
    }
}
