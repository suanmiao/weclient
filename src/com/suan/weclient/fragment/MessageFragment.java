package com.suan.weclient.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.MessageListAdapter;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.MessageChangeListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;

public class MessageFragment extends Fragment implements
		OnRefreshListener<ListView> {
	View view;
	private DataManager mDataManager;
	private PullToRefreshListView pullToRefreshListView;
	private MessageListAdapter messageListAdapter;
	private MessageHandler mHandler;

	private static final int PAGE_MESSAGE_AMOUNT = 20;

	public MessageFragment(DataManager dataManager) {

		mDataManager = dataManager;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.message_fragment, null);

		mHandler = new MessageHandler();
		initWidgets();
		initListener();
		initData();


		return view;
	}

	private void initWidgets() {
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.reply_list);
		pullToRefreshListView.setClickable(true);

		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void initData() {

		if (mDataManager.getCurrentMessageHolder() != null) {

			messageListAdapter = new MessageListAdapter(getActivity(),
					mDataManager);
			pullToRefreshListView.setAdapter(messageListAdapter);
			pullToRefreshListView.setOnRefreshListener(MessageFragment.this);

		}

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initListener() {

		mDataManager.addUserGroupListener(new UserGroupListener() {

			@Override
			public void onGroupChangeEnd() {
				// TODO Auto-generated method stub
				if (mDataManager.getUserGroup().size() == 0) {
					messageListAdapter.notifyDataSetChanged();

				}

			}

			@Override
			public void onAddUser() {
				// TODO Auto-generated method stub

			}

			@Override
			public void deleteUser(int index) {
				// TODO Auto-generated method stub

			}
		});
		mDataManager.addMessageChangeListener(new MessageChangeListener() {

			@Override
			public void onMessageGet(boolean changed) {
				// TODO Auto-generated method stub
				if (changed) {

					messageListAdapter.updateCache();

					messageListAdapter.notifyDataSetChanged();
				}else{
//					Toast.makeText(getActivity(), "没有新消息", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	public DataManager getMessageChangeListener() {
		return mDataManager;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		new GetDataTask(refreshView).execute();

	}

	public class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);
			Boolean changed = (Boolean) msg.obj;

			mDataManager.doMessageGet(changed);

		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		PullToRefreshBase<?> mRefreshedView;
		private boolean end = false;

		public GetDataTask(PullToRefreshBase<?> refreshedView) {
			mRefreshedView = refreshedView;
			end = false;
			if (mDataManager.getCurrentMessageHolder() == null) {
				end = true;
				return;
			}

			try {
				if (mRefreshedView.getCurrentMode() == Mode.PULL_FROM_END) {

					int size = mDataManager.getCurrentMessageHolder()
							.getMessageList().size();

					// must be fuul amount of page

					if (size % PAGE_MESSAGE_AMOUNT == 0) {
						int page = size
								/ PAGE_MESSAGE_AMOUNT
								+ ((size / PAGE_MESSAGE_AMOUNT == 0) ? size
										% PAGE_MESSAGE_AMOUNT
										/ (PAGE_MESSAGE_AMOUNT / 2) : 0) + 1;

						mDataManager.getWechatManager().getNextMessageList(
								page, mDataManager.getCurrentPosition(),
								new OnActionFinishListener() {

									@Override
									public void onFinish(int code,Object object) {
										// TODO Auto-generated method stub
										Message message = new Message();
										message.obj = object;
										mHandler.sendMessage(message);

										end = true;

									}
								});

					} else {
						end = true;
					}

				} else if (mRefreshedView.getCurrentMode() == Mode.PULL_FROM_START) {

					mDataManager.getWechatManager().getNewMessageList(false,
							mDataManager.getCurrentPosition(),
							new OnActionFinishListener() {

								@Override
								public void onFinish(int code,Object object) {
									// TODO Auto-generated method stub

									Message message = new Message();
									message.obj = object;
									mHandler.sendMessage(message);
									end = true;
									mDataManager
											.getWechatManager()
											.getUserProfile(
													false,false,
													mDataManager
															.getCurrentPosition(),
													new OnActionFinishListener() {

														@Override
														public void onFinish(
																int code,Object object) {
															// TODO
															// Auto-generated
															// method stub

														}
													});
								}
							});

				}
			} catch (Exception e) {

			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Simulates a background job.
			try {

				while (!end) {
					Thread.sleep(50);
				}

			} catch (Exception exception) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mRefreshedView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

}
