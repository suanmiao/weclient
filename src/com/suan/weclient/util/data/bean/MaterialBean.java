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

    private String content = "";

    public static final int MATERIAL_TYPE_TEXT = 1;
    public static final int MATERIAL_TYPE_IMG = 2;
    public static final int MATERIAL_TYPE_VOICE = 3;


    public static final int MATERIAL_TYPE_APP = 10;

    /*
    about app item
     */
    private AppItemBean appItemBean;

    public MaterialBean(AppItemBean appItemBean){
        this.appItemBean = appItemBean;
        this.type = MATERIAL_TYPE_APP;
    }

    public MaterialBean(String content){
        this.content = content;
        this.type = MATERIAL_TYPE_TEXT;
    }

    public MaterialBean (String fileId,int type){
        this.file_id = fileId;
        this.type = type;

    }

    public AppItemBean getAppItemBean(){
        return appItemBean;
    }


    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }

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

    public void setType(int type){
        this.type = type;
    }

}
