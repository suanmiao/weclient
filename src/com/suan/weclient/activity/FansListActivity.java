package com.suan.weclient.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.FansListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.FansListChangeListener;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.actionbar.CustomFansActionView;

public class FansListActivity extends SherlockActivity implements
		OnRefreshListener<ListView> {

    private ActionBar actionBar;
    private ImageView backButton;
	private PullToRefreshListView mRefreshListView;
	private FansListAdapter fansListAdapter;
	private DataManager mDataManager;
	private FansHandler fansHandler;
	private static final int PAGE_FANS = 10;

	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fans_list_layout);
        initWidgets();
        initData();
        actionBar = getSupportActionBar();
        initActionBar();
		initListener();

	}

	private void initWidgets() {

		mRefreshListView = (PullToRefreshListView) findViewById(R.id.fans_list);

	}


    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);


        CustomFansActionView customFansActionView = new CustomFansActionView(this);
/*
        backButton = (ImageView)customFansActionView.findViewById(R.id.actionbar_fans_img_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FansListActivity.this.finish();

            }
        });

 */
        customFansActionView.init(mDataManager);

       ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customFansActionView, layoutParams);


    }

	private void initData() {


		GlobalContext globalContext = (GlobalContext) getApplicationContext();
		mDataManager = globalContext.getDataManager();
		fansListAdapter = new FansListAdapter(this, mDataManager);
		mRefreshListView.setAdapter(fansListAdapter);
		mRefreshListView.setOnRefreshListener(this);
		fansHandler = new FansHandler();

		mDataManager.getWechatManager().getFansList(0,
				mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),
				new OnActionFinishListener() {

					@Override
					public void onFinish(int code,Object object) {
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
                Log.e("fans get",""+mDataManager.getCurrentFansHolder().getCurrentGroupIndex());
                SharedPreferenceManager.putLastNewPeople(FansListActivity.this,0);
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
								mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),
								new OnActionFinishListener() {

									@Override
									public void onFinish(int code,Object object) {
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
							mDataManager.getCurrentPosition(), mDataManager.getCurrentFansHolder().getCurrentGroupId(),

							new OnActionFinishListener() {


								@Override
								public void onFinish(int code,Object object) {
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
