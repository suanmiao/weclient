package com.suan.weclient.util.data.holder.resultHolder;

import com.suan.weclient.util.data.bean.FansBean;
import com.suan.weclient.util.data.bean.FansGroupBean;

import java.util.ArrayList;

/**
 * Created by lhk on 3/17/14.
 */
public class FansResultHolder {
    private ArrayList<FansBean> fansBeans;
    private ArrayList<FansGroupBean> fansGroupBeans;
    private int currentGroupIndex = -1;

    public static final int RESULT_MODE_ADD = 3;
    public static final int RESULT_MODE_REFRESH = 4;

    private int resultMode = RESULT_MODE_REFRESH;

    public FansResultHolder(ArrayList<FansBean> fansBeans,ArrayList<FansGroupBean> fansGroupBeans,int currentGroupIndex,int resultMode){
        this.fansBeans = fansBeans;
        this.fansGroupBeans = fansGroupBeans;
        this.currentGroupIndex = currentGroupIndex;
        this.resultMode = resultMode;
    }

    public ArrayList<FansBean> getFansBeans(){
        return fansBeans;
    }

    public ArrayList<FansGroupBean> getFansGroupBeans(){
        return fansGroupBeans;
    }

    public int getCurrentGroupIndex(){
        return currentGroupIndex;
    }

    public int getResultMode(){
        return resultMode;
    }


}
