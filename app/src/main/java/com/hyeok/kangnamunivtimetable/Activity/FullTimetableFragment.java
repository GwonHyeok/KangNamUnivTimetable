package com.hyeok.kangnamunivtimetable.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyeok.kangnamunivtimetable.CustomViews.IButton;
import com.hyeok.kangnamunivtimetable.R;
import com.hyeok.kangnamunivtimetable.Utils.ControlSharedPref;

import java.lang.reflect.Type;
import java.util.Random;

/**
 * Created by GwonHyeok on 14. 7. 28..
 */
public class FullTimetableFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mainLayout, tableLayout;
    private TextView[][] dayTextView = new TextView[5][10];
    private TextView[] timeTextView = new TextView[10];
    private IButton closebutton;

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
                layoutParamss.height = 100;
                anADayTextView.setLayoutParams(layoutParamss);
            }
        }

        for (TextView aTimeTextView : timeTextView) {
            ViewGroup.LayoutParams layoutParamses = aTimeTextView.getLayoutParams();
            layoutParamses.height = 100;
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
        for(int i=0; i<5; i++) {
            drawTimeTable(i);
        }
    }

    private void drawTimeTable(int index) {
        ControlSharedPref pref = new ControlSharedPref(getActivity(), "timetable.pref");
        String TimeTableValueKey;
        switch(index) {
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
        int height = dayTextView[0][0].getLayoutParams().height;
        int width = dayTextView[0][0].getLayoutParams().width;
        int timewidth = timeTextView[0].getWidth();
        int newsize;
        if(size % 2 == 0) {
            newsize = size / 2 * height;
        } else {
            newsize = (size / 2 * height) + height / 2;
        }
        Log.d("tag", "size : "+size);
        Log.d("tag", "newsize : "+newsize);
        // Make Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, newsize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        int color = Color.rgb(r,g,b);
        paint.setColor(color);
        canvas.drawRect(0, 0, width, newsize, paint);

        Paint textpaint = new Paint();
        textpaint.setColor(Color.WHITE);
        textpaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textpaint.setTextSize(40);
        textpaint.setAntiAlias(true);
        String str_sub = value.split(" ")[0];
        String str_class = value.split(" ")[1];
        int textheight = 0;
        Log.d("tag", "sub : " + str_sub.length() + " class : " + str_class.length());
        if(str_sub.length() > 4) {
            canvas.drawText(str_sub.substring(0, 4) , 10, textheight+=40, textpaint);
            canvas.drawText(str_sub.substring(4, str_sub.length() > 8 ? 8 : str_sub.length()) , 10, textheight+=50, textpaint);
        }  else {
            canvas.drawText(str_sub.substring(0, str_sub.length()) , 10, textheight+=40, textpaint);
        }
        //noinspection UnusedAssignment
        canvas.drawText(str_class, 10, textheight+=50, textpaint);

        ImageView view = new ImageView(getActivity().getApplicationContext());
        if(Build.VERSION.SDK_INT >= 16) {
            view.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
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

    private void viewInit(View view) {
        Context mContext = view.getContext();
        tableLayout = (RelativeLayout)view.findViewById(R.id.FULL_TIME_TABLE_LAYOUT);
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

        // Button Init
        closebutton = (IButton) view.findViewById(R.id.FullTTBButton);
        closebutton.setOnClickListener(this);

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
        closebutton.setImageResource(R.drawable.ic_btn_point_close_dark);
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
