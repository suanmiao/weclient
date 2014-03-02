package com.suan.weclient.util.data.bean;

import org.json.JSONObject;

import android.util.Log;

public class UserBean {

    private JSONObject contentObject;

    public static final int USER_TYPE_SUBSTRICTION = 1;
    public static final int USER_TYPE_SERVICE = 2;
    public static final int USER_TYPE_NOT_INITED = -1;
    public static final int USER_TYPE_NONE = 0;

    private int userType = -1;
    private String nickNameString = "";
    private int massLeft = 0;
    private String userNameString = "";
    private String pwdString = "";
    private String tokenString = "";
    private String slaveSidString = "";
    private String slaveUserString = "";
    private String newMessageString = "";
    private String newPeopleString = "";
    private String totalPeopleString = "";
    private String fakeIdString = "";


    /*
    for message push
     */
    private String lastMsgId = "";

    public UserBean(JSONObject contentJsonObject) {
        this.contentObject = contentJsonObject;

        try {
            String valueString = contentJsonObject.getString("nickname");
            if (valueString != null) {
                nickNameString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "nickname" + exception);

        }

        try {

            userType = contentJsonObject.getInt("userType");

        } catch (Exception exception) {
            Log.e("user bean parse error", "userType" + exception);

        }

		/*
         * for the version transition
		 */
        if (userType == USER_TYPE_NOT_INITED) {
            try {

                contentJsonObject.put("userType", USER_TYPE_NONE);

            } catch (Exception exception) {

            }

        }

        try {
            String valueString = contentJsonObject.getString("new_message");
            if (valueString != null) {
                newMessageString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "new message" + exception);

        }

        try {
            String valueString = contentJsonObject.getString("fake_id");
            if (valueString != null) {
                fakeIdString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "fake id" + exception);

        }

        try {
            String valueString = contentJsonObject.getString("new_people");
            if (valueString != null) {
                newPeopleString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "new people" + exception);

        }

        try {
            String valueString = contentJsonObject.getString("total_people");
            if (valueString != null) {
                totalPeopleString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "total people" + exception);

        }
        try {
            String valueString = contentJsonObject.getString("username");
            if (valueString != null) {
                userNameString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "user name" + exception);

        }

        try {
            String valueString = contentJsonObject.getString("pwd");
            if (valueString != null) {
                pwdString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "pwd" + exception);

        }
        try {
            String valueString = contentJsonObject.getString("token");
            if (valueString != null) {
                tokenString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "token" + exception);

        }
        try {
            String valueString = contentJsonObject.getString("slave_sid");
            if (valueString != null) {
                slaveSidString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "slave sid" + exception);

        }
        try {
            String valueString = contentJsonObject.getString("slave_user");
            if (valueString != null) {
                slaveUserString = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "slave user" + exception);

        }

        try {

            String valueString = contentJsonObject.getString("last_msg_id");
            if (valueString != null) {
                lastMsgId = valueString;
            }

        } catch (Exception exception) {
            Log.e("user bean parse error", "last msg id" + exception);

        }


    }

    public UserBean(String userName, String pwd) {
        contentObject = new JSONObject();

        userNameString = userName;
        pwdString = pwd;


    }


    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;

    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {


        this.userType = userType;

    }

    public int getMassLeft() {
        return massLeft;
    }

    public void setMassLeft(int massLeft) {

        this.massLeft = massLeft;
    }

    public String getNickname() {
        return nickNameString;
    }

    public void setNickname(String nickname) {

        nickNameString = nickname;

    }

    public String getFakeId() {
        return fakeIdString;
    }

    public void setFakeId(String fakeId) {
        fakeIdString = fakeId;

    }

    public String getNewPeople() {
        return newPeopleString;
    }

    public void setNewPeople(String newPeople) {
        newPeopleString = newPeople;


    }

    public String getNewMessage() {
        return newMessageString;
    }

    public void setNewMessage(String newMessage) {
        newMessageString = newMessage;


    }

    public String getTotalPeople() {
        return totalPeopleString;
    }

    public void setTotalPeople(String totalPeople) {
        totalPeopleString = totalPeople;


    }

    public String getUserName() {
        return userNameString;
    }

    public void setUserName(String userName) {
        userNameString = userName;


    }

    public String getPwd() {
        return pwdString;
    }

    public void setPwd(String pwd) {
        pwdString = pwd;


    }

    public String getToken() {
        return tokenString;
    }

    public void setToken(String token) {
        tokenString = token;


    }

    public String getSlaveSid() {
        return slaveSidString;
    }

    public void setSlaveSid(String slaveSid) {
        slaveSidString = slaveSid;


    }

    public String getSlaveUser() {
        return slaveUserString;
    }

    public void setSlaveUser(String slaveUser) {
        slaveUserString = slaveUser;


    }

    public JSONObject getContentObject() {
        try {

            contentObject.put("username", userNameString);
            contentObject.put("pwd", pwdString);
        } catch (Exception exception) {

        }
        try {
            contentObject.put("last_msg_id", lastMsgId);
        } catch (Exception exception) {

            Log.e("put error", "last msg id " + exception);
        }
        try {
            contentObject.put("userType", userType);
        } catch (Exception exception) {

            Log.e("put error", "usertype " + exception);
        }

        try {
            contentObject.put("nickname", nickNameString);
        } catch (Exception exception) {
            Log.e("put error", "nickname" + exception);

        }
        try {
            contentObject.put("fake_id", fakeIdString);
        } catch (Exception exception) {

        }
        try {
            contentObject.put("new_people", newPeopleString);
        } catch (Exception exception) {

            Log.e("put error", "new people " + exception);
        }
        try {
            contentObject.put("new_message", newMessageString);
        } catch (Exception exception) {

            Log.e("put error", "new message " + exception);
        }
        try {
            contentObject.put("total_people", totalPeopleString);
        } catch (Exception exception) {

        }
        try {
            contentObject.put("username", userNameString);
        } catch (Exception exception) {

        }
        try {

            contentObject.put("pwd", pwdString);
        } catch (Exception exception) {

        }
        try {

            contentObject.put("token", tokenString);
        } catch (Exception exception) {

        }
        try {

            contentObject.put("slave_sid", slaveSidString);
        } catch (Exception exception) {

        }
        try {

            contentObject.put("slave_user", slaveUserString);
        } catch (Exception exception) {

        }
        return contentObject;
    }

    public String toString() {
        return contentObject.toString();
    }

}
