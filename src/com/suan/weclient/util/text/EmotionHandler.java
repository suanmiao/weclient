package com.suan.weclient.util.text;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by lhk on 1/20/14.
 */

public class EmotionHandler implements TextWatcher{

    private final EditText mEditor;
    private final ArrayList<SImageSpan> mEmoticonsToRemove = new ArrayList<SImageSpan>();

    public EmotionHandler(EditText editor) {
        // Attach the handler to listen for text changes.
        mEditor = editor;
        mEditor.addTextChangedListener(this);
    }

    public void insert(String key) {
        // Create the SImageSpan
        SImageSpan span = SpanUtil.getImgSpan(mEditor.getContext(), mEditor.getTextSize(),key);

        // Get the selected text.
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        Editable message = mEditor.getEditableText();

        // Insert the emoticon.
        message.replace(start, end, key);
        message.setSpan(span, start, start + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        // Check if some text will be removed.
        if (count > 0) {
            int end = start + count;
            Editable message = mEditor.getEditableText();
            SImageSpan[] list = message.getSpans(start, end, SImageSpan.class);

            for (SImageSpan span : list) {
                // Get only the emoticons that are inside of the changed
                // region.
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                //when the span is not complete,it means the span text is deleted
                if ((spanStart < end) && (spanEnd > start)) {
                    // Add to remove list
                    mEmoticonsToRemove.add(span);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable text) {
        Editable message = mEditor.getEditableText();

        // Commit the emoticons to be removed.
        for (SImageSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);

            // Remove the span
            message.removeSpan(span);

            // Remove the remaining emoticon text.
            if (start != end) {
                message.delete(start, end);
            }
        }
        mEmoticonsToRemove.clear();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {

    }

}
