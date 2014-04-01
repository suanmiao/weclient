package com.suan.weclient.util.data.holder.resultHolder;

import com.suan.weclient.util.data.bean.MessageBean;

import java.util.ArrayList;

/**
 * Created by lhk on 3/16/14.
 */

public class MessageResultHolder {
    private ArrayList<MessageBean> messageBeans;
    private String lastMsgId = "";
    private int messageMode = 0;

    private int resultMode = RESULT_MODE_REFRESH;

    public static final int RESULT_MODE_REFRESH = 2;
    public static final int RESULT_MODE_ADD = 3;

    public MessageResultHolder(ArrayList<MessageBean> messageBeans, String lastMsgId, int messageMode, int resultMode) {
        this.messageBeans = messageBeans;
        this.lastMsgId = lastMsgId;
        this.messageMode = messageMode;
        this.resultMode = resultMode;
    }

    public ArrayList<MessageBean> getMessageBeans(){
        return messageBeans;
    }

    public String getLastMsgId(){
        return lastMsgId;
    }

    public int getMessageMode(){
        return messageMode;
    };

    public int getResultMode(){
        return resultMode;
    }

}

