package com.heibuddy.xiaohuoband;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.heibuddy.xiaohuoband.R;

public class WebViewActivity extends Activity {

	private WebView wv;
	private ImageButton btn_back;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		initViews();
	}

	private void initViews() {
		wv = (WebView)findViewById(R.id.webview);
		btn_back=(ImageButton)findViewById(R.id.btn_webview_back);
		url=getIntent().getStringExtra("url");
//		url=new String("http://m.baidu.com");
//		System.out.println(url);
		wv.setWebViewClient(new WebViewClient(){});
		wv.loadUrl(url);
		
		btn_back.setOnClickListener(backListener);
	}
	
	
	private OnClickListener backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			WebViewActivity.this.setResult(RESULT_OK);
			WebViewActivity.this.finish();
		}
	};
	
//	private OnClickListener viewSourceListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			//Uri sourceURI=Uri.parse("http://news.xinmin.cn/shehui/2013/04/19/19822290.html");
//			Uri sourceURI=Uri.parse(getIntent().getStringExtra("content_url"));
//			Intent intent=new Intent(Intent.ACTION_VIEW,sourceURI);
//			startActivity(intent);
//			
//		}
//	};
//	private OnClickListener listener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			NewsContentActivity.this.setResult(RESULT_OK);
//			NewsContentActivity.this.finish();
//		}
//	};
//
//	public void onBackPressed() {
//		if (webView.canGoBack()) {
//			webView.goBack();
//		} else {
//			NewsContentActivity.this.setResult(RESULT_OK);
//			NewsContentActivity.this.finish();
//		}
//	};
//}
}