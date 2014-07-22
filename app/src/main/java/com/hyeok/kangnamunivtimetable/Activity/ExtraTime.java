package com.hyeok.kangnamunivtimetable.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;
import com.hyeok.kangnamunivtimetable.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class ExtraTime extends Activity implements View.OnClickListener {

    private TextView time_tv, class_tv, subject_tv, info_tv, extratime_tv;
    private RelativeLayout relativeLayout;
    private Button cornfirm_btn, share_btn;
    private ImageView main_bg_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mActionBarHeight = ActionBarTransparent();
        setContentView(R.layout.extratime_layout);
        // 설정 프리퍼런스 불러오기
        ControlSharedPref settingepref = new ControlSharedPref(this, "Setting.pref");
        boolean IS_DARK_THEME = settingepref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1;
        // 텍스트뷰 초기화
        time_tv = (TextView) findViewById(R.id.extratime_time_tv);
        class_tv = (TextView) findViewById(R.id.extratime_class_tv);
        subject_tv = (TextView) findViewById(R.id.extratime_subject_tv);
        extratime_tv = (TextView) findViewById(R.id.extratime_extratime_tv);
        info_tv = (TextView) findViewById(R.id.extratime_info_tv);
        relativeLayout = (RelativeLayout) findViewById(R.id.extratime_main_layout);
        main_bg_iv = (ImageView) findViewById(R.id.extratime_bg);
        cornfirm_btn = (Button) findViewById(R.id.extratime_cornfirm_btn);
        share_btn = (Button) findViewById(R.id.extratime_share_btn);
        cornfirm_btn.setOnClickListener(this); // 확인버튼 리스너
        share_btn.setOnClickListener(this);
        // 액션바 오버레이 처리에 의한 padding 설정
        relativeLayout.setPadding(0, mActionBarHeight, 0, 0);
        // 인텐트로 과목 시간 강의실 정보  요일 가져오기.
        Intent intent = new Intent(this.getIntent());
        String class_txt = intent.getStringExtra("class");
        String subject = intent.getStringExtra("subject");
        String time = intent.getStringExtra("time");
        int DayofWeek = intent.getIntExtra("day", 0);
        // 텍스트뷰 텍스트 설정
        time_tv.setText(time);
        class_tv.setText(class_txt);
        subject_tv.setText(subject);
        // 남은시간 설정
        extraTime(DayofWeek);
        // 테마 처리
        if (IS_DARK_THEME) {
            setDarkTheme();
        }
    }

    private void setDarkTheme() {
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.background_main_dark));
        time_tv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        class_tv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        subject_tv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        extratime_tv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        info_tv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        main_bg_iv.setImageResource(R.drawable.bg_clock_dark);
        cornfirm_btn.setBackgroundResource(R.drawable.ic_cornfirm);
        share_btn.setBackgroundResource(R.drawable.ic_share);
    }

    @Override
    public void onClick(View view) {
        int view_id = view.getId();
        if (view_id == cornfirm_btn.getId()) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (view_id == share_btn.getId()) {
            shareExtraTime();
        }
    }

    private void shareExtraTime() {
        /**
         * subject_tv.getText()
         * class_tv.getText()
         * time_tv.getText()
         * 다음수업까지 extratime_tv.getText() 남았습니다.
         */
        String tmp_time_msg = extratime_tv.getText().toString();
        if (tmp_time_msg.contains("0일")) tmp_time_msg = tmp_time_msg.replace("0일", "");
        if (tmp_time_msg.contains("0시간")) tmp_time_msg = tmp_time_msg.replace("0시간", "");
        String msg = String.format(getResources().getString(R.string.EXTRATIME_SHARE_TEXT), subject_tv.getText(), class_tv.getText(), time_tv.getText(), tmp_time_msg);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(i);
    }

    public void extraTime(int week) {
        /**
         *      포지션         투데이
         *        X          토요일 : 7
         *        X          일요일 : 1
         *     월요일 : 0      월요일 : 2
         *     화요일 : 1      화요일 : 3
         *
         */
        try {
            Calendar calendar = Calendar.getInstance();
            // 현재 요일 가져옴.
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            // 남은 요일.
            int extraday = week - (today - 2);
            // 시간 텍스트 뷰에서 시간 받아오기.
            String starttime;
            starttime = time_tv.getText().toString().split(": ")[1].split("-")[0];
            // 시간
            int hour = Integer.parseInt(starttime.split(":")[0].replaceAll(" ", ""));
            // 분
            int minute = Integer.parseInt(starttime.split(":")[1].replaceAll(" ", ""));
            // 캘린더 시간지정
            Calendar calendar1 = Calendar.getInstance();
            //noinspection ResourceType
            calendar1.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)), calendar.get(Calendar.DATE), hour, minute, 0);
            // 남은 시간 가져오기
            long extratimeMills = calendar1.getTimeInMillis() - calendar.getTimeInMillis();
            int etsc = (int) (TimeUnit.MILLISECONDS.toSeconds(extratimeMills));
            int etHour = etsc / 3600;
            int etminute = (etsc - (etHour * 3600)) / 60;

            // 같은 날이지만 시간이 지났을경우.
            if (extratimeMills <= 0 && (today - 2) == week) {
                extraday = 6;
                etHour = 23 - Math.abs(etHour);
                etminute = 60 - Math.abs(etminute);
            } else if (extratimeMills <= 0) {
                extraday = extraday - 1;
                etHour = 23 - Math.abs(etHour);
                etminute = 60 - Math.abs(etminute);
            }
            if (extraday < 0) extraday += 7; // 날짜가 음수일때 +7일을 함
            // 텍스트뷰 텍스트 설정.
            String time = getResources().getString(R.string.EXTRATIME_TEXT);
            time = String.format(time, extraday, etHour, etminute);
            extratime_tv.setText(time);

            // 로그좀 보자.
            Log.d("hour", "" + hour);
            Log.d("minute", "" + minute);
            Log.d("year", "" + calendar.get(Calendar.YEAR));
            Log.d("month", "" + (calendar.get(Calendar.MONTH) + 1));
            Log.d("day", "" + calendar.get(Calendar.DATE));
        } catch (ArrayIndexOutOfBoundsException e) {
            // 공강이거나 수업정보가 문제가 있을때 Exception으로 텍스트 설정.
            extratime_tv.setText("수업이 없습니다.");
        }
    }

    /**
     * 백키를 눌렀을때도 에니메이션 실행.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private int ActionBarTransparent() {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // 액션바 오바레이.
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar))); // 액션바 색상 설정.
        actionBar.setDisplayShowHomeEnabled(false); // 액션바 로고 제거
        View mview = getLayoutInflater().inflate(R.layout.action_bar_only_title, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mview, layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}); // 액션바 크기.
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0); // 액션바 크기.
        styledAttributes.recycle();
        return mActionBarSize;
    }
}
