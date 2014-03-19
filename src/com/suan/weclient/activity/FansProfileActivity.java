package com.suan.weclient.activity;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.FansBean;
import com.suan.weclient.util.data.bean.FansGroupBean;
import com.suan.weclient.util.net.DataParser;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.images.ImageCacheManager;

import java.util.ArrayList;

public class FansProfileActivity extends SherlockActivity {

    private ActionBar actionBar;
    private ImageView backButton;
    private TextView titleTextView;
    private DataManager mDataManager;
    private FansHandler fansHandler;

    /*
    about widgets in data loading
     */
    private TextView nicknameTextView, remarkNameTextView, groupTextView, countryTextView, cityTextView, signatureTextView;
    private ImageView profileImageView, genderImageView;
    private ImageView remarkEditButton, groupEditButton;
    private Button sendMsgButton;

    private DataParser.FansProfileHolder fansProfileHolder;

    /*
about dialog
 */
    private EditText popContentEditText;
    private TextView popTitleTextView;
    private TextView textAmountTextView;
    private Button popCancelButton, popSureButton;

    private ListView popListView;
    private Dialog dialog;


    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fans_profile_layout);

        initWidgets();
        initData();
        initActionBar();
        initListener();
        loadData();

    }

    private void initWidgets() {

        nicknameTextView = (TextView) findViewById(R.id.fans_profile_text_nickname);
        remarkNameTextView = (TextView) findViewById(R.id.fans_profile_text_remark_name);
        groupTextView = (TextView) findViewById(R.id.fans_profile_text_group);
        countryTextView = (TextView) findViewById(R.id.fans_profile_text_country);
        cityTextView = (TextView) findViewById(R.id.fans_profile_text_city);
        signatureTextView = (TextView) findViewById(R.id.fans_profile_text_signature);

        profileImageView = (ImageView) findViewById(R.id.fans_profile_img_profile);
        genderImageView = (ImageView) findViewById(R.id.fans_profile_img_gender);

        remarkEditButton = (ImageView) findViewById(R.id.fans_profile_button_edit_remark);
        groupEditButton = (ImageView) findViewById(R.id.fans_profile_button_edit_group);

        sendMsgButton = (Button) findViewById(R.id.fans_profile_button_send_message);

        remarkEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fansProfileHolder != null && fansProfileHolder.fansBean != null) {

                    popEditRemark();
                }
            }
        });


        groupEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fansProfileHolder != null && fansProfileHolder.fansBean != null) {

                    popGroupList();
                }
            }
        });

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fansProfileHolder != null && fansProfileHolder.fansBean != null) {

                    mDataManager.createChat(mDataManager.getCurrentUser(),
                            fansProfileHolder.fansBean.getFake_id(), fansProfileHolder.fansBean.getNickname());
                    Intent jumbIntent = new Intent();
                    jumbIntent.setClass(FansProfileActivity.this, ChatActivity.class);
                    startActivity(jumbIntent);
                    overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);

                }
            }
        });

    }


    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View customActionBarView = layoutInflater.inflate(R.layout.custom_actionbar_back_with_title, null);

        backButton = (ImageView) customActionBarView.findViewById(R.id.actionbar_back_with_title_img_back);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FansProfileActivity.this.finish();
                FansProfileActivity.this.overridePendingTransition(R.anim.activity_movein_from_left_anim, R.anim.activity_moveout_to_right_anim);
            }
        });

        titleTextView = (TextView) customActionBarView.findViewById(R.id.actionbar_back_with_title_text_title);
        titleTextView.setText(getResources().getString(R.string.detail_information));

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customActionBarView, layoutParams);


    }

    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();

        fansHandler = new FansHandler();

    }

    private void initListener() {

    }

    private void setProfileData() {
        FansBean fansBean = fansProfileHolder.fansBean;
        nicknameTextView.setText(fansBean.getNickname());
        remarkNameTextView.setText(fansBean.getRemarkName());
        signatureTextView.setText(fansBean.getSignature());
        countryTextView.setText(fansBean.getCountry());
        cityTextView.setText(fansBean.getCity());
        genderImageView.setVisibility(View.VISIBLE);
        genderImageView.setSelected(fansBean.getGender() == FansBean.GENDER_FEMALE);
        groupTextView.setText(getGroupName(fansBean.getGoupId()));

    }

    private String getGroupName(int index) {
        ArrayList<FansGroupBean> fansGroupBeans = fansProfileHolder.fansGroupBeans;

        try {
            return fansGroupBeans.get(index).getGroupName();

        } catch (Exception e) {

        }

        return "...";
    }

    private void loadData() {

        Bitmap contentBitmap = mDataManager.getCacheManager()
                .getBitmap(
                        ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                                + mDataManager.getFansProfileFakeId());
        if (contentBitmap != null) {
            profileImageView.setImageBitmap(contentBitmap);
        } else {

            mDataManager.getWechatManager().getMessageHeadImg(
                    mDataManager.getCurrentPosition(),
                    mDataManager.getFansProfileFakeId(),
                    "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&day=7&token=1288653525&lang=zh_CN",
                    profileImageView, new WechatManager.OnActionFinishListener() {

                @Override
                public void onFinish(int code, Object object) {
                    // TODO Auto-generated method stub
                    if (code == WechatManager.ACTION_SUCCESS) {
                        if (object != null) {
                            Bitmap roundBitmap = Util.roundCornerWithBorder((Bitmap) object,
                                    profileImageView.getWidth(), 10,
                                    Color.parseColor("#c6c6c6"));

                            mDataManager.getCacheManager().putBitmap(
                                    ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                                            + mDataManager.getFansProfileFakeId(), roundBitmap, true);
                            profileImageView.setImageBitmap(roundBitmap);
                            profileImageView.setTag(roundBitmap);

                        }

                    }

                }
            });

        }

        mDataManager.getWechatManager().getFansProfile(mDataManager.getFansProfileFakeId(), mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
            @Override
            public void onFinish(int code, Object object) {

                switch (code) {
                    case WechatManager.ACTION_SUCCESS:
                        fansProfileHolder = (DataParser.FansProfileHolder) object;
                        setProfileData();

                        break;
                    case WechatManager.ACTION_SPECIFICED_ERROR:
                        Toast.makeText(FansProfileActivity.this, "相关参数过期，无法获取相关内容", Toast.LENGTH_LONG).show();


                        break;
                    default:

                        break;
                }


            }
        });

    }


    public void popEditRemark() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_edit_text_title);

        popContentEditText = (EditText) dialogView
                .findViewById(R.id.dialog_edit_edit_text);
        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_edit_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_edit_button_cancel);

        textAmountTextView = (TextView) dialogView
                .findViewById(R.id.dialog_edit_text_num);
        textAmountTextView.setText("0 x");
        textAmountTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popContentEditText.setText("");

            }
        });


        popContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                textAmountTextView.setTextColor(Color.rgb(0, 0, 0));
                textAmountTextView.setText(s.length() + " x");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        popTitleTextView.setText("修改备注名:"
                + fansProfileHolder.fansBean.getNickname());
        popSureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String editContent = popContentEditText.getText().toString();
                if (editContent.length() == 0) {
                    Toast.makeText(FansProfileActivity.this, "备注名不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    editRemark(editContent);
                }


            }
        });
        popCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        dialog = new Dialog(this, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }


    private void editRemark(final String replyContent) {

        if (replyContent.length() == 0) {

            Toast.makeText(this, "请输入内容", Toast.LENGTH_LONG).show();

            return;
        }
        dialog.dismiss();

        mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(),
                WeChatLoader.MODIFY_CONTACTS_ACTION_REMARK, fansProfileHolder.fansBean.getFake_id(), "",
                replyContent, new WechatManager.OnActionFinishListener() {
            @Override
            public void onFinish(int code, Object object) {
                Log.e("edit remark", "ok");
                Toast.makeText(FansProfileActivity.this, "修改分组成功", Toast.LENGTH_SHORT).show();
                try {
                    fansProfileHolder.fansBean.setRemarkName(replyContent);
                    remarkNameTextView.setText(fansProfileHolder.fansBean.getRemarkName());
                } catch (Exception e) {

                }

            }
        });

    }


    public void popGroupList() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_list_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_list_text_title);
        popListView = (ListView) dialogView.findViewById(R.id.dialog_list_list_content);

        ArrayAdapter adapter = new ArrayAdapter<String>(
                this,
                R.layout.dialog_list_item, R.id.dialog_list_item_text, getGroupNameData());
        popListView.setAdapter(adapter);
        popListView.setOnItemClickListener(new ItemClickListener());

        popTitleTextView.setText(getResources().getString(R.string.group) + ":");

        dialog = new Dialog(this, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {

        public ItemClickListener() {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int index, long id) {

            dialog.dismiss();

            mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(),
                    WeChatLoader.MODIFY_CONTACTS_ACTION_MODIFY, fansProfileHolder.fansBean.getFansId(),
                    getGroupIdArray()[index] + "", "", new WechatManager.OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    Toast.makeText(FansProfileActivity.this, "修改分组成功", Toast.LENGTH_SHORT).show();
                    try {
                        fansProfileHolder.fansBean.setGroupId(getGroupIdArray()[index]);
                        groupTextView.setText(getGroupNameData().get(index));
                    } catch (Exception e) {

                    }


                }
            });
        }

    }

    private ArrayList<String> getGroupNameData() {

        ArrayList<String> result = new ArrayList<String>();
        ArrayList<FansGroupBean> fansGroupBeans = fansProfileHolder.fansGroupBeans;
        for (int i = 0; i < fansGroupBeans.size(); i++) {
            String nowGroupNameString = fansGroupBeans.get(i).getGroupName();
            result.add(nowGroupNameString);

        }
        return result;
    }

    private int getItemGroupIndex(int itemGroupId) {
        for (int i = 0; i < mDataManager.getCurrentFansHolder()
                .getFansGroupBeans().size(); i++) {
            int nowGroupId = mDataManager.getCurrentFansHolder()
                    .getFansGroupBeans().get(i).getGroupId();
            if (itemGroupId == nowGroupId) {
                return i;

            }
        }
        return 0;

    }


    private int[] getGroupIdArray() {
        ArrayList<FansGroupBean> fansGroupBeans = fansProfileHolder.fansGroupBeans;
        int groupSize = fansGroupBeans.size();
        int[] groupStrings = new int[groupSize];
        for (int i = 0; i < groupSize; i++) {
            groupStrings[i] = fansGroupBeans.get(i).getGroupId();

        }

        return groupStrings;

    }


    public class FansHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);

        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_movein_from_left_anim, R.anim.activity_moveout_to_right_anim);
    }

}
