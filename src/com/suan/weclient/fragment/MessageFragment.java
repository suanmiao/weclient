package com.suan.weclient.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suan.weclient.R;
import com.suan.weclient.adapter.MessageListAdapter;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.ptr.PTRListview;

public class MessageFragment extends Fragment implements
        PTRListview.OnRefreshListener, PTRListview.OnLoadListener {
    View view;
    private DataManager mDataManager;
    private PTRListview ptrListview;
    private MessageListAdapter messageListAdapter;
    private MessageHandler mHandler;

    private static final int PAGE_MESSAGE_AMOUNT = 20;

    public MessageFragment() {

    }

    public MessageFragment(DataManager dataManager) {

        mDataManager = dataManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment, null);

        mHandler = new MessageHandler();
        initWidgets();
        initListener();
        initData();

        return view;
    }

    private void initWidgets() {
        ptrListview = (PTRListview) view.findViewById(R.id.reply_list);


    }


    private void initData() {

        if (mDataManager.getCurrentMessageHolder() != null) {

            messageListAdapter = new MessageListAdapter(getActivity(),
                    mDataManager);
            ptrListview.setAdapter(messageListAdapter);
            ptrListview.setOnScrollListener(messageListAdapter);

            ptrListview.setonRefreshListener(MessageFragment.this);
            ptrListview.setOnLoadListener(MessageFragment.this);
            //indecate loading
            ptrListview.onRefreshStart();

        }

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initListener() {

        mDataManager.addUserGroupListener(new UserGroupListener() {

            @Override
            public void onGroupChangeEnd() {
                // TODO Auto-generated method stub
                if (mDataManager.getUserGroup().size() == 0) {
                    messageListAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onAddUser() {
                // TODO Auto-generated method stub

            }

            @Override
            public void deleteUser(int index) {
                // TODO Auto-generated method stub

            }
        });
        mDataManager.addMessageChangeListener(new DataManager.MessageGetListener() {

            @Override
            public void onMessageGet(int mode) {
                // TODO Auto-generated method stub

                ptrListview.requestLayout();
                switch (mode) {
                    case PTRListview.PTR_MODE_REFRESH:

                        messageListAdapter.updateCache();
                        ptrListview.onRefreshComplete();
                        break;

                    case PTRListview.PTR_MODE_LOAD:
                        ptrListview.onLoadComplete();

                        break;
                }

                messageListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onRefresh() {

        new GetDataTask(ptrListview, PTRListview.PTR_MODE_REFRESH).execute();

    }

    @Override
    public void onLoad() {

        new GetDataTask(ptrListview, PTRListview.PTR_MODE_LOAD).execute();

    }

    public class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);

            mDataManager.doMessageGet(msg.arg1);

        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        private PTRListview mRefreshedView;
        private boolean end = false;
        private int mode;

        public GetDataTask(PTRListview refreshedView, int mode) {
            mRefreshedView = refreshedView;
            this.mode = mode;
            end = false;
            if (mDataManager.getCurrentMessageHolder() == null) {
                end = true;
                mRefreshedView.onLoadComplete();
                return;
            }
            try {
                if (mode == PTRListview.PTR_MODE_LOAD) {

                    int size = mDataManager.getCurrentMessageHolder()
                            .getMessageList().size();

                    // must be fuul amount of page

                    if (size % PAGE_MESSAGE_AMOUNT == 0) {
                        int page = size
                                / PAGE_MESSAGE_AMOUNT
                                + ((size / PAGE_MESSAGE_AMOUNT == 0) ? size
                                % PAGE_MESSAGE_AMOUNT
                                / (PAGE_MESSAGE_AMOUNT / 2) : 0) + 1;

                        mDataManager.getWechatManager().getNextMessageList(
                                page, mDataManager.getCurrentPosition(),
                                new OnActionFinishListener() {

                                    @Override
                                    public void onFinish(int code, Object object) {
                                        // TODO Auto-generated method stub
                                        Message message = new Message();
                                        message.obj = object;
                                        message.arg1 = PTRListview.PTR_MODE_LOAD;
                                        mHandler.sendMessage(message);

                                        end = true;

                                    }
                                });

                    } else {

                        ptrListview.onLoadComplete();
                        end = true;
                    }

                } else if (mode == PTRListview.PTR_MODE_REFRESH) {

                    Log.e("now message mode",""+mDataManager.getCurrentMessageHolder().getNowMessageMode());

                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_NO,
                            mDataManager.getCurrentPosition(),
                            new OnActionFinishListener() {

                                @Override
                                public void onFinish(int code, Object object) {
                                    // TODO Auto-generated method stub
                                    Log.e("get message", "" + code);

                                    Message message = new Message();
                                    message.obj = object;
                                    message.arg1 = PTRListview.PTR_MODE_REFRESH;
                                    mHandler.sendMessage(message);
                                    end = true;
                                    mDataManager
                                            .getWechatManager()
                                            .getUserProfile(
                                                    WechatManager.DIALOG_POP_NO,
                                                    mDataManager
                                                            .getCurrentPosition(),
                                                    new OnActionFinishListener() {

                                                        @Override
                                                        public void onFinish(
                                                                int code, Object object) {
                                                            // TODO
                                                            // Auto-generated
                                                            // method stub

                                                        }
                                                    });
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
            switch(mode){
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
