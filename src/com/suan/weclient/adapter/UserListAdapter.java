package com.suan.weclient.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.activity.LoginActivity;
import com.suan.weclient.activity.SplashActivity;
import com.suan.weclient.fragment.UserListFragment;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.UserGoupPushHelper;
import com.suan.weclient.util.data.UserListItem;
import com.suan.weclient.util.net.DataParser;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter implements OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Activity mActivity;
    private UserBean addBean;

    private int selectPosition;

    /*
      * whether the scroll is busy
      */
    private boolean mBusy = false;
    /*
     * whether the user cancel the last reply if so ,we will save it
     */
    /*
    about dialog
     */

    private Dialog popDialog;

    private TextView popContentTextView;
    private TextView popTitleTextView;
    private TextView popTextAmountTextView;
    private Button popCancelButton, popSureButton;
    private ArrayList<UserListItem> userListItems;


    public UserListAdapter(Activity mActivity, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(mActivity);
        this.mDataManager = dataManager;
        this.mActivity = mActivity;
        this.userListItems = new ArrayList<UserListItem>();
        this.mListCacheManager = new ListCacheManager();
    }

    private ArrayList<UserListItem> getUserItems() {
        if (userListItems.size() != mDataManager.getUserGroup().size() + 1) {


            userListItems = new ArrayList<UserListItem>();
            ArrayList<UserBean> userBeans = mDataManager.getUserGroup();
            for (int i = 0; i < userBeans.size(); i++) {
                userListItems.add(new UserListItem(userBeans.get(i), UserListItem.TYPE_USER));
            }
            userListItems.add(new UserListItem(null, UserListItem.TYPE_ADD));

        }
        return userListItems;
    }

    @Override
    public int getCount() {
        return getUserItems().size();
    }

    @Override
    public Object getItem(int position) {
        return getUserItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectPosition(int position) {
        selectPosition = position;
    }

    public void updateCache() {
        mListCacheManager.clearData();
    }

    public View newView(final int position) {

        View convertView = null;
        switch (getUserItems().get(position).getItemType()) {
            case UserListItem.TYPE_USER:

                convertView = LayoutInflater.from(mActivity).inflate(
                        R.layout.user_group_user_item, null);

                break;
            case UserListItem.TYPE_ADD:

                convertView = LayoutInflater.from(mActivity).inflate(
                        R.layout.user_group_add_item, null);

                break;
        }

        return convertView;

    }

    private ItemViewHolder getHolder(final View view, int position) {

        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder(view, position);
            view.setTag(holder);
        }
        return holder;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        switch (getUserItems().get(position).getItemType()) {
            case UserListItem.TYPE_USER:

                mDataManager.setCurrentPosition(position);
                UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(mActivity));
                userGoupPushHelper.updateUserGroup(mDataManager);
                SharedPreferenceManager.putPushUserGroup(mActivity, userGoupPushHelper.getString());

                mDataManager.doAutoLogin();

                break;
            case UserListItem.TYPE_ADD:


                popDialog = Util.createLoginDialog(mActivity, "登录", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText userIdEdit = (EditText) popDialog.findViewById(R.id.dialog_login_edit_user_id);
                                EditText pwdEdit = (EditText) popDialog.findViewById(R.id.dialog_login_edit_pass_word);
                                String userId = userIdEdit.getText().toString();
                                String pwd = pwdEdit.getText().toString();
                                popDialog.dismiss();
                                addUserAfterLogin(userId, pwd);


                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                popDialog.dismiss();
                            }
                        }
                );

                popDialog.show();



               /* Intent jumbIntent = new Intent();
                jumbIntent.setClass(mActivity, LoginActivity.class);
                jumbIntent.putExtra(SplashActivity.JUMB_KEY_ENTER_STATE, SplashActivity.JUMB_VALUE_NONE);
                mActivity.startActivityForResult(jumbIntent,
                        UserListFragment.START_ACTIVITY_LOGIN);
*/
                break;
        }

        mDataManager.getUserListControlListener().onUserListDismiss();


        /*
        funny things ,if not do this,the user index icon will not change
         */

        notifyDataSetChanged();


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        popDeleteUser(position);
        notifyDataSetChanged();
        return false;
    }

    public class ItemViewHolder {

        private View parentView;

        private ImageView profileImageView;
        private TextView profileTextView;
        private ImageView indexImageView;

        public ItemViewHolder(View parentView, final int position) {

            this.parentView = parentView;
            switch (getUserItems().get(position).getItemType()) {
                case UserListItem.TYPE_USER:

                    profileTextView = (TextView) parentView
                            .findViewById(R.id.user_group_text_user_name);
                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.user_group_img_profile);
                    indexImageView = (ImageView) parentView.findViewById(R.id.user_group_img_index);

                    break;
                case UserListItem.TYPE_ADD:

                    break;
            }

        }

    }


    private void setHeadImg(final ItemViewHolder holder, final int position) {

        boolean imgLoaded = false;
        if (holder.profileImageView.getTag() != null) {
            imgLoaded = (Boolean) holder.profileImageView.getTag();
        }

        if (!mBusy && !imgLoaded) {

            Bitmap headBitmap = mDataManager.getCacheManager().getBitmap(
                    ImageCacheManager.CACHE_USER_PROFILE
                            + mDataManager.getUserGroup().get(position)
                            .getUserName());
            if (headBitmap != null) {

                holder.profileImageView.setImageBitmap(headBitmap);

            } else {
                holder.profileImageView.setTag(true);

                mDataManager.getWechatManager().getUserImgDirectly(WechatManager.DIALOG_POP_NO,
                        position, holder.profileImageView, new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub
                        if (code == WechatManager.ACTION_SUCCESS) {
                            if (object != null) {
                                holder.profileImageView.setTag(true);
                                Bitmap nowUserBitmap = (Bitmap) object;
                                mDataManager.getCacheManager().putBitmap(
                                        ImageCacheManager.CACHE_USER_PROFILE
                                                + mDataManager.getUserGroup()
                                                .get(position)
                                                .getUserName(),
                                        nowUserBitmap, true);

                            }

                        } else {

                        }

                    }
                });


            }
        } else {

        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;

        if (!mListCacheManager.containView(position + "|" + userListItems.get(position).getItemType())) {
            v = newView(position);
            mListCacheManager.putView(v, position + "|" + userListItems.get(position).getItemType());

        } else {

            v = mListCacheManager.getView(position + "|" + userListItems.get(position).getItemType());

        }

        bindView(v, position);

        return v;
    }


    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        switch (getUserItems().get(position).getItemType()) {
            case UserListItem.TYPE_USER:


                String shortedName = Util.getShortString(mDataManager.getUserGroup().get(position)
                        .getNickname(), 6, 3);

                holder.profileTextView.setText(shortedName
                );

                if (position == selectPosition) {
                    // row.setBackgroundResource(R.drawable.biz_navigation_tab_bg_pressed);
                    holder.profileTextView.setSelected(true);
                }

                setHeadImg(holder, position);
                if (position == mDataManager.getCurrentPosition()) {

                    holder.indexImageView.setVisibility(View.VISIBLE);

                } else {

                    holder.indexImageView.setVisibility(View.INVISIBLE);

                }

                break;
            case UserListItem.TYPE_ADD:

                break;
        }

    }


    private void popDeleteUser(final int position) {


        LayoutInflater inflater = (LayoutInflater)
                mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_ensure_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_ensure_text_title);
        popContentTextView = (TextView) dialogView.findViewById(R.id.dialog_ensure_text_content);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_cancel);

        popTitleTextView.setText("删除账户");
        popContentTextView.setText("删除账户将删除账户相关的所有数据，确认删除？");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferenceManager.deleteUser(mActivity,
                        mDataManager.getUserGroup().get(position)
                                .getUserName());
                mDataManager.updateUserGroup();
                mDataManager.doGroupChangeEnd();
                popDialog.dismiss();


            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popDialog.cancel();

            }
        });

        popDialog = new Dialog(mActivity, R.style.dialog);

        popDialog.setContentView(dialogView);
        popDialog.show();


    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) { // 滑动停止
            mBusy = false;

        } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 滑动手未松开

            mBusy = true;
        } else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {// 滑动中手已松开
            mBusy = true;
        }
    }


    private void addUserAfterLogin(final String userName, final String pwd) {
        WeChatLoader.wechatLogin(
                new WeChatLoader.WechatLoginCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult, String slaveSid,
                                       String slaveUser) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {
                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                popDialog.dismiss();

                                popDialog = Util.createEnsureDialog(
                                        new DataManager.DialogSureClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                // TODO Auto-generated method stub
                                                popDialog.dismiss();
                                                addUserAfterLogin(userName, pwd);

                                            }
                                        }, false, mActivity, "网络", "网络错误，重试？", true);
                                popDialog.show();
                                break;
                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                popDialog.dismiss();

                                popDialog = Util.createEnsureDialog(
                                        new DataManager.DialogSureClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                // TODO Auto-generated method stub
                                                addUserAfterLogin(userName, pwd);

                                            }
                                        }, false, mActivity, "网络", "网络错误，重试？", true);
                                popDialog.show();
                                break;
                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {

                                    addBean = new UserBean(userName, WeChatLoader
                                            .getMD5Str(pwd));
                                    addBean.setSlaveSid(slaveSid);
                                    addBean.setSlaveUser(slaveUser);
                                    int loginResult = DataParser.parseLogin(addBean,
                                            strResult, slaveSid, slaveUser,
                                            mActivity.getApplicationContext());
                                    switch (loginResult) {
                                        case DataParser.PARSE_SUCCESS:

                                            SharedPreferenceManager
                                                    .insertUser(
                                                            mActivity,
                                                            addBean);

                                            mDataManager.updateUserGroup();
                                            mDataManager.doAddUser();
                                            mDataManager.doGroupChangeEnd();
                                            mDataManager.doAutoLogin();

                                            popDialog.dismiss();

                                            break;

                                        case DataParser.PARSE_FAILED:

                                            popDialog.dismiss();

                                            popDialog = Util.createEnsureDialog(
                                                    new DataManager.DialogSureClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated method stub
                                                            popDialog.dismiss();

                                                        }
                                                    }, false, mActivity, "错误", "登录失败，请检查账户名和密码",
                                                    true);

                                            popDialog.show();
                                            break;
                                    }

                                } catch (Exception exception) {

                                }

                                break;

                        }

                    }
                }, userName, WeChatLoader
                .getMD5Str(pwd), "", "json"
        );

    }


}