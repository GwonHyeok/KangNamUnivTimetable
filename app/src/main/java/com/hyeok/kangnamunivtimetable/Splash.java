package com.hyeok.kangnamunivtimetable;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Splash extends Activity {

	@Override
	public void onConfigurationChanged(Configuration cfg) {
		super.onConfigurationChanged(cfg);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				finish();
			}
		};
		ControlSharedPref pref = new ControlSharedPref(this,null);
		if ( pref.getValue("name", null) != null ){
		handler.sendEmptyMessageDelayed(0, 1500);
		} else {
		final Intent i = new Intent(this, login.class);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(i);
				finish();
			}
		}, 1000);
		}
	}
}
