package com.hyeok.kangnamunivtimetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class CampusMap extends Activity {
	String CAMPUS_MAP_URL="https://m.kangnam.ac.kr/knusmart/p000/p131.jsp";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		getActionBar().setSubtitle(this.getResources().getString(R.string.CAMPUS_MAP_WEBVIEW_TITLE));
		setContentView(R.layout.campusmap);
		WebView wv = (WebView)findViewById(R.id.campusmap_webView1);
		wv.getSettings().setJavaScriptEnabled(true);
		// 학사 일정때문에 추가 해놓음//
		if (i.getStringExtra("url") != null) {
			CAMPUS_MAP_URL = i.getStringExtra("url");
			getActionBar().setSubtitle(this.getResources().getString(R.string.UNIV_SCHEDULE_WEBVIEW_TITLE));
			}
		// 학사일정 관련 내용 끞//
		wv.loadUrl(CAMPUS_MAP_URL);
        if (!Utils.NetWorkState(this)) Toast.makeText(this, getResources().getString(R.string.Network_NotConnect_msg), Toast.LENGTH_SHORT).show();
	}

}
