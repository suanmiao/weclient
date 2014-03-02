package com.suan.weclient.view.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.Util;

import javax.security.auth.login.LoginException;

/**
 * Created by lhk on 2/6/14.
 */
public class PTRListview extends ListView implements AbsListView.OnScrollListener {

    private final static int RELEASE_TO_REFRESH = 0;
    private final static int PULL_TO_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private final static int RATIO = 2;
    private LayoutInflater inflater;

    private LinearLayout headerView;
    private TextView ptrHeaderTipTextView;
    private ImageView ptrHeaderArrowImageView;
    private ImageView ptrHeaderCircleImageView;

    private int headerContentHeight;

    /*
    about footer view
     */
    private LinearLayout footerLayout;
    private ImageView footerCircleImageView;
    private int footerHeight;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private RotateAnimation circleAnimation;

    private int startY;
    private int state;

    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecored;

    private boolean isBack = false;

    private int lastLastVisibleItem = 0;

    private boolean loading = false;

    private OnRefreshListener refreshListener;
    private OnLoadListener onLoadListener;
    private OnScrollListener mScrollListener;

    private boolean catchMotionEvent;


    /*
    outer variable
     */
    private boolean refreshEnable = true;
    private boolean loadEnable = true;

    public static final int PTR_MODE_REFRESH = 2;
    public static final int PTR_MODE_LOAD = 3;

    public PTRListview(Context context) {
        super(context);
        init(context);
    }

    public PTRListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PTRListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
///        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        inflater = LayoutInflater.from(context);
        headerView = (LinearLayout) inflater.inflate(R.layout.ptr_header_layout, null);
        ptrHeaderTipTextView = (TextView) headerView
                .findViewById(R.id.ptr_header_text_tip);

        ptrHeaderArrowImageView = (ImageView) headerView
                .findViewById(R.id.ptr_header_arrow);
        //set arrow height and width
        ptrHeaderArrowImageView.setMinimumWidth(70);
        ptrHeaderArrowImageView.setMinimumHeight(50);

        ptrHeaderCircleImageView = (ImageView) headerView.findViewById(R.id.ptr_header_circle);


        //get height of head,cause it has not been measured yet

        headerContentHeight = (int)Util.dipToPx(60,getResources());

        //hide the head
        headerView.setPadding(0, -1 * headerContentHeight, 0, 0);


        //set header to listview
        addHeaderView(headerView, null, false);


        super.setOnScrollListener(this);


        //init animations
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        circleAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        circleAnimation.setInterpolator(new LinearInterpolator());
        circleAnimation.setDuration(500);
        circleAnimation.setRepeatCount(-1);


        //init state
        state = DONE;
        //if catch the event
        catchMotionEvent = false;

        /*
        about footer layout
         */
        footerLayout = (LinearLayout) inflater.inflate(R.layout.ptr_loading_layout, null);
        addFooterView(footerLayout);

        footerCircleImageView = (ImageView) footerLayout.findViewById(R.id.ptr_footer_circle);
        footerHeight = (int)Util.dipToPx(60,getResources());
        footerLayout.setPadding(0, 0, 0, -footerHeight);


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (catchMotionEvent && refreshEnable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if (!isRecored) {
                        isRecored = true;
                    }

                    //find the issue ,should set start y out of record
                    //else some event will not be recorded
                    startY = (int) ev.getY();// position of event start
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == PULL_TO_REFRESH) {

                            state = DONE;
                            changeHeaderViewByState();
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onListRefresh();
                        }
                    }
                    isRecored = false;

                    break;

                case MotionEvent.ACTION_MOVE:

                    int tempY = (int) ev.getY();
                    if (!isRecored) {
                        isRecored = true;
                        startY = tempY;
                    }
                    if (state != REFRESHING && isRecored && state != LOADING) {

                        if (state == RELEASE_TO_REFRESH) {
                            //ensure the section is always the first one
                            setSelection(0);
                            if ((tempY - startY) > 0) {
                                if ((tempY - startY) / RATIO < headerContentHeight) {
                                    isBack = true;
                                    state = PULL_TO_REFRESH;
                                    changeHeaderViewByState();
                                }

                            } else {
                                state = DONE;
                                Log.e("refresh","done");
                                changeHeaderViewByState();

                            }
                        }

                        if (state == PULL_TO_REFRESH) {
                            setSelection(0);
                            if ((tempY - startY) / RATIO >= headerContentHeight) {
                                // change state to rtr
                                state = RELEASE_TO_REFRESH;
                                changeHeaderViewByState();
                            } else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        // done state
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                isBack = false;
                                state = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                        }

                        //set header position
                        if (state == PULL_TO_REFRESH || state == RELEASE_TO_REFRESH) {

                            headerView.setPadding(0, -headerContentHeight
                                    + (tempY - startY) / RATIO, 0, 0);

                        }

                    }
                    break;

                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void changeHeaderViewByState() {
/*        if(headerContentHeight==0){
            measureView(headerView);
            headerContentHeight = headerView.getMeasuredHeight();
        }
 */       switch (state) {
            case RELEASE_TO_REFRESH:

                ptrHeaderArrowImageView.setVisibility(View.VISIBLE);
                ptrHeaderCircleImageView.clearAnimation();
                ptrHeaderCircleImageView.setVisibility(View.GONE);
                ptrHeaderTipTextView.setVisibility(View.VISIBLE);

                ptrHeaderArrowImageView.clearAnimation();// 清除动画
                ptrHeaderArrowImageView.startAnimation(animation);// 开始动画效果

                ptrHeaderTipTextView.setText("松开刷新");
                break;
            case PULL_TO_REFRESH:
                ptrHeaderCircleImageView.clearAnimation();
                ptrHeaderCircleImageView.setVisibility(View.GONE);
                ptrHeaderTipTextView.setVisibility(View.VISIBLE);
                ptrHeaderArrowImageView.clearAnimation();
                ptrHeaderArrowImageView.setVisibility(View.VISIBLE);
                // if the state comes from "release to refresh",
                // there should be a back animation for arrow
                if (isBack) {
                    isBack = false;
                    ptrHeaderArrowImageView.clearAnimation();
                    ptrHeaderArrowImageView.startAnimation(reverseAnimation);

                    ptrHeaderTipTextView.setText("下拉刷新");
                } else {
                    ptrHeaderTipTextView.setText("下拉刷新");
                }
                break;

            case REFRESHING:

                headerView.setPadding(0, 0, 0, 0);

                ptrHeaderCircleImageView.setVisibility(View.VISIBLE);
                ptrHeaderCircleImageView.clearAnimation();
                ptrHeaderCircleImageView.startAnimation(circleAnimation);

                ptrHeaderArrowImageView.clearAnimation();
                ptrHeaderArrowImageView.setVisibility(View.GONE);
                ptrHeaderTipTextView.setText("正在刷新...");
                break;
            case DONE:
                headerView.setPadding(0, -1 * headerContentHeight, 0, 0);

                ptrHeaderCircleImageView.setVisibility(View.GONE);
                ptrHeaderArrowImageView.clearAnimation();
                ptrHeaderArrowImageView.setImageResource(R.drawable.ptr_arrow);
                ptrHeaderTipTextView.setText("下拉刷新");
                break;
        }
    }
/*

    //measure the width and height for head layout
    private void measureView(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
                params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
*/

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        catchMotionEvent = true;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mScrollListener = onScrollListener;


    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }


    public void setLoadEnable(boolean loadEnable) {
        this.loadEnable = loadEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    public void onRefreshStart() {

        state = REFRESHING;

        changeHeaderViewByState();
    }

    public void onRefreshComplete() {
        state = DONE;
        changeHeaderViewByState();
    }

    public void onLoadStart() {
        loading = true;
        footerLayout.setPadding(0, 0, 0, 0);
        footerCircleImageView.clearAnimation();
        footerCircleImageView.startAnimation(circleAnimation);

    }

    public void onLoadComplete() {
        loading = false;
        footerLayout.setPadding(0, 0, 0, -footerHeight);
        footerCircleImageView.clearAnimation();

    }

    private void onListRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            catchMotionEvent = true;
        } else {
            catchMotionEvent = false;
        }
        int nowLastVisibileItem = firstVisibleItem + visibleItemCount;
        if (nowLastVisibileItem == totalItemCount
                && lastLastVisibleItem != totalItemCount
                && totalItemCount >= visibleItemCount
                &&totalItemCount>2) {
            if (loadEnable && !loading) {
                prepareLoad();
            }
        }
        lastLastVisibleItem = nowLastVisibileItem;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }

    private void prepareLoad() {
        if (onLoadListener != null) {

            onLoadListener.onLoad();
        }
        onLoadStart();

    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public interface OnLoadListener {
        public void onLoad();
    }
}
