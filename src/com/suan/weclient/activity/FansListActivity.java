package com.suan.weclient.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.FansListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.FansListChangeListener;
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

    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fans_list_layout);
        initWidgets();
        initData();
        initActionBar();
        initListener();

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
        ptrListview.onRefreshStart();
        fansHandler = new FansHandler();

        mDataManager.getWechatManager().getFansList(0,
                mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub

                        Message message = new Message();
                        message.obj = object;
                        message.arg1 = PTRListview.PTR_MODE_REFRESH;

                        fansHandler.sendMessage(message);

                    }
                });

    }

    private void initListener() {
        mDataManager.addFansListChangeListener(new FansListChangeListener() {

            @Override
            public void onFansGet(int mode) {
                // TODO Auto-generated method stub

                switch (mode) {
                    case PTRListview.PTR_MODE_LOAD:
                        ptrListview.onLoadComplete();
                        break;
                    case PTRListview.PTR_MODE_REFRESH:

                        ptrListview.onRefreshComplete();
                        break;
                }
                fansListAdapter.notifyDataSetChanged();

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
            mDataManager.doFansGet(msg.arg1);

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

                    if (mDataManager.getCurrentFansHolder().getFansBeans()
                            .size()
                            % PAGE_FANS != 0) {
                        end = true;
                        mRefreshedView.onLoadComplete();
                    } else {
                        int page = mDataManager.getCurrentFansHolder()
                                .getFansBeans().size() / 10;

                        mDataManager.getWechatManager().getFansList(page,
                                mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),
                                new OnActionFinishListener() {

                                    @Override
                                    public void onFinish(int code, Object object) {
                                        // TODO Auto-generated method stub
                                        Message message = new Message();
                                        message.obj = object;
                                        message.arg1 = PTRListview.PTR_MODE_LOAD;

                                        fansHandler.sendMessage(message);
                                        end = true;

                                    }
                                });

                    }
                } else if (mode == PTR_MODE_REFRESH) {
                    mDataManager.getWechatManager().getFansList(0,
                            mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),

                            new OnActionFinishListener() {


                                @Override
                                public void onFinish(int code, Object object) {
                                    // TODO Auto-generated method stub
                                    Message message = new Message();
                                    message.obj = object;
                                    message.arg1 = PTRListview.PTR_MODE_REFRESH;

                                    fansHandler.sendMessage(message);
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
}
