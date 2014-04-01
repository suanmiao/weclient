package com.suan.weclient.util.data.holder.resultHolder;

import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.MessageBean;

import java.util.ArrayList;

/**
 * Created by lhk on 3/16/14.
 */

public class MaterialResultHolder {
    private ArrayList<MaterialBean> materialBeans;

    private int resultMode = RESULT_MODE_REFRESH;

    public static final int RESULT_MODE_REFRESH = 2;
    public static final int RESULT_MODE_ADD = 3;

    public MaterialResultHolder(ArrayList<MaterialBean> materialBeans1, int resultMode) {
        this.materialBeans = materialBeans1;
        this.resultMode = resultMode;
    }

    public ArrayList<MaterialBean> getMaterialBeans() {
        return materialBeans;
    }

    public int getResultMode() {
        return resultMode;
    }

}

