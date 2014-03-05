package com.suan.weclient.util.net;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.holder.AppItemHolder;
import com.suan.weclient.util.data.holder.ChatHolder;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.DialogSureClickListener;
import com.suan.weclient.util.data.holder.FansHolder;
import com.suan.weclient.util.data.bean.MessageBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.MaterialHolder;
import com.suan.weclient.util.net.DataParser.ChatListParseCallback;
import com.suan.weclient.util.net.DataParser.FansListParseCallback;
import com.suan.weclient.util.net.DataParser.MessageListParseCallBack;
import com.suan.weclient.util.net.DataParser.MessageResultHolder;
import com.suan.weclient.util.net.DataParser.ParseMassDataCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetFansList;
import com.suan.weclient.util.net.WeChatLoader.WechatGetHeadImgCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetMassData;
import com.suan.weclient.util.net.WeChatLoader.WechatGetMessageImgCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetUserProfleCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetVoiceMsgCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatLoginCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMassCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageListCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessagePageCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageReplyCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageStarCallBack;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class WechatManager {

    public static final int ACTION_SUCCESS = 1;
    public static final int ACTION_TIME_OUT = 2;
    public static final int ACTION_OTHER = 3;
    public static final int ACTION_SPECIFICED_ERROR = 4;


    public static final int GET_NEW_MESSAGE_COUNT_SUCCESS = 0;

    /*
    about pop dialog
     */
    public static final int DIALOG_POP_NOT_CANCELABLE = 3;
    public static final int DIALOG_POP_CANCELABLE = 2;
    public static final int DIALOG_POP_NO = 1;

    public interface OnActionFinishListener {

        public void onFinish(int code, Object object);

    }

    private DataManager mDataManager;
    private Context mContext;

    public WechatManager(DataManager dataManager, Context context) {
        mDataManager = dataManager;
        mContext = context;

    }


    public void login(final int userIndex, final int popDialog,
                      final OnActionFinishListener onActionFinishListener) {

        if (popDialog > DIALOG_POP_NO) {
            mDataManager.doDismissAllDialog();
            mDataManager.doLoadingStart("登录...", popDialog);

        }

        WeChatLoader
                .wechatLogin(

                        new WechatLoginCallBack() {

                            @Override
                            public void onBack(int resultCode, String strResult,
                                               String slaveSid, String slaveUser) {
                                // TODO Auto-generated method stub
                                switch (resultCode) {
                                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                        try {
                                            UserBean nowBean = mDataManager
                                                    .getUserGroup().get(userIndex);
                                            int loginResult = DataParser.parseLogin(
                                                    nowBean, strResult, slaveSid,
                                                    slaveUser, mContext);
                                            nowBean.setSlaveSid(slaveSid);
                                            nowBean.setSlaveUser(slaveUser);
                                            switch (loginResult) {
                                                case DataParser.PARSE_SUCCESS:
                                                    mDataManager.doLoginSuccess(nowBean);

                                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                                    return;

                                                case DataParser.PARSE_FAILED:


                                                    onActionFinishListener.onFinish(ACTION_SPECIFICED_ERROR, null);
                                                    return;

                                            }
                                        } catch (Exception exception) {
                                            Log.e("login parese failed", "" + exception);

                                        }
                                        onActionFinishListener.onFinish(ACTION_OTHER, null);

                                        return;
                                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:


                                        if (popDialog > DIALOG_POP_NO) {

                                            mDataManager.doPopEnsureDialog(true, false,
                                                    "失败", "登录失败 重试?",
                                                    new DialogSureClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated method
                                                            // stub
                                                            login(userIndex, popDialog,
                                                                    onActionFinishListener);

                                                        }
                                                    });

                                        }
                                        return;
                                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:


                                        if (popDialog > DIALOG_POP_NO) {

                                            mDataManager.doPopEnsureDialog(true, false,
                                                    "失败", "登录失败 重试?",
                                                    new DialogSureClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated method
                                                            // stub
                                                            login(userIndex, popDialog,
                                                                    onActionFinishListener);

                                                        }
                                                    });

                                        }
                                        return;
                                }

                                onActionFinishListener.onFinish(ACTION_OTHER, null);

                            }
                        }, mDataManager.getUserGroup().get(userIndex)
                        .getUserName(),
                        mDataManager.getUserGroup().get(userIndex).getPwd(),
                        "", "json"
                );

    }

    public void getUserProfile(final int popDialog, final int userIndex,
                               final OnActionFinishListener onActionFinishListener) {
        if (popDialog > DIALOG_POP_NO) {
            mDataManager.doLoadingStart("获取用户数据...", popDialog);

        }

        WeChatLoader.wechatGetUserProfile(

                new WechatGetUserProfleCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method
                        // stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {
                                    if (popDialog > DIALOG_POP_NO) {

                                        mDataManager.doLoadingStart("解析用户数据...", popDialog);

                                    }
                                    int getUserProfileState = DataParser.parseUserProfile(
                                            strResult,
                                            mDataManager.getUserGroup().get(userIndex));

                                    switch (getUserProfileState) {
                                        case DataParser.GET_USER_PROFILE_SUCCESS:
                                            mDataManager.doProfileGet(mDataManager.getUserGroup()
                                                    .get(userIndex));
                                            onActionFinishListener.onFinish(ACTION_SUCCESS, referer);
                                            return;


                                        case DataParser.GET_USER_PROFILE_FAILED:

                                            break;

                                    }
                                } catch (Exception exception) {

                                }

                                return;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:


                                if (popDialog > DIALOG_POP_NO) {

                                    mDataManager.doPopEnsureDialog(true, false,
                                            "失败", "获取用户信息失败 重试?",
                                            new DialogSureClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    getUserProfile(popDialog, userIndex, onActionFinishListener);

                                                }
                                            });

                                }
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                return;


                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:


                                if (popDialog > DIALOG_POP_NO) {

                                    mDataManager.doPopEnsureDialog(true, false,
                                            "失败", "获取用户信息失败 重试?",
                                            new DialogSureClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    getUserProfile(popDialog, userIndex, onActionFinishListener);

                                                }
                                            });

                                }
                                onActionFinishListener.onFinish(ACTION_OTHER, null);

                                return;

                        }


                    }
                }, mDataManager.getUserGroup().get(userIndex)
        );

    }

    public void getUserImgDirectly(final int popDialog,
                                   final int userIndex, final ImageView userProfileImageView,
                                   final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetUserProfile(

                new WechatGetUserProfleCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method
                        // stub

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                try {
                                    int getUserProfileState = DataParser.parseUserProfile(
                                            strResult,
                                            mDataManager.getUserGroup().get(userIndex));

                                    switch (getUserProfileState) {
                                        case DataParser.GET_USER_PROFILE_SUCCESS:

                                            getUserImgWithReferer(userIndex, popDialog,
                                                    userProfileImageView, onActionFinishListener,
                                                    referer);

                                            break;

                                        case DataParser.GET_USER_PROFILE_FAILED:

                                            break;
                                    }
                                } catch (Exception exception) {

                                    onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                                }
                                return;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);

                                return;
                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                return;

                        }
                    }
                }, mDataManager.getUserGroup().get(userIndex)
        );

    }

    public void getMessageHeadImg(final int userIndex, final String fakeId,
                                  final String referer, final ImageView imageView,
                                  final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetHeadImg(

                new WechatGetHeadImgCallBack() {

                    @Override
                    public void onBack(int resultCode, Bitmap bitmap, String referer,
                                       ImageView imageView) {
                        // TODO Auto-generated method stub

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                if (bitmap != null) {

                                    onActionFinishListener.onFinish(ACTION_SUCCESS, bitmap);
                                    return;
                                } else {
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }

                    }
                }, mDataManager.getUserGroup().get(userIndex), fakeId, referer,
                imageView
        );

    }

    public void getMessageVoice(final int userIndex, final MessageBean messageBean,
                                final int length, final UserBean userBean,
                                final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetVoiceMsg(
                new WechatGetVoiceMsgCallBack() {

                    @Override
                    public void onBack(int resultCode, byte[] bytes) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                if (bytes != null) {

                                    onActionFinishListener.onFinish(ACTION_SUCCESS, bytes);
                                } else {
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }

                    }
                }, userBean, messageBean, length
        );

    }

    public void getMessageImg(final int userIndex, final MessageBean messageBean, final ImageView imageView,
                              final String imgType,
                              final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetMessageImg(new WechatGetMessageImgCallBack() {

            @Override
            public void onBack(int resultCode, Bitmap bitmap, ImageView imageView) {
                // TODO Auto-generated method stub
                switch (resultCode) {

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                            onActionFinishListener.onFinish(ACTION_SUCCESS, bitmap);

                        } else {
                            onActionFinishListener.onFinish(ACTION_OTHER, null);
                        }
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);
                        break;

                }

            }
        }, mDataManager.getUserGroup().get(userIndex), messageBean, imageView,
                WeChatLoader.WECHAT_URL_MESSAGE_IMG_LARGE
        );

    }

    public void getUserImgWithReferer(final int userIndex,
                                      final int popDialog, final ImageView imageView,
                                      final OnActionFinishListener onActionFinishListener,
                                      final String referer) {
        if (popDialog > DIALOG_POP_NO) {
            mDataManager.doLoadingStart("获取用户头像...", popDialog);

        }

        WeChatLoader.wechatGetHeadImg(

                new WechatGetHeadImgCallBack() {

                    @Override
                    public void onBack(int resultCode, Bitmap bitmap, String referer,
                                       ImageView imageView) {

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                bitmap = Util.roundCorner(bitmap, bitmap.getWidth() / 2);

                                if (popDialog > DIALOG_POP_NO) {

                                    mDataManager.doLoadingStart("设置用户头像...", popDialog);
                                }
                                mDataManager.getCacheManager().putBitmap(
                                        ImageCacheManager.CACHE_USER_PROFILE
                                                + mDataManager.getUserGroup()
                                                .get(userIndex).getUserName(),
                                        bitmap, true);
                                if (imageView != null) {
                                    imageView.setImageBitmap(bitmap);
                                }

                                if (popDialog > DIALOG_POP_NO) {

                                    mDataManager.doDismissAllDialog();
                                }
                                onActionFinishListener.onFinish(ACTION_SUCCESS, bitmap);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, bitmap);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, bitmap);
                                break;

                        }

                    }
                }, mDataManager.getUserGroup().get(userIndex), mDataManager
                .getUserGroup().get(userIndex).getFakeId(), referer, imageView
        );

    }

    public void getMassData(final int userIndex, final int popDialog,
                            final OnActionFinishListener onActionFinishListener) {
        if (popDialog > DIALOG_POP_NO) {
            mDataManager.doLoadingStart("获取用户群发数据...", popDialog);

        }

        WeChatLoader.wechatGetMassData(new WechatGetMassData() {

            @Override
            public void onBack(int resultCode, String strResult) {
                // TODO Auto-generated method stub

                switch (resultCode) {

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                        try {
                            if (popDialog > DIALOG_POP_NO) {

                                mDataManager.doLoadingStart("解析用户群发数据...", popDialog);

                            }
                            DataParser.parseMassData(strResult, mDataManager
                                    .getUserGroup().get(userIndex),
                                    new ParseMassDataCallBack() {

                                        @Override
                                        public void onBack(int code, UserBean userBean) {
                                            // TODO Auto-generated method stub
                                            if (code == DataParser.PARSE_SUCCESS) {

                                                mDataManager.doProfileGet(mDataManager
                                                        .getUserGroup().get(userIndex));
                                                if (popDialog > DIALOG_POP_NO) {
                                                    mDataManager.doDismissAllDialog();
                                                }
                                                onActionFinishListener.onFinish(ACTION_SUCCESS, null);

                                            } else {
                                                onActionFinishListener.onFinish(ACTION_OTHER, null);

                                            }

                                        }
                                    });

                        } catch (Exception exception) {
                            Log.e("mass parse error", "" + exception);
                            onActionFinishListener.onFinish(ACTION_OTHER, null);

                        }
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);

                        break;

                }
            }
        }, mDataManager.getUserGroup().get(userIndex)
        );
    }

    public void getNewMessageList(final int popLoadingDialog,
                                  final int userIndex,
                                  final OnActionFinishListener onActionFinishListener) {
        if (popLoadingDialog > DIALOG_POP_NO) {

            mDataManager.doLoadingStart("获取消息数据...", popLoadingDialog);

        }

        WeChatLoader.wechatGetMessageList(

                new WechatMessageListCallBack() {
                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method
                        // stub

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {
                                    if (popLoadingDialog > DIALOG_POP_NO) {
                                        mDataManager.doDismissAllDialog();
                                        mDataManager.doLoadingStart("解析消息数据...", popLoadingDialog);
                                    }

                                    DataParser.parseNewMessage(new MessageListParseCallBack() {

                                        @Override
                                        public void onBack(
                                                MessageResultHolder messageResultHolder,
                                                int code) {
                                            // TODO
                                            // Auto-generated
                                            // method
                                            // stub
                                            switch (code) {
                                                case DataParser.PARSE_SUCCESS:
                                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                                    mDataManager.doLoadingEnd();

                                                    break;
                                                case DataParser.PARSE_FAILED:
                                                    Log.e("get new message","parse failed");
                                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                                    break;
                                                case DataParser.PARSE_SPECIFIC_ERROR:

                                                    onActionFinishListener.onFinish(ACTION_SPECIFICED_ERROR, null);
                                                    break;
                                            }

                                        }
                                    }, strResult, mDataManager.getUserGroup().get(userIndex),
                                            mDataManager.getMessageHolders().get(userIndex),
                                            referer);
                                    mDataManager.doDismissAllDialog();

                                } catch (Exception exception) {
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:


                                if (popLoadingDialog > DIALOG_POP_NO) {

                                    mDataManager.doPopEnsureDialog(true, false,
                                            "失败", "获取消息信息失败， 重试?",
                                            new DialogSureClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    getNewMessageList(popLoadingDialog, userIndex, onActionFinishListener);

                                                }
                                            });

                                }
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }
                    }
                }, mDataManager.getUserGroup().get(userIndex), mDataManager.getMessageHolders().get(userIndex).getNowMessageMode(), "", SharedPreferenceManager.getHideKeyWordMessage(mContext)
        );
    }


    public void getSearchMessageList(final String keyword,
                                     final int userIndex,
                                     final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetMessageList(

                new WechatMessageListCallBack() {
                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method
                        // stub

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {

                                    DataParser.parseNewMessage(new MessageListParseCallBack() {

                                        @Override
                                        public void onBack(
                                                MessageResultHolder messageResultHolder,
                                                int code) {
                                            // TODO
                                            // Auto-generated
                                            // method
                                            // stub
                                            switch (code) {
                                                case DataParser.PARSE_SUCCESS:
                                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                                    mDataManager.doLoadingEnd();
                                                    break;
                                                case DataParser.PARSE_FAILED:

                                                    onActionFinishListener.onFinish(ACTION_OTHER, null);
                                                    break;
                                                case DataParser.PARSE_SPECIFIC_ERROR:

                                                    onActionFinishListener.onFinish(ACTION_SPECIFICED_ERROR, null);
                                                    break;

                                            }

                                        }
                                    }, strResult, mDataManager.getUserGroup().get(userIndex),
                                            mDataManager.getSearchMessageHolder(),
                                            referer);

                                } catch (Exception exception) {
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:


                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }
                    }
                }, mDataManager.getUserGroup().get(userIndex), WeChatLoader.GET_MESSAGE_MODE_SEARCH, keyword, SharedPreferenceManager.getHideKeyWordMessage(mContext)
        );
    }


    public void getMaterialList(final int type, final int begin,
                                final int userIndex,
                                final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetMaterialList(new WeChatLoader.WechatMaterialListCallBack() {
            @Override
            public void onBack(int resultCode, String strResult, String referer) {

                switch (resultCode) {

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                        try {

                            DataParser.parseMaterialList(new DataParser.MaterialListParseCallBack() {
                                @Override
                                public void onBack(int code, MaterialHolder materialHolder) {
                                    switch (code) {
                                        case DataParser.PARSE_SUCCESS:

                                            onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                            break;
                                        case DataParser.PARSE_FAILED:

                                            onActionFinishListener.onFinish(ACTION_OTHER, null);

                                            break;
                                        case DataParser.PARSE_SPECIFIC_ERROR:

                                            onActionFinishListener.onFinish(ACTION_SPECIFICED_ERROR, null);
                                            break;

                                    }


                                }
                            }, strResult, mDataManager.getUserGroup().get(userIndex), mDataManager.getMaterialHolder(), referer);

                        } catch (Exception exception) {
                            onActionFinishListener.onFinish(ACTION_OTHER, null);

                        }
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:


                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);
                        break;

                }

            }
        }, mDataManager.getUserGroup().get(userIndex), type, begin);

    }


    public void getFansList(final int page, final int userIndex,
                            final String groupId,
                            final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetFansList(
                new WechatGetFansList() {

                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                boolean refresh = false;
                                if (page == 0) {

                                    refresh = true;

                                }

                                DataParser.parseFansList(strResult, referer, mDataManager
                                        .getFansHolders().get(userIndex), mDataManager
                                        .getUserGroup().get(userIndex), refresh,
                                        new FansListParseCallback() {

                                            @Override
                                            public void onBack(FansHolder fansHolder,
                                                               boolean dataChanged) {
                                                // TODO Auto-generated method stub

                                                onActionFinishListener.onFinish(ACTION_SUCCESS, dataChanged);

                                            }
                                        });
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }


                    }
                }, mDataManager.getUserGroup().get(userIndex), groupId, page
        );

    }


    public void modifyContacts(final int userIndex, final int position,
                               final int action,
                               final String toFakeId, final String groupId,
                               final String remark,

                               final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatModifyContacts(
                new WeChatLoader.WechatModifyContactsCallBack() {
                    @Override
                    public void onBack(int resultCode, String strResult) {
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                try {
                                    Log.e("edit result", strResult);

                                    JSONObject resultJsonObject = new JSONObject(
                                            strResult);
                                    if (resultJsonObject.get("ret") != null) {
                                        if (Integer.parseInt(resultJsonObject
                                                .get("ret") + "") == WeChatLoader.WECHAT_EDIT_GROUP_OK) {

                                            switch (action) {
                                                case WeChatLoader.MODIFY_CONTACTS_ACTION_REMARK:

                                                    Toast.makeText(mContext, "修改备注成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    break;

                                                case WeChatLoader.MODIFY_CONTACTS_ACTION_MODIFY:

                                                    Toast.makeText(mContext, "修改分组成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    break;
                                            }

                                            onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                            return;

                                        }

                                    }

                                } catch (Exception exception) {
                                    Log.e("star result parse error", "" + exception);
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }


                    }
                }, mDataManager.getUserGroup().get(userIndex), action, groupId, toFakeId, remark
        );

    }

    public void getNextMessageList(final int page, final int userIndex,
                                   final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetMessagePage(

                new WechatMessagePageCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult, String referer) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {

                                    DataParser.parseNextMessage(new MessageListParseCallBack() {

                                        @Override
                                        public void onBack(
                                                MessageResultHolder messageResultHolder,
                                                int code) {
                                            // TODO
                                            // Auto-generated
                                            // method
                                            switch (code) {
                                                case DataParser.PARSE_SUCCESS:

                                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                                    break;
                                                case DataParser.PARSE_FAILED:
                                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                                    break;
                                                case DataParser.PARSE_SPECIFIC_ERROR:

                                                    onActionFinishListener.onFinish(ACTION_SPECIFICED_ERROR, null);
                                                    break;
                                            }


                                        }
                                    }, strResult, mDataManager.getUserGroup().get(userIndex),
                                            mDataManager.getMessageHolders().get(userIndex),
                                            referer);

                                } catch (Exception exception) {
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }

                    }
                }, mDataManager.getMessageHolders().get(userIndex), page, mDataManager.getMessageHolders().get(userIndex).getNowMessageMode(), SharedPreferenceManager.getHideKeyWordMessage(mContext)
        );

    }

    public void getAppMsgList(final int userIndex, final OnActionFinishListener onActionFinishListener) {
        WeChatLoader.wechatGetAppListMsg(new WeChatLoader.WechatGetAppMsgListCallBack() {
            @Override
            public void onBack(int resultCode, String result) {
                switch (resultCode) {

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                        try {
                            DataParser.parseAppMsgList(new DataParser.AppMsgListParseCallBack() {
                                @Override
                                public void onBack(int code, AppItemHolder appItemHolder) {

                                }
                            }, result, mDataManager.getUserGroup().get(userIndex), "");


                        } catch (Exception exception) {
                            onActionFinishListener.onFinish(ACTION_OTHER, null);

                        }
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);
                        break;

                }

            }
        }, mDataManager.getUserGroup().get(userIndex));
    }

    public void getChatList(final int userIndex,
                            final OnActionFinishListener onActionFinishListener) {

        ChatHolder chatHolder = mDataManager.getChatHolder();
        WeChatLoader.wechatGetChatList(
                new WeChatLoader.WechatGetChatList() {

                    @Override
                    public void onBack(int resultCode, String strResult) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                DataParser.parseChatList(strResult,
                                        mDataManager.getChatHolder(),
                                        new ChatListParseCallback() {

                                            @Override
                                            public void onBack(ChatHolder chatHolder,
                                                               boolean dataChanged) {
                                                // TODO Auto-generated method stub

                                                onActionFinishListener.onFinish(ACTION_SUCCESS, dataChanged);

                                            }
                                        });
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }


                    }
                }, chatHolder.getUserBean(), chatHolder.getToFakeId()
        );

    }

    public void singleChat(final UserBean userBean,
                           final MessageBean messageBean,
                           final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatChatSingle(
                new WeChatLoader.WechatChatSingleCallBack() {

                    @Override
                    public void onBack(int resultCode, String result) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                try {
                                    JSONObject resultJsonObject = new JSONObject(result);
                                    JSONObject stateJsonObject = resultJsonObject
                                            .getJSONObject("base_resp");
                                    int ret = DataParser.getRet(stateJsonObject);

                                    if (ret == WeChatLoader.WECHAT_SINGLE_CHAT_OK) {
                                        messageBean.setSendState(MessageBean.MESSAGE_SEND_OK);
                                        onActionFinishListener.onFinish(ACTION_SUCCESS, true);
                                        return;
                                    } else if (ret == WeChatLoader.WECHAT_SINGLE_CHAT_OUT_OF_DATE) {

                                        messageBean.setSendState(MessageBean.MESSAGE_SEND_FAILED_LIMIT_OF_TIME);
                                        onActionFinishListener.onFinish(ACTION_SUCCESS, false);
                                        return;
                                    }else if(ret==WeChatLoader.WECHAT_SINGLE_CHAT_FANS_NOT_RECEIVE){
                                         messageBean.setSendState(MessageBean.MESSAGE_SEND_FAILED_FANS_NOT_RECEIVE);
                                        onActionFinishListener.onFinish(ACTION_SUCCESS, false);
                                        return;

                                    }

                                } catch (Exception exception) {
                                    Log.e("single chat result parse error", "" + exception);

                                }

                                messageBean.setSendState(MessageBean.MESSAGE_SEND_FAILED_LIMIT_OF_TIME);
                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }


                    }
                }, userBean, messageBean
        );

    }

    public void reply(final int userIndex, final MessageBean messageBean,
                      final String replyContent,
                      final OnActionFinishListener onActionFinishListener) {
        WeChatLoader.wechatMessageReply(
                new WechatMessageReplyCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult) {
                        // TODO Auto-generated method stub

                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                try {

                                    // String strResult = EntityUtils.toString(response
                                    // .getEntity());
                                    Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT)
                                            .show();
                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);
                                } catch (Exception exception) {

                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:
                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);

                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }
                    }
                }, mDataManager.getUserGroup().get(userIndex), messageBean, replyContent
        );

    }

    public void star(final int userIndex, final int position,
                     final boolean star,
                     final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatMessageStar(

                new WechatMessageStarCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult) {
                        // TODO Auto-generated method

                        // stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {

                                    JSONObject resultJsonObject = new JSONObject(
                                            strResult);
                                    if (resultJsonObject.get("ret") != null) {
                                        if (Integer.parseInt(resultJsonObject
                                                .get("ret") + "") == WeChatLoader.WECHAT_STAR_OK) {
                                            if (star) {

                                                Toast.makeText(mContext, "加星标成功",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {

                                                Toast.makeText(mContext, "取消星标成功",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            mDataManager.getMessageHolders()
                                                    .get(userIndex).getMessageList()
                                                    .get(position).setStarred(star);
                                            onActionFinishListener.onFinish(ACTION_SUCCESS, null);

                                        }

                                    }

                                } catch (Exception exception) {
                                    Log.e("star result parse error", "" + exception);

                                }
                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }

                    }
                }, mDataManager.getUserGroup().get(userIndex), mDataManager
                .getMessageHolders().get(userIndex).getMessageList()
                .get(position), star
        );
    }

    public void mass(final int userIndex, final String massContent,
                     final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatMass(
                new WechatMassCallBack() {

                    @Override
                    public void onBack(int resultCode, String strResult) {
                        // TODO Auto-generated method stub
                        switch (resultCode) {

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                                try {
                                    mDataManager.doLoadingEnd();

                                    JSONObject resultJsonObject = new JSONObject(strResult);

                                    if (resultJsonObject.get("ret") != null) {

                                        if (Integer.parseInt(resultJsonObject.get("ret") + "") == WeChatLoader.WECHAT_MASS_OK) {
                                            onActionFinishListener.onFinish(ACTION_SUCCESS, null);

                                        } else if (Integer.parseInt(resultJsonObject.get("ret")
                                                + "") == WeChatLoader.WECHAT_MASS_ERROR_ONLY_ONE) {

                                            Log.e("mass only ", "");

                                            mDataManager.doPopEnsureDialog(false, true,
                                                    "哎呀", "每天只能群发一条哦～",
                                                    new DialogSureClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated method stub
                                                            mDataManager.doDismissAllDialog();

                                                        }
                                                    });
                                        }

                                    } else {
                                        mDataManager.doLoadingEnd();
                                    }
                                } catch (Exception exception) {

                                    Log.e("mass result parse exception", "" + exception);
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);
                                }
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }

                    }
                }, mDataManager.getCurrentUser(), massContent
        );
    }


    public void getNewMessageCount(final int userIndex, final String lastMsgId,
                                   final OnActionFinishListener onActionFinishListener) {

        WeChatLoader.wechatGetNewMessageCount(
                new WeChatLoader.WechatGetNewMessageCountCallBack() {
                    @Override
                    public void onBack(int resultCode, String result) {
                        switch (resultCode) {
                            case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                                try {
                                    JSONObject resultObject = new JSONObject(result);

                                    int ret = DataParser.getRet(resultObject);

                                    if (ret == GET_NEW_MESSAGE_COUNT_SUCCESS) {

                                        int newMessageCount = Integer.parseInt(resultObject.get("newTotalMsgCount").toString());
                                        onActionFinishListener.onFinish(ACTION_SUCCESS, newMessageCount);
                                        return;

                                    }
                                } catch (Exception e) {
                                    Log.e("parse new message count error", "" + e);
                                    onActionFinishListener.onFinish(ACTION_OTHER, null);

                                }

                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                                onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                                break;

                            case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                                onActionFinishListener.onFinish(ACTION_OTHER, null);
                                break;

                        }


                    }
                }, mDataManager.getUserGroup().get(userIndex), lastMsgId
        );

    }


    public void getUploadInfo(final int userIndex,
                              final OnActionFinishListener onActionFinishListener) {


        WeChatLoader.wechatGetUploadInfo(new WeChatLoader.WechatGetUploadInfo() {
            @Override
            public void onBack(int resultCode, String strResult) {

                switch (resultCode) {
                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:

                        DataParser.parseUploadInfo(new DataParser.UploadInfoParseCallBack() {
                            @Override
                            public void onBack(int code) {
                                if (code == DataParser.PARSE_SUCCESS) {
                                    onActionFinishListener.onFinish(ACTION_SUCCESS, null);

                                    Log.e("parse upload info success", "success");

                                }

                            }
                        }, strResult, mDataManager.getMessageHolders().get(userIndex).getUploadHelper());
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);
                        break;

                }
            }
        }, mDataManager.getUserGroup().get(userIndex));
    }


    public void uploadImg(final int userIndex, final String filePath,
                          final OnActionFinishListener onActionFinishListener) {


        WeChatLoader.wechatUploadFile(new WeChatLoader.WechatUploadFileCallBack() {
            @Override
            public void onBack(int resultCode, String result) {

                switch (resultCode) {
                    case WeChatLoader.WECHAT_RESULT_MESSAGE_OK:


                        //{"base_resp":{"ret":0,"err_msg":"ok"},"location":"bizfile","type":"image","content":"200009060"}
                        try {

                            JSONObject resultObject = new JSONObject(result);
                            JSONObject retObject = resultObject.getJSONObject("base_resp");
                            int ret = DataParser.getRet(retObject);
                            if (ret == 0) {

                                Gson gson = new Gson();
                                UploadHelper.NowUploadBean nowUploadBean = (UploadHelper.NowUploadBean) gson.fromJson(result, UploadHelper.NowUploadBean.class);
                                mDataManager.getMessageHolders().get(userIndex).getUploadHelper().setNowUploadBean(nowUploadBean);
                                onActionFinishListener.onFinish(ACTION_SUCCESS, null);


                            }


                            return;
                        } catch (Exception e) {

                        }
                        onActionFinishListener.onFinish(ACTION_OTHER, null);

                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_TIMEOUT:

                        onActionFinishListener.onFinish(ACTION_TIME_OUT, null);
                        break;

                    case WeChatLoader.WECHAT_RESULT_MESSAGE_ERROR_OTHER:

                        onActionFinishListener.onFinish(ACTION_OTHER, null);
                        break;

                }
            }
        }, filePath, mDataManager.getUserGroup().get(userIndex),
                mDataManager.getMessageHolders().get(userIndex).getUploadHelper().getTicket());


    }


}

