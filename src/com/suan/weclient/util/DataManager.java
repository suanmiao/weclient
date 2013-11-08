package com.suan.weclient.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.view.View.OnClickListener;

import com.suan.weclient.util.net.images.ImageCacheManager;

public class DataManager {

	private ArrayList<MessageHolder> messageHolders;
	private ArrayList<UserBean> userBeans;
	ArrayList<MessageChangeListener > messageChangeListeners ;
	ArrayList<ProfileGetListener> profileGetListeners;
	ArrayList<LoginListener> loginListeners;
	private ContentFragmentChangeListener contentFragmentChangeListener;
	ArrayList<DialogListener> dialogListeners ;
	ArrayList<UserGroupListener> userGroupListeners;
	private int currentPosition = 0;
	
	
	private WechatManager mWechatManager;
	private Context mContext;
	
	
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
	
	public ImageCacheManager getCacheManager(){
		return mImageCacheManager;
	}

	public DataManager(Context context) {
		messageChangeListeners = new ArrayList<DataManager.MessageChangeListener>();
		profileGetListeners = new ArrayList<DataManager.ProfileGetListener>();
		loginListeners = new ArrayList<DataManager.LoginListener>();
		dialogListeners = new ArrayList<DataManager.DialogListener>();
		userGroupListeners = new ArrayList<DataManager.UserGroupListener>();
		mContext = context;
		mWechatManager = new WechatManager(this, context);

		userBeans = SharedPreferenceManager.getUserGroup(context);
		messageHolders = new ArrayList<MessageHolder>();
		for (int i = 0; i < userBeans.size(); i++) {
			messageHolders.add(new MessageHolder(userBeans.get(i)));
		}
	}
	
	public void updateUserGroup(){
		
		userBeans = SharedPreferenceManager.getUserGroup(mContext);
	}
	
	public WechatManager getWechatManager(){
		return mWechatManager;
	}
	
	

	
	public int getCurrentPosition(){
		return currentPosition;
	}

	public ArrayList<UserBean> getUserGroup() {
		return userBeans;
	}

	public ArrayList<MessageHolder> getMessageHolders() {
		return messageHolders;
	}

	public UserBean getCurrentUser() {
		if (currentPosition >= 0 && currentPosition < userBeans.size()) {

			return userBeans.get(currentPosition);
		}
		return null;

	}

	public MessageHolder getCurrentMessageHolder() {
		if (currentPosition >= 0 && currentPosition < messageHolders.size()) {

			return messageHolders.get(currentPosition);
		}
		return null;

	}

	public UserBean updateUser(int position) {
		if (position >= 0 && position < userBeans.size()) {

			currentPosition = position;
			return userBeans.get(position);
		}
		return null;

	}

	public MessageHolder updateMessageHolder(int position) {
		if (position >= 0 && position < messageHolders.size()) {
			currentPosition = position;

			return messageHolders.get(position);
		}
		return null;

	}

	public void setCurrentPosition(int position) {
		currentPosition = position;
	}

	public void addMessageChangeListener(
			MessageChangeListener messageChangeListener) {
		this.messageChangeListeners.add(messageChangeListener);
	}

	public void addProfileGetListener(ProfileGetListener profileGetListener) {
		this.profileGetListeners.add(profileGetListener);

	}

	public void addLoginListener(LoginListener loginListener) {
		this.loginListeners.add(loginListener);

	}
	
	public void addUserGroupListener(UserGroupListener userGroupListener){
		this.userGroupListeners.add(userGroupListener);
	}
	
	
	public void setContentFragmentListener(ContentFragmentChangeListener contentFragmentChangeListener){
		this.contentFragmentChangeListener = contentFragmentChangeListener;
		
	}
	
	public void addLoadingListener(DialogListener dialogListener){
		this.dialogListeners.add(dialogListener);
	}

	public void doProfileGet(UserBean userBean) {
		for(int i = 0;i<profileGetListeners.size();i++){
			profileGetListeners.get(i).onGet(userBean);
		}
	}

	public void doMessageGet(MessageHolder messageHolder) {
		for(int i = 0;i<messageChangeListeners.size();i++){
			messageChangeListeners.get(i).onChange(messageHolder);
		}
	}

	public void doLoginSuccess(UserBean userBean) {
		for(int i = 0;i<loginListeners.size();i++){
			loginListeners.get(i).onLogin(userBean);
		}
	}
	
	public void doGroupChange(){
		for(int i =0;i<userGroupListeners.size();i++){
			userGroupListeners.get(i).onGroupChange();
		}
	}
	
	
	public void doChangeContentFragment(int index){
		contentFragmentChangeListener.onChange(index);
	}
	
	public void doLoadingStart(String loadingText){
		for(int i = 0;i<dialogListeners.size();i++){
			dialogListeners.get(i).onLoad(loadingText);
		}
	}


	public void doLoadingEnd(){
		for(int i = 0;i<dialogListeners.size();i++){
			dialogListeners.get(i).onFinishLoad();
		}
	}
	
	public void doPopEnsureDialog(boolean cancelVisible,boolean cancelable,String titleText,DialogSureClickListener dialogSureClickListener){
		
		for(int i = 0;i<dialogListeners.size();i++){
			dialogListeners.get(i).onPopEnsureDialog(cancelVisible,cancelable,titleText, dialogSureClickListener);
		}
	}
	

	public void doDismissAllDialog(){
		for(int i = 0;i<dialogListeners.size();i++){
			dialogListeners.get(i).onDismissAllDialog();
		}
	}
	
	public interface MessageChangeListener {
		public void onChange(MessageHolder nowHolder);
	}

	public interface ProfileGetListener {
		public void onGet(UserBean userBean);
	}

	public interface LoginListener {
		public void onLogin(UserBean userBean);
	}
	
	public interface UserGroupListener{
		public void onGroupChange();
	}
	
	
	public interface ContentFragmentChangeListener{
		public void onChange(int index);
	}
	
	public interface DialogListener{
		public void onLoad(String loaingText);
		
		public void onFinishLoad();
		
		public void onPopEnsureDialog(boolean cancelVisible,boolean cancelable,String titleText,DialogSureClickListener dialogSureClickListener);
		
		public void onDismissAllDialog();
		
	}
	
	public interface DialogSureClickListener extends OnClickListener{
		
	}
}
