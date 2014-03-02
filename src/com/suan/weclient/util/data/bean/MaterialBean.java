package com.suan.weclient.util.data.bean;

/**
 * Created by lhk on 2/16/14.
 */
public class MaterialBean {

    private String file_id = "";
    private String name = "";
    private int type = -1;
    private String size= "";
    private String update_time = "";
    private String play_length = "";

    public String getFile_id(){
        return file_id;

    }

    public String getName(){
        return name;
    }

    public String getSize(){
        return size;
    }

    public String getUpdate_time(){
        return update_time;
    }

    public String getPlay_length(){
        return play_length;
    }

    public int getType(){
        return type;
    }

}
