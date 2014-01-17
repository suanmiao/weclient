package com.suan.weclient.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.FansListActivity;
import com.suan.weclient.activity.SettingActivity;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ProfileGetListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.UserBean;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import java.util.List;

public class ProfileFragment extends Fragment {

    private DataManager mDataManager;
    private View view;
    private RelativeLayout userLayout, settingLayout, feedbackLayout, checkUpdateLayout, exitAccountLayout;
    private RelativeLayout newPeopleLayout;
    private TextView newUserTextView, versionTextView;


    private Dialog popDialog;

    private EditText popContentEditText;
    private TextView popTitleTextView;
    private TextView popTextAmountTextView;
    private ImageButton popCancelButton, popSureButton;

    private FeedbackAgent agent;
    private Conversation defaultConversation;

    public ProfileFragment(DataManager dataManager) {

        mDataManager = dataManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_layout, null);
        initWidgets();
        initAgent();
        initListener();
        initData();
        return view;
    }

    private void initWidgets() {
        userLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_user);
        settingLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_setting);
        feedbackLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_feedback);
        checkUpdateLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_check_for_update);
        exitAccountLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_exit_account);

        userLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDataManager.getUserGroup().size() > 0) {
                    mDataManager.getCurrentUser().setNewPeople(0 + "");
                    refreshNewPeopleLayout(mDataManager.getCurrentUser());

                    Intent jumbIntent = new Intent();
                    jumbIntent.setClass(getActivity(), FansListActivity.class);
                    getActivity().startActivity(jumbIntent);

                }

            }

        });

        settingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(getActivity(), SettingActivity.class);
                getActivity().startActivity(jumbIntent);

            }
        });

        feedbackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popFeedback();

            }
        });

        checkUpdateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();

            }
        });

        newPeopleLayout = (RelativeLayout) view.findViewById(R.id.profile_layout_new_people);
        newPeopleLayout.setVisibility(View.GONE);

        newUserTextView = (TextView) view.findViewById(R.id.profile_text_new_user);
        versionTextView = (TextView) view.findViewById(R.id.profile_text_version);

    }


    private void initAgent() {

        agent = new FeedbackAgent(getActivity());
        defaultConversation = agent.getDefaultConversation();
    }

    private void initListener() {
        mDataManager.addUserGroupListener(new UserGroupListener() {

            @Override
            public void onGroupChangeEnd() {
                // TODO Auto-generated method stub
                if (mDataManager.getUserGroup().size() == 0) {

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


        mDataManager.addProfileGetListener(new ProfileGetListener() {

            @Override
            public void onGet(UserBean userBean) {
                // TODO Auto-generated method stub
                refreshNewPeopleLayout(userBean);

            }
        });

    }

    private void refreshNewPeopleLayout(UserBean userBean) {
        if (userBean.getNewPeople().equals("0")) {
            newPeopleLayout.setVisibility(View.GONE);

        } else {

            newPeopleLayout.setVisibility(View.VISIBLE);
            newUserTextView.setText(userBean.getNewPeople());
        }

    }

    private void initData() {


        try {
            String versionString = getVersionName();
            versionTextView.setText("V " + versionString);

        } catch (Exception exception) {

        }


        if (mDataManager.getCurrentUser() != null) {

        }
    }

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    private void checkUpdate() {


        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update

                        break;
                    case 1: // has no update
                        Toast.makeText(getActivity(), "没有新版本", Toast.LENGTH_LONG).show();
                        break;
                    case 2: // none wifi
                        break;
                    case 3: // time out
                        Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        UmengUpdateAgent.update(getActivity());
    }


    private void popFeedback() {

        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_feedback_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.pop_feedback_text_title);

        popContentEditText = (EditText) dialogView
                .findViewById(R.id.pop_feedback_edit_text);
        popSureButton = (ImageButton) dialogView
                .findViewById(R.id.pop_feedback_button_sure);
        popCancelButton = (ImageButton) dialogView
                .findViewById(R.id.pop_feedback_button_cancel);

        popTextAmountTextView = (TextView) dialogView
                .findViewById(R.id.pop_feedback_text_num);
        popTextAmountTextView.setOnClickListener(new OnClickListener() {

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
                popTextAmountTextView.setTextColor(Color.rgb(0, 0, 0));
                popTextAmountTextView.setText(popContentEditText.getText().length() + " x");

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

        popTitleTextView.setText("反馈");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String content = popContentEditText.getEditableText()
                        .toString();
                defaultConversation.addUserReply(content);

                mDataManager.doLoadingStart("反馈发送中...");

                sync();
            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popDialog.cancel();

            }
        });

        popDialog = new Dialog(getActivity(), R.style.dialog);

        popDialog.setContentView(dialogView);
        popDialog.show();

    }

    void sync() {
        Conversation.SyncListener listener = new Conversation.SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
                popContentEditText.setText("");
                mDataManager.doLoadingEnd();
                popDialog.dismiss();
                Toast.makeText(getActivity(), "反馈发送成功!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onReceiveDevReply(List<DevReply> replyList) {
            }
        };
        defaultConversation.sync(listener);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /*
    interface
     */

    public interface UserListControlListener {
        public void onUserListShow();

        public void onUserListDismiss();
    }

}
