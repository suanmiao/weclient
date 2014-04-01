package com.suan.weclient.util.data.bean;

public class FansBean {
	
	private String id = "";
	private String nick_name = "";
	private String remark_name = "";
	private int group_id = 0;

    /*
    added in fans profile
     */
    private String fake_id = "";
    private String user_name = "";
    private String signature = "";
    private String city = "";
    private String province = "";
    private String country = "";
    private int gender = 1;

    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 0;

	private String referer = "";



    private int beanType = BEAN_TYPE_USER;
    public  static final int BEAN_TYPE_USER = 2;
    public static final int BEAN_TYPE_DATA = 3;

    public int getBeanType(){
        return beanType;
    }


    public void setBeanType(int beanType){
        this.beanType = beanType;
    }


    public String getFake_id(){
        return fake_id;
    }

    public String getUser_name(){
        return user_name;
    }

    public String getSignature(){
        return signature;
    }
    public String getCity(){
        return city;
    }

    public String getProvince(){
        return province;
    }

    public String getCountry(){
        return country;
    }

    public int getGender(){
        return gender;
    }


	public String getFansId(){
		return id;
	}
	
	public String getNickname (){
		return nick_name;
	}
	
	public String getRemarkName(){
		return remark_name;
	}

    public void setRemarkName(String remarkName){
        this.remark_name = remarkName;
    }
	public int getGoupId(){
		return group_id;
	}

    public void setGroupId(int groupId){
        this.group_id = groupId;
    }
	public void setReferer(String referer){
		this.referer = referer;
	}
	
	public String getReferer(){
		return referer;
	}

}
