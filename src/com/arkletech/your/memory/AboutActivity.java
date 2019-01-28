package com.arkletech.your.memory;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;


public class AboutActivity extends Activity {
	static final String TAG = ">>>> MyAndroidDebugTAG <<<<";
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		
		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		int versionNumber = pinfo.versionCode;
		String versionName = pinfo.versionName;
		
		TextView tv = (TextView) findViewById(R.id.textViewVersion);
		tv.setText("v"+versionName);
		
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(getResources().getString(R.string.about_url));
	}

}
