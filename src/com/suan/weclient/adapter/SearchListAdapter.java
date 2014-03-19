package com.suan.weclient.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.suan.weclient.activity.FansProfileActivity;
import com.suan.weclient.activity.ShowImgActivity;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.MessageBean;
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

public class SearchListAdapter extends BaseAdapter implements OnScrollListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Activity mActivity;

    private Dialog dialog;
    private static final int MAX_TEXT_LENGTH = 140;
    private String canceledReplyContent = "";


    /*
     * whether the scroll is busy
     */
    private boolean mBusy = false;
    /*
     * whether the user cancel the last reply if so ,we will save it
     */
    private boolean lastReplyCanceled = false;

    public SearchListAdapter(Activity activity, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(activity);
        this.mDataManager = dataManager;
        this.mActivity = activity;
        this.mListCacheManager = new ListCacheManager();

        //when user index change
        mDataManager.addUserIndexChangeListener(new DataManager.UserIndexChangeListener() {
            @Override
            public void onChange(int oldIndex, int nowIndex) {
                notifyDataSetChanged();
            }
        });
    }

    private ArrayList<MessageBean> getMessageItems() {
        if (mDataManager.getUserGroup().size() == 0) {
            ArrayList<MessageBean> blankArrayList = new ArrayList<MessageBean>();
            return blankArrayList;
        }

        return mDataManager.getSearchMessageHolder().getMessageList();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return getMessageItems().size();
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

        switch (getMessageItems().get(position).getType()) {
            case MessageBean.MESSAGE_TYPE_TEXT:
                convertView = mInflater.inflate(R.layout.message_item_text_layout,
                        null);
                break;

            case MessageBean.MESSAGE_TYPE_IMG:
                convertView = mInflater.inflate(R.layout.message_item_img_layout,
                        null);
                break;
            case MessageBean.MESSAGE_TYPE_VOICE:

                convertView = mInflater.inflate(R.layout.message_item_voice_layout,
                        null);

                break;

            case MessageBean.MESSAGE_TYPE_EMPTY:

                convertView = mInflater.inflate(R.layout.message_item_empty_layout,
                        null);

                break;
            case MessageBean.MESSAGE_TYPE_DATA:

                convertView = mInflater.inflate(R.layout.message_item_data_layout,

                        null);

                break;


            default:

                convertView = mInflater.inflate(R.layout.message_item_text_layout,
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

        private LinearLayout headerLayout;
        private ImageView profileImageView;
        private TextView profileTextView;
        private TextView timeTextView;
        private TextView hasReplyTextView;

        private LinearLayout contentLayout;
        private ImageView contentImageView;
        private TextView contentTextView;
        private RelativeLayout voicePlayLayout;
        private TextView voiceInfoTextView;
        private ImageView voicePlayView;

        private LinearLayout longClickLayout;


        private RelativeLayout copyLayout, shareLayout, downloadLayout;
        private ImageView copyButton, shareButton, downloadButton;

        private RelativeLayout replyLayout, starLayout;
        private ImageView replyButton, starImageButton;

        /*
        about data layout
         */
        private TextView totalPeopleTextView, newMsgTextView, newPeopleTextView;
        private LinearLayout userLayout;


        /*
        about data
         */
        private MessageBean contentBean;
        private boolean dataLoaded = false;
        private Object data;

        private boolean headImgLoaded = false;
        private Bitmap headImgBitmap;


        public boolean getDataLoaded() {
            return dataLoaded;
        }

        public void setDataLoaded(boolean dataLoaded) {
            this.dataLoaded = dataLoaded;
        }

        public boolean getHeadImgLoaded() {
            return headImgLoaded;
        }

        public void setHeadImgLoaded(boolean headImgLoaded) {
            this.headImgLoaded = headImgLoaded;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Bitmap getHeadImgBitmap() {
            return headImgBitmap;
        }

        public void setHeadImgBitmap(Bitmap bitmap) {
            this.headImgBitmap = bitmap;
        }

        public MessageBean getMessageBean() {
            return contentBean;

        }

        public ItemViewHolder(View parentView, final int position) {

            this.parentView = parentView;

            this.contentBean = getMessageItems().get(position);

            switch (contentBean.getType()) {
                case MessageBean.MESSAGE_TYPE_TEXT:

                    dataLoaded = true;

                    headerLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_header);
                    contentLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_content);

                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_text_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_time);

                    hasReplyTextView = (TextView) parentView.findViewById(R.id.message_item_text_text_has_reply);


                    contentTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_content);

                    longClickLayout = (LinearLayout) parentView.findViewById(R.id.message_item_text_layout_long_click);

                    copyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_copy);
                    shareLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_share);

                    copyButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_copy);
                    shareButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_share);

                    starLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_star);
                    replyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_reply);

                    starImageButton = (ImageView) parentView
                            .findViewById(R.id.message_item_text_button_star);
                    replyButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_reply);
                    String content = getMessageItems().get(position)
                            .getContent();
                    SpanUtil.setHtmlSpanAndImgSpan(contentTextView, content, mActivity);
                    break;

                case MessageBean.MESSAGE_TYPE_IMG:

                    headerLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_header);
                    contentLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_content);
                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_img_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_img_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_img_text_time);

                    hasReplyTextView = (TextView) parentView.findViewById(R.id.message_item_img_text_has_reply);
                    contentImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_img_img_content);

                    longClickLayout = (LinearLayout) parentView.findViewById(R.id.message_item_img_layout_long_click);
                    downloadLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_img_layout_download);
                    shareLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_img_layout_share);

                    downloadButton = (ImageView) parentView.findViewById(R.id.message_item_img_button_download);
                    shareButton = (ImageView) parentView.findViewById(R.id.message_item_img_button_share);


                    starLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_img_layout_star);
                    replyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_img_layout_reply);

                    starImageButton = (ImageView) parentView
                            .findViewById(R.id.message_item_img_button_star);

                    replyButton = (ImageView) parentView.findViewById(R.id.message_item_img_button_reply);
                    break;

                case MessageBean.MESSAGE_TYPE_VOICE:

                    headerLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_header);
                    contentLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_content);
                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_voi_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_voi_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_voi_text_time);
                    hasReplyTextView = (TextView) parentView.findViewById(R.id.message_item_voi_text_has_reply);

                    voiceInfoTextView = (TextView) parentView.findViewById(R.id.message_item_voi_text_info);
                    voicePlayLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_voi_layout_play);
                    voicePlayView = (ImageView) parentView.findViewById(R.id.message_item_voi_button_play);


                    longClickLayout = (LinearLayout) parentView.findViewById(R.id.message_item_voi_layout_long_click);

                    downloadLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_voi_layout_download);
                    shareLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_voi_layout_share);

                    downloadButton = (ImageView) parentView.findViewById(R.id.message_item_voi_button_download);
                    shareButton = (ImageView) parentView.findViewById(R.id.message_item_voi_button_share);


                    starLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_voi_layout_star);
                    replyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_voi_layout_reply);

                    starImageButton = (ImageView) parentView
                            .findViewById(R.id.message_item_voi_button_star);

                    replyButton = (ImageView) parentView.findViewById(R.id.message_item_voi_button_reply);
                    break;

                case MessageBean.MESSAGE_TYPE_EMPTY:

                    break;

                case MessageBean.MESSAGE_TYPE_DATA:
                    newPeopleTextView = (TextView) parentView.findViewById(R.id.message_item_data_text_new_user);
                    newMsgTextView = (TextView) parentView.findViewById(R.id.message_item_data_text_new_message);
                    totalPeopleTextView = (TextView) parentView.findViewById(R.id.message_item_data_text_total_user);
                    userLayout = (LinearLayout) parentView.findViewById(R.id.message_item_data_layout_user);

                    break;

                default:


                    headerLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_header);
                    contentLayout = (LinearLayout) parentView.findViewById(R.id.message_item_layout_content);
                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_text_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_time);

                    hasReplyTextView = (TextView) parentView.findViewById(R.id.message_item_text_text_has_reply);

                    contentTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_content);

                    longClickLayout = (LinearLayout) parentView.findViewById(R.id.message_item_text_layout_long_click);

                    copyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_copy);
                    shareLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_share);

                    copyButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_copy);
                    shareButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_share);

                    starLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_star);
                    replyLayout = (RelativeLayout) parentView.findViewById(R.id.message_item_text_layout_reply);

                    starImageButton = (ImageView) parentView
                            .findViewById(R.id.message_item_text_button_star);
                    replyButton = (ImageView) parentView.findViewById(R.id.message_item_text_button_reply);
                    contentTextView.setText("[目前暂不支持该类型消息]");
                    break;

            }
        }

    }

    public class LongClickListener implements View.OnLongClickListener {
        private ItemViewHolder holder;

        public LongClickListener(ItemViewHolder holder) {
            this.holder = holder;

        }

        @Override
        public boolean onLongClick(View v) {
            if (holder.longClickLayout.getVisibility() != View.VISIBLE) {

                holder.longClickLayout.setVisibility(View.VISIBLE);
            } else {
                holder.longClickLayout.setVisibility(View.GONE);

            }
            return false;
        }

    }

    public void bindView(View view, final int position) {

        ItemViewHolder holder = getHolder(view, position);

        switch (getMessageItems().get(position).getType()) {
            case MessageBean.MESSAGE_TYPE_TEXT:

                holder.contentTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataManager.createChat(mDataManager.getCurrentUser(),
                                getMessageItems().get(position).getFakeId(), getMessageItems().get(position).getNickName());
                        Intent jumbIntent = new Intent();
                        jumbIntent.setClass(mActivity, ChatActivity.class);
                        mActivity.startActivity(jumbIntent);
                        mActivity.overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);

                    }
                });

                break;


            case MessageBean.MESSAGE_TYPE_IMG:

                setImgMessageContent(holder);

                break;

            case MessageBean.MESSAGE_TYPE_VOICE:

                setVoiceMessageContent(holder);

                break;

            case MessageBean.MESSAGE_TYPE_EMPTY:

                break;
            case MessageBean.MESSAGE_TYPE_DATA:


                break;

            default:

                break;

        }

        MessageBean currentBean = getMessageItems().get(position);

        if (currentBean.getType() != MessageBean.MESSAGE_TYPE_EMPTY && currentBean.getType() != MessageBean.MESSAGE_TYPE_DATA) {

            holder.headerLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    mDataManager.setFansProfileFakeId(getMessageItems().get(position).getFakeId());
                    Intent jumbIntent = new Intent();
                    jumbIntent.setClass(mActivity, FansProfileActivity.class);
                    mActivity.startActivity(jumbIntent);
                    mActivity.overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);


                }
            });

            holder.contentLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataManager.createChat(mDataManager.getCurrentUser(),
                            getMessageItems().get(position).getFakeId(), getMessageItems().get(position).getNickName());
                    Intent jumbIntent = new Intent();
                    jumbIntent.setClass(mActivity, ChatActivity.class);
                    mActivity.startActivity(jumbIntent);
                    mActivity.overridePendingTransition(R.anim.activity_movein_from_right_anim, R.anim.activity_moveout_to_left_anim);

                }
            });


            holder.replyLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mDataManager.getUserGroup().size() == 0) {

                    } else {

                        popReply(getMessageItems().get(position));

                    }

                }
            });
            holder.starLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    // TODO Auto-generated method stub
                    boolean stared = mDataManager.getCurrentMessageHolder()
                            .getMessageList().get(position).getStarred();
                    mDataManager.getWechatManager().star(
                            mDataManager.getCurrentPosition(), position,
                            !stared, new OnActionFinishListener() {


                        @Override
                        public void onFinish(int code, Object object) {
                            // TODO Auto-generated method stub

                            setStarBackground(v, getMessageItems().get(position));
                        }
                    });

                }
            });

            setStarBackground(holder.starImageButton, holder.getMessageBean());

            long time = Long.parseLong(currentBean
                    .getDateTime());
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
            String timeString = "" + format.format(date);

            holder.timeTextView.setText(timeString);

            holder.hasReplyTextView.setVisibility((Integer.parseInt(currentBean.getHasReply()) == 1) ? View.VISIBLE : View.GONE);


            holder.profileTextView.setText(""
                    + currentBean.getNickName());

            setHeadImg(holder);

        }

    }

    private void setVoiceMessageContent(final ItemViewHolder holder) {

        if (!mBusy && !holder.getDataLoaded()) {

            mDataManager.getWechatManager().getMessageVoice(
                    mDataManager.getCurrentPosition(),
                    holder.getMessageBean(),
                    Integer.parseInt(holder.getMessageBean()
                            .getLength()), mDataManager.getCurrentUser(),
                    new OnActionFinishListener() {

                        @Override
                        public void onFinish(int code, Object object) {
                            // TODO Auto-generated method stub
                            if (code == WechatManager.ACTION_SUCCESS) {
                                if (object != null) {
                                    byte[] bytes = (byte[]) object;
                                    VoiceHolder voiceHolder = new VoiceHolder(
                                            bytes, holder.getMessageBean()
                                            .getPlayLength(),
                                            holder.getMessageBean()
                                                    .getLength());
                                    int playLength = Integer.parseInt(holder.getMessageBean().getPlayLength());
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
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(mActivity, ShowImgActivity.class);
                mDataManager.createImgHolder(holder.getMessageBean(), mDataManager.getCurrentUser());
                mActivity.startActivity(jumbIntent);
                mActivity.overridePendingTransition(R.anim.search_activity_fly_in, R.anim.search_activity_fly_out);

            }

        });

        if (!mBusy && !holder.getDataLoaded()) {

            Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                    ImageCacheManager.CACHE_MESSAGE_CONTENT
                            + holder.getMessageBean().getId());
            if (contentBitmap == null) {
                mDataManager.getWechatManager().getMessageImg(
                        mDataManager.getCurrentPosition(),
                        holder.getMessageBean(),
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
                                                        + holder.getMessageBean(),
                                                bitmap, true);


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

    private void setHeadImg(final ItemViewHolder holder) {


        Bitmap headBitmap = mDataManager.getCacheManager().getBitmap(
                ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                        + holder.getMessageBean().getFakeId());
        if (headBitmap != null) {
            holder.profileImageView.setImageBitmap(headBitmap);
            holder.setHeadImgBitmap(headBitmap);
            holder.setHeadImgLoaded(true);

        } else {
            if (!mBusy && !holder.getHeadImgLoaded()) {
                mDataManager.getWechatManager().getMessageHeadImg(
                        mDataManager.getCurrentPosition(),
                        holder.getMessageBean().getFakeId(),
                        holder.getMessageBean().getReferer(),
                        holder.profileImageView, new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub

                        if (code == WechatManager.ACTION_SUCCESS) {
                            if (object != null) {
                                Bitmap roundBitmap = Util.roundCornerWithBorder((Bitmap) object,
                                        holder.profileImageView.getWidth(), 10,
                                        Color.parseColor("#c6c6c6"));
                                holder.profileImageView.setImageBitmap(roundBitmap);
                                holder.setHeadImgBitmap(roundBitmap);
                                holder.setHeadImgLoaded(true);

                                mDataManager.getCacheManager().putBitmap(
                                        ImageCacheManager.CACHE_MESSAGE_LIST_PROFILE
                                                + holder.getMessageBean().getFakeId(),
                                        roundBitmap, true);

                            }

                        }
                    }
                });

            }
        }
    }

    private String getMessageId(int position) {
        return getMessageItems().get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;
        if (!mListCacheManager.containView(getMessageId(position))) {
            v = newView(position);
            mListCacheManager.putView(v, getMessageId(position));
        } else {

            v = mListCacheManager.getView(getMessageId(position));

        }
        bindView(v, position);

        return v;
    }

    public void popReply(final MessageBean messageBean) {
        dialog = Util.createReplyDialog(mActivity, messageBean.getNickName(), lastReplyCanceled, canceledReplyContent, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastReplyCanceled = false;
                        String replyContent = ((EditText) dialog.findViewById(R.id.dialog_edit_edit_text)).getText().toString();
                        reply(messageBean, replyContent);
                        dialog.cancel();

                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String replyContent = ((EditText) dialog.findViewById(R.id.dialog_edit_edit_text)).getText().toString();
                        lastReplyCanceled = true;
                        canceledReplyContent = replyContent;
                        dialog.cancel();
                    }
                }
        );
        dialog.show();
    }

    private void setStarBackground(View view, MessageBean messageBean) {
        boolean star = messageBean.getStarred();

        View starView = null;

        switch (messageBean.getType()) {
            case MessageBean.MESSAGE_TYPE_TEXT:
                starView = view.findViewById(R.id.message_item_text_button_star);

                break;

            case MessageBean.MESSAGE_TYPE_IMG:

                starView = view.findViewById(R.id.message_item_img_button_star);
                break;
            case MessageBean.MESSAGE_TYPE_VOICE:

                starView = view.findViewById(R.id.message_item_voi_button_star);

                break;

            default:

                starView = view.findViewById(R.id.message_item_text_button_star);

                break;

        }
        if (star) {
            starView.setBackgroundResource(R.drawable.msg_starred_button_bg);
        } else {
            starView.setBackgroundResource(R.drawable.msg_star_button_bg);

        }

    }

    private void reply(final MessageBean messageBean, String replyContent) {

        if (replyContent.length() > MAX_TEXT_LENGTH) {

            Toast.makeText(mActivity, "字数超过限制", Toast.LENGTH_LONG).show();

            return;
        } else if (replyContent.length() == 0) {

            Toast.makeText(mActivity, "请输入内容", Toast.LENGTH_LONG).show();

            return;
        }
        dialog.dismiss();
        mDataManager.getWechatManager().reply(
                mDataManager.getCurrentPosition(), messageBean, replyContent,
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub
                        messageBean.setHasReply("1");


                    }
                });
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
                if (!holder.getHeadImgLoaded()) {
                    setHeadImg(holder);
                }
                if (!holder.getDataLoaded()) {

                    MessageBean contentBean = holder.getMessageBean();
                    switch (contentBean.getType()) {
                        case MessageBean.MESSAGE_TYPE_IMG:
                            setImgMessageContent(holder);

                            break;

                        case MessageBean.MESSAGE_TYPE_VOICE:
                            setVoiceMessageContent(holder);

                            break;
                    }


                }

            }

        }
    }

}