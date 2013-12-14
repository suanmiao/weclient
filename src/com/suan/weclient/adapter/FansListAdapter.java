package com.suan.weclient.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.ListCacheManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.FansBean;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class FansListAdapter extends BaseAdapter implements OnScrollListener {
	private LayoutInflater mInflater;
	private ListCacheManager mListCacheManager;
	private DataManager mDataManager;
	Context mContext;
	/*
	 * whether the scroll is busy
	 */
	private boolean mBusy = false;

	/*
	 * whether the user cancel the last reply if so ,we will save it
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

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

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
		private ImageView profileImageView;
		private Spinner groupSpinner;
		private ImageButton remarkButton;

		public ItemViewHolder(View parentView) {

			profileImageView = (ImageView) parentView
					.findViewById(R.id.fans_item_img_profile);
			profileTextView = (TextView) parentView
					.findViewById(R.id.fans_item_text_profile);
			groupSpinner = (Spinner) parentView
					.findViewById(R.id.fans_item_spinner_group);
			remarkButton = (ImageButton) parentView
					.findViewById(R.id.fans_item_button_edit_remark);

		}

	}

	public void bindView(View view, final int position) {

		ItemViewHolder holder = getHolder(view, position);

		String nickname = "";
		if (getFansItems().get(position).getRemarkName().length() != 0) {
			nickname = getFansItems().get(position).getRemarkName() + "("
					+ getFansItems().get(position).getNickname() + ")";

		} else {

			nickname = getFansItems().get(position).getNickname();
		}

		holder.profileTextView.setText(nickname);

		holder.remarkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		setGroupSpinner(holder, position);

		setProfileImage(holder, position);

	}

	private void setGroupSpinner(final ItemViewHolder holder, final int position) {

		String[] mItems = getGroupType();
		// 建立Adapter并且绑定数据源
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, mItems);

		holder.groupSpinner.setAdapter(spinnerAdapter);
		holder.groupSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// String str = parent.getItemAtPosition(position)
						// .toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
					}
				}

				);
		holder.groupSpinner.setSelection(getItemGroup(position));

	}

	private int getItemGroup(int position) {
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

	private String[] getGroupType() {
		int groupSize = mDataManager.getCurrentFansHolder().getFansGroupBeans()
				.size();
		String[] groupStrings = new String[groupSize];
		for (int i = 0; i < groupSize; i++) {
			groupStrings[i] = mDataManager.getCurrentFansHolder()
					.getFansGroupBeans().get(i).getGroupName();

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
					.getDiskBitmap(
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
							public void onFinish(Object object) {
								// TODO Auto-generated method stub
								Bitmap bitmap = (Bitmap) object;
								mDataManager.getCacheManager().putDiskBitmap(
										ImageCacheManager.CACHE_MESSAGE_PROFILE
												+ getFansItems().get(position)
														.getFansId(), bitmap);
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