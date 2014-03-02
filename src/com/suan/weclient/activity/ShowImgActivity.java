package com.suan.weclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.suan.weclient.R;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.umeng.analytics.MobclickAgent;

public class ShowImgActivity extends Activity {

    Dialog loadingDialog;
    private LinearLayout mainLayout;
    private RelativeLayout bgLayout;
    private ImageView contentImageView;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
    // quality is ignored
    // but must be provided
    private ImageCacheManager mImageCacheManager;
    private DataManager mDataManager;

	/*
     *
	 * 12:too much time
	 */

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		/* request no title mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.show_img_layout);
        initImgCache();
        initWidget();
        initData();

        loadImg();
    }

    public void initImgCache() {

        mImageCacheManager = ImageCacheManager.getInstance();
        mImageCacheManager.init(this, this.getPackageCodePath(),
                DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY);
    }

    private void initData() {
        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void loadImg() {
        Bitmap imgBitmap = mImageCacheManager
                .getBitmap(ImageCacheManager.CACHE_MESSAGE_CONTENT + mDataManager.getImgHolder().getMessageBean().getId());
        if (imgBitmap == null) {

            loadingDialog.show();
            mDataManager.getWechatManager().getMessageImg(
                    mDataManager.getCurrentPosition(), mDataManager.getImgHolder().getMessageBean(),
                    contentImageView,
                    WeChatLoader.WECHAT_URL_MESSAGE_IMG_LARGE, new WechatManager.OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        try {
                            Bitmap bitmap = (Bitmap) object;
                            mImageCacheManager
                                    .putBitmap(
                                            ImageCacheManager.CACHE_MESSAGE_CONTENT
                                                    + mDataManager.getImgHolder().getMessageBean().getId(), bitmap, true);
                            contentImageView.setImageBitmap(bitmap);
                            loadingDialog.dismiss();

                        } catch (Exception exception) {

                        }

                    }

                }
            });

        } else {

            contentImageView.setImageBitmap(imgBitmap);

        }

    }

    private void initWidget() {
        loadingDialog = Util.createLoadingDialog(this, "图片加载中", WechatManager.DIALOG_POP_NO);
        mainLayout = (LinearLayout) findViewById(R.id.show_img_layout);
        mainLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                overridePendingTransition(R.anim.search_activity_fly_in,R.anim.search_activity_fly_out);

            }
        });

        bgLayout = (RelativeLayout) findViewById(R.id.show_img_bg_layout);

        contentImageView = (ImageView) findViewById(R.id.show_img_img_content);

    }

}
