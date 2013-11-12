package com.suan.weclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.util.SpanUtil;

public class AboutActivity extends Activity {

	/** Called when the activity is first created. */

	private ImageView profileImageView;
	private ImageButton backButton;
	private TextView contentTextView;
	private int clickTime = 0;
	private TextView versionTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* request no title mode */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		initWidgets();

	}
	 private String getVersionName() throws Exception
	   {
	           // 获取packagemanager的实例
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()是你当前类的包名，0代表是获取版本信息
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	   }

	private void initWidgets() {
		backButton = (ImageButton) findViewById(R.id.about_button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		versionTextView = (TextView)findViewById(R.id.about_text_version);
		try{
			String versionString = getVersionName();
			versionTextView.setText("V "+versionString);
			
		}catch (Exception exception){
			
		}

		profileImageView = (ImageView) findViewById(R.id.about_img_title);

		profileImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickTime++;
				if(clickTime == 20){
					String url = "https://www.google.com.hk/#newwindow=1&q=%E8%8D%89%E6%A6%B4&safe=strict";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					
				}else if(clickTime == 19){
					Toast.makeText(getApplicationContext(), "你被选中了！再点一次送你草榴邀请码！", Toast.LENGTH_SHORT).show();
				}
		
			}
		});
		contentTextView = (TextView) findViewById(R.id.about_text_intro);
		
		String source = 
	"<p>&nbsp;&nbsp;公众平台小助手是一款非官方的微信公众平台管理应用，由华中科技大学三名本科生完成。一直以来在手机上对微信公众平台进行管理都是一个令人困扰的问题，我们这样一个工具能够帮您解决一些苦恼。</p>"+
	"<p><strong>FAQ</strong></p>"+
	"<p><strong>Q：公众平台助手是如何做到让用户管理微信公众平台的？</strong></p>"+
	"<p>A：它的原理与使用网页版直接登陆一样，只是将流程以及界面优化得更加适合手机上使用。</p>"+
	"<p><strong>Q：这款软件和微信公众平台是什么关系？</strong></p>"+
	"<p>A：公众平台助手是由华中科技大学三名本科生开发，不属于微信官方的产品，但可以作为微信公众平台的一个补充来使用。</p>"+
	"<p><strong>Q：为什么需要用户输入密码？不可以像微博一样使用OAuth方式吗？</strong></p>"+
	"<p>A：因为微信官方并没有提供一个微信公众平台的OAuth接口，所以只能通过账号密码来访问。另外，账号信息只会在本地保留（密码使用标准的MD5方式加密），不会上传到任何的服务器。如果对安全性有极端高的要求请不要使用。<br />"+
	"PS，如果有必要，我们会在适当的时候提供一个开源的版本，让用户以及安全专家去检查确认无安全隐患后自行编译使用。</p>"+
	"<p><strong>Q：为什么有时候会出现无法回复消息或者群发消息的现象？</strong></p>"+
	"<p>A：若微信公众平台升级后本应用无法正常使用请手动检查更新。</p>"+
	"<p><strong>Q：为什么做这样一个东西？</strong></p>"+
	"<p>A：出发点很简单：我们在需要一个可以让我们在手机上管理微信公众平台的东西。一开始我们觉得微信自己迟早会推出，但是没有，所以只有自己来了。做这一切的目的就是让微信公众平台更好用。可以认为我们试图更加完善微信的生态，也因此一些破坏微信生态的事情是我们不愿意做的。</p>"+
	"<p><strong>Q：你们会做微信相关的外包吗？</strong></p>"+
	"<p>A：还有更多有趣的事情等着我们去做：）</p>"+
	"<p><strong>Q：你们是谁？除了公众平台助手你们还有什么其他的产品吗？</strong></p>"+
	"<p>A：我们三个都是华中科技大学联创团队的成员。联创团队近期做过的产品包括Fuubo、Tuudo 等。今年六月我们与豌豆荚合作举办了全国首届校园Hackday。PS，联创团队并非一个创业团队，是一个学生技术团队。想要查看更多关于联创的信息请登陆网站：www.hustunique.com</p>"+
	"<p>如果还有其他的问题，请发送用户反馈，如果需要得到回复，请在反馈内容中留下电子邮件地址噢～<br />"+
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
		},  Color.parseColor("#0A8CD2"));
		
		SpanUtil.setBoldSpan(spannableString, "Q：.*");
		SpanUtil.setBoldSpan(spannableString, "FAQ");
		
		
		contentTextView.setText(spannableString);
		contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
		contentTextView.setFocusable(false);
		contentTextView.setClickable(false);
	}
	


}
