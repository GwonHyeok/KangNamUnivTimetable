package com.hyeok.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Utils.appUtils;

public class CampusMap extends Activity {
    String CAMPUS_MAP_URL = "https://m.kangnam.ac.kr/knusmart/p000/p131.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        ActionbarInit();
        setContentView(R.layout.campusmap);
        WebView wv = (WebView) findViewById(R.id.campusmap_webView1);
        wv.getSettings().setJavaScriptEnabled(true);
        // 학사 일정때문에 추가 해놓음//
        if (i.getStringExtra("url") != null) {
            CAMPUS_MAP_URL = i.getStringExtra("url");
        }
        // 학사일정 관련 내용 끞//
        wv.loadUrl(CAMPUS_MAP_URL);
        if (!appUtils.NetWorkState(this))
            Toast.makeText(this, getResources().getString(R.string.Network_NotConnect_msg), Toast.LENGTH_SHORT).show();
    }

    private void ActionbarInit() {
        //noinspection ConstantConditions
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(84, 141, 179)));
        getActionBar().setDisplayShowHomeEnabled(false);
    }
}