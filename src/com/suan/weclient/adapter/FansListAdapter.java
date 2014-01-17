package com.suan.weclient.adapter;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.AdapterView.OnItemSelectedListener;
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
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.FansBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.umeng.analytics.a.h;

public class FansListAdapter extends BaseAdapter implements OnScrollListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    Context mContext;
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

    public FansListAdapter(Context context, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataManager = dataManager;
        this.mContext = context;
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
        convertView = mInflater.inflate(R.layout.fans_item_layout, null);

        return convertView;

    }

    private ItemViewHolder getHolder(final View view, int position) {

        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    public class ItemViewHolder {

        private TextView profileTextView;
        private TextView nickNameTextView;
        private ImageView profileImageView;
        private LinearLayout groupLayout;
        private TextView groupTextView;
        private ImageButton remarkButton;

        public ItemViewHolder(View parentView) {

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

        }

    }

    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        if (getFansItems().get(position).getRemarkName().length() != 0) {
            holder.profileTextView.setText(getFansItems().get(position).getRemarkName());
            holder.nickNameTextView.setText("(" + getFansItems().get(position).getNickname() + ")");

        } else {
            holder.profileTextView.setText(getFansItems().get(position).getNickname());

        }


        holder.remarkButton.setOnClickListener(new ClickListener(holder, position));
        setGroupSpinner(holder, position);

        setProfileImage(holder, position);

    }

    public class ClickListener implements OnClickListener {
        private ItemViewHolder holder;
        private int position;

        public ClickListener(ItemViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;

        }

        @Override
        public void onClick(View v) {

            popEditRemark(position, holder);
        }
    }

    public void popEditRemark(final int position, final ItemViewHolder holder) {

        LayoutInflater inflater = (LayoutInflater) mContext
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

        popTitleTextView.setText("Edit:"
                + getFansItems().get(position).getNickname());
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                editRemark(position, popContentEditText.getText().toString(), holder);

            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        dialog = new Dialog(mContext, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }


    private void editRemark(final int position, final String replyContent, final ItemViewHolder holder) {

        if (replyContent.length() == 0) {

            Toast.makeText(mContext, "请输入内容", Toast.LENGTH_LONG).show();

            return;
        }
        dialog.dismiss();

        mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(), position, WeChatLoader.MODIFY_CONTACTS_ACTION_REMARK, getFansItems().get(position).getFansId(), "", replyContent, new OnActionFinishListener() {
            @Override
            public void onFinish(int code, Object object) {
                Log.e("edit remark", "ok");
                getFansItems().get(position).setRemarkName(replyContent);
                if (holder != null) {
                    holder.profileTextView.setText(replyContent + "(" + getFansItems().get(position).getNickname() + ")");
                }

            }
        });

    }


    private void setGroupSpinner(final ItemViewHolder holder, final int position) {

        String groupName = "";
        groupName = getGroupNameData().get(getItemGroupIndex(position));
        holder.groupTextView.setText(groupName);
        holder.groupLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popGroupList(position, holder);
            }
        });


    }

    public void popGroupList(final int position, final ItemViewHolder holder) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_list_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_list_text_title);
        popListView = (ListView) dialogView.findViewById(R.id.dialog_list_list_content);

        ArrayAdapter adapter = new ArrayAdapter<String>(
                mContext,
                R.layout.dialog_list_item, R.id.dialog_list_item_text, getGroupNameData());
        popListView.setAdapter(adapter);
        popListView.setOnItemClickListener(new ItemClickListener(holder, position));

        popTitleTextView.setText("Group:");

        dialog = new Dialog(mContext, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {
        private ItemViewHolder holder;
        private int position;

        public ItemClickListener(ItemViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view,final int index, long id) {

            dialog.dismiss();

            mDataManager.getWechatManager().modifyContacts(mDataManager.getCurrentPosition(), position, WeChatLoader.MODIFY_CONTACTS_ACTION_MODIFY, getFansItems().get(position).getFansId(), getGroupIdArray()[index], "", new OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    Log.e("edit group", "ok");
                    getFansItems().get(position).setGroupId(getGroupIdArray()[index]);
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

    private int getItemGroupIndex(int position) {
        String itemGroupIdString = getFansItems().get(position).getGoupId();
        for (int i = 0; i < mDataManager.getCurrentFansHolder()
                .getFansGroupBeans().size(); i++) {
            String nowGroupIdString = mDataManager.getCurrentFansHolder()
                    .getFansGroupBeans().get(i).getGroupId();
            if (itemGroupIdString.equals(nowGroupIdString)) {
                return i;

            }
        }
        return 0;

    }


    private String[] getGroupIdArray() {
        int groupSize = mDataManager.getCurrentFansHolder().getFansGroupBeans()
                .size();
        String[] groupStrings = new String[groupSize];
        for (int i = 0; i < groupSize; i++) {
            groupStrings[i] = mDataManager.getCurrentFansHolder()
                    .getFansGroupBeans().get(i).getGroupId();

        }

        return groupStrings;

    }

    private void setProfileImage(final ItemViewHolder holder, final int position) {
        boolean imgLoaded = false;
        if (holder.profileImageView.getTag() != null) {
            imgLoaded = true;

        }

        if (!mBusy && !imgLoaded) {

            Bitmap contentBitmap = mDataManager.getCacheManager()
                    .getBitmap(
                            ImageCacheManager.CACHE_MESSAGE_PROFILE
                                    + getFansItems().get(position).getFansId());
            if (contentBitmap != null) {
                holder.profileImageView.setImageBitmap(contentBitmap);
            } else {

                mDataManager.getWechatManager().getMessageHeadImg(
                        mDataManager.getCurrentPosition(),
                        getFansItems().get(position).getFansId(),
                        getFansItems().get(position).getReferer(),
                        holder.profileImageView, new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub
                        Bitmap bitmap = (Bitmap) object;
                        mDataManager.getCacheManager().putBitmap(
                                ImageCacheManager.CACHE_MESSAGE_PROFILE
                                        + getFansItems().get(position)
                                        .getFansId(), bitmap, true);
                        holder.profileImageView.setTag(bitmap);

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