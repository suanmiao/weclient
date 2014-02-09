package com.suan.weclient.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.ChatActivity;
import com.suan.weclient.activity.ShowImgActivity;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.data.UserGoupPushHelper;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.text.SpanUtil;
import com.suan.weclient.util.voice.VoiceHolder;
import com.suan.weclient.util.voice.VoiceManager.AudioPlayListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserListAdapter extends BaseAdapter implements OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Context mContext;

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

    public UserListAdapter(Context context, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataManager = dataManager;
        this.mContext = context;
        this.mListCacheManager = new ListCacheManager();
    }

    private ArrayList<UserBean> getUserItems() {

        return mDataManager.getUserGroup();
    }

    @Override
    public int getCount() {
        return mDataManager.getUserGroup().size();
    }

    @Override
    public Object getItem(int position) {
        return mDataManager.getUserGroup().get(position);
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

        convertView = LayoutInflater.from(mContext).inflate(
                R.layout.user_group_item, null);

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
        UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(mContext));
        userGoupPushHelper.updateUserGroup(mDataManager);


        SharedPreferenceManager.putPushUserGroup(mContext, userGoupPushHelper.getString());

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

        public ItemViewHolder(View parentView, final int position) {

            this.parentView = parentView;

            profileTextView = (TextView) parentView
                    .findViewById(R.id.user_group_text_user_name);
            profileImageView = (ImageView) parentView
                    .findViewById(R.id.user_group_img_profile);

        }

    }


    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

/*
            holder.parentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });*/
        String shortedName = Util.getShortString(mDataManager.getUserGroup().get(position)
                .getNickname(), 13, 3);

        holder.profileTextView.setText(shortedName
        );
        if (position == selectPosition) {
            // row.setBackgroundResource(R.drawable.biz_navigation_tab_bg_pressed);
            holder.profileTextView.setSelected(true);
        }
        setHeadImg(holder, position);

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
                            Log.e("get user list img failed", "" + code);

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
        if (!mListCacheManager.containView(getUserItems().get(position).getFakeId())) {
            v = newView(position);
            mListCacheManager.putView(v, getUserItems().get(position).getFakeId());
        } else {

            v = mListCacheManager.getView(getUserItems().get(position).getFakeId());

        }
        bindView(v, position);

        return v;
    }

    private void popDeleteUser(final int position) {


        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                SharedPreferenceManager.deleteUser(mContext,
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

        popDialog = new Dialog(mContext, R.style.dialog);

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