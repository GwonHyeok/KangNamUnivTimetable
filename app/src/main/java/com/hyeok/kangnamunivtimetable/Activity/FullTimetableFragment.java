package com.hyeok.kangnamunivtimetable.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;

import java.util.Random;

/*
 * Created by GwonHyeok on 14. 7. 28..
 */
public class FullTimetableFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mainLayout, tableLayout;
    private TextView[][] dayTextView = new TextView[5][10];
    private TextView[] timeTextView = new TextView[10];

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        LayoutInflater vi = (LayoutInflater) container.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(R.layout.activity_full_time_table, null);
        viewInit(view);
        return view;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_up, R.anim.anim_slide_down);
        transaction.remove(this);
        transaction.commit();
    }

    private void viewResize(int width, int height) {
        Log.d("TAG", "WIDTH / HEIGHT : " + width + "/" + height);
        int spacewidth = width - timeTextView[0].getWidth();
        int newwidth = spacewidth / 5;
        for (TextView[] aDayTextView : dayTextView) {
            ViewGroup.LayoutParams layoutParamss = aDayTextView[0].getLayoutParams();
            layoutParamss.width = newwidth;
            aDayTextView[0].setLayoutParams(layoutParamss);
        }

        for (TextView[] aDayTextView : dayTextView) {
            for (TextView anADayTextView : aDayTextView) {
                ViewGroup.LayoutParams layoutParamss = anADayTextView.getLayoutParams();
                layoutParamss.height = checkDensityHeight();
                anADayTextView.setLayoutParams(layoutParamss);
            }
        }

        for (TextView aTimeTextView : timeTextView) {
            ViewGroup.LayoutParams layoutParamses = aTimeTextView.getLayoutParams();
            layoutParamses.height = checkDensityHeight();
            aTimeTextView.setLayoutParams(layoutParamses);
        }

        for (TextView[] aDayTextView : dayTextView) {
            for (TextView anADayTextView : aDayTextView) {
                anADayTextView.setGravity(Gravity.CENTER);
            }
        }

        for (TextView atimeTextView : timeTextView) {
            atimeTextView.setGravity(Gravity.CENTER);
        }
        for (int i = 0; i < 5; i++) {
            drawTimeTable(i);
        }
    }

    private void drawTimeTable(int index) {
        ControlSharedPref pref = new ControlSharedPref(getActivity(), "timetable.pref");
        String TimeTableValueKey;
        switch (index) {
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

        for (int i = 0; pref.getAll().size() / 5 != i; i++) {
            if (!pref.getValue(TimeTableValueKey + i, "null").equals("null") && !pref.getValue(TimeTableValueKey + i, "").equals(pref.getValue(TimeTableValueKey + (i + 1), ""))) {
                int tmp = i;
                while (tmp != 0) {
                    if (!pref.getValue(TimeTableValueKey + tmp, "").equals(pref.getValue(TimeTableValueKey + (tmp - 1), "")))
                        break;
                    tmp--;
                }
                int size = i - tmp + 1;
                addTableBitmap(index, tmp, size, pref.getValue(TimeTableValueKey + i, ""));
            }
        }
    }

    private void addTableBitmap(int day, int startposition, int size, String value) {
        ControlSharedPref settingpref = new ControlSharedPref(getActivity(), "Setting.pref");
        int textSize = settingpref.getValue(MainAppSettingActivity.TTB_TABLE_TEXT_SIZE, 14);
        int height = dayTextView[0][0].getLayoutParams().height;
        int width = dayTextView[0][0].getLayoutParams().width;
        int timewidth = timeTextView[0].getWidth();
        int newsize;
        if (size % 2 == 0) {
            newsize = size / 2 * height;
        } else {
            newsize = (size / 2 * height) + height / 2;
        }
        // Set TextView
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        int color = Color.rgb(r, g, b);
        TextView view = new TextView(getActivity());
        view.setText(value);
        view.setHeight(newsize);
        view.setWidth(width);
        view.setBackgroundColor(color);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        if (startposition % 2 == 0) {
            view.setX(timewidth + (day * width));
            view.setY(height + (startposition / 2) * height);
        } else {
            startposition = (startposition - 1) / 2;
            view.setX(timewidth + (day * width));
            view.setY(height / 2 + height + startposition * height);
        }
        view.setTag(value);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        tableLayout.addView(view);
    }

    @SuppressWarnings("deprecation")
    private void removeListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    private int checkDensityHeight() {
        int dpi = getResources().getDisplayMetrics().densityDpi;
        /*
         * 0 < dpi <= 160  == LDPI == 32
         * 160 < dpi <= 240 == HDPI == 50
         * 240 < dpi <= 320 == XHDPI == 66
         * 320 < dpi <= 480 == XXHDPI == 100
         * 480 < dpi <= 640 == XXXHDPI == 134
         */
        if (0 < dpi && dpi <= DisplayMetrics.DENSITY_MEDIUM) {
            return 32;
        } else if (DisplayMetrics.DENSITY_MEDIUM < dpi && dpi <= DisplayMetrics.DENSITY_HIGH) {
            return 50;
        } else if (DisplayMetrics.DENSITY_HIGH < dpi && dpi <= DisplayMetrics.DENSITY_XHIGH) {
            return 66;
        } else if (DisplayMetrics.DENSITY_XHIGH < dpi && dpi <= DisplayMetrics.DENSITY_XXHIGH) {
            return 100;
        } else if (DisplayMetrics.DENSITY_XXHIGH < dpi && dpi <= DisplayMetrics.DENSITY_XXXHIGH) {
            return 134;
        }
        return 100;
    }

    private void viewInit(View view) {
        Context mContext = view.getContext();
        tableLayout = (RelativeLayout) view.findViewById(R.id.FULL_TIME_TABLE_LAYOUT);
        // TextView Init
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                String TextViewname = "";
                int textViewId;
                switch (i) {
                    case 0:
                        TextViewname = "FULL_TABLE_MON_" + j;
                        break;
                    case 1:
                        TextViewname = "FULL_TABLE_TUE_" + j;
                        break;
                    case 2:
                        TextViewname = "FULL_TABLE_WEN_" + j;
                        break;
                    case 3:
                        TextViewname = "FULL_TABLE_THU_" + j;
                        break;
                    case 4:
                        TextViewname = "FULL_TABLE_FRI_" + j;
                        break;
                }
                textViewId = getResources().getIdentifier(TextViewname, "id", mContext.getPackageName());
                dayTextView[i][j] = (TextView) view.findViewById(textViewId);
            }
        }
        for (int i = 0; i < timeTextView.length; i++) {
            String TextViewname = "FULL_TABLE_TIME_" + i;
            int textViewId = getResources().getIdentifier(TextViewname, "id", mContext.getPackageName());
            timeTextView[i] = (TextView) view.findViewById(textViewId);
        }

        // MainLayout Init
        mainLayout = (RelativeLayout) view.findViewById(R.id.FULL_TIME_TABLE_MAIN);
        ControlSharedPref controlSharedPref = new ControlSharedPref(view.getContext(), "Setting.pref");
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mainLayout.getWidth() > 0) {
                    viewResize(mainLayout.getWidth(), mainLayout.getHeight());
                    removeListener(mainLayout, this);
                }
            }
        });

        // Set Theme
        boolean IS_DARK_THEME = controlSharedPref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1;
        if (IS_DARK_THEME) {
            setDarkTheme();
        } else {
            setWhiteTheme();
        }
    }

    private void setWhiteTheme() {
        for (TextView[] aDayTextView : dayTextView) {
            for (TextView anADayTextView : aDayTextView) {
                anADayTextView.setTextColor(getResources().getColor(R.color.fontcolor_main));
                anADayTextView.setBackgroundResource(R.drawable.table_border);
            }
        }

        for (TextView aTimeTextView : timeTextView) {
            aTimeTextView.setTextColor(getResources().getColor(R.color.fontcolor_main));
            aTimeTextView.setBackgroundResource(R.drawable.table_border);
            aTimeTextView.setPadding(10, 0, 10, 0);
        }
    }

    private void setDarkTheme() {
        mainLayout.setBackgroundColor(getResources().getColor(R.color.background_main_dark));
        for (TextView[] aDayTextView : dayTextView) {
            for (TextView anADayTextView : aDayTextView) {
                anADayTextView.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
                anADayTextView.setBackgroundResource(R.drawable.table_border_dark);
            }
        }

        for (TextView aTimeTextView : timeTextView) {
            aTimeTextView.setTextColor(getResources().getColor(R.color.fontcolor_main_dark));
            aTimeTextView.setBackgroundResource(R.drawable.table_border_dark);
            aTimeTextView.setPadding(10, 0, 10, 0);
        }
    }
}
