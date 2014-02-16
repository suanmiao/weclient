package com.suan.weclient.util.data;

/**
 * Created by lhk on 2/15/14.
 */
public class UserListItem {
    public static final int TYPE_USER = 2;
    public static final int TYPE_ADD = 3;
    private int itemType = TYPE_USER;
    private UserBean userBean;

    public UserListItem(UserBean userBean,int type){
        this.itemType = type;
        this.userBean = userBean;
    }

    public int getItemType(){
        return this.itemType;
    }

    public UserBean getUserBean(){
        return this.userBean;
    }


}
