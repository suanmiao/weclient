package com.suan.weclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.adapter.SearchListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.holder.resultHolder.MessageResultHolder;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.view.ptr.PTRListview;
import com.umeng.analytics.MobclickAgent;

public class SearchActivity extends Activity {

    private DataManager mDataManager;

    private LinearLayout parentLayout;
    private EditText contentEditText;
    private RelativeLayout searchLayout;
    private PTRListview ptrListview;
    private SearchListAdapter searchListAdapter;
    private SearchHandler searchHandler;

    private static final int MSG_ONLY_REFRESH = 3;
    private static final int MSG_REFRESH_WITH_DATA = 4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* request no title mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.search_layout);
        initWidget();
        initData();

    }


    private void initData() {
        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();

        mDataManager.createSearch(mDataManager.getCurrentUser());

        searchListAdapter = new SearchListAdapter(SearchActivity.this, mDataManager);
        ptrListview.setAdapter(searchListAdapter);
        searchHandler = new SearchHandler();
    }

    public class SearchHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);

            switch (msg.arg1) {
                case MSG_REFRESH_WITH_DATA:
                    MessageResultHolder messageResultHolder = (MessageResultHolder) msg.obj;
                    mDataManager.getSearchMessageHolder().mergeMessageResult(messageResultHolder);
                    if (messageResultHolder != null) {
                        switch (messageResultHolder.getResultMode()) {
                            case MessageResultHolder.RESULT_MODE_ADD:

                                ptrListview.onLoadComplete();
                                break;
                            case MessageResultHolder.RESULT_MODE_REFRESH:

                                ptrListview.onRefreshComplete();
                                ptrListview.setSelection(1);

                                break;
                        }

                    }
                    break;
                case MSG_ONLY_REFRESH:
                    mDataManager.getSearchMessageHolder().clearMessage(false);

                    break;
            }

            searchListAdapter.notifyDataSetChanged();
            ptrListview.requestLayout();

        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initWidget() {
        parentLayout = (LinearLayout) findViewById(R.id.search_layout_parent);
        searchLayout = (RelativeLayout) findViewById(R.id.search_layout_search);
        contentEditText = (EditText) findViewById(R.id.search_edit_content);
        ptrListview = (PTRListview) findViewById(R.id.search_list);

        if (contentEditText.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    ptrListview.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String content = contentEditText.getText().toString();
                    if (content.length() == 0) {

                        Toast.makeText(SearchActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    search(content);

                    return true;
                }
                return false;
            }
        });
        ptrListview.setRefreshEnable(false);
        ptrListview.setLoadEnable(false);


        searchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentEditText.getText().toString();
                if (content.length() == 0) {

                    Toast.makeText(SearchActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();

                    return;
                }
                search(content);

            }
        });
        parentLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.search_activity_fly_in, R.anim.search_activity_fly_out);

            }
        });

    }

    private void search(String content) {
        ptrListview.setVisibility(View.VISIBLE);
        mDataManager.getSearchMessageHolder().clearMessage(false);
        Message msg = new Message();
        msg.arg1 = MSG_ONLY_REFRESH;
        searchHandler.sendMessage(msg);

        ptrListview.onRefreshStart();
        mDataManager.getWechatManager().getSearchMessageList(content, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
            @Override
            public void onFinish(int code, Object object) {
                ptrListview.onRefreshComplete();

                switch (code) {
                    case WechatManager.ACTION_SUCCESS:
                        Message msg = new Message();
                        msg.arg1 = MSG_REFRESH_WITH_DATA;
                        msg.obj = object;
                        searchHandler.sendMessage(msg);

                        break;
                    case WechatManager.ACTION_OTHER:

                        break;
                    case WechatManager.ACTION_TIME_OUT:

                        break;
                }

            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.search_activity_fly_in, R.anim.search_activity_fly_out);

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
