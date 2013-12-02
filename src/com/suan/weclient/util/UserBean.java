package com.suan.weclient.util;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class UserBean {

	private JSONObject contentObject;

	public static final int USER_TYPE_SUBSTRICTION = 1;
	public static final int USER_TYPE_SERVICE = 2;

	private int userType = 0;
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
	}

	public UserBean(String userName, String pwd) {
		contentObject = new JSONObject();

		userNameString = userName;
		pwdString = pwd;

		try {

			contentObject.put("username", userName);
			contentObject.put("pwd", pwd);
		} catch (Exception exception) {

		}

	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {

		try {
			contentObject.put("userType", userType);
			this.userType = userType;
		} catch (Exception exception) {

			Log.e("put error", "usertype " + exception);
		}
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
		try {
			contentObject.put("nickname", nickname);
		} catch (Exception exception) {
			Log.e("put error", "nickname" + exception);

		}
	}

	public String getFakeId() {
		return fakeIdString;
	}

	public void setFakeId(String fakeId) {
		fakeIdString = fakeId;

		try {
			contentObject.put("fake_id", fakeId);
		} catch (Exception exception) {

		}
	}

	public String getNewPeople() {
		return newPeopleString;
	}

	public void setNewPeople(String newPeople) {
		newPeopleString = newPeople;

		try {
			contentObject.put("new_people", newPeople);
		} catch (Exception exception) {

			Log.e("put error", "new people " + exception);
		}
	}

	public String getNewMessage() {
		return newMessageString;
	}

	public void setNewMessage(String newMessage) {
		newMessageString = newMessage;

		try {
			contentObject.put("new_message", newMessage);
		} catch (Exception exception) {

			Log.e("put error", "new message " + exception);
		}
	}

	public String getTotalPeople() {
		return totalPeopleString;
	}

	public void setTotalPeople(String totalPeople) {
		totalPeopleString = totalPeople;

		try {
			contentObject.put("total_people", totalPeople);
		} catch (Exception exception) {

		}
	}

	public String getUserName() {
		return userNameString;
	}

	public void setUserName(String userName) {
		userNameString = userName;

		try {
			contentObject.put("username", userName);
		} catch (Exception exception) {

		}
	}

	public String getPwd() {
		return pwdString;
	}

	public void setPwd(String pwd) {
		pwdString = pwd;

		try {

			contentObject.put("pwd", pwd);
		} catch (Exception exception) {

		}
	}

	public String getToken() {
		return tokenString;
	}

	public void setToken(String token) {
		tokenString = token;

		try {

			contentObject.put("token", token);
		} catch (Exception exception) {

		}
	}

	public String getSlaveSid() {
		return slaveSidString;
	}

	public void setSlaveSid(String slaveSid) {
		slaveSidString = slaveSid;

		try {

			contentObject.put("slave_sid", slaveSid);
		} catch (Exception exception) {

		}
	}

	public String getSlaveUser() {
		return slaveUserString;
	}

	public void setSlaveUser(String slaveUser) {
		slaveUserString = slaveUser;

		try {

			contentObject.put("slave_user", slaveUser);
		} catch (Exception exception) {

		}
	}

	public JSONObject getContentObject() {
		return contentObject;
	}

	public String toString() {
		return contentObject.toString();
	}

}
