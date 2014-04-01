package com.suan.weclient.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;

import com.suan.weclient.R;
import com.suan.weclient.adapter.FansListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.FansListChangeListener;
import com.suan.weclient.util.data.holder.resultHolder.FansResultHolder;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.actionbar.CustomFansActionView;
import com.suan.weclient.view.ptr.PTRListview;

public class FansListActivity extends SherlockActivity implements
        PTRListview.OnLoadListener, PTRListview.OnRefreshListener {

    private ActionBar actionBar;
    private PTRListview ptrListview;
    private FansListAdapter fansListAdapter;
    private DataManager mDataManager;
    private FansHandler fansHandler;
    private static final int PAGE_FANS = 10;

    /*
    about adapter list change
     */
    public static final int MESSAGE_NOTIFY_TYPE_ONLY_REFRESH = 2;
    public static final int MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA = 3;


    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fans_list_layout);
        initWidgets();
        initData();
        initActionBar();
        initListener();
        loadData();

    }

    private void initWidgets() {

        ptrListview = (PTRListview) findViewById(R.id.fans_list);

    }


    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        CustomFansActionView customFansActionView = new CustomFansActionView(this);
        customFansActionView.init(mDataManager, this);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customFansActionView, layoutParams);


    }

    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();
        fansListAdapter = new FansListAdapter(this, mDataManager);
        ptrListview.setAdapter(fansListAdapter);
        ptrListview.setonRefreshListener(this);
        ptrListview.setOnLoadListener(this);
        fansHandler = new FansHandler();


    }

    private void loadData() {

        mDataManager.doListLoadStart();
        mDataManager.getWechatManager().getFansList(0,
                mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId() + "",
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub

                        switch (code) {
                            case WechatManager.ACTION_SUCCESS:

                                mDataManager.doFansGet((FansResultHolder) object);

                                break;
                            case WechatManager.ACTION_TIME_OUT:

                                break;
                            case WechatManager.ACTION_OTHER:

                                break;
                            case WechatManager.ACTION_SPECIFICED_ERROR:


                                break;
                        }
                        ptrListview.onRefreshComplete();


                    }
                });

    }

    public void onPause() {
        super.onPause();
        mDataManager.clearListLoadingListner();
    }

    private void initListener() {
        mDataManager.addFansListChangeListener(new FansListChangeListener() {

            @Override
            public void onFansGet(FansResultHolder fansResultHolder) {
                // TODO Auto-generated method stub

                Message notifyMessage = new Message();
                notifyMessage.arg1 = MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA;
                notifyMessage.obj = fansResultHolder;
                fansHandler.sendMessage(notifyMessage);


            }
        });


        mDataManager.setListLoadingListener(new DataManager.ListLoadingListener() {
            @Override
            public void onLoadStart() {
                ptrListview.onRefreshStart();
            }

            @Override
            public void onLoadFinish() {
                ptrListview.onRefreshComplete();

            }
        });
    }


    @Override
    public void onLoad() {

        new GetDataTask(ptrListview, GetDataTask.PTR_MODE_LOAD).execute();
    }

    @Override
    public void onRefresh() {

        new GetDataTask(ptrListview, GetDataTask.PTR_MODE_REFRESH).execute();
    }

    public class FansHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            switch (msg.arg1) {
                case MESSAGE_NOTIFY_TYPE_ONLY_REFRESH:

                    break;
                case MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA:
                    FansResultHolder messageResultHolder = (FansResultHolder) msg.obj;
                    mDataManager.getCurrentFansHolder().mergeFansResult(messageResultHolder);
                    if (messageResultHolder != null) {
                        switch (messageResultHolder.getResultMode()) {
                            case FansResultHolder.RESULT_MODE_ADD:

                                ptrListview.onLoadComplete();
                                break;
                            case FansResultHolder.RESULT_MODE_REFRESH:

                                fansListAdapter.updateCache();
                                ptrListview.onRefreshComplete();
                                ptrListview.setSelection(1);

                                break;
                        }

                    }

                    break;
            }


            int size = mDataManager.getCurrentFansHolder().getFansCount();

            ptrListview.setLoadEnable(size % PAGE_FANS == 0);
            ptrListview.requestLayout();
            fansListAdapter.notifyDataSetChanged();

        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        PTRListview mRefreshedView;
        private boolean end = false;
        private int mode;

        public static final int PTR_MODE_REFRESH = 2;
        public static final int PTR_MODE_LOAD = 3;

        public GetDataTask(PTRListview refreshedView, int mode) {
            this.mode = mode;
            mRefreshedView = refreshedView;
            end = false;
            if (mDataManager.getCurrentFansHolder() == null) {
                end = true;
                ptrListview.onLoadComplete();

                return;
            }

            try {
                if (mode == PTR_MODE_LOAD) {

                    if (mDataManager.getCurrentFansHolder().getFansCount()
                            % PAGE_FANS != 0) {
                        end = true;
                        mRefreshedView.onLoadComplete();
                    } else {
                        int page = mDataManager.getCurrentFansHolder()
                                .getFansBeans().size() / 10;

                        mDataManager.getWechatManager().getFansList(page,
                                mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId() + "",
                                new OnActionFinishListener() {

                                    @Override
                                    public void onFinish(int code, Object object) {
                                        // TODO Auto-generated method stub
                                        switch (code) {
                                            case WechatManager.ACTION_SUCCESS:

                                                mDataManager.doFansGet((FansResultHolder) object);

                                                break;
                                            case WechatManager.ACTION_TIME_OUT:

                                                break;
                                            case WechatManager.ACTION_OTHER:

                                                break;
                                            case WechatManager.ACTION_SPECIFICED_ERROR:


                                                break;
                                        }
                                        ptrListview.onLoadComplete();

                                        end = true;

                                    }
                                });

                    }
                } else if (mode == PTR_MODE_REFRESH) {
                    mDataManager.getWechatManager().getFansList(0,
                            mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId() + "",

                            new OnActionFinishListener() {


                                @Override
                                public void onFinish(int code, Object object) {
                                    // TODO Auto-generated method stub
                                    switch (code) {
                                        case WechatManager.ACTION_SUCCESS:

                                            mDataManager.doFansGet((FansResultHolder) object);

                                            break;
                                        case WechatManager.ACTION_TIME_OUT:

                                            break;
                                        case WechatManager.ACTION_OTHER:

                                            break;
                                        case WechatManager.ACTION_SPECIFICED_ERROR:

                                            break;
                                    }
                                    ptrListview.onRefreshComplete();

                                    end = true;

                                }
                            });

                }
            } catch (Exception e) {

            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Simulates a background job.
            try {

                while (!end) {
                    Thread.sleep(50);
                }

            } catch (Exception exception) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            switch (mode) {
                case PTRListview.PTR_MODE_LOAD:
                    mRefreshedView.onLoadComplete();

                    break;
                case PTRListview.PTR_MODE_REFRESH:
                    mRefreshedView.onRefreshComplete();

                    break;

            }

        }
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);
    }


}
