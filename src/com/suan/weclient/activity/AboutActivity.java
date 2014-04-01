package com.suan.weclient.activity;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.util.text.SpanUtil;

public class AboutActivity extends SherlockActivity {

    /**
     * Called when the activity is first created.
     */

    private ActionBar actionBar;

    private TextView titleTextView;

    private ImageView profileImageView;
    private ImageView backButton;
    private TextView contentTextView;
    private int clickTime = 0;
    private TextView versionTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* request no title mode */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        initActionBar();
        initWidgets();

    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);


        View customActionBarView = layoutInflater.inflate(R.layout.custom_actionbar_back_with_title, null);

        backButton = (ImageView) customActionBarView.findViewById(R.id.actionbar_back_with_title_img_back);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });

        titleTextView = (TextView) customActionBarView.findViewById(R.id.actionbar_back_with_title_text_title);
        titleTextView.setText(getResources().getString(R.string.about));

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customActionBarView, layoutParams);

    }

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    private void initWidgets() {

        versionTextView = (TextView) findViewById(R.id.about_text_version);
        try {
            String versionString = getVersionName();
            versionTextView.setText("V " + versionString);

        } catch (Exception exception) {

        }

        profileImageView = (ImageView) findViewById(R.id.about_img_title);

        profileImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                clickTime++;
                if (clickTime == 20) {
                    String url = "https://www.google.com.hk/#newwindow=1&q=%E8%8D%89%E6%A6%B4&safe=strict";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } else if (clickTime == 19) {
                    Toast.makeText(getApplicationContext(), "你被选中了！再点一次送你草榴邀请码！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        contentTextView = (TextView) findViewById(R.id.about_text_intro);

        String source =
                "<p>&nbsp;&nbsp;公众平台小助手是一款非官方的微信公众平台管理应用，由华中科技大学三名本科生完成。一直以来在手机上对微信公众平台进行管理都是一个令人困扰的问题，我们这样一个工具能够帮您解决一些苦恼。</p>" +
                        "<p><strong>关于您的账户信息</strong></p>" +
                        "<p>公众平台小助手不会将您的帐号信息以任何形式发送到任何服务器上，应用除了用户反馈和检查更新使用友盟的开发者统计工具之外不会和其他服务器有数据交换，用户反馈和检查更新中的数据仅涉及手机机型信息</p>" +
                        "<p>同时，应用是完全开源的，在Github及Gitcafe提供最新开源的版本，让用户以及安全专家去检查确认无安全隐患后自行编译使用。</p>" +
                        "<p><strong>FAQ</strong></p>" +
                        "<p><strong>Q：公众平台助手是如何做到让用户管理微信公众平台的？</strong></p>" +
                        "<p>A：它的原理与使用网页版直接登陆一样，只是将流程以及界面优化得更加适合手机上使用。</p>" +
                        "<p><strong>Q：这款软件和微信公众平台是什么关系？</strong></p>" +
                        "<p>A：公众平台助手是由华中科技大学三名本科生开发，不属于微信官方的产品，但可以作为微信公众平台的一个补充来使用。</p>" +
                        "<p><strong>Q：为什么需要用户输入密码？不可以像微博一样使用OAuth方式吗？</strong></p>" +
                        "<p>A：因为微信官方并没有提供一个微信公众平台的OAuth接口，所以只能通过账号密码来访问。另外，账号信息只会在本地保留（密码使用标准的MD5方式加密），不会上传到任何的服务器。如果对安全性有极端高的要求请不要使用。<br />" +
                        "<p><strong>Q：为什么有时候会出现无法回复消息或者群发消息的现象？</strong></p>" +
                        "<p>A：若微信公众平台升级后本应用无法正常使用请手动检查更新。</p>" +
                        "<p><strong>Q：你们是谁？除了公众平台助手你们还有什么其他的产品吗？</strong></p>" +
                        "<p>A：我们三个都是华中科技大学联创团队的成员。联创团队近期做过的产品包括Fuubo、Tuudo 等。今年六月我们与豌豆荚合作举办了全国首届校园Hackday。PS，联创团队并非一个创业团队，是一个学生技术团队。想要查看更多关于联创的信息请登陆网站：www.hustunique.com</p>" +
                        "<p>如果还有其他的问题，请发送用户反馈，如果需要得到回复，请在反馈内容中留下电子邮件地址噢～<br />" +
                        "<p><strong>Q：为什么做这样一个东西？</strong></p>" +
                        "<p>A：出发点很简单：我们在需要一个可以让我们在手机上管理微信公众平台的东西。一开始我们觉得微信自己迟早会推出，但是没有，所以只有自己来了。做这一切的目的就是让微信公众平台更好用。可以认为我们试图更加完善微信的生态，也因此一些破坏微信生态的事情是我们不愿意做的。" +
                        "同时这也是我们三人开发小组在产品开发上的一次尝试。</p>" +

                        "<p><strong>Personal Thinking</strong></p>" +

                        "<p>&nbsp;&nbsp;如您所见，小组成员在应用的开发和推广中都是无偿参与的，从第一版的简单查看消息功能，到第二版新增的图片和语音消息产看以及用户管理等功能到应用的交互设计的重新布局都是我们三人团队在产品开发中不断学习不断尝试的结果。" +
                        "非常感谢应用发布至今给我们提供宝贵反馈意见的用户,您的意见在开发过程中给了我们很多灵感和动力。因为小组成员都是在校学生的缘故，能够投入到开发中的精力有限，许多用户关于新增功能的建议我们都一一做下了记录但无法在短期内全部实现，如果由于功能的缺乏给您的使用带来不便希望您能够谅解。</p>" +
                        "<br/>谢谢：）</p>";


        contentTextView.setText(Html.fromHtml(source));

        SpannableString spannableString = new SpannableString(contentTextView.getText().toString());

//		SpanUtil.setLinkSpan(spannableString, "@华中科技大学联创团队", new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				String url = "http://weibo.com/uniquestudio";
//				Intent i = new Intent(Intent.ACTION_VIEW);
//				i.setData(Uri.parse(url));
//				startActivity(i);
//				
//				
//			}
//		}, Color.parseColor("#0A8CD2"));

        SpanUtil.setLinkSpan(spannableString, "www.hustunique.com", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "http://www.hustunique.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        }, Color.parseColor("#0A8CD2"));

        SpanUtil.setLinkSpan(spannableString, "Github", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "https://github.com/UniqueStudio/weclient";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }

        }, Color.parseColor("#0A8CD2"));

         SpanUtil.setLinkSpan(spannableString, "Gitcafe", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "https://gitcafe.com/suanmiao/weclient";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        }, Color.parseColor("#379e53"));


        SpanUtil.setBoldSpan(spannableString, "Q：.*");
        SpanUtil.setBoldSpan(spannableString, "FAQ");
        SpanUtil.setBoldSpan(spannableString, "关于您的账户信息");
        SpanUtil.setBoldSpan(spannableString, "Personal Thinking");


        contentTextView.setText(spannableString);
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        contentTextView.setFocusable(false);
        contentTextView.setClickable(false);
    }


}
