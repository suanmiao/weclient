package com.suan.weclient.util.data.holder;

import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.resultHolder.MaterialResultHolder;

import java.util.ArrayList;

/**
 * Created by lhk on 2/16/14.
 */
public class MaterialHolder {

    private ArrayList<MaterialBean> materialBeans;
    private UserBean userBean;

    private int materialCount = 0;

    public MaterialHolder(UserBean userBean){
        this.userBean = userBean;
        materialBeans = new ArrayList<MaterialBean>();

    }


    public ArrayList<MaterialBean> getMaterialBeans(){
        return materialBeans;
    }


    public void mergeMaterialResult(MaterialResultHolder messageResultHolder){
        switch(messageResultHolder.getResultMode()){
            case MaterialResultHolder.RESULT_MODE_REFRESH:
                materialBeans = messageResultHolder.getMaterialBeans();

                break;
            case MaterialResultHolder.RESULT_MODE_ADD:
                addMaterial(messageResultHolder.getMaterialBeans());

                break;
        }

        initMessageCount();
    }

    private void addMaterial(ArrayList<MaterialBean> nowArrayList) {
        for (int i = 0; i < nowArrayList.size(); i++) {
            materialBeans.add(nowArrayList.get(i));
        }

    }

    private void initMessageCount(){

        materialCount = 0;
        for(int i = 0;i<materialBeans.size();i++){
            MaterialBean nowBean = materialBeans.get(i);
                materialCount++;
        }

    }

    public int getMaterialCount(){
        return materialCount;
    }


}
