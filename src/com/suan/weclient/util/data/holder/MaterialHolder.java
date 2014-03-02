package com.suan.weclient.util.data.holder;

import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.UserBean;

import java.util.ArrayList;

/**
 * Created by lhk on 2/16/14.
 */
public class MaterialHolder {

    private ArrayList<MaterialBean> materialBeans;
    private UserBean userBean;

    public MaterialHolder(UserBean userBean){
        this.userBean = userBean;
        materialBeans = new ArrayList<MaterialBean>();

    }

    public void setMaterialList(ArrayList<MaterialBean> getList){
        this.materialBeans = getList;
    }

    public ArrayList<MaterialBean> getMaterialBeans(){
        return materialBeans;
    }


}
