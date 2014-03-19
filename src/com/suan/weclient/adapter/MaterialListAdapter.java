package com.suan.weclient.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.AppItemBean;
import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.MultiItemBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.voice.VoiceHolder;
import com.suan.weclient.util.voice.VoiceManager.AudioPlayListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MaterialListAdapter extends BaseAdapter implements OnScrollListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Activity mActivity;


    /*
     * whether the scroll is busy
     */
    private boolean mBusy = false;

    private int selectedPosition = -1;
    private ItemViewHolder selectedHolder;

    public MaterialListAdapter(Activity activity, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(activity);
        this.mDataManager = dataManager;
        this.mActivity = activity;
        this.mListCacheManager = new ListCacheManager();

    }

    private ArrayList<MaterialBean> getMaterialBeans() {
        if (mDataManager.getUserGroup().size() == 0) {
            ArrayList<MaterialBean> blankArrayList = new ArrayList<MaterialBean>();
            return blankArrayList;
        }

        return mDataManager.getMaterialHolder().getMaterialBeans();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return getMaterialBeans().size();
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

        switch (getMaterialBeans().get(position).getType()) {

            case MaterialBean.MATERIAL_TYPE_IMG:
                convertView = mInflater.inflate(R.layout.material_item_img_layout,
                        null);
                break;
            case MaterialBean.MATERIAL_TYPE_VOICE:

                convertView = mInflater.inflate(R.layout.material_item_voice_layout,
                        null);

                break;

            case MaterialBean.MATERIAL_TYPE_APP:

                convertView = mInflater.inflate(R.layout.material_item_app_layout,
                        null);

                break;

            default:

                convertView = mInflater.inflate(R.layout.material_item_img_layout,
                        null);
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

        private View parentView;

        private TextView nameTextView;
        private TextView sizeTextView;
        private TextView timeTextView;


        private LinearLayout contentLayout;
        private ImageView contentImageView;


        private RelativeLayout voicePlayLayout;
        private TextView voiceInfoTextView;
        private ImageView voicePlayView;

        /*
        about app msg
         */
        private RelativeLayout coverLayout;
        private RelativeLayout[] itemLayout;


        private ImageView coverImageView;
        private ImageView[] itemImageView;

        private TextView titleTextView;
        private TextView coverTextView;
        private TextView[] itemTextView;



        /*
        about data
         */

        private MaterialBean materialBean;
        private boolean dataLoaded = false;
        private Object data;


        public boolean getDataLoaded() {
            return dataLoaded;
        }

        public void setDataLoaded(boolean dataLoaded) {
            this.dataLoaded = dataLoaded;
        }


        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public MaterialBean getMaterialBean() {
            return materialBean;
        }

        public ItemViewHolder(View parentView, final int position) {

            this.parentView = parentView;

            this.materialBean = getMaterialBeans().get(position);

            switch (materialBean.getType()) {

                case MaterialBean.MATERIAL_TYPE_IMG:

                    nameTextView = (TextView) parentView.findViewById(R.id.material_item_text_name);
                    sizeTextView = (TextView) parentView.findViewById(R.id.material_item_text_size);
                    timeTextView = (TextView) parentView.findViewById(R.id.material_item_text_time);

                    contentLayout = (LinearLayout) parentView.findViewById(R.id.material_item_layout_content);
                    contentImageView = (ImageView) parentView.findViewById(R.id.material_item_img_img_content);


                    break;
                case MaterialBean.MATERIAL_TYPE_VOICE:


                    nameTextView = (TextView) parentView.findViewById(R.id.material_item_text_name);
                    sizeTextView = (TextView) parentView.findViewById(R.id.material_item_text_size);
                    timeTextView = (TextView) parentView.findViewById(R.id.material_item_text_time);

                    contentLayout = (LinearLayout) parentView.findViewById(R.id.material_item_layout_content);
                    voicePlayLayout = (RelativeLayout) parentView.findViewById(R.id.material_item_voi_layout_play);
                    voicePlayView = (ImageView) parentView.findViewById(R.id.material_item_voi_button_play);
                    voiceInfoTextView = (TextView) parentView.findViewById(R.id.material_item_voi_text_info);

                    break;

                case MaterialBean.MATERIAL_TYPE_APP:

                    titleTextView = (TextView) parentView.findViewById(R.id.material_item_app_text_title);
                    timeTextView = (TextView) parentView.findViewById(R.id.material_item_text_time);
                    coverLayout = (RelativeLayout) parentView.findViewById(R.id.material_item_app_layout_cover);
                    itemLayout = new RelativeLayout[3];
                    itemImageView = new ImageView[3];
                    itemTextView = new TextView[3];
                    itemLayout[0] = (RelativeLayout) parentView.findViewById(R.id.material_item_app_layout_first_item);
                    itemLayout[1] = (RelativeLayout) parentView.findViewById(R.id.material_item_app_layout_second_item);
                    itemLayout[2] = (RelativeLayout) parentView.findViewById(R.id.material_item_app_layout_third_item);

                    coverImageView = (ImageView) parentView.findViewById(R.id.material_item_app_img_cover);
                    itemImageView[0] = (ImageView) parentView.findViewById(R.id.material_item_app_img_first);
                    itemImageView[1] = (ImageView) parentView.findViewById(R.id.material_item_app_img_second);
                    itemImageView[2] = (ImageView) parentView.findViewById(R.id.material_item_app_img_third);

                    coverTextView = (TextView) parentView.findViewById(R.id.material_item_app_text_cover_title);
                    itemTextView[0] = (TextView) parentView.findViewById(R.id.material_item_app_text_first_title);
                    itemTextView[1] = (TextView) parentView.findViewById(R.id.material_item_app_text_second_title);
                    itemTextView[2] = (TextView) parentView.findViewById(R.id.material_item_app_text_third_title);


                    break;

                default:


                    nameTextView = (TextView) parentView.findViewById(R.id.material_item_text_name);
                    sizeTextView = (TextView) parentView.findViewById(R.id.material_item_text_size);
                    timeTextView = (TextView) parentView.findViewById(R.id.material_item_text_time);
                    break;


            }
        }

    }

    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        MaterialBean materialBean = holder.getMaterialBean();

        switch (materialBean.getType()) {

            case MaterialBean.MATERIAL_TYPE_IMG:
                setImgMessageContent(holder);

                break;
            case MaterialBean.MATERIAL_TYPE_VOICE:
                setVoiceMessageContent(holder);

                break;

            case MaterialBean.MATERIAL_TYPE_APP:
                setAPPMessageContent(holder);

                break;

            default:

                break;


        }

        if (position == selectedPosition) {
            holder.parentView.setActivated(true);
            selectedHolder = holder;
        } else {
            holder.parentView.setActivated(false);
        }


        holder.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setActivated(true);
                selectedPosition = position;
                notifyDataSetChanged();

            }
        });

        if (materialBean.getType() == MaterialBean.MATERIAL_TYPE_IMG ||
                materialBean.getType() == MaterialBean.MATERIAL_TYPE_VOICE) {
            holder.nameTextView.setText(materialBean.getName());
            String sizeString = "" + materialBean.getSize();
            holder.sizeTextView.setText(sizeString);

            long time = Long.parseLong(materialBean.getUpdate_time());
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
            String timeString = "" + format.format(date);
            holder.timeTextView.setText(timeString);

        }

    }

    public void setAPPMessageContent(final ItemViewHolder holder) {
        AppItemBean appItemBean = holder.getMaterialBean().getAppItemBean();
        int itemCount = appItemBean.getMulti_item().size();
        holder.coverTextView.setText(appItemBean.getDigest());
        holder.titleTextView.setText(appItemBean.getTitle());

        Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                ImageCacheManager.CACHE_MESSAGE_CONTENT
                        + holder.getMaterialBean().getAppItemBean().getFile_id());
        if (contentBitmap == null) {
            mDataManager.getWechatManager().getNormalImg(mDataManager.getCurrentPosition(), holder.getMaterialBean().getAppItemBean().getImg_url(), holder.coverImageView, new OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        if (object != null) {
                            Bitmap bitmap = (Bitmap) object;
                            mDataManager.getCacheManager().putBitmap(
                                    ImageCacheManager.CACHE_MESSAGE_CONTENT
                                            + holder.getMaterialBean().getAppItemBean().getFile_id(),
                                    bitmap, true);

                            holder.coverImageView.setImageBitmap(bitmap);

                        }

                    }

                }
            });


        } else {
            holder.coverImageView.setImageBitmap(contentBitmap);
        }


        for (int i = 0; i < 3; i++) {
            if (i < itemCount - 1) {
                holder.itemLayout[i].setVisibility(View.VISIBLE);
                holder.itemTextView[i].setText(appItemBean.getMulti_item().get(i + 1).getDigest());
                setAppItemImg(i, holder);

            } else {
                holder.itemLayout[i].setVisibility(View.GONE);
            }

        }


    }

    private void setAppItemImg(final int index, final ItemViewHolder holder) {
        MultiItemBean multiItemBean = holder.getMaterialBean().getAppItemBean().getMulti_item().get(index);

        Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                ImageCacheManager.CACHE_MESSAGE_CONTENT
                        + multiItemBean.getFile_id());
        if (contentBitmap == null) {
            mDataManager.getWechatManager().getNormalImg(mDataManager.getCurrentPosition(), multiItemBean.getCover(), holder.itemImageView[index], new OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        if (object != null) {
                            Bitmap bitmap = (Bitmap) object;
                            mDataManager.getCacheManager().putBitmap(
                                    ImageCacheManager.CACHE_MESSAGE_CONTENT
                                            + holder.getMaterialBean().getAppItemBean().getMulti_item().get(index).getFile_id(),
                                    bitmap, true);

                            holder.itemImageView[index].setImageBitmap(bitmap);

                        }

                    }

                }
            });


        } else {
            holder.itemImageView[index].setImageBitmap(contentBitmap);
        }


    }

    public ItemViewHolder getSelectedHolder() {
        return selectedHolder;

    }

    private void setVoiceMessageContent(final ItemViewHolder holder) {

        if (!mBusy && !holder.getDataLoaded()) {

            mDataManager.getWechatManager().getMaterialVoice(
                    mDataManager.getCurrentPosition(),
                    holder.getMaterialBean(),
                    mDataManager.getCurrentUser(),
                    new OnActionFinishListener() {

                        @Override
                        public void onFinish(int code, Object object) {
                            // TODO Auto-generated method stub
                            if (code == WechatManager.ACTION_SUCCESS) {
                                if (object != null) {
                                    byte[] bytes = (byte[]) object;
                                    VoiceHolder voiceHolder = new VoiceHolder(
                                            bytes, holder.getMaterialBean().getPlay_length(),
                                            bytes.length + "");
                                    int playLength = Integer.parseInt(holder.getMaterialBean().getPlay_length());
                                    int seconds = playLength / 1000;
                                    int minutes = seconds / 60;
                                    int leaveSecond = seconds % 60;
                                    String info = "";
                                    if (minutes != 0) {
                                        info += minutes + "'";

                                    }

                                    info += " " + leaveSecond + "'";
                                    holder.voiceInfoTextView.setText(info);

                                    holder.setData(voiceHolder);
                                    holder.setDataLoaded(true);

                                }

                            }

                        }
                    });

        }

        holder.voicePlayLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (holder.getDataLoaded()) {

                    final VoiceHolder voiceHolder = (VoiceHolder) holder.getData();
                    if (voiceHolder.getPlaying()) {
                        mDataManager.getVoiceManager().stopMusic();

                    } else {

                        mDataManager.getVoiceManager().playVoice(
                                voiceHolder.getBytes(),
                                voiceHolder.getPlayLength(),
                                voiceHolder.getLength(),
                                new AudioPlayListener() {

                                    @Override
                                    public void onAudioStop() {
                                        // TODO Auto-generated method stub

                                        voiceHolder.setPlaying(false);

                                        holder.voicePlayView.setSelected(false);

                                    }

                                    @Override
                                    public void onAudioStart() {
                                        // TODO Auto-generated method stub
                                        voiceHolder.setPlaying(true);
                                        holder.voicePlayView.setSelected(true);

                                    }

                                    @Override
                                    public void onAudioError() {
                                        // TODO Auto-generated method stub

                                    }
                                });
                    }

                } else {

                }

            }
        });
    }


    private void setImgMessageContent(final ItemViewHolder holder) {


        holder.contentImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


               /* Intent jumbIntent = new Intent();
                jumbIntent.setClass(mActivity, ShowImgActivity.class);
                mDataManager.createImgHolder(holder.getMessageBean(), mDataManager.getCurrentUser());
                mActivity.startActivity(jumbIntent);
                mActivity.overridePendingTransition(R.anim.search_activity_fly_in, R.anim.search_activity_fly_out);
*/


            }

        });

        if (!mBusy && !holder.getDataLoaded()) {

            Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                    ImageCacheManager.CACHE_MESSAGE_CONTENT
                            + holder.getMaterialBean().getFile_id());
            if (contentBitmap == null) {
                mDataManager.getWechatManager().getMaterialImg(
                        mDataManager.getCurrentPosition(),
                        holder.getMaterialBean(),
                        holder.contentImageView,
                        WeChatLoader.WECHAT_URL_MESSAGE_IMG_SMALL,
                        new OnActionFinishListener() {

                            @Override
                            public void onFinish(int code, Object object) {
                                // TODO Auto-generated method stub
                                if (code == WechatManager.ACTION_SUCCESS) {
                                    if (object != null) {
                                        Bitmap bitmap = (Bitmap) object;
                                        mDataManager.getCacheManager().putBitmap(
                                                ImageCacheManager.CACHE_MESSAGE_CONTENT
                                                        + holder.getMaterialBean().getFile_id(),
                                                bitmap, true);
                                        holder.contentImageView.setImageBitmap(bitmap);

                                        holder.setData(bitmap);
                                        holder.setDataLoaded(true);
                                    }

                                }

                            }
                        });

            } else {
                holder.contentImageView.setImageBitmap(contentBitmap);
                holder.setData(contentBitmap);
                holder.setDataLoaded(true);
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;

        if (!mListCacheManager.containView(getMaterialBeans().get(position).getFile_id()) ||
                getMaterialBeans().get(position).getType() == MaterialBean.MATERIAL_TYPE_APP) {
            v = newView(position);
            mListCacheManager.putView(v, getMaterialBeans().get(position).getFile_id());
        } else {

            v = mListCacheManager.getView(getMaterialBeans().get(position).getFile_id());

        }

//        v = newView(position);


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
            loadData(view);


        } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 滑动手未松开
            mBusy = true;
        } else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {// 滑动中手已松开
            mBusy = true;
        }


    }


    private void loadData(AbsListView fatherView) {

        for (int i = 0; i < fatherView.getChildCount(); i++) {
            View nowView = fatherView.getChildAt(i);
            if (nowView.getTag() != null) {
                ItemViewHolder holder = (ItemViewHolder) nowView.getTag();
                if (!holder.getDataLoaded()) {

                    MaterialBean materialBean = holder.materialBean;
                    switch (materialBean.getType()) {
                        case MaterialBean.MATERIAL_TYPE_IMG:
                            setImgMessageContent(holder);

                            break;

                        case MaterialBean.MATERIAL_TYPE_VOICE:
                            setVoiceMessageContent(holder);

                            break;
                        case MaterialBean.MATERIAL_TYPE_APP:
                            setAPPMessageContent(holder);

                            break;
                    }


                }

            }

        }
    }

}