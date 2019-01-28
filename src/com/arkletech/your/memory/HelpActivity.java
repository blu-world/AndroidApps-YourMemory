package com.arkletech.your.memory;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class HelpActivity extends Activity {
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help);
		
		webview = (WebView) findViewById(R.id.webViewHelp);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(getResources().getString(R.string.help_url));
	}
}
