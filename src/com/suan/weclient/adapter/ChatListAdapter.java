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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.ShowImgActivity;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.voice.VoiceHolder;
import com.suan.weclient.util.voice.VoiceManager.AudioPlayListener;

public class ChatListAdapter extends BaseAdapter implements OnScrollListener {
	private LayoutInflater mInflater;
	private ListCacheManager mListCacheManager;
	private DataManager mDataManager;
	private Context mContext;
	/*
	 * whether the scroll is busy
	 */
	private boolean mBusy = false;

	public ChatListAdapter(Context context, DataManager dataManager) {
		this.mInflater = LayoutInflater.from(context);
		this.mDataManager = dataManager;
		this.mContext = context;
		this.mListCacheManager = new ListCacheManager();
	}

	private ArrayList<MessageBean> getMessageItems() {
		if (mDataManager.getChatHolder() == null) {
			ArrayList<MessageBean> blankArrayList = new ArrayList<MessageBean>();
			return blankArrayList;
		}
		return mDataManager.getChatHolder().getMessageList();
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
		switch (getMessageItems().get(position).getType()
				+ getMessageItems().get(position).getOwner() * 10) {
		case MessageBean.MESSAGE_OWNER_HER * 10 + MessageBean.MESSAGE_TYPE_TEXT:
			convertView = mInflater.inflate(R.layout.chat_item_her_text_layout,
					null);
			break;

		case MessageBean.MESSAGE_OWNER_HER * 10 + MessageBean.MESSAGE_TYPE_IMG:
			convertView = mInflater.inflate(R.layout.chat_item_her_img_layout,
					null);
			break;
		case MessageBean.MESSAGE_OWNER_HER * 10
				+ MessageBean.MESSAGE_TYPE_VOICE:

			convertView = mInflater.inflate(
					R.layout.chat_item_her_voice_layout, null);

			break;

		case MessageBean.MESSAGE_OWNER_ME * 10 + MessageBean.MESSAGE_TYPE_TEXT:
			convertView = mInflater.inflate(R.layout.chat_item_me_text_layout,
					null);
			break;

		case MessageBean.MESSAGE_OWNER_ME * 10 + MessageBean.MESSAGE_TYPE_IMG:
			convertView = mInflater.inflate(R.layout.chat_item_me_img_layout,
					null);
			break;
		case MessageBean.MESSAGE_OWNER_ME * 10 + MessageBean.MESSAGE_TYPE_VOICE:

			convertView = mInflater.inflate(R.layout.chat_item_me_voice_layout,
					null);

			break;
		default:

			convertView = mInflater.inflate(R.layout.message_item_text_layout,
					null);
			break;

		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDataManager.getUserGroup().size() == 0) {

				} else {

				}

			}
		});

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

		private ImageView contentImageView;
		private TextView contentTextView;
		private ImageView profileImageView;
		private TextView profileTextView;
		private TextView timeTextView;

		public ItemViewHolder(View parentView, final int position) {

			switch (getMessageItems().get(position).getType()
					+ getMessageItems().get(position).getOwner() * 10) {
			case MessageBean.MESSAGE_OWNER_HER * 10
					+ MessageBean.MESSAGE_TYPE_TEXT:

				contentTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_text_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_time);
				contentTextView.setText(getMessageItems().get(position)
						.getContent());
				break;

			case MessageBean.MESSAGE_OWNER_HER * 10
					+ MessageBean.MESSAGE_TYPE_IMG:

				contentImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_img_img_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_img_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_img_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_img_text_time);

				break;

			case MessageBean.MESSAGE_OWNER_HER * 10
					+ MessageBean.MESSAGE_TYPE_VOICE:

				contentImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_voice_img_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_voice_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_voice_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_voice_text_time);
				break;

			case MessageBean.MESSAGE_OWNER_ME * 10
					+ MessageBean.MESSAGE_TYPE_TEXT:

				contentTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_text_text_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_me_text_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_text_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_text_text_time);
				contentTextView.setText(getMessageItems().get(position)
						.getContent());
				break;

			case MessageBean.MESSAGE_OWNER_ME * 10
					+ MessageBean.MESSAGE_TYPE_IMG:

				contentImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_me_img_img_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_me_img_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_img_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_img_text_time);

				break;

			case MessageBean.MESSAGE_OWNER_ME * 10
					+ MessageBean.MESSAGE_TYPE_VOICE:

				contentImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_me_voice_img_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_me_voice_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_voice_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_me_voice_text_time);
				break;

			default:

				contentTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_content);
				profileImageView = (ImageView) parentView
						.findViewById(R.id.chat_item_her_text_img_profile);
				profileTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_profile);
				timeTextView = (TextView) parentView
						.findViewById(R.id.chat_item_her_text_text_time);
				contentTextView.setText("[目前暂不支持该类型消息]");
				break;

			}
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

		long time = Long.parseLong(getMessageItems().get(position)
				.getDateTime());
		Date date = new Date(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
		String timeString = "" + format.format(date);

		holder.timeTextView.setText(timeString);
		holder.profileImageView.setBackgroundResource(R.drawable.ic_launcher);
		holder.profileTextView.setText(""
				+ getMessageItems().get(position).getNickName());

		setHeadImg(holder, position);

	}

	private void setVoiceMessageContent(final ItemViewHolder holder,
			final int position) {

		boolean voiceLoaded = false;
		if (holder.contentImageView.getTag() != null) {
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
						public void onFinish(Object object) {
							// TODO Auto-generated method stub
							try {

								byte[] bytes = (byte[]) object;
								VoiceHolder voiceHolder = new VoiceHolder(
										bytes, getMessageItems().get(position)
												.getPlayLength(),
										getMessageItems().get(position)
												.getLength());
								holder.contentImageView.setTag(voiceHolder);

							} catch (Exception exception) {

							}

						}
					});

		}
		holder.contentImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (holder.contentImageView.getTag() != null) {

					final VoiceHolder voiceHolder = (VoiceHolder) holder.contentImageView
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

										holder.contentImageView
												.setBackgroundResource(R.drawable.ic_launcher);

									}

									@Override
									public void onAudioStart() {
										// TODO Auto-generated method stub
										voiceHolder.setPlaying(true);
										holder.contentImageView
												.setBackgroundColor(Color.RED);

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

			Bitmap contentBitmap = mDataManager.getCacheManager().getRomBitmap(
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
							public void onFinish(Object object) {
								// TODO Auto-generated method stub
								holder.contentImageView.setTag(true);
								Bitmap bitmap = (Bitmap) object;
								mDataManager.getCacheManager().putRomBitmap(
										ImageCacheManager.CACHE_MESSAGE_CONTENT
												+ getMessageItems().get(
														position).getId(),
										bitmap);

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

			Bitmap headBitmap = mDataManager.getCacheManager().getDiskBitmap(
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
							public void onFinish(Object object) {
								// TODO Auto-generated method stub
								holder.profileImageView.setTag(true);
								Bitmap bitmap = (Bitmap) object;

								mDataManager.getCacheManager().putDiskBitmap(
										ImageCacheManager.CACHE_MESSAGE_PROFILE
												+ getMessageItems().get(
														position).getFakeId(),
										bitmap);
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