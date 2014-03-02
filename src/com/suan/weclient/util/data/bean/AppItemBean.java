package com.suan.weclient.util.data.bean;

import java.util.ArrayList;

/**
 * Created by lhk on 2/7/14.
 */
public class AppItemBean {

    private int seq = -1;
    private String app_id = "";
    private String file_id = "";
    private String title = "";
    private String digest = "";
    private String create_time = "";
    private ArrayList<MultiItemBean> multi_item = new ArrayList<MultiItemBean>();

    public int getSeq(){
        return seq;
    }

    public String getApp_id(){
        return app_id;
    }

    public String getFile_id(){
        return file_id;
    }

    public String getTitle(){
        return title;
    }

    public String getDigest(){
        return digest;
    }

    public String getCreate_time(){
        return create_time;
    }

    public ArrayList<MultiItemBean> getMulti_item(){
        return multi_item;
    }

}
