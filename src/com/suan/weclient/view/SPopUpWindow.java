package com.suan.weclient.view;

import java.util.ArrayList;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;

public class SPopUpWindow extends PopupWindow {

	private DataManager mDataManager;
	private ListView contentListView;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayAdapter<String> adapter; 
	private int i = 0;

	public SPopUpWindow(DataManager dataManager,View contentView, int width, int height,
			boolean focusable) {

		super(contentView, width, height, focusable);
		mDataManager = dataManager;
		contentListView = (ListView) contentView
				.findViewById(R.id.drop_down_list);

		adapter= new ArrayAdapter<String>(
				contentView.getContext(),
				R.layout.drop_down_item,R.id.drop_down_item_text,getData());
		contentListView.setAdapter(adapter);

	}

	private ArrayList<String> getData() {
		list.clear();
		list.add(""+i);
		list.add(""+(i+1));
		list.add(""+(i+2));
		i++;
		return list;
	}

	public void updateData() {
		getData();

	}

}
