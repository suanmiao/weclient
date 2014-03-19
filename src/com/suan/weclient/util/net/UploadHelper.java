package com.suan.weclient.util.net;

/**
 * Created by lhk on 2/9/14.
 */
public class UploadHelper {
    private String ticket;
    private NowUploadBean nowUploadBean;

    public UploadHelper() {

    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

    public void setNowUploadBean(NowUploadBean nowUploadBean){
        this.nowUploadBean = nowUploadBean;
    }

    public NowUploadBean getNowUploadBean(){
        return this.nowUploadBean;
    }

    public class NowUploadBean {
        private String location = "";
        private String type = "";
        private String content = "";

        public String getLocation() {
            return location;
        }

       public String getType() {
            return type;
        }

        public String getContent() {
            return content;
        }


    }
}
