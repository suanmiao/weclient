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
import android.util.Log;
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
public class FaceHolderView extends LinearLayout {

    LayoutInflater layoutInflater;
    private LinearLayout contentLayout;
    private ViewPager viewPager;

    public FaceHolderView(Context context) {
        super(context);
        init();
    }

    public FaceHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FaceHolderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentLayout = (LinearLayout) layoutInflater.inflate(R.layout.face_holder_layout, null);
        viewPager = (ViewPager) contentLayout.findViewById(R.id.face_pager_main);
        ArrayList<View> childLayout = new ArrayList<View>();
        for (int i = 0; i < 3; i++) {
            LinearLayout pageLayout = addFacePage(i);
            childLayout.add(pageLayout);

        }
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(childLayout);
        viewPager.setAdapter(myPagerAdapter);
        this.addView(contentLayout);
    }

    private InputFaceListener inputFaceListener;

    public void setInputFaceListener(InputFaceListener inputFaceListener) {
        this.inputFaceListener = inputFaceListener;

    }

    private LinearLayout addFacePage(int index) {


        LinearLayout pageLayout = (LinearLayout) layoutInflater.inflate(R.layout.face_page_layout, null);
        switch (index) {
            case 0:
                pageLayout.setBackgroundColor(Color.RED);

                break;

            case 1:

                pageLayout.setBackgroundColor(Color.BLACK);
                break;
        }

        GridView pageGrid = (GridView) pageLayout.findViewById(R.id.face_page_grid);
        HashMap<String, String> faceMap = SpanUtil.getFaceMap();
        Set<String> keySet = faceMap.keySet();
        ArrayList<FaceItem> pageFaceItems = new ArrayList<FaceItem>();
        for (String nowKey : keySet) {
            pageFaceItems.add(new FaceItem(nowKey, faceMap.get(nowKey)));
        }

        GridAdapter gridAdapter = new GridAdapter(getContext(), pageFaceItems);
        pageGrid.setAdapter(gridAdapter);


        return pageLayout;

    }

    private DataManager dataManager;

    public void init(DataManager mDatamanager) {
        this.dataManager = mDatamanager;

    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

        dataManager.setPagerScrollDisableRect(new Rect(left, top, right, bottom));

        super.onLayout(changed, left, top, right, bottom);

    }


    private class MyPagerAdapter extends PagerAdapter {
        ArrayList<View> childList;

        public MyPagerAdapter(ArrayList<View> childList) {
            this.childList = childList;
        }

        @Override
        public Object instantiateItem(View collection, int position) {

            ((ViewPager) collection).addView(childList.get(position), 0);

            return childList.get(position);
        }


        @Override
        public int getCount() {
            return childList.size();
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView(childList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }


        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }


    private class FaceItem {
        private String key;
        private String value;

        public FaceItem(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

    private class GridAdapter implements ListAdapter {
        private ArrayList<FaceItem> faceItems;
        private Context mContext;

        public GridAdapter(Context context, ArrayList<FaceItem> faceItems) {
            this.faceItems = faceItems;
            this.mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return faceItems.size();
        }

        @Override
        public Object getItem(int position) {
            return faceItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RelativeLayout itemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.face_item_layout, null);
            itemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputFaceListener.onInput(faceItems.get(position).getKey());

                }
            });
            ImageView contentImage = (ImageView) itemLayout.findViewById(R.id.face_item_img);
            Drawable drawable = null;
            String sourceName = mContext.getPackageName() + ":drawable/"
                    + faceItems.get(position).getValue();
            int id = mContext.getResources().getIdentifier(sourceName, null, null);
            if (id != 0) {
                drawable = mContext.getResources().getDrawable(id);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                }
            }

            contentImage.setBackground(drawable);

            Log.e("face", "getview");

            return itemLayout;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

    }

    public interface InputFaceListener {
        public void onInput(String key);
    }
}
