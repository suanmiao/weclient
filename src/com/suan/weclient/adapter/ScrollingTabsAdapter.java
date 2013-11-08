package com.suan.weclient.adapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.suan.weclient.R;
import com.suan.weclient.view.TabAdapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class ScrollingTabsAdapter implements TabAdapter {

	private final Activity activity;

	public ScrollingTabsAdapter(Activity act) {
		activity = act;
	}

	public View getView(int position) {
		LayoutInflater inflater = activity.getLayoutInflater();
		final Button tab = (Button) inflater.inflate(R.layout.tabs, null);
		final String[] mTitles = activity.getResources().getStringArray(
				R.array.tab_weclient_titles);
		Set<String> tab_sets = new HashSet<String>(Arrays.asList(mTitles));
		String[] tabs_new = new String[tab_sets.size()];
		int cnt = 0;
		for (int i = 0; i < mTitles.length; i++) {
			if (tab_sets.contains(mTitles[i])) {
				tabs_new[cnt] = mTitles[i];
				cnt++;
			}
		}
		if (position < tabs_new.length)
			tab.setText(tabs_new[position].toUpperCase());
		return tab;
	}

}
