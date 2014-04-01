package com.suan.weclient.util.data.holder;

import com.suan.weclient.util.data.bean.MessageBean;
import com.suan.weclient.util.data.bean.UserBean;

/**
 * Created by lhk on 2/3/14.
 */
public class ImgHolder {
    private MessageBean messageBean;
    private UserBean userBean;
    public ImgHolder(MessageBean messageBean,UserBean userBean){
        this.messageBean = messageBean;
        this.userBean = userBean;

    }

    public MessageBean getMessageBean(){
        return this.messageBean;
    }

    public UserBean getUserBean(){
        return this.userBean;
    }
}
