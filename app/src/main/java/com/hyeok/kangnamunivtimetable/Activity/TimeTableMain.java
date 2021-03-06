package com.hyeok.kangnamunivtimetable.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Ui.NavigationDrawerAdapter;
import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;
import com.hyeok.kangnamunivtimetable.Utils.GetTimetableUtils;
import com.hyeok.kangnamunivtimetable.Utils.appUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeTableMain extends ActionBarActivity implements
        OnPageChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView ddaytv, extratimetv, MemoPrevewText, TV_MAIN_MEMO_TITLE, TV_MAIN_ALARM_TITLE, TV_MAIN_DDAY_TITLE;
    private LinearLayout LR_MEMO, LR_ALARM, LR_DDAY, LR_TV_MAIN;
    private ImageView IV_MAIN_MEMO, IV_MAIN_ALARM, IV_MAIN_DDAY;
    private RelativeLayout LR_MAIN;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private int currentColor = 0xFF666666;
    private boolean m_close_flag = false, extra_thread_check = true, IS_DARK_THEME = false, EXTRATIME_TMP_FLAG = true;
    private Handler extratime = null;
    private ControlSharedPref MemoDate = new ControlSharedPref(TimeTableMain.this,
            "MemoDate");
    ControlSharedPref timetablepref = new ControlSharedPref(this,
            "timetable.pref");
    ControlSharedPref settingepref = new ControlSharedPref(this, "Setting.pref");
    ControlSharedPref shuttlepref = new ControlSharedPref(this, "shuttlebus.pref");
    ControlSharedPref pref = new ControlSharedPref(this, null);
    private long extratime_mills;
    private int ThemeListIndex, StyleListIndex;
    private Thread extratimeThread;
    static boolean ShowSplash = true;

    /*
     *  Gcm
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "608725707278", regid; // GCM SENDER ID
    GoogleCloudMessaging gcm;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ShowSplash) {
            startActivity(new Intent(this, login.class));
            ShowSplash = false;
        }
        if (pref.getValue("name", "null").equals("null"))
            finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_main);
        viewInit();
        actionbarInit();
        SetPreviewMemo();


        if (pref.getValue("name", null) != null
                && timetablepref.getValue("time1", null) != null) {
            setDDayTextView();

            // 디데이 텍스트뷰 누르면 학사일정 보여주기
            ddaytv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), CampusMap.class);
                    i.putExtra("url", "https://m.kangnam.ac.kr/knusmart/p000/p121.jsp");
                    startActivity(i);
                }
            });

            extratimetv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("KKT", "Click");
                    changeView();
                }
            });

            // 시험 당일 DDAY 처리
            if (ddaytv.getText().toString().contains("D0")) {
                ddaytv.setText("D-Day");
            }
            //남은 시간 텍스트 설정하기.
            SetExtraTime();

            // 테마 처리
            IS_DARK_THEME = settingepref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1;
            if (IS_DARK_THEME) {
                SetDarkTheme();
            }
        }
        tabs.invalidate();
        tabs.refreshDrawableState();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getApplicationContext());

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i("K_TIME_GCM", "No valid Google Play Services APK found.");
        }

    }

    @SuppressWarnings("ResourceType")
    private void setDDayTextView() {
        //DDAY 설정
        Calendar cl = Calendar.getInstance();
        Calendar examcalendar = Calendar.getInstance();
//        int T_Month = cl.get(Calendar.MONTH) + 1;
//        int T_Day = Integer.parseInt(new SimpleDateFormat("dd").format(cl.getTime()));
        int T_Year = cl.get(Calendar.YEAR);
        String MiddleTime = pref.getValue(login.MIDDLE_EXAM_PREF, null);
        String FinalTime = pref.getValue(login.FINAL_EXAM_PREF, null);

        if (MiddleTime != null || FinalTime != null) {
            assert MiddleTime != null;
            MiddleTime = MiddleTime.split("~")[0];
            FinalTime = FinalTime.split("~")[0];
            int MiddleSmonth = Integer.parseInt(MiddleTime.split("~")[0].substring(0, 2));
            int MiddleSday = Integer.parseInt(MiddleTime.split("~")[0].substring(3, 5));
            int FinalSmonth = Integer.parseInt(FinalTime.split("~")[0].substring(0, 2));
            int FinalSday = Integer.parseInt(FinalTime.split("~")[0].substring(3, 5));
            String DDAY_MSG;
            examcalendar.set(T_Year, MiddleSmonth - 1, MiddleSday);

            if (cl.getTimeInMillis() <= examcalendar.getTimeInMillis()) {
                //중간고사 볼드채 사용
                DDAY_MSG = getResources().getString(R.string.MIDDLE_EXAM) + " " + getResources().getString(R.string.dday_text) + String.valueOf(caldate(Calendar.getInstance().get(Calendar.YEAR), MiddleSmonth - 1, MiddleSday));
                ddaytv.setText(DDAY_MSG);
                if (examcalendar.getTimeInMillis() < cl.getTimeInMillis() && cl.getTimeInMillis() <= examcalendar.getTimeInMillis() + 604800000) {
                    ddaytv.setText(getResources().getString(R.string.DDAY_TIME_TO_EXAM));
                }
            } else {
                examcalendar.set(T_Year, FinalSmonth - 1, FinalSday);
                DDAY_MSG = getResources().getString(R.string.FINAL_EXAM) + " " + getResources().getString(R.string.dday_text) + String.valueOf(caldate(Calendar.getInstance().get(Calendar.YEAR), FinalSmonth - 1, FinalSday));
                ddaytv.setText(DDAY_MSG);
                if (examcalendar.getTimeInMillis() < cl.getTimeInMillis() && cl.getTimeInMillis() <= examcalendar.getTimeInMillis() + 604800000) {
                    ddaytv.setText(getResources().getString(R.string.DDAY_TIME_TO_EXAM));
                }
            }
        } else {
            String msg = getResources().getString(R.string.NO_EXAM_DATA);
            ddaytv.setText(msg);
        }
    }

    private void actionbarInit() {
        //Hide Icon
        //Custom ActionBar
        ActionBar actionbar = getActionBar();
        assert actionbar != null;
        actionbar.setTitle("");
        actionbar.setDisplayShowHomeEnabled(false);
        View mview = getLayoutInflater().inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionbar.setCustomView(mview, layout);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_main)));

        ImageButton ActionBarDrawer_btn = (ImageButton) mview.findViewById(R.id.ActionBar_drawerbutton);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);
        ActionBarDrawer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                View drawerView = findViewById(R.id.drawerView);
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(drawerView);
                }

            }
        });
    }

    private void viewInit() {
        // View Initialize
        LR_MEMO = (LinearLayout) findViewById(R.id.MAIN_MEMO_LAYOUT);
        LR_ALARM = (LinearLayout) findViewById(R.id.MAIN_ALARM_LAYOUT);
        LR_DDAY = (LinearLayout) findViewById(R.id.MAIN_DDAY_LAYOUT);
        LR_MAIN = (RelativeLayout) findViewById(R.id.MAIN_LAYOUT);
        LR_TV_MAIN = (LinearLayout) findViewById(R.id.Text_View);
        TV_MAIN_MEMO_TITLE = (TextView) findViewById(R.id.Main_Memo_Title);
        TV_MAIN_ALARM_TITLE = (TextView) findViewById(R.id.Main_Alarm_Title);
        TV_MAIN_DDAY_TITLE = (TextView) findViewById(R.id.Main_Dday_Title);
        IV_MAIN_MEMO = (ImageView) findViewById(R.id.Main_Memo_Icon);
        IV_MAIN_ALARM = (ImageView) findViewById(R.id.Main_Alarm_Icon);
        IV_MAIN_DDAY = (ImageView) findViewById(R.id.Main_Dday_Icon);
        MemoPrevewText = (TextView) findViewById(R.id.Main_Memo_Preview);
        ddaytv = (TextView) findViewById(R.id.dday_textview);
        extratimetv = (TextView) findViewById(R.id.Extra_Time_Textview);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
                        .getDisplayMetrics()
        );
        pager.setPageMargin(pageMargin);
        tabs.setTextColor(getResources().getColor(R.color.fontcolor_main));
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);

        currentColor = getCurrentColor(this, pager.getCurrentItem());
        changeColor(currentColor);

        /*
         * Memo Click Listener
		 */
        MemoPrevewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(arg0.getContext(), Memo.class));
            }
        });

        /*
        * Set TimeTable Type
        */
        if (settingepref.getValue("ttbtype", 0) != 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.MainLayout, new FullTimetableFragment()).commit();
        }

        /*
         * Navigation Drawer View Init
         */
        ListView mDrawerListview = (ListView) findViewById(R.id.drawer_listview);
        NavigationDrawerAdapter drawerAdapter = new NavigationDrawerAdapter(this, android.R.layout.simple_list_item_1, getDrawerList());
        mDrawerListview.setAdapter(drawerAdapter);
        TextView mDrawerShare = (TextView) findViewById(R.id.drawer_share);
        TextView mDrawerSetting = (TextView) findViewById(R.id.drawer_setting);
        TextView mDrawerStudentName = (TextView) findViewById(R.id.drawer_studentname);
        ImageView mDrawerRefresh = (ImageView) findViewById(R.id.drawer_refresh);
        mDrawerStudentName.setText(getStudentName());
        mDrawerSetting.setOnClickListener(this);
        mDrawerShare.setOnClickListener(this);
        mDrawerRefresh.setOnClickListener(this);
        mDrawerListview.setOnItemClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("CutPasteId")
    private void SetDarkTheme() {
        if (Build.VERSION.SDK_INT >= 16) {
            LR_MEMO.setBackground(getResources().getDrawable(R.drawable.textview_border_dark));
            LR_ALARM.setBackground(getResources().getDrawable(R.drawable.textview_border_dark));
            LR_DDAY.setBackground(getResources().getDrawable(R.drawable.textview_border_dark));
        } else {
            LR_MEMO.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_border_dark));
            LR_ALARM.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_border_dark));
            LR_DDAY.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_border_dark));
        }

        TV_MAIN_MEMO_TITLE.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        TV_MAIN_ALARM_TITLE.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        TV_MAIN_DDAY_TITLE.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        TV_MAIN_MEMO_TITLE.setCompoundDrawablePadding(4);
        MemoPrevewText.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        ddaytv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        extratimetv.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        LR_MAIN.setBackgroundColor(getResources().getColor(R.color.background_main_dark));
        LR_TV_MAIN.setBackgroundColor(getResources().getColor(R.color.background_main_dark));
        tabs.setBackgroundColor(getResources().getColor(R.color.background_main_dark));
        tabs.setDividerColorResource(R.color.background_main_dark);
        tabs.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        View mview = getLayoutInflater().inflate(R.layout.action_bar, null);
        TextView ActionBarTitle = (TextView) mview.findViewById(R.id.Actionbar_Title);
        ImageButton ActionBarDrawer_btn = (ImageButton) mview.findViewById(R.id.ActionBar_drawerbutton);
        ActionBarTitle.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        //noinspection ConstantConditions
        getActionBar().setCustomView(mview, layout);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_main_dark)));
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);
        ActionBarDrawer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                View drawerView = findViewById(R.id.drawerView);
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(drawerView);
                }

            }
        });
        ActionBarDrawer_btn.setImageResource(R.drawable.ic_menu_white_24dp);
        IV_MAIN_MEMO.setImageResource(R.drawable.ic_memo_main_dark);
        IV_MAIN_ALARM.setImageResource(R.drawable.ic_alarm_main_dark);
        IV_MAIN_DDAY.setImageResource(R.drawable.ic_cal_main_dark);
        IV_MAIN_DDAY.setPadding(0, 16, 0, 0);
        IV_MAIN_ALARM.setPadding(0, 16, 0, 0);
        TV_MAIN_ALARM_TITLE.setPadding(8, 16, 0, 0);
        TV_MAIN_DDAY_TITLE.setPadding(8, 16, 0, 0);


    }

    @SuppressWarnings("ResourceType")
    public void SetExtraTime() {
        Calendar cl = Calendar.getInstance();
        int today = cl.get(Calendar.DAY_OF_WEEK);
        int year = cl.get(Calendar.YEAR);
        int month = cl.get(Calendar.MONTH);
        int day = cl.get(Calendar.DAY_OF_MONTH);
        today = today - 2;
        try {
            ArrayList<String> timelist = getExtraTime_Time(this, today);
            int et_h, et_m;

            try {
                Calendar tmp_cl = Calendar.getInstance();
                int tmp_i;
                for (tmp_i = 0; tmp_i != timelist.size(); tmp_i++) {
                    int tmp_hour = Integer.parseInt(timelist.get(tmp_i).split(":")[0].replaceAll(" ", ""));
                    int tmp_minute = Integer.parseInt(timelist.get(tmp_i).split(":")[1].replaceAll(" ", ""));
                    tmp_cl.set(year, month, day, tmp_hour, tmp_minute, 0);
                    if (cl.getTimeInMillis() < tmp_cl.getTimeInMillis()) {
                        break;
                    }
                }
                et_h = Integer.parseInt(timelist.get(tmp_i).split(":")[0].replaceAll(" ", ""));
                et_m = Integer.parseInt(timelist.get(tmp_i).split(":")[1].replaceAll(" ", ""));
                Calendar et_cl = Calendar.getInstance();
                et_cl.set(year, month, day, et_h, et_m, 0);
                long classtime = et_cl.getTimeInMillis();
                long currenttime = cl.getTimeInMillis();
                extratime_mills = (classtime - currenttime);

                extratime = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int etsc = (int) (TimeUnit.MILLISECONDS.toSeconds(extratime_mills));
                        int Hour = etsc / 3600;
                        int minute = (etsc - (Hour * 3600)) / 60;
                        //noinspection UnusedDeclaration
                        int second = (etsc - (Hour * 3600) - (minute * 60));
                        String extra_time_msg = "";
                        if (Hour != 0) extra_time_msg += Hour + "시간 ";
                        if (minute != 0) extra_time_msg += minute + "분 ";
                        extra_time_msg += "남음...";
                        extratimetv.setText(extra_time_msg);

                    }
                };

                // 스레드에서 뷰 접근 못해서 핸들러로 접근.
                final Handler noclass_handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        extratimetv.setText(getResources().getString(R.string.NO_CLASS_MSG));
                    }
                };

                extratimeThread = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            if (EXTRATIME_TMP_FLAG) {
                                try {
                                    extratime.sendEmptyMessage(0);
                                    Thread.sleep(1000);
                                    extratime_mills = extratime_mills - 1000;
                                    if (extratime_mills < 0) {
                                        extratimeThread.interrupt();
                                        noclass_handler.sendEmptyMessage(0);
                                        break;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                };

                if (extra_thread_check) {
                    extratimeThread.start();
                    extra_thread_check = false;
                }
            } catch (IndexOutOfBoundsException e) {
                extratimetv.setText(getResources().getString(R.string.NO_CLASS_MSG));
            }
        } catch (NullPointerException e) {
            extratimetv.setText(getResources().getString(R.string.NO_CLASS_MSG));
        }

    }

    private void changeView() {
        Log.d("KKT", "ChangeView");
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        extratimetv.startAnimation(anim);
        TV_MAIN_ALARM_TITLE.startAnimation(anim);
        IV_MAIN_ALARM.startAnimation(anim);
        if (TV_MAIN_ALARM_TITLE.getText().toString().equals(getResources().getString(R.string.MAIN_ALARM_TITLE))) {
            // 남은시간이 떠있을때 .....
            TV_MAIN_ALARM_TITLE.setText(getResources().getString(R.string.MAIN_SHUTTLE_TITLE));
            extratimetv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            EXTRATIME_TMP_FLAG = false;
            if (IS_DARK_THEME) {
                IV_MAIN_ALARM.setImageResource(R.drawable.ic_bus_main_dark);
            } else {
                IV_MAIN_ALARM.setImageResource(R.drawable.ic_bus_main);
            }
            String msg = getShuttleBusTime();
            if (msg != null) {
                extratimetv.setText(msg);
            }
        } else if (TV_MAIN_ALARM_TITLE.getText().toString().equals(getResources().getString(R.string.MAIN_SHUTTLE_TITLE))) {
            // 달구지시간이 떠있을때 .....
            extratimetv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            TV_MAIN_ALARM_TITLE.setText(getResources().getString(R.string.MAIN_ALARM_TITLE));
            EXTRATIME_TMP_FLAG = true;
            if (IS_DARK_THEME) {
                IV_MAIN_ALARM.setImageResource(R.drawable.ic_alarm_main_dark);
            } else {
                IV_MAIN_ALARM.setImageResource(R.drawable.ic_alarm_main);
            }
            SetExtraTime();
        }
    }

    private String getShuttleBusTime() {
        String msg;
        ArrayList<String> new_kh_arr = new ArrayList<String>();
        ArrayList<String> new_ek_arr = new ArrayList<String>();
        Calendar now_calendar = Calendar.getInstance();
        Calendar shuttle_calendar = Calendar.getInstance();
        long now_time_mills = Calendar.getInstance().getTimeInMillis();
        int i;
        int shuttle_hour, shuttle_minute;
        /*
         * 기흥역 출발 달구지 리스트.
         */
        for (i = 1; i <= shuttlepref.getAll().size() / 2; i++) {
            shuttle_hour = Integer.parseInt(shuttlepref.getValue("kh_start_" + i, null).split(":")[0]);
            shuttle_minute = Integer.parseInt(shuttlepref.getValue("kh_start_" + i, null).split(":")[1]);
            //noinspection ResourceType
            shuttle_calendar.set(now_calendar.get(Calendar.YEAR), now_calendar.get(Calendar.MONTH), now_calendar.get(Calendar.DAY_OF_MONTH), shuttle_hour, shuttle_minute);
            if (now_time_mills <= shuttle_calendar.getTimeInMillis()) break;
        }
        // Index값 가져옴 ....... 거기서 + 3 까지 구해야 하므로
        try {
            for (int j = i; j < i + 3; j++) {
                new_kh_arr.add(shuttlepref.getValue("kh_start_" + j, null).split(":")[0] + ":" + shuttlepref.getValue("kh_start_" + j, null).split(":")[1]);
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (NullPointerException ignored) {
        }
        /*
         * 이공관 출발 달구지 리스트.
         */
        for (i = 1; i <= shuttlepref.getAll().size() / 2; i++) {
            shuttle_hour = Integer.parseInt(shuttlepref.getValue("ek_start_" + i, null).split(":")[0]);
            shuttle_minute = Integer.parseInt(shuttlepref.getValue("ek_start_" + i, null).split(":")[1]);
            //noinspection ResourceType
            shuttle_calendar.set(now_calendar.get(Calendar.YEAR), now_calendar.get(Calendar.MONTH), now_calendar.get(Calendar.DAY_OF_MONTH), shuttle_hour, shuttle_minute);
            if (now_time_mills <= shuttle_calendar.getTimeInMillis()) break;
        }
        try {
            for (int j = i; j < i + 3; j++) {
                new_ek_arr.add(shuttlepref.getValue("ek_start_" + j, null).split(":")[0] + ":" + shuttlepref.getValue("ek_start_" + j, null).split(":")[1]);
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (NullPointerException ignored) {
        }
        if (new_kh_arr.size() == 0 && new_ek_arr.size() == 0) return "셔틀버스가 없습니다.";
        /*
         * 정보 합치기.
         */
        int MAXSIZE = 2;
        msg = "이공관 - 기흥역\n";
        for (int k = 0; k < MAXSIZE; k++) {
            Log.d("KKT_KINDEX:", "" + k);
            msg += new_kh_arr.get(k) + " - " + new_ek_arr.get(k) + "\n";
        }
        return msg;
    }

    private ArrayList<String> getExtraTime_Time(Context mContext, int position) {
        ArrayList<String> al = new ArrayList<String>();
        String week = null;
        switch (position) {
            case 0:
                week = "mon_";
                break;
            case 1:
                week = "tues";
                break;
            case 2:
                week = "wends";
                break;
            case 3:
                week = "thur";
                break;
            case 4:
                week = "fri";
                break;
        }
        if (week == null) {
            al = null;
        } else {
            ControlSharedPref pref = new ControlSharedPref(mContext, "timetable.pref");
            int prefsize = pref.getAll().size();
            int tmp;
            String time;
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue(week + i, "null").equals("null") && !pref.getValue(week + i, "").equals(pref.getValue("mon_" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue(week + tmp, "").equals(pref.getValue(week + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(mContext, tmp, i);
                    time = time.split("-")[0];
                    al.add(time);
                }
            }
        }
        return al;
    }

    private String getStudentName() {
        try {
            String tmp_realname = appUtils.getName(pref.getValue("name_e", null).split(";")[0].substring(12).replaceAll("\"", ""));
            return appUtils.getURLDecode(tmp_realname);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void SetPreviewMemo() {
        String PreviewText = MemoDate.getValue(Memo.MEMO_DATE_KEY, "null");
        MemoPrevewText.setText("");
        if (!PreviewText.equals("null")) {
            MemoPrevewText.append(PreviewText);
        }
    }

    public int caldate(int myear, int mmonth, int mday) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar dday = Calendar.getInstance();
            dday.set(myear, mmonth, mday);
            long day = dday.getTimeInMillis() / 86400000;
            long tday = today.getTimeInMillis() / 86400000;
            long count = tday - day;
            return (int) count;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void changeColor(int newColor) {
        tabs.setIndicatorColor(newColor);
        currentColor = newColor;
    }

    @Override
    public void onConfigurationChanged(Configuration newconfiguration) {
        if (newconfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tabs.setTabsWidth(Configuration.ORIENTATION_LANDSCAPE);
            LR_TV_MAIN.setVisibility(View.INVISIBLE);
            Animation hideanim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down);
            hideanim.setAnimationListener(hideAnimlistener);
            LR_TV_MAIN.startAnimation(hideanim);
        } else if (newconfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            tabs.setTabsWidth(Configuration.ORIENTATION_PORTRAIT);
            LR_TV_MAIN.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_slide_up));
            LR_TV_MAIN.setVisibility(View.VISIBLE);
        }
        super.onConfigurationChanged(newconfiguration);
    }

    private Animation.AnimationListener hideAnimlistener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            LR_TV_MAIN.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent intent) {
        super.onActivityResult(requestcode, resultcode, intent);
        switch (requestcode) {
            case 0:
                TimeTableMain.this.finish();
                startActivity(new Intent(TimeTableMain.this, TimeTableMain.class));
                break;
        }
    }

    @Override
    public void onResume() {
        SetExtraTime();
        // Set Extra Time
        if (extratime != null) {
            extratime.removeMessages(0);
        }
        SetPreviewMemo();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tabs.setTabsWidth(Configuration.ORIENTATION_LANDSCAPE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tabs.setTabsWidth(Configuration.ORIENTATION_PORTRAIT);
        }
        currentColor = getCurrentColor(this, pager.getCurrentItem());
        changeColor(currentColor);
        int newcolor = getCurrentColor(this, pager.getCurrentItem());
        changeColor((newcolor));
        tabs.setCurrentTextColor(pager.getCurrentItem(), newcolor);
        super.onResume();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.mainmenu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_drawer:
//                startActivity(new Intent(this, MainAppSettingActivity.class));
//                break;
//            case R.id.action_share:
//                ShareTimeTable();
//                break;
//        }
//        return false;
//    }

    int Share_index = 0;

    private void ShareTimeTable() {
        String mon_str = getResources().getString(R.string.DAY_monday);
        String tues_str = getResources().getString(R.string.DAY_tuesday);
        String wends_str = getResources().getString(R.string.DAY_wendsday);
        String thur_str = getResources().getString(R.string.DAY_thursday);
        String fri_str = getResources().getString(R.string.DAY_friday);
        final String items[] = {mon_str, tues_str, wends_str, thur_str,
                fri_str};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(
                R.string.SHARE_TIMETABLE_TITLE));
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Share_index = which;
                    }
                }
        ).setPositiveButton(
                getResources().getString(R.string.SHARE_TIMETABLE_OK),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        StringBuilder date = Share_TTB(Share_index);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, date.toString());
                        startActivity(i);
                    }
                }
        ).setNegativeButton(
                getResources().getString(
                        R.string.SHARE_TIMETABLE_CANCEL),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                }
        );
        builder.show();
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final ControlSharedPref prefs = new ControlSharedPref(context, "gcm.pref");
        prefs.put(PROPERTY_REG_ID, regId);
        prefs.put(PROPERTY_APP_VERSION, getAppVersion(context));
    }

    private ArrayList<String> getDrawerList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("시간표 타입");
        list.add("테마 바꾸기");
        list.add("위젯 설정");
        return list;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        final ControlSharedPref prefs = new ControlSharedPref(context, "gcm.pref");
        String registrationId = prefs.getValue(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("K_TIME_GCM", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getValue(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("K_TIME_GCM", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void sendRegistrationIdToBackend() {
        try {
            URL url = new URL("http://kh4975.iptime.org/KangNamUnivTTB/GcmRegister.jsp");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            OutputStream ops = (connection.getOutputStream());
            ops.write(("GCMID=" + regid + "&").getBytes());
            ops.write(("IDENTIFIER=" + Build.MODEL + "&").getBytes());
            ops.write(("APPVERSION=" + getAppVersion(this)).getBytes());
            ops.flush();
            ops.close();
            Log.d(getClass().getSimpleName(), connection.getResponseCode() == 200 ? "Gcm Register Sucess" : "Gcm Register Fail");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("K_TIME_GCM", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private StringBuilder Share_TTB(int day) {
        StringBuilder date = new StringBuilder();
        int prefsize = timetablepref.getAll().size() / 5;
        int tmp;
        String time;
        switch (day) {
            case 0:
                date.append(getResources().getString(R.string.DAY_monday)).append("\n");
                for (int i = 0; prefsize != i; i++) {
                    if (!timetablepref.getValue("mon_" + i, "null").equals("null") && !timetablepref.getValue("mon_" + i, "").equals(timetablepref.getValue("mon_" + (i + 1), ""))) {
                        tmp = i;
                        while (tmp != 0) {
                            if (!timetablepref.getValue("mon_" + tmp, "").equals(timetablepref.getValue("mon_" + (tmp - 1), "")))
                                break;
                            tmp--;
                        }
                        time = TIME(this, tmp, i);
                        date.append(time).append(":").append(timetablepref.getValue("mon_" + i, "").replace(
                                "null", "")).append("\n");
                    }
                }
                break;
            case 1:
                date.append(getResources().getString(R.string.DAY_tuesday)).append("\n");
                for (int i = 0; prefsize != i; i++) {
                    if (!timetablepref.getValue("tues" + i, "null").equals("null") && !timetablepref.getValue("tues" + i, "").equals(timetablepref.getValue("tues" + (i + 1), ""))) {
                        tmp = i;
                        while (tmp != 0) {
                            if (!timetablepref.getValue("tues" + tmp, "").equals(timetablepref.getValue("tues" + (tmp - 1), "")))
                                break;
                            tmp--;
                        }
                        time = TIME(this, tmp, i);
                        date.append(time).append(":").append(timetablepref.getValue("tues" + i, "").replace(
                                "null", "")).append("\n");
                    }
                }
                break;
            case 2:
                date.append(getResources().getString(R.string.DAY_wendsday)).append("\n");
                for (int i = 0; prefsize != i; i++) {
                    if (!timetablepref.getValue("wends" + i, "null").equals("null") && !timetablepref.getValue("wends" + i, "").equals(timetablepref.getValue("wends" + (i + 1), ""))) {
                        tmp = i;
                        while (tmp != 0) {
                            if (!timetablepref.getValue("wends" + tmp, "").equals(timetablepref.getValue("wends" + (tmp - 1), "")))
                                break;
                            tmp--;
                        }
                        time = TIME(this, tmp, i);
                        date.append(time).append(":").append(timetablepref.getValue("wends" + i, "").replace(
                                "null", "")).append("\n");
                    }
                }
                break;
            case 3:
                date.append(getResources().getString(R.string.DAY_thursday)).append("\n");
                for (int i = 0; prefsize != i; i++) {
                    if (!timetablepref.getValue("thur" + i, "null").equals("null") && !timetablepref.getValue("thur" + i, "").equals(timetablepref.getValue("thur" + (i + 1), ""))) {
                        tmp = i;
                        while (tmp != 0) {
                            if (!timetablepref.getValue("thur" + tmp, "").equals(timetablepref.getValue("thur" + (tmp - 1), "")))
                                break;
                            tmp--;
                        }
                        time = TIME(this, tmp, i);
                        date.append(time).append(":").append(timetablepref.getValue("thur" + i, "").replace(
                                "null", "")).append("\n");
                    }
                }
                break;
            case 4:
                date.append(getResources().getString(R.string.DAY_friday)).append("\n");
                for (int i = 0; prefsize != i; i++) {
                    if (!timetablepref.getValue("fri" + i, "null").equals("null") && !timetablepref.getValue("fri" + i, "").equals(timetablepref.getValue("fri" + (i + 1), ""))) {
                        tmp = i;
                        while (tmp != 0) {
                            if (!timetablepref.getValue("fri" + tmp, "").equals(timetablepref.getValue("fri" + (tmp - 1), "")))
                                break;
                            tmp--;
                        }
                        time = TIME(this, tmp, i);
                        date.append(time).append(":").append(timetablepref.getValue("fri" + i, "").replace(
                                "null", "")).append("\n");
                    }
                }
                break;
            default:
                date.append(getResources().getString(
                        R.string.SETTING_TIMETABLE_GONGGANG_MESSAGE_TITLE));
        }

        return date;
    }

    private void showSetTimeTableTypeDialog() {
        final ControlSharedPref settingpref = new ControlSharedPref(this, "Setting.pref");
        CharSequence[] sequences = getResources().getStringArray(R.array.time_table_type_entries);
        new AlertDialog.Builder(this).setSingleChoiceItems(sequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StyleListIndex = i;
            }
        }).setPositiveButton(getResources().getString(R.string.SETTING_LOTOUT_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                settingpref.put("ttbtype", StyleListIndex);
                TimeTableMain.this.finish();
                startActivity(new Intent(TimeTableMain.this, TimeTableMain.class));
                ThemeListIndex = -1;
            }
        }).setNegativeButton(getResources().getString(R.string.SETTING_LOGOUT_CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThemeListIndex = -1;
            }
        }).setTitle(getResources().getString(R.string.SETTING_TIMETABLE_STYLE_TITLE)).show().getListView().setItemChecked(settingpref.getValue("ttbtype", 0), true);
    }

    private void showSetThemeDialog() {
        final ControlSharedPref settingpref = new ControlSharedPref(this, "Setting.pref");
        CharSequence[] sequences = getResources().getStringArray(R.array.time_table_theme_entries);
        new AlertDialog.Builder(this).setSingleChoiceItems(sequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThemeListIndex = i;
            }
        }).setPositiveButton(getResources().getString(R.string.SETTING_LOTOUT_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                settingpref.put("ttbtheme", ThemeListIndex);
                TimeTableMain.this.finish();
                startActivity(new Intent(TimeTableMain.this, TimeTableMain.class));
                ThemeListIndex = -1;
            }
        }).setNegativeButton(getResources().getString(R.string.SETTING_LOGOUT_CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ThemeListIndex = -1;
            }

        }).setTitle(getResources().getString(R.string.SETTING_TIMETABLE_THEME_TITLE)).show().getListView().setItemChecked(settingpref.getValue("ttbtheme", 0), true);
    }

    private void refreshTimeTableData() {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(TimeTableMain.this);
                dialog.setTitle("작업중");
                dialog.setMessage("시간표정보를 새로고침 중입니다");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                GetTimetableUtils.RefreshTimeTable(TimeTableMain.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                dialog.cancel();
                TimeTableMain.this.finish();
                startActivity(new Intent(TimeTableMain.this, TimeTableMain.class));
            }
        }.execute();
    }

    public String TIME(Context mContext, int tmp, int i) {
        ControlSharedPref pref = new ControlSharedPref(mContext, "timetable.pref");
        String Starttime = pref.getValue("time" + tmp, "").split("-")[0];
        String Lasttime = pref.getValue("time" + i, "").split("-")[1];
        return Starttime + "-" + Lasttime;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int pageposition) {
        int newcolor = getCurrentColor(this, pageposition);
        changeColor((newcolor));
        tabs.setCurrentTextColor(pageposition, newcolor);
    }

    Handler backhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            m_close_flag = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (!m_close_flag) {
            Toast.makeText(this,
                    getResources().getString(R.string.DOUBLE_BACK_KILL_APP),
                    Toast.LENGTH_SHORT).show();
            m_close_flag = true;
            backhandler.sendEmptyMessageDelayed(0, 1000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        backhandler.removeMessages(0);
    }

    public static int getCurrentColor(Context context, int pageposition) {
        int hex;
        int newcolor;

        ControlSharedPref settingepref = new ControlSharedPref(context,
                "Setting.pref");
        switch (pageposition) {
            case 0:
                hex = Color.parseColor("#FF96AA39");
                newcolor = settingepref.getValue(
                        MainAppSettingActivity.TTB_COLOR_MON_data, hex);
                break;
            case 1:
                hex = Color.parseColor("#FFC74B46");
                newcolor = settingepref.getValue(
                        MainAppSettingActivity.TTB_COLOR_TUE_data, hex);
                break;
            case 2:
                hex = Color.parseColor("#FFF4842D");
                newcolor = settingepref.getValue(
                        MainAppSettingActivity.TTB_COLOR_WEN_data, hex);
                break;
            case 3:
                hex = Color.parseColor("#FF3F9FE0");
                newcolor = settingepref.getValue(
                        MainAppSettingActivity.TTB_COLOR_THUR_data, hex);
                break;
            case 4:
                hex = Color.parseColor("#FF5161BC");
                newcolor = settingepref.getValue(
                        MainAppSettingActivity.TTB_COLOR_FRI_data, hex);
                break;
            default:
                hex = Color.parseColor("#FF666666");
                newcolor = hex;
                break;
        }
        return newcolor;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_setting:
                startActivityForResult(new Intent(this, MainAppSettingActivity.class), 0);
                break;
            case R.id.drawer_share:
                ShareTimeTable();
                break;
            case R.id.drawer_refresh:
                refreshTimeTableData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                showSetTimeTableTypeDialog();
                break;
            case 1:
                showSetThemeDialog();
                break;
            case 2:
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"월", "화", "수", "목", "금"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position);
        }
    }
}