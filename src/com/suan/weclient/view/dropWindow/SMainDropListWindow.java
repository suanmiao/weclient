package com.suan.weclient.view.dropWindow;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;

public class SMainDropListWindow extends PopupWindow {

    private DataManager mDataManager;
    private ListView contentListView;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Context mContext;

    public SMainDropListWindow(DataManager dataManager, Context context, View contentView, int width, int height,
                               boolean focusable) {

        super(contentView, width, height, focusable);
        mContext = context;
        mDataManager = dataManager;
        contentListView = (ListView) contentView
                .findViewById(R.id.drop_down_list);

        adapter = new ArrayAdapter<String>(
                contentView.getContext(),
                R.layout.drop_down_item, R.id.drop_down_item_text, getData());
        contentListView.setAdapter(adapter);
        contentListView.setOnItemClickListener(new ItemClickListener());

    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_ALL) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_ALL);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();
                                mDataManager
                                        .doAutoLoginEnd();

                            }
                        });
/*
                    }
*/

                    break;

                case 1:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_TODAY) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_TODAY);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();

                            }
                        });
/*
                    }
*/

                    break;

                case 2:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_YESTERDAY) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_YESTERDAY);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();

                            }
                        });
/*
                    }
*/

                    break;
                case 3:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_DAY_BEFORE) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_DAY_BEFORE);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();

                            }
                        });
/*
                    }
*/

                    break;
                case 4:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_OLDER) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_OLDER);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();

                            }
                        });
/*
                    }
*/


                    break;
                case 5:
/*
                    if (mDataManager.getCurrentMessageHolder().getNowMessageMode() == WeChatLoader.GET_MESSAGE_STAR) {

                    } else {
*/

                        mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_STAR);
                        mDataManager.getWechatManager().getNewMessageList(true, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code,Object object) {
                                mDataManager
                                        .doMessageGet();

                            }
                        });
/*
                    }
*/

                    break;

            }


        }

    }

    private ArrayList<String> getData() {
        list.add("" + mContext.getResources().getString(R.string.message_all));
        list.add("" + mContext.getResources().getString(R.string.message_today));
        list.add("" + mContext.getResources().getString(R.string.message_yesterday));
        list.add("" + mContext.getResources().getString(R.string.message_day_before));
        list.add("" + mContext.getResources().getString(R.string.message_older));
        list.add("" + mContext.getResources().getString(R.string.message_star));
        return list;
    }


}
