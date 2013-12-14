package com.suan.weclient.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.FansListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.FansListChangeListener;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;

public class FansListActivity extends Activity implements
		OnRefreshListener<ListView> {

	private PullToRefreshListView mRefreshListView;
	private FansListAdapter fansListAdapter;
	private DataManager mDataManager;
	private FansHandler fansHandler;
	private static final int PAGE_FANS = 10;
	private int groupId = -1;

	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fans_list_layout);
		initWidgets();
		initData();
		initListener();

	}

	private void initWidgets() {

		mRefreshListView = (PullToRefreshListView) findViewById(R.id.fans_list);

	}

	private void initData() {

		GlobalContext globalContext = (GlobalContext) getApplicationContext();
		mDataManager = globalContext.getDataManager();
		fansListAdapter = new FansListAdapter(this, mDataManager);
		mRefreshListView.setAdapter(fansListAdapter);
		mRefreshListView.setOnRefreshListener(this);
		fansHandler = new FansHandler();

		mDataManager.getWechatManager().getFansList(0,
				mDataManager.getCurrentPosition(), groupId,
				new OnActionFinishListener() {

					@Override
					public void onFinish(Object object) {
						// TODO Auto-generated method stub

						Message message = new Message();
						message.obj = object;

						fansHandler.sendMessage(message);

					}
				});

	}

	private void initListener() {
		mDataManager.addFansListChangeListener(new FansListChangeListener() {

			@Override
			public void onFansGet(boolean changed) {
				// TODO Auto-generated method stub
				if (changed) {

					fansListAdapter.updateCache();
					fansListAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(FansListActivity.this, "没有新粉丝", Toast.LENGTH_SHORT).show();

				}

			}
		});

	}

	public class FansHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);
			Boolean changed = (Boolean) msg.obj;
			mDataManager.doFansGet(changed);

		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

		new GetDataTask(refreshView).execute();

	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		PullToRefreshBase<?> mRefreshedView;
		private boolean end = false;

		public GetDataTask(PullToRefreshBase<?> refreshedView) {
			mRefreshedView = refreshedView;
			end = false;
			if (mDataManager.getCurrentFansHolder() == null) {
				end = true;
				return;
			}

			try {
				if (mRefreshedView.getCurrentMode() == Mode.PULL_FROM_END) {

					if (mDataManager.getCurrentFansHolder().getFansBeans()
							.size()
							% PAGE_FANS != 0) {
						end = true;
					} else {
						int page = mDataManager.getCurrentFansHolder()
								.getFansBeans().size() / 10;

						mDataManager.getWechatManager().getFansList(page,
								mDataManager.getCurrentPosition(), groupId,
								new OnActionFinishListener() {

									@Override
									public void onFinish(Object object) {
										// TODO Auto-generated method stub
										Message message = new Message();
										message.obj = object;

										fansHandler.sendMessage(message);
										end = true;

									}
								});

					}
				} else if (mRefreshedView.getCurrentMode() == Mode.PULL_FROM_START) {
					mDataManager.getWechatManager().getFansList(0,
							mDataManager.getCurrentPosition(), 0,
							new OnActionFinishListener() {

								@Override
								public void onFinish(Object object) {
									// TODO Auto-generated method stub
									Message message = new Message();
									message.obj = object;

									fansHandler.sendMessage(message);
									end = true;

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
