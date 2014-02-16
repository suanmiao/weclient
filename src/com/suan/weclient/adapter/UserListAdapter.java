package com.suan.weclient.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.activity.LoginActivity;
import com.suan.weclient.fragment.UserListFragment;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.data.UserGoupPushHelper;
import com.suan.weclient.util.data.UserListItem;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter implements OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Activity mActivity;

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
        mDataManager.getUserListControlListener().onUserListDismiss();
        mDataManager.setCurrentPosition(position);
        UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(mActivity));
        userGoupPushHelper.updateUserGroup(mDataManager);


        SharedPreferenceManager.putPushUserGroup(mActivity, userGoupPushHelper.getString());

        mDataManager.doAutoLogin();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        popDeleteUser(position);
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


    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        switch (getUserItems().get(position).getItemType()) {
            case UserListItem.TYPE_USER:
                String shortedName = Util.getShortString(mDataManager.getUserGroup().get(position)
                        .getNickname(), 13, 3);

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
                holder.parentView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        mDataManager.getUserListControlListener().onUserListDismiss();
                        Intent jumbIntent = new Intent();
                        jumbIntent.setClass(mActivity, LoginActivity.class);
                        mActivity.startActivityForResult(jumbIntent,
                                UserListFragment.START_ACTIVITY_LOGIN);

                    }
                });

                break;
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


        if (getUserItems().get(position).getItemType() == UserListItem.TYPE_ADD || !mListCacheManager.containView(getUserItems().get(position).getUserBean().getFakeId())) {
            v = newView(position);
            if (getUserItems().get(position).getItemType() == UserListItem.TYPE_USER) {

                mListCacheManager.putView(v, getUserItems().get(position).getUserBean().getFakeId());
            }
        } else {

            v = mListCacheManager.getView(getUserItems().get(position).getUserBean().getFakeId());

        }
        bindView(v, position);

        return v;
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
                UserListAdapter.this.notifyDataSetChanged();
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

}