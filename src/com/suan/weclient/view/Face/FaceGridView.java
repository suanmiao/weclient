package com.suan.weclient.view.Face;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.text.SpanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by lhk on 1/20/14.
 */
public class FaceGridView extends GridView {

    LayoutInflater layoutInflater;

    public FaceGridView(Context context) {
        super(context);
        init();
    }

    public FaceGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FaceGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    private DataManager dataManager;

    public void init(DataManager mDatamanager) {
        this.dataManager = mDatamanager;

    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

    }


    public interface InputFaceListener {
        public void onInput(String key);
    }
}
