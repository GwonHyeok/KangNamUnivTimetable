package com.hyeok.kangnamunivtimetable.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;
import com.hyeok.kangnamunivtimetable.Activity.MainAppSettingActivity;
import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Activity.TimeTableMain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeTableWidget extends AppWidgetProvider {
    private PendingIntent pendingIntent;
    private PendingIntent pendingIntent_12;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated
        // with it.
//        final int N = appWidgetIds.length;
        //noinspection StatementWithEmptyBody
//        for (int appWidgetId : appWidgetIds) {
        //NewAppWidgetConfigureActivity.deleteTitlePref(context,
        //	appWidgetIds[i]);
//        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent intent = new Intent(context, TimeTableWidget.class);
        pendingIntent_12 = PendingIntent.getBroadcast(context,
                0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        AlarmManager alarmmanager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent_12);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntent_12);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context.getClass().getName(), "Widget Update!");
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context
                .getPackageName(), TimeTableWidget.class.getName());
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private StringBuilder SetWidgetText(Context context) {
        ControlSharedPref pref = new ControlSharedPref(context, "timetable.pref");

        //	if ( pref.getValue(key, dftValue))
        final StringBuilder list = new StringBuilder();
//        String gonggangmsg = context.getResources().getString(R.string.DAY_GONGGANG);
        int prefsize = pref.getAll().size() / 5;
        Calendar mCalendar = Calendar.getInstance();
        int position = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        int tmp;
        String time;
        if (position == 0) {
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue("mon_" + i, "null").equals("null") && !pref.getValue("mon_" + i, "").equals(pref.getValue("mon_" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue("mon_" + tmp, "").equals(pref.getValue("mon_" + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(context, tmp, i);
                    list.append(time).append(":").append(pref.getValue("mon_" + i, "").replace("null", "")).append("\n");
                }
            }
        } else if (position == 1) {
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue("tues" + i, "null").equals("null") && !pref.getValue("tues" + i, "").equals(pref.getValue("tues" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue("tues" + tmp, "").equals(pref.getValue("tues" + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(context, tmp, i);
                    list.append(time).append(":").append(pref.getValue("tues" + i, "").replace("null", "")).append("\n");
                }
            }
        } else if (position == 2) {
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue("wends" + i, "null").equals("null") && !pref.getValue("wends" + i, "").equals(pref.getValue("wends" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue("wends" + tmp, "").equals(pref.getValue("wends" + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(context, tmp, i);
                    list.append(time).append(":").append(pref.getValue("wends" + i, "").replace("null", "")).append("\n");
                }
            }
        } else if (position == 3) {
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue("thur" + i, "null").equals("null") && !pref.getValue("thur" + i, "").equals(pref.getValue("thur" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue("thur" + tmp, "").equals(pref.getValue("thur" + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(context, tmp, i);
                    list.append(time).append(":").append(pref.getValue("thur" + i, "").replace("null", "")).append("\n");
                }
            }
        } else if (position == 4) {
            for (int i = 0; prefsize != i; i++) {
                if (!pref.getValue("fri" + i, "null").equals("null") && !pref.getValue("fri" + i, "").equals(pref.getValue("fri" + (i + 1), ""))) {
                    tmp = i;
                    while (tmp != 0) {
                        if (!pref.getValue("fri" + tmp, "").equals(pref.getValue("fri" + (tmp - 1), "")))
                            break;
                        tmp--;
                    }
                    time = TIME(context, tmp, i);
                    list.append(time).append(":").append(pref.getValue("fri" + i, "").replace("null", "")).append("\n");
                }
            }
        }
        return list;
    }

    public String TIME(Context mContext, int tmp, int i) {
        ControlSharedPref pref = new ControlSharedPref(mContext, "timetable.pref");
        String Starttime = pref.getValue("time" + tmp, "").split("-")[0];
        String Lasttime = pref.getValue("time" + i, "").split("-")[1];
        return Starttime + "-" + Lasttime;
    }

    private void updateAppWidget(Context context,
                                 AppWidgetManager appWidgetManager, int appWidgetId) {
        ControlSharedPref settingpref = new ControlSharedPref(context, "Setting.pref");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat(context.getResources().getString(R.string.SETTING_WIDGET_UPDATE_TIME_FORMAT));
        settingpref.put(MainAppSettingActivity.WIDGET_UPDATE_TIME, format.format(date));

        int interval = settingpref.getValue(MainAppSettingActivity.UPDATE_INTERVAL_data, 6);
        int textsize = settingpref.getValue(MainAppSettingActivity.WIDGET_TEXT_SIZE, 12);
        int textcolor = settingpref.getValue(MainAppSettingActivity.WIDGET_TEXT_COLOR, Color.rgb(0, 0, 0));
        int textbgcolor = settingpref.getValue(MainAppSettingActivity.WIDGET_BACKGROUND_COLOR, Color.argb(0, 0, 0, 0));
        AlarmManager al = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeTableWidget.class);
        pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += interval * 3600 * 1000;
        al.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, pendingIntent);

        String widgetText;
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        widgetText = SetWidgetText(context).toString();
        if (widgetText.equals("")) {
            settingpref = new ControlSharedPref(context, "Setting.pref");
            widgetText = settingpref.getValue(MainAppSettingActivity.GONGGANG_MESSAGE_data, context.getString(R.string.DAY_GONGGANG));
        }
        // Widget Touch Event
        PendingIntent appMainActivityIntent = PendingIntent.getActivity(context, 0, new Intent(context, TimeTableMain.class), 0);
        views.setOnClickPendingIntent(R.id.widget_layout, appMainActivityIntent);

        views.setTextViewText(R.id.widget_timetable_textlabel, widgetText);
        views.setTextColor(R.id.widget_timetable_textlabel, textcolor);
        views.setFloat(R.id.widget_timetable_textlabel, "setTextSize", textsize);
        views.setInt(R.id.widget_timetable_textlabel, "setBackgroundColor", textbgcolor); // SetTextview Background
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
