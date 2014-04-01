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
import com.suan.weclient.util.data.holder.resultHolder.MessageResultHolder;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.view.ptr.PTRListview;

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

                    mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_MODE_ALL);
                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_CANCELABLE, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    mDataManager
                                            .doMessageGet((MessageResultHolder) object);
                                    mDataManager
                                            .doAutoLoginEnd();

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    break;
                                default:

                                    break;
                            }

                        }
                    });

                    break;

                case 1:

                    mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_MODE_TODAY);
                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_CANCELABLE, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    mDataManager
                                            .doMessageGet((MessageResultHolder) object);
                                    mDataManager
                                            .doAutoLoginEnd();

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    break;
                                default:

                                    break;
                            }

                        }
                    });

                    break;

                case 2:

                    mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_MODE_YESTERDAY);
                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_CANCELABLE, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    mDataManager
                                            .doMessageGet((MessageResultHolder) object);
                                    mDataManager
                                            .doAutoLoginEnd();

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    break;
                                default:

                                    break;
                            }
                        }
                    });

                    break;
                case 3:

                    mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_MODE_OLDER);
                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_CANCELABLE, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    mDataManager
                                            .doMessageGet((MessageResultHolder) object);
                                    mDataManager
                                            .doAutoLoginEnd();

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    break;
                                default:

                                    break;
                            }
                        }
                    });

                    break;
                case 4:

                    mDataManager.getCurrentMessageHolder().setNowMessageMode(WeChatLoader.GET_MESSAGE_MODE_STAR);
                    mDataManager.getWechatManager().getNewMessageList(WechatManager.DIALOG_POP_CANCELABLE, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    mDataManager
                                            .doMessageGet((MessageResultHolder) object);
                                    mDataManager
                                            .doAutoLoginEnd();

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    break;
                                default:

                                    break;
                            }
                        }
                    });


                    break;

            }

            dismiss();

        }

    }

    private ArrayList<String> getData() {
        list.add("" + mContext.getResources().getString(R.string.message_all));
        list.add("" + mContext.getResources().getString(R.string.message_today));
        list.add("" + mContext.getResources().getString(R.string.message_yesterday));
        list.add("" + mContext.getResources().getString(R.string.message_older));
        list.add("" + mContext.getResources().getString(R.string.message_star));
        return list;
    }


}
