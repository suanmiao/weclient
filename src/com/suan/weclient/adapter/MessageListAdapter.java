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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.ShowImgActivity;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.MessageItem;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class MessageListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private DataManager mDataManager;
	private Context mContext;
	private EditText popContentEditText;
	private TextView popTitleTextView;
	private TextView textAmountTextView;
	private ImageButton popCancelButton, popSureButton;
	private Dialog dialog;
	private static final int MAX_TEXT_LENGTH = 140;
	private String canceledReplyContent = "";
	private boolean lastReplyCanceled = false;

	public MessageListAdapter(Context context, DataManager dataManager) {
		this.mInflater = LayoutInflater.from(context);
		this.mDataManager = dataManager;
		this.mContext = context;
	}

	private ArrayList<MessageItem> getMessageItems() {
		if (mDataManager.getUserGroup().size() == 0) {
			ArrayList<MessageItem> blankArrayList = new ArrayList<MessageItem>();
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
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ItemViewHolder viewHolder = null;
		viewHolder = new ItemViewHolder();

		switch (getMessageItems().get(position).getType()) {
		case MessageItem.MESSAGE_TYPE_TEXT:

			convertView = mInflater.inflate(R.layout.message_item_text_layout,
					null);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_content);
			viewHolder.starImageButton = (ImageButton) convertView
					.findViewById(R.id.message_item_text_button_star);
			viewHolder.profileImageView = (ImageView) convertView
					.findViewById(R.id.message_item_text_img_profile);
			viewHolder.profileTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_profile);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_time);
			viewHolder.contentTextView.setText(getMessageItems().get(position)
					.getContent());
			break;

		case MessageItem.MESSAGE_TYPE_IMG:

			convertView = mInflater.inflate(R.layout.message_item_img_layout,
					null);
			viewHolder.contentImageView = (ImageView) convertView
					.findViewById(R.id.message_item_img_img_content);
			viewHolder.starImageButton = (ImageButton) convertView
					.findViewById(R.id.message_item_img_button_star);
			viewHolder.profileImageView = (ImageView) convertView
					.findViewById(R.id.message_item_img_img_profile);
			viewHolder.profileTextView = (TextView) convertView
					.findViewById(R.id.message_item_img_text_profile);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_img_text_time);

			viewHolder.contentImageView
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent jumbIntent = new Intent();
							jumbIntent
									.setClass(mContext, ShowImgActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("slaveSid", mDataManager
									.getCurrentUser().getSlaveSid());
							bundle.putString("slaveUser", mDataManager
									.getCurrentUser().getSlaveUser());
							bundle.putString("msgId",
									getMessageItems().get(position).getId());
							bundle.putString("token", mDataManager
									.getCurrentUser().getToken());
							bundle.putString("referer",
									getMessageItems().get(position)
											.getReferer());
							jumbIntent.putExtras(bundle);
							mContext.startActivity(jumbIntent);

						}
					});

			Bitmap contentBitmap = mDataManager.getCacheManager().getRomBitmap(
					ImageCacheManager.CACHE_MESSAGE_CONTENT
							+ getMessageItems().get(position).getId());
			if (contentBitmap == null) {
				mDataManager.getWechatManager().getMessageImg(mDataManager.getCurrentPosition(), getMessageItems().get(position).getId(),
						mDataManager.getCurrentUser().getSlaveSid(),
						mDataManager.getCurrentUser().getSlaveUser(),
						mDataManager.getCurrentUser().getToken(),
						getMessageItems().get(position).getReferer(),
						viewHolder.contentImageView,
						WeChatLoader.WECHAT_URL_MESSAGE_IMG_SMALL,new OnActionFinishListener() {
							
							@Override
							public void onFinish(Object object) {
								// TODO Auto-generated method stub
								Bitmap bitmap = (Bitmap)object;
									mDataManager
											.getCacheManager()
											.putRomBitmap(
													ImageCacheManager.CACHE_MESSAGE_CONTENT
															+ getMessageItems()
																	.get(position)
																	.getId(),
													bitmap);
								
							}
						});

			} else {
				viewHolder.contentImageView.setImageBitmap(contentBitmap);
			}
			break;

		default:

			convertView = mInflater.inflate(R.layout.message_item_text_layout,
					null);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_content);
			viewHolder.starImageButton = (ImageButton) convertView
					.findViewById(R.id.message_item_text_button_star);
			viewHolder.profileImageView = (ImageView) convertView
					.findViewById(R.id.message_item_text_img_profile);
			viewHolder.profileTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_profile);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_text_text_time);
			viewHolder.contentTextView.setText("[目前暂不支持该类型消息]");
			break;

		}
		convertView.setTag(viewHolder);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDataManager.getUserGroup().size() == 0) {

				} else {

					popReply(position);

				}

			}
		});
		setStarBackground(viewHolder.starImageButton, position);

		long time = Long.parseLong(getMessageItems().get(position)
				.getDateTime());
		Date date = new Date(time * 1000);
		SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
		// format.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeString = "" + format.format(date);

		viewHolder.timeTextView.setText(timeString);
		viewHolder.profileImageView
				.setBackgroundResource(R.drawable.ic_launcher);
		viewHolder.profileTextView.setText(""
				+ getMessageItems().get(position).getNickName());
		viewHolder.starImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				boolean stared = mDataManager.getCurrentMessageHolder()
						.getMessageList().get(position).getStarred();
				mDataManager.getWechatManager().star(
						mDataManager.getCurrentPosition(), position,
						(ImageButton) v, !stared, new OnActionFinishListener() {

							@Override
							public void onFinish(Object object) {
								// TODO Auto-generated method stub

								setStarBackground(v, position);
							}
						});

			}
		});

		Bitmap headBitmap = mDataManager.getCacheManager().getDiskBitmap(
				ImageCacheManager.CACHE_MESSAGE_PROFILE
						+ getMessageItems().get(position).getFakeId());
		if (headBitmap != null) {
			viewHolder.profileImageView.setImageBitmap(headBitmap);

		} else {
			mDataManager.getWechatManager().getMessageHeadImg(
					mDataManager.getCurrentPosition(),
					getMessageItems().get(position).getFakeId(),
					getMessageItems().get(position).getReferer(),
					viewHolder.profileImageView, new OnActionFinishListener() {

						@Override
						public void onFinish(Object object) {
							// TODO Auto-generated method stub
							Bitmap bitmap = (Bitmap) object;

							mDataManager.getCacheManager().putDiskBitmap(
									ImageCacheManager.CACHE_MESSAGE_PROFILE
											+ getMessageItems().get(position)
													.getFakeId(), bitmap);
						}
					});

		}

		return convertView;
	}

	public void popReply(final int position) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.dialog_edit_layout, null);
		popTitleTextView = (TextView) dialogView
				.findViewById(R.id.dialog_edit_text_title);

		popContentEditText = (EditText) dialogView
				.findViewById(R.id.dialog_edit_edit_text);
		popSureButton = (ImageButton) dialogView
				.findViewById(R.id.dialog_edit_button_sure);
		popCancelButton = (ImageButton) dialogView
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

		if (star) {
			view.setBackgroundResource(R.drawable.msg_starred_button_bg);
		} else {
			view.setBackgroundResource(R.drawable.msg_star_button_bg);

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
					public void onFinish(Object object) {
						// TODO Auto-generated method stub

					}
				});
	}

	public class ItemViewHolder {
		private ImageView contentImageView;
		private TextView contentTextView;
		private ImageButton starImageButton;
		private ImageView profileImageView;
		private TextView profileTextView;
		private TextView timeTextView;

	}
}