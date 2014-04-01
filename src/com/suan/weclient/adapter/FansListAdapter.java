package com.suan.weclient.adapter;

import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.ChatActivity;
import com.suan.weclient.activity.FansProfileActivity;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.FansBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class FansListAdapter extends BaseAdapter implements OnScrollListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    Activity mActivity;
    /*
    about dialog
     */
    private EditText popContentEditText;
    private TextView popTitleTextView;
    private TextView textAmountTextView;
    private Button popCancelButton, popSureButton;

    private ListView popListView;
    private Dialog dialog;


    /*
     * whether the scroll is busy
     */
    private boolean mBusy = false;

	/*
     * whether the user cancel the last editRemark if so ,we will save it
	 */

    public FansListAdapter(Activity mActivity, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(mActivity);
        this.mDataManager = dataManager;
        this.mActivity = mActivity;
        this.mListCacheManager = new ListCacheManager();
    }

    private ArrayList<FansBean> getFansItems() {
        if (mDataManager.getUserGroup().size() == 0) {
            ArrayList<FansBean> blankArrayList = new ArrayList<FansBean>();
            return blankArrayList;
        }
        return mDataManager.getFansHolders()
                .get(mDataManager.getCurrentPosition()).getFansBeans();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return getFansItems().size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public void updateCache() {
        mListCacheManager.clearData();
    }

    public View newView(final int position) {

        View convertView = null;

        switch (getFansItems().get(position).getBeanType()) {
            case FansBean.BEAN_TYPE_USER:

                convertView = mInflater.inflate(R.layout.fans_item_user_layout, null);
                break;
            case FansBean.BEAN_TYPE_DATA:
                convertView = mInflater.inflate(R.layout.fans_item_data_layout, null);

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

    public class ItemViewHolder {

        private View parentLayout;

        private TextView profileTextView;
        private TextView nickNameTextView;
        private ImageView profileImageView;
        private LinearLayout groupLayout;
        private TextView groupTextView;
        private ImageButton remarkButton;

        /*
        about data
         */
        private TextView totalPeopleTextView;
        private TextView newPeopleTextView;


        private FansBean fansBean;

        public FansBean getFansBean() {
            return fansBean;
        }

        public ItemViewHolder(View parentView, int postion) {
            fansBean = getFansItems().get(postion);
            this.parentLayout = parentView;

            switch (fansBean.getBeanType()) {
                case FansBean.BEAN_TYPE_DATA:
                    newPeopleTextView = (TextView) parentView.findViewById(R.id.fans_data_text_new);
                    totalPeopleTextView = (TextView) parentView.findViewById(R.id.fans_data_text_total);

                    break;
                case FansBean.BEAN_TYPE_USER:

                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.fans_item_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.fans_item_text_profile);
                    nickNameTextView = (TextView) parentView.findViewById(R.id.fans_item_text_nickname);
                    groupLayout = (LinearLayout) parentView.findViewById(R.id.fans_item_layout_group);
                    groupTextView = (TextView) parentView.findViewById(R.id.fans_item_text_group
                    );
                    remarkButton = (ImageButton) parentView
                            .findViewById(R.id.fans_item_button_edit_remark);

                    break;
            }

        }

    }

    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        switch (holder.getFansBean().getBeanType()) {
            case FansBean.BEAN_TYPE_DATA:
                if (mDataManager.getCurrentUser() != null) {
                    UserBean currentUser = mDataManager.getCurrentUser();
                    int newPeopleCount = 0;
                    try {
                        newPeopleCount = Integer.parseInt(currentUser.getNewPeople());
                    } catch (Exception e) {

                    }
                    if (newPeopleCount == 0) {
                        holder.newPeopleTextView.setVisibility(View.INVISIBLE);

                    } else {
                        holder.newPeopleTextView.setVisibility(View.VISIBLE);
                        holder.newPeopleTextView.setText(mActivity.getResources().getString(R.string.new_fans).replace("...", newPeopleCount + ""));
                    }

                    holder.totalPeopleTextView.setText(": " + currentUser.getTotalPeople());
                }

                break;
            case FansBean.BEAN_TYPE_USER:

                holder.parentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        /*
                        take care
                        fansId and fakeId
                        fansId is used in fans list
                        fakeId is used in message list
                         */

                        mDataManager.setFansProfileFakeId(getFansItems().get(position).getFansId());
                        Intent jumbIntent = new Intent();
                        jumbIntent.setClass(mActivity, FansProfileActivity.class);
                        mActivity.startActivity(jumbIntent);
                        mActivity.overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);

                    }
                });
                if (holder.getFansBean().getRemarkName().length() != 0) {
                    holder.profileTextView.setText(holder.getFansBean().getRemarkName());
                    String nickNameString = Util.getShortString(holder.getFansBean().getNickname(), 13, 3);
                    holder.nickNameTextView.setText(nickNameString);

                } else {
                    holder.profileTextView.setText(holder.getFansBean().getNickname());

                }


                holder.remarkButton.setOnClickListener(new ClickListener(holder));
                setGroupSpinner(holder);

                setProfileImage(holder);

                break;
        }

    }

    public class ClickListener implements OnClickListener {
        private ItemViewHolder holder;

        public ClickListener(ItemViewHolder holder) {
            this.holder = holder;

        }

        @Override
        public void onClick(View v) {

            popEditRemark(holder);
        }
    }

    public void popEditRemark(final ItemViewHolder holder) {

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        textAmountTextView.setOnClickListener(new OnClickListener() {

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
                + holder.getFansBean().getNickname());
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String editContent = popContentEditText.getText().toString();
                if (editContent.length() == 0) {
                    Toast.makeText(mActivity, "备注名不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    editRemark(editContent, holder);
                }


            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        dialog = new Dialog(mActivity, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }


    private void editRemark(final String replyContent, final ItemViewHolder holder) {

        if (replyContent.length() == 0) {

            Toast.makeText(mActivity, "请输入内容", Toast.LENGTH_LONG).show();

            return;
        }
        dialog.dismiss();

        mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(),
                WeChatLoader.MODIFY_CONTACTS_ACTION_REMARK, holder.getFansBean().getFansId(), "", replyContent, new OnActionFinishListener() {
            @Override
            public void onFinish(int code, Object object) {
                holder.getFansBean().setRemarkName(replyContent);
                if (holder != null) {
                    holder.profileTextView.setText(replyContent + "(" + holder.getFansBean().getNickname() + ")");
                }

            }
        });

    }


    private void setGroupSpinner(final ItemViewHolder holder) {

        String groupName = "";
        groupName = getGroupNameData().get(getItemGroupIndex(holder));
        holder.groupTextView.setText(groupName);
        holder.groupLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popGroupList(holder);
            }
        });


    }

    public void popGroupList(final ItemViewHolder holder) {

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_list_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_list_text_title);
        popListView = (ListView) dialogView.findViewById(R.id.dialog_list_list_content);

        ArrayAdapter adapter = new ArrayAdapter<String>(
                mActivity,
                R.layout.dialog_list_item, R.id.dialog_list_item_text, getGroupNameData());
        popListView.setAdapter(adapter);
        popListView.setOnItemClickListener(new ItemClickListener(holder));

        popTitleTextView.setText("Group:");

        dialog = new Dialog(mActivity, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {
        private ItemViewHolder holder;

        public ItemClickListener(ItemViewHolder holder) {
            this.holder = holder;

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int index, long id) {

            dialog.dismiss();

            mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(),
                    WeChatLoader.MODIFY_CONTACTS_ACTION_MODIFY, holder.getFansBean().getFansId(),
                    getGroupIdArray()[index] + "", "", new OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    holder.getFansBean().setGroupId(getGroupIdArray()[index]);
                    holder.groupTextView.setText(getGroupNameData().get(index));


                }
            });
        }

    }

    private ArrayList<String> getGroupNameData() {

        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < mDataManager.getCurrentFansHolder().getFansGroupBeans().size(); i++) {
            String nowGroupNameString = mDataManager.getCurrentFansHolder()
                    .getFansGroupBeans().get(i).getGroupName();
            result.add(nowGroupNameString);

        }
        return result;
    }

    private int getItemGroupIndex(ItemViewHolder holder) {
        int itemGroupId = holder.getFansBean().getGoupId();
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
        int groupSize = mDataManager.getCurrentFansHolder().getFansGroupBeans()
                .size();
        int[] groupStrings = new int[groupSize];
        for (int i = 0; i < groupSize; i++) {
            groupStrings[i] = mDataManager.getCurrentFansHolder()
                    .getFansGroupBeans().get(i).getGroupId();

        }

        return groupStrings;

    }

    private void setProfileImage(final ItemViewHolder holder) {

        holder.profileImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataManager.createChat(mDataManager.getCurrentUser(),
                        holder.getFansBean().getFansId(), holder.getFansBean().getNickname());
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(mActivity, ChatActivity.class);
                mActivity.startActivity(jumbIntent);

            }
        });

        boolean imgLoaded = false;
        if (holder.profileImageView.getTag() != null) {
            imgLoaded = true;

        }

        if (!mBusy && !imgLoaded) {

            Bitmap contentBitmap = mDataManager.getCacheManager()
                    .getBitmap(
                            ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                                    + holder.getFansBean().getFansId());
            if (contentBitmap != null) {
                holder.profileImageView.setImageBitmap(contentBitmap);
            } else {

                mDataManager.getWechatManager().getMessageHeadImg(
                        mDataManager.getCurrentPosition(),
                        holder.getFansBean().getFansId(),
                        holder.getFansBean().getReferer(),
                        holder.profileImageView, new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub
                        if (code == WechatManager.ACTION_SUCCESS) {
                            if (object != null) {
                                Bitmap roundBitmap = Util.roundCornerWithBorder((Bitmap) object,
                                        holder.profileImageView.getWidth(), 10,
                                        Color.parseColor("#c6c6c6"));

                                mDataManager.getCacheManager().putBitmap(
                                        ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                                                + holder.getFansBean()
                                                .getFansId(), roundBitmap, true);
                                holder.profileImageView.setImageBitmap(roundBitmap);
                                holder.profileImageView.setTag(roundBitmap);

                            }

                        }

                    }
                });

            }
        }
    }

    private String getFansId(int position) {
        return getFansItems().get(position).getFansId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;
        if (!mListCacheManager.containView(getFansId(position))) {
            v = newView(position);
            mListCacheManager.putView(v, getFansId(position));
        } else {

            v = mListCacheManager.getView(getFansId(position));

        }
        bindView(v, position);

        return v;
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