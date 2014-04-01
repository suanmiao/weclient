package com.suan.weclient.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.suan.weclient.R;
import com.suan.weclient.util.text.FaceItem;
import com.suan.weclient.view.Face.FaceGridView;

import java.util.ArrayList;

/**
 * Created by lhk on 3/17/14.
 */
public class FaceGridAdapter implements ListAdapter {
    private ArrayList<FaceItem> faceItems;
    private Context mContext;
    private FaceGridView.InputFaceListener inputFaceListener;
    private LayoutInflater layoutInflater;

    public FaceGridAdapter(Context context, ArrayList<FaceItem> faceItems,FaceGridView.InputFaceListener inputFaceListener) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.inputFaceListener = inputFaceListener;
        this.faceItems = faceItems;
        this.mContext = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return faceItems.size();
    }

    @Override
    public Object getItem(int position) {
        return faceItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RelativeLayout itemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.face_item_layout, null);
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFaceListener.onInput(faceItems.get(position).getKey());

            }
        });
        ImageView contentImage = (ImageView) itemLayout.findViewById(R.id.face_item_img);
        Drawable drawable = null;
        String sourceName = mContext.getPackageName() + ":drawable/"
                + faceItems.get(position).getValue();
        int id = mContext.getResources().getIdentifier(sourceName, null, null);
        if (id != 0) {
            drawable = mContext.getResources().getDrawable(id);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            }
        }

        contentImage.setBackground(drawable);

        return itemLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
