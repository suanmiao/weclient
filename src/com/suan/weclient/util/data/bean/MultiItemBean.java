package com.suan.weclient.util.data.bean;

/**
 * Created by lhk on 2/7/14.
 */
public class MultiItemBean {

    private int seq = -1;
    private String cover = "";
    private String title = "";
    private String digest = "";
    private String content_url = "";
    private String file_id = "";
    private String source_url = "";
    private String author = "";
    private int show_cover_pic = 0;

    public int getSeq(){
        return seq;
    }

    public String getCover(){
        return cover;
    }

    public String getTitle(){
        return title;
    }

    public String getDigest(){
        return digest;
    }

    public String getContent_url(){
        return content_url;

    }

    public String getFile_id(){
        return file_id;
    }

    public String getSource_url(){
        return source_url;
    }

    public String getAuthor(){
        return source_url;
    }

    public int  getShow_cover_pic(){
        return show_cover_pic;
    }

}
