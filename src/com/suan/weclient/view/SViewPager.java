package com.suan.weclient.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;

public class SViewPager extends ViewPager {


    public SViewPager(Context context) {
        super(context);
    }

    public SViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(DataManager mDatamanager) {
        mDatamanager.setPagerScrollListener(new ScrollEnableListener() {
           public void setFaceHolderRect(Rect rect){
                childRect = rect;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        if (childRect != null) {
            Log.e("yes not null","lkjkl");
            if (childRect.contains(x, y)) {
                return false;

            } else {

            }
        }

        return super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
       int x = (int) event.getX();
        int y = (int) event.getY();
        if (childRect != null) {
            Log.e("yes not null","lkjkl");
            if (childRect.contains(x, y)) {
                return true;

            } else {

            }
        }


        return super.onTouchEvent(event);

    }

    private ViewPager childPager;
    private Rect childRect;

    private void findChildPager() {
        childPager = (ViewPager) this.findViewById(R.id.face_pager_main);
        childPager.getGlobalVisibleRect(childRect);

    }

    public interface ScrollEnableListener {

        public void setFaceHolderRect(Rect rect);

    }


}