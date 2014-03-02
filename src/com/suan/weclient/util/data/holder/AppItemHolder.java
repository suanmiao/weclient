package com.suan.weclient.util.data.holder;

import com.suan.weclient.util.data.bean.AppItemBean;

import java.util.ArrayList;

/**
 * Created by lhk on 2/7/14.
 */
public class AppItemHolder {
    private int total = -1;
    private int img_cnt = -1;
    private int voice_cnt = -1;
    private int video_cnt = -1;
    private int app_msg_cnt = -1;
    private int commondity_cnt = -1;
    private int video_mgs_cnt = -1;

    /*

     */

    private ArrayList<AppItemBean> appItemBeans;

    public ArrayList<AppItemBean> getAppItemBeans(){
        return appItemBeans;
    }

    public void setAppItemBeans(ArrayList<AppItemBean> appItemBeans){
        this.appItemBeans = appItemBeans;
    }

    public int getTotal(){
        return total;
    }

    public int getImg_cnt(){
        return img_cnt;
    }

    public int getVoice_cnt(){
        return  voice_cnt;
    }

    public int getVideo_cnt(){
        return video_cnt;
    }

    public int getApp_msg_cnt(){
        return app_msg_cnt;
    }

    public int getCommondity_cnt(){
        return commondity_cnt;
    }

    public int getVideo_mgs_cnt(){
        return video_mgs_cnt;
    }



}
