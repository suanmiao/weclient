package com.suan.weclient.util.data.holder;

import android.util.Log;

import com.suan.weclient.util.data.bean.FansBean;
import com.suan.weclient.util.data.bean.FansGroupBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.resultHolder.FansResultHolder;

import java.util.ArrayList;

public class FansHolder {

    private ArrayList<FansBean> fansBeans;
    private ArrayList<FansGroupBean> fansGroupBeans;
    private int currentGroupIndex = -1;
    private int totalAmount = 0;
    private UserBean userBean;

    private int fansCount = 0;


    public FansHolder(UserBean userBean) {
        fansBeans = new ArrayList<FansBean>();
        fansGroupBeans = new ArrayList<FansGroupBean>();
        this.userBean = userBean;
    }

    public void mergeFansResult(FansResultHolder fansResultHolder) {
        this.currentGroupIndex = fansResultHolder.getCurrentGroupIndex();
        this.fansGroupBeans = fansResultHolder.getFansGroupBeans();
        switch (fansResultHolder.getResultMode()) {
            case FansResultHolder.RESULT_MODE_ADD:
                addFans(fansResultHolder.getFansBeans());

                break;

            case FansResultHolder.RESULT_MODE_REFRESH:
                this.fansBeans = fansResultHolder.getFansBeans();

                break;
        }
        initFansCount();

    }


    private void addFans(ArrayList<FansBean> nowArrayList) {
        for (int i = 0; i < nowArrayList.size(); i++) {
            fansBeans.add(nowArrayList.get(i));
        }
    }

    public int getCurrentGroupIndex() {
        return currentGroupIndex;
    }

    public void setCurrentGroupIndex(int currentGroupIndex) {
        this.currentGroupIndex = currentGroupIndex;
    }

    public int getCurrentGroupId() {
        if (currentGroupIndex == -1) {
            return -1;
        } else {
            return fansGroupBeans.get(currentGroupIndex).getGroupId();
        }
    }

    private void initFansCount() {

        fansCount = 0;
        for (int i = 0; i < fansBeans.size(); i++) {
            FansBean nowBean = fansBeans.get(i);
            if (nowBean.getBeanType() != FansBean.BEAN_TYPE_DATA) {
                fansCount++;
            }
        }

    }

    public int getFansCount() {
        return fansCount;
    }

    public ArrayList<FansBean> getFansBeans() {
        return fansBeans;
    }

    public ArrayList<FansGroupBean> getFansGroupBeans() {
        return fansGroupBeans;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public UserBean getUserBean() {
        return userBean;
    }

}
