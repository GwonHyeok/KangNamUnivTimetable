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

import com.hyeok.kangnamunivtimetable.Activity.MainAppSettingActivity;
import com.hyeok.kangnamunivtimetable.Activity.TimeTableMain;
import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;
import com.hyeok.kangnamunivtimetable.Utils.appUtils;

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

    private StringBuilder SetWidgetText(Context mContext) {
        final StringBuilder list = new StringBuilder();
        ControlSharedPref pref = new ControlSharedPref(mContext, "timetable.pref");
        Calendar mCalendar = Calendar.getInstance();
        int position = mCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        int prefsize = pref.getAll().size() / 5;
        String TimeTableValueKey;

        switch (position) {
            case 0:
                TimeTableValueKey = "mon_";
                break;
            case 1:
                TimeTableValueKey = "tues";
                break;
            case 2:
                TimeTableValueKey = "wends";
                break;
            case 3:
                TimeTableValueKey = "thur";
                break;
            case 4:
                TimeTableValueKey = "fri";
                break;
            default:
                TimeTableValueKey = "";
                break;
        }
        for (int i = 0; prefsize != i; i++) {
            if (!pref.getValue(TimeTableValueKey + i, "null").equals("null") && !pref.getValue(TimeTableValueKey + i, "").equals(pref.getValue(TimeTableValueKey + (i + 1), ""))) {
                int tmp = i;
                while (tmp != 0) {
                    if (!pref.getValue(TimeTableValueKey + tmp, "").equals(pref.getValue(TimeTableValueKey + (tmp - 1), "")))
                        break;
                    tmp--;
                }
                String time = appUtils.TIME(mContext, tmp, i);
                list.append(time).append(":").append(pref.getValue(TimeTableValueKey + i, "").replace("null", "")).append("\n");
            }
        }
        return list;
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
