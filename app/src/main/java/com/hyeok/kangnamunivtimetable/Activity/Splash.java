package com.hyeok.kangnamunivtimetable.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;

public class Splash extends ActionBarActivity {

    @Override
    public void onConfigurationChanged(Configuration cfg) {
        super.onConfigurationChanged(cfg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
//        setBackGround(findViewById(R.id.splash_imageview), R.drawable.loading);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };
        ControlSharedPref pref = new ControlSharedPref(this, null);
        if (pref.getValue("name", null) != null) {
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

    private void setBackGround(View view, int resid) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 2;
        options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resid, options);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }
}
