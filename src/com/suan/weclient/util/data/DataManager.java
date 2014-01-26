package com.suan.weclient.util.data;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.view.View.OnClickListener;

import com.suan.weclient.fragment.ProfileFragment;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.voice.VoiceManager;
import com.suan.weclient.view.SViewPager;
import com.suan.weclient.view.actionbar.CustomMainActionView;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class DataManager {


    private ArrayList<MessageHolder> messageHolders;
    private ArrayList<FansHolder> fansHolders;
    private ArrayList<UserBean> userBeans;

    ArrayList<AutoLoginListener> autoLoginListeners;
    ArrayList<MessageChangeListener> messageChangeListeners;
    ArrayList<ChatItemChangeListener> chatItemChangeListeners;
    ArrayList<FansListChangeListener> fansListChangeListeners;
    ArrayList<ProfileGetListener> profileGetListeners;
    ArrayList<MassDataGetListener> massDataGetListeners;
    ArrayList<LoginListener> loginListeners;
    ArrayList<DialogListener> dialogListeners;
    ArrayList<UserGroupListener> userGroupListeners;

    ArrayList<ChatNewItemGetListener> chatNewItemGetListeners;

    private IWXAPI api;

    private ContentFragmentChangeListener contentFragmentChangeListener;


    /*
     * about chat
     *
     */
    private ChatHolder chatHolder;


    private WechatManager mWechatManager;
    private VoiceManager mVoiceManager;
    private Context mContext;

    public CustomMainActionView customMainActionView;

    private PagerListener pagerListener;
    private TabListener tabListener;

    private SViewPager.ScrollEnableListener scrollEnableListener;


    private ProfileFragment.UserListControlListener userListControlListener;


    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
    // quality is ignored
    // but must be provided
    private ImageCacheManager mImageCacheManager;


    /**
     * * Create the image cache.
     */
    public void createImageCache(Context context) {
        mImageCacheManager = ImageCacheManager.getInstance();

        mImageCacheManager.init(context, context.getPackageCodePath(),
                DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY);
    }

    public ImageCacheManager getCacheManager() {
        return mImageCacheManager;
    }

    public DataManager(Context context) {
        autoLoginListeners = new ArrayList<DataManager.AutoLoginListener>();
        messageChangeListeners = new ArrayList<DataManager.MessageChangeListener>();
        chatItemChangeListeners = new ArrayList<DataManager.ChatItemChangeListener>();
        fansListChangeListeners = new ArrayList<DataManager.FansListChangeListener>();
        profileGetListeners = new ArrayList<DataManager.ProfileGetListener>();
        massDataGetListeners = new ArrayList<MassDataGetListener>();
        loginListeners = new ArrayList<DataManager.LoginListener>();
        dialogListeners = new ArrayList<DataManager.DialogListener>();
        userGroupListeners = new ArrayList<DataManager.UserGroupListener>();
        mContext = context;
        mWechatManager = new WechatManager(this, context);
        mVoiceManager = new VoiceManager(context);

        userBeans = SharedPreferenceManager.getUserGroup(context);
        messageHolders = new ArrayList<MessageHolder>();
        fansHolders = new ArrayList<FansHolder>();
        for (int i = 0; i < userBeans.size(); i++) {
            messageHolders.add(new MessageHolder(userBeans.get(i)));
            fansHolders.add(new FansHolder(userBeans.get(i)));
        }

    }


    public void updateUserGroup() {
        //if add ,set it the first,and autologin
        //if delete ,if delete the first ,autologin

        ArrayList<UserBean> newGroupArrayList = SharedPreferenceManager.getUserGroup(mContext);
        if (newGroupArrayList.size() > userBeans.size()) {
            //when add user
            for (int i = 0; i < newGroupArrayList.size(); i++) {
                boolean exist = false;
                for (int j = 0; j < userBeans.size(); j++) {
                    if (userBeans.get(j).getUserName().equals(newGroupArrayList.get(i).getUserName())) {
                        exist = true;
                    }
                }
                if (!exist) {
                    UserBean newBean = newGroupArrayList.get(i);
                    //add the user to the head
                    messageHolders.add(0, new MessageHolder(newBean));
                    fansHolders.add(0, new FansHolder(newBean));
                    userBeans.add(0, newBean);
                    doAutoLogin();
                }

            }


        } else {
            for (int i = 0; i < userBeans.size(); i++) {
                boolean exist = false;
                for (int j = 0; j < newGroupArrayList.size(); j++) {
                    if (newGroupArrayList.get(j).getUserName().equals(userBeans.get(i).getUserName())) {
                        exist = true;
                    }
                }
                if (!exist) {
                    int deleteIndex = i;
                    userBeans.remove(deleteIndex);
                    messageHolders.remove(deleteIndex);
                    fansHolders.remove(deleteIndex);
                    if (deleteIndex == getCurrentPosition()) {
                        //should relogin
                        this.doAutoLogin();

                    }
                }
            }

        }

        doGroupChangeEnd();


    }

    public WechatManager getWechatManager() {
        return mWechatManager;
    }

    public VoiceManager getVoiceManager() {
        return mVoiceManager;
    }


    public int getCurrentPosition() {
        int currentPosition = SharedPreferenceManager.getCurentIndex(mContext);
        if (currentPosition >= 0 && currentPosition < userBeans.size()) {

            return currentPosition;
        }
        return 0;

    }

    public ArrayList<UserBean> getUserGroup() {
        return userBeans;
    }

    public ArrayList<MessageHolder> getMessageHolders() {
        return messageHolders;
    }

    public ArrayList<FansHolder> getFansHolders() {

        return fansHolders;
    }

    public UserBean getCurrentUser() {
        try {

            return userBeans.get(getCurrentPosition());
        } catch (Exception e) {

        }
        return null;

    }

    public MessageHolder getCurrentMessageHolder() {

        try{

            return messageHolders.get(getCurrentPosition());
        }catch (Exception e){

        }
        return null;

    }

    public FansHolder getCurrentFansHolder() {


        try{

            return fansHolders.get(getCurrentPosition());
        }catch (Exception e){

        }
        return null;
    }

    public UserBean updateUser(int position) {
        if (position >= 0 && position < userBeans.size()) {

            setCurrentPosition(position);
            return userBeans.get(position);
        }
        return null;

    }

    public MessageHolder updateMessageHolder(int position) {
        if (position >= 0 && position < messageHolders.size()) {
            setCurrentPosition(position);

            return messageHolders.get(position);
        }
        return null;

    }


    public ChatHolder getChatHolder() {
        return chatHolder;
    }

    public void createChat(UserBean userBean, String toFakeId) {
        chatHolder = new ChatHolder(userBean, toFakeId);
        chatNewItemGetListeners = new ArrayList<ChatNewItemGetListener>();
    }

    public void setCurrentPosition(int position) {
        SharedPreferenceManager.putCurrentIndex(mContext, position);
    }


    public void addAutoLoginListener(
            AutoLoginListener autoLoginListener) {
        this.autoLoginListeners.add(autoLoginListener);
    }

    public void addMessageChangeListener(
            MessageChangeListener messageChangeListener) {
        this.messageChangeListeners.add(messageChangeListener);
    }

    public void addChatItemChangeListenr(ChatItemChangeListener changeListener) {
        this.chatItemChangeListeners.add(changeListener);
    }

    public void addChatNewItemGetListener(ChatNewItemGetListener chatNewItemGetListener) {
        this.chatNewItemGetListeners.add(chatNewItemGetListener);
    }


    public void addFansListChangeListener(FansListChangeListener fansListChangeListener) {
        this.fansListChangeListeners.add(fansListChangeListener);
    }


    public void addProfileGetListener(ProfileGetListener profileGetListener) {
        this.profileGetListeners.add(profileGetListener);

    }


    public void addMassDataGetListener(MassDataGetListener massDataGetListener) {
        this.massDataGetListeners.add(massDataGetListener);

    }
    public void addLoginListener(LoginListener loginListener) {
        this.loginListeners.add(loginListener);

    }

    public void addUserGroupListener(UserGroupListener userGroupListener) {
        this.userGroupListeners.add(userGroupListener);
    }



    public void setContentFragmentListener(ContentFragmentChangeListener contentFragmentChangeListener) {
        this.contentFragmentChangeListener = contentFragmentChangeListener;

    }

    public void addLoadingListener(DialogListener dialogListener) {
        this.dialogListeners.add(dialogListener);
    }


    public void doAutoLogin() {
        for (int i = 0; i < autoLoginListeners.size(); i++) {
            autoLoginListeners.get(i).autoLogin();
        }
    }

    public void doAutoLoginEnd() {
        for (int i = 0; i < autoLoginListeners.size(); i++) {
            autoLoginListeners.get(i).onAutoLoginEnd();
        }
    }

    public void doProfileGet(UserBean userBean) {
        for (int i = 0; i < profileGetListeners.size(); i++) {
            profileGetListeners.get(i).onGet(userBean);
        }
    }


    public void doMassDataGet(UserBean userBean) {
        for (int i = 0; i < massDataGetListeners.size(); i++) {
            massDataGetListeners.get(i).onGet(userBean);
        }
    }
    public void doMessageGet(boolean changed) {
        for (int i = 0; i < messageChangeListeners.size(); i++) {
            messageChangeListeners.get(i).onMessageGet(changed);
        }
    }

    public void doChatItemGet(boolean changed) {
        for (int i = 0; i < chatItemChangeListeners.size(); i++) {
            chatItemChangeListeners.get(i).onItemGet(changed);
        }
    }

    public void doChatNewItemGet(ArrayList<MessageBean> getMessage, String msgId) {
        for (int i = 0; i < chatNewItemGetListeners.size(); i++) {
            chatNewItemGetListeners.get(i).onChatItemGet(getMessage, msgId);
        }
    }


    public void doFansGet(boolean changed) {
        for (int i = 0; i < fansListChangeListeners.size(); i++) {
            fansListChangeListeners.get(i).onFansGet(changed);
        }
    }

    public void doLoginSuccess(UserBean userBean) {
        for (int i = 0; i < loginListeners.size(); i++) {
            loginListeners.get(i).onLogin(userBean);
        }
    }

    public void deleteUser(int index) {
        for (int i = 0; i < userGroupListeners.size(); i++) {
            userGroupListeners.get(i).deleteUser(index);
        }
    }

    public void doAddUser() {
        for (int i = 0; i < userGroupListeners.size(); i++) {
            userGroupListeners.get(i).onAddUser();
        }
    }


    public void doGroupChangeEnd() {
        for (int i = 0; i < userGroupListeners.size(); i++) {
            userGroupListeners.get(i).onGroupChangeEnd();
        }
    }

    public void doChangeContentFragment(int index) {
        contentFragmentChangeListener.onChange(index);
    }

    public void doLoadingStart(String loadingText) {
        for (int i = 0; i < dialogListeners.size(); i++) {
            dialogListeners.get(i).onLoad(loadingText);
        }
    }


    public void doLoadingEnd() {
        for (int i = 0; i < dialogListeners.size(); i++) {
            dialogListeners.get(i).onFinishLoad();
        }
    }

    public void doPopEnsureDialog(boolean cancelVisible, boolean cancelable, String titleText, DialogSureClickListener dialogSureClickListener) {

        for (int i = 0; i < dialogListeners.size(); i++) {
            dialogListeners.get(i).onPopEnsureDialog(cancelVisible, cancelable, titleText, dialogSureClickListener);
        }
    }


    public void doDismissAllDialog() {
        for (int i = 0; i < dialogListeners.size(); i++) {
            dialogListeners.get(i).onDismissAllDialog();
        }
    }

    public interface AutoLoginListener {
        public void autoLogin();

        public void onAutoLoginEnd();
    }

    public interface MessageChangeListener {
        public void onMessageGet(boolean changed);
    }

    public interface ChatItemChangeListener {
        public void onItemGet(boolean changed);
    }

    public interface ChatNewItemGetListener {
        public void onChatItemGet(ArrayList<MessageBean> getMessage, String msgId);
    }

    public interface FansListChangeListener {
        public void onFansGet(boolean changed);
    }


    public interface ProfileGetListener {
        public void onGet(UserBean userBean);
    }


    public interface MassDataGetListener {
        public void onGet(UserBean userBean);
    }

    public interface LoginListener {
        public void onLogin(UserBean userBean);
    }

    public interface UserGroupListener {
        public void onGroupChangeEnd();

        public void deleteUser(int index);

        public void onAddUser();
    }



    public void setWechatShareApi(IWXAPI api){
        this.api = api;

    }

    public IWXAPI getWechatShareApi(){
        return this.api;
    }

    public interface ContentFragmentChangeListener {
        public void onChange(int index);
    }

    public interface DialogListener {
        public void onLoad(String loaingText);

        public void onFinishLoad();

        public void onPopEnsureDialog(boolean cancelVisible, boolean cancelable, String titleText, DialogSureClickListener dialogSureClickListener);

        public void onDismissAllDialog();

    }

    public interface DialogSureClickListener extends OnClickListener {

    }


    public void setUserListControlListener(ProfileFragment.UserListControlListener userListControlListener) {
        this.userListControlListener = userListControlListener;
    }

    public ProfileFragment.UserListControlListener getUserListControlListener() {
        return this.userListControlListener;
    }

	
	/*
	 * interface about ui
	 */

    public interface PagerListener {
        public void onScroll(int page, double pagePercent);

        public void onPage(int page);

    }

    public interface TabListener {
        public void onClickTab(int page);
    }

    public void setPagerListener(PagerListener pagerListener) {
        this.pagerListener = pagerListener;
    }

    public void setTabListener(TabListener tabListener) {
        this.tabListener = tabListener;
    }

    public PagerListener getPagerListener() {
        return pagerListener;
    }

    public TabListener getTabListener() {
        return tabListener;
    }

    public void setPagerScrollListener(SViewPager.ScrollEnableListener scrollEnableListener){
        this.scrollEnableListener = scrollEnableListener;
    }

    public void setPagerScrollDisableRect(Rect rect){
        if(scrollEnableListener!=null){
            scrollEnableListener.setFaceHolderRect(rect);
        }

    }
}
