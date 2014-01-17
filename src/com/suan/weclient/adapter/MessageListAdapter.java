package com.suan.weclient.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.suan.weclient.activity.ShowImgActivity;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.voice.VoiceHolder;
import com.suan.weclient.util.voice.VoiceManager.AudioPlayListener;

public class MessageListAdapter extends BaseAdapter implements OnScrollListener {
    private LayoutInflater mInflater;
    private ListCacheManager mListCacheManager;
    private DataManager mDataManager;
    private Context mContext;

    private EditText popContentEditText;
    private TextView popTitleTextView;
    private TextView textAmountTextView;
    private Button popCancelButton, popSureButton;
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

    public MessageListAdapter(Context context, DataManager dataManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataManager = dataManager;
        this.mContext = context;
        this.mListCacheManager = new ListCacheManager();
    }

    private ArrayList<MessageBean> getMessageItems() {
        if (mDataManager.getUserGroup().size() == 0) {
            ArrayList<MessageBean> blankArrayList = new ArrayList<MessageBean>();
            return blankArrayList;
        }
        return mDataManager.getCurrentMessageHolder().getMessageList();
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

        private ImageView profileImageView;
        private TextView profileTextView;
        private TextView timeTextView;

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

        public ItemViewHolder(View parentView, final int position) {

            this.parentView = parentView;

            switch (getMessageItems().get(position).getType()) {
                case MessageBean.MESSAGE_TYPE_TEXT:

                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_text_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_time);

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
                    contentTextView.setText(getMessageItems().get(position)
                            .getContent());
                    break;

                case MessageBean.MESSAGE_TYPE_IMG:

                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_img_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_img_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_img_text_time);
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

                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_voi_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_voi_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_voi_text_time);

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

                default:


                    profileImageView = (ImageView) parentView
                            .findViewById(R.id.message_item_text_img_profile);
                    profileTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_profile);
                    timeTextView = (TextView) parentView
                            .findViewById(R.id.message_item_text_text_time);

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

                break;

            case MessageBean.MESSAGE_TYPE_IMG:

                setImgMessageContent(holder, position);

                break;

            case MessageBean.MESSAGE_TYPE_VOICE:

                setVoiceMessageContent(holder, position);

                break;

            default:

                break;

        }

//        holder.parentView.setOnLongClickListener(new LongClickListener(holder));
        holder.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataManager.createChat(mDataManager.getCurrentUser(),
                        getMessageItems().get(position).getFakeId());
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(mContext, ChatActivity.class);
                mContext.startActivity(jumbIntent);

            }
        });

        holder.replyLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mDataManager.getUserGroup().size() == 0) {

                } else {

                    popReply(position);

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
                    public void onFinish(int code,Object object) {
                        // TODO Auto-generated method stub

                        setStarBackground(v, position);
                    }
                });

            }
        });

        setStarBackground(holder.starImageButton, position);

        long time = Long.parseLong(getMessageItems().get(position)
                .getDateTime());
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
        String timeString = "" + format.format(date);

        holder.timeTextView.setText(timeString);
        holder.profileImageView.setBackgroundResource(R.drawable.ic_launcher);

        holder.profileImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDataManager.createChat(mDataManager.getCurrentUser(),
                        getMessageItems().get(position).getFakeId());
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(mContext, ChatActivity.class);
                mContext.startActivity(jumbIntent);

            }
        });

        holder.profileTextView.setText(""
                + getMessageItems().get(position).getNickName());

        setHeadImg(holder, position);

    }

    private void setVoiceMessageContent(final ItemViewHolder holder,
                                        final int position) {


        boolean voiceLoaded = false;
        if (holder.voicePlayLayout.getTag() != null) {
            voiceLoaded = true;
        }

        if (!mBusy && !voiceLoaded) {
            mDataManager.getWechatManager().getMessageVoice(
                    mDataManager.getCurrentPosition(),
                    getMessageItems().get(position).getId(),
                    Integer.parseInt(getMessageItems().get(position)
                            .getLength()), mDataManager.getCurrentUser(),
                    new OnActionFinishListener() {

                        @Override
                        public void onFinish(int code,Object object) {
                            // TODO Auto-generated method stub
                            try {

                                byte[] bytes = (byte[]) object;
                                VoiceHolder voiceHolder = new VoiceHolder(
                                        bytes, getMessageItems().get(position)
                                        .getPlayLength(),
                                        getMessageItems().get(position)
                                                .getLength());
                                int playLength = Integer.parseInt(getMessageItems().get(position).getPlayLength());
                                int seconds = playLength / 1000;
                                int minutes = seconds / 60;
                                int leaveSecond = seconds % 60;
                                String info = "";
                                if (minutes != 0) {
                                    info += minutes + "'";

                                }
                                info += " " + leaveSecond + "'";
                                holder.voiceInfoTextView.setText(info);

                                holder.voicePlayLayout.setTag(voiceHolder);

                            } catch (Exception exception) {

                            }

                        }
                    });

        }
        holder.voicePlayLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (holder.voicePlayLayout.getTag() != null) {

                    final VoiceHolder voiceHolder = (VoiceHolder) holder.voicePlayLayout
                            .getTag();
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

    private void setImgMessageContent(final ItemViewHolder holder,
                                      final int position) {

        holder.contentImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(mContext, ShowImgActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("slaveSid", mDataManager.getCurrentUser()
                        .getSlaveSid());
                bundle.putString("slaveUser", mDataManager.getCurrentUser()
                        .getSlaveUser());
                bundle.putString("msgId", getMessageItems().get(position)
                        .getId());
                bundle.putString("token", mDataManager.getCurrentUser()
                        .getToken());
                bundle.putString("referer", getMessageItems().get(position)
                        .getReferer());
                jumbIntent.putExtras(bundle);
                mContext.startActivity(jumbIntent);

            }
        });
        boolean imgLoaded = false;
        if (holder.contentImageView.getTag() != null) {
            imgLoaded = (Boolean) holder.contentImageView.getTag();
        }

        if (!mBusy || !imgLoaded) {

            Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                    ImageCacheManager.CACHE_MESSAGE_CONTENT
                            + getMessageItems().get(position).getId());
            if (contentBitmap == null) {
                mDataManager.getWechatManager().getMessageImg(
                        mDataManager.getCurrentPosition(),
                        getMessageItems().get(position).getId(),
                        mDataManager.getCurrentUser().getSlaveSid(),
                        mDataManager.getCurrentUser().getSlaveUser(),
                        mDataManager.getCurrentUser().getToken(),
                        getMessageItems().get(position).getReferer(),
                        holder.contentImageView,
                        WeChatLoader.WECHAT_URL_MESSAGE_IMG_SMALL,
                        new OnActionFinishListener() {

                            @Override
                            public void onFinish(int code,Object object) {
                                // TODO Auto-generated method stub
                                holder.contentImageView.setTag(true);
                                Bitmap bitmap = (Bitmap) object;
                                mDataManager.getCacheManager().putBitmap(
                                        ImageCacheManager.CACHE_MESSAGE_CONTENT
                                                + getMessageItems().get(
                                                position).getId(),
                                        bitmap,true);

                            }
                        });

            } else {
                holder.contentImageView.setImageBitmap(contentBitmap);
            }
        }
    }

    private void setHeadImg(final ItemViewHolder holder, final int position) {

        boolean imgLoaded = false;
        if (holder.profileImageView.getTag() != null) {
            imgLoaded = (Boolean) holder.profileImageView.getTag();
        }

        if (!mBusy || !imgLoaded) {

            Bitmap headBitmap = mDataManager.getCacheManager().getBitmap(
                    ImageCacheManager.CACHE_MESSAGE_PROFILE
                            + getMessageItems().get(position).getFakeId());
            if (headBitmap != null) {
                holder.profileImageView.setImageBitmap(headBitmap);

            } else {
                mDataManager.getWechatManager().getMessageHeadImg(
                        mDataManager.getCurrentPosition(),
                        getMessageItems().get(position).getFakeId(),
                        getMessageItems().get(position).getReferer(),
                        holder.profileImageView, new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code,Object object) {
                        // TODO Auto-generated method stub
                        holder.profileImageView.setTag(true);
                        Bitmap bitmap = (Bitmap) object;

                        mDataManager.getCacheManager().putBitmap(
                                ImageCacheManager.CACHE_MESSAGE_PROFILE
                                        + getMessageItems().get(
                                        position).getFakeId(),
                                bitmap,true);
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

    public void popReply(final int position) {

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
        textAmountTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popContentEditText.setText("");

            }
        });

        if (lastReplyCanceled) {
            popContentEditText.setText(canceledReplyContent);
        }

        popContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                int remainTextAmount = MAX_TEXT_LENGTH - s.length();
                if (remainTextAmount >= 0) {
                    textAmountTextView.setTextColor(Color.rgb(0, 0, 0));
                } else {
                    textAmountTextView.setTextColor(Color.RED);
                }
                textAmountTextView.setText(remainTextAmount + " x");

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

        popTitleTextView.setText("Re:"
                + mDataManager.getCurrentMessageHolder().getMessageList()
                .get(position).getNickName());
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lastReplyCanceled = false;

                reply(position);

            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lastReplyCanceled = true;
                canceledReplyContent = popContentEditText.getText().toString();
                dialog.cancel();

            }
        });

        dialog = new Dialog(mContext, R.style.dialog);

        dialog.setContentView(dialogView);
        dialog.show();

    }

    private void setStarBackground(View view, int position) {
        boolean star = mDataManager.getCurrentMessageHolder().getMessageList()
                .get(position).getStarred();

        View starView = null;

        switch (getMessageItems().get(position).getType()) {
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

    private void reply(int position) {

        String replyContent = popContentEditText.getText().toString();
        if (replyContent.length() > MAX_TEXT_LENGTH) {

            Toast.makeText(mContext, "字数超过限制", Toast.LENGTH_LONG).show();

            return;
        } else if (replyContent.length() == 0) {

            Toast.makeText(mContext, "请输入内容", Toast.LENGTH_LONG).show();

            return;
        }
        dialog.dismiss();
        mDataManager.getWechatManager().reply(
                mDataManager.getCurrentPosition(), position, replyContent,
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code,Object object) {
                        // TODO Auto-generated method stub

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

        } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 滑动手未松开
            mBusy = true;
        } else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {// 滑动中手已松开
            mBusy = true;
        }
    }

}