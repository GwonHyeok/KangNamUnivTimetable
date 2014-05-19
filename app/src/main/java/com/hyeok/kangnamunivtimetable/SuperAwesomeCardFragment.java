/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyeok.kangnamunivtimetable;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SuperAwesomeCardFragment extends Fragment {
	InfoCustomDialog dialog;
	
	private static final String ARG_POSITION = "position";
	private int position;
    private boolean IS_DARK_THEME = false;

    public static SuperAwesomeCardFragment newInstance(int position) {
		SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public void onPause() {
		try {
		if ( dialog != null) {dialog.dismiss(); dialog = null;}
		   } catch (Exception e) {
		        e.printStackTrace();
		    }
		super.onPause();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ControlSharedPref pref = new ControlSharedPref(getActivity(), "timetable.pref");
		ControlSharedPref settingpref = new ControlSharedPref(getActivity(), "Setting.pref");
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);

		final int LRmargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
                .getDisplayMetrics());
		final int Topmargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
                .getDisplayMetrics());
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());
        IS_DARK_THEME = settingpref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1 ? true : false;
        final ArrayList<ListViewData> list = new ArrayList<ListViewData>();
		String gonggangmsg = settingpref.getValue(MainAppSettingActivity.GONGGANG_MESSAGE_data, getResources().getString(R.string.DAY_GONGGANG));
		int prefsize = pref.getAll().size()/5 -3;
		int tmp;
		String time;
        int color = TimeTableMain.getCurrentColor(getActivity(), position);
		if ( position == 0){
			for(int i=0; prefsize != i; i++) {
				if ( !pref.getValue("mon_"+i, "null").equals("null") && !pref.getValue("mon_"+i, "").equals(pref.getValue("mon_"+(i+1), ""))) {
					tmp = i;
					while(tmp!=0){
					if (!pref.getValue("mon_"+tmp, "").equals(pref.getValue("mon_"+(tmp-1), ""))) break;
					tmp--;
					}
					time = TIME(getActivity(), tmp, i);
					list.add(new ListViewData(time, pref.getValue("mon_"+i, "").replace("null", "").split(" ")[0],pref.getValue("mon_"+i, "").replace("null", "").split(" ")[1],color));
				}

				
			}
			if (list.size() == 0) list.add(new ListViewData(null, gonggangmsg, null,color));
		} else if ( position == 1) {
			for(int i=0; prefsize != i; i++) {
				if ( !pref.getValue("tues"+i, "null").equals("null") && !pref.getValue("tues"+i, "").equals(pref.getValue("tues"+(i+1), ""))) {
					tmp = i;
					while(tmp!=0){
						if (!pref.getValue("tues"+tmp, "").equals(pref.getValue("tues"+(tmp-1), ""))) break;
						tmp--;
					}
					time = TIME(getActivity(), tmp, i);
					list.add(new ListViewData(time, pref.getValue("tues"+i, "").replace("null", "").split(" ")[0],pref.getValue("tues"+i, "").replace("null", "").split(" ")[1],color));
				}
			}
			if (list.size() == 0) list.add(new ListViewData(null, gonggangmsg,null,color));
		} else if ( position == 2) {
			for(int i=0; prefsize != i; i++) {
				if ( !pref.getValue("wends"+i, "null").equals("null") && !pref.getValue("wends"+i, "").equals(pref.getValue("wends"+(i+1), ""))) {
					tmp = i;
					while(tmp!=0){
						if (!pref.getValue("wends"+tmp, "").equals(pref.getValue("wends"+(tmp-1), ""))) break;
						tmp--;
					}
					time = TIME(getActivity(), tmp, i);
					list.add(new ListViewData(time, pref.getValue("wends"+i, "").replace("null", "").split(" ")[0],pref.getValue("wends"+i, "").replace("null", "").split(" ")[1],color));
				}
			}
			if (list.size() == 0) list.add(new ListViewData(null, gonggangmsg ,null,color));
		} else if (position ==3 ) {
			for(int i=0; prefsize != i; i++) {
				if ( !pref.getValue("thur"+i, "null").equals("null") && !pref.getValue("thur"+i, "").equals(pref.getValue("thur"+(i+1), ""))) {
					tmp = i;
					while(tmp!=0){
						if (!pref.getValue("thur"+tmp, "").equals(pref.getValue("thur"+(tmp-1), ""))) break;
						tmp--;
					}
					time = TIME(getActivity(), tmp, i);
					list.add(new ListViewData(time, pref.getValue("thur"+i, "").replace("null", "").split(" ")[0],pref.getValue("thur"+i, "").replace("null", "").split(" ")[1],color));
				}
			}
			if (list.size() == 0) list.add(new ListViewData(null, gonggangmsg ,null,color));
		} else if (position ==4 ) {
			for(int i=0; prefsize != i; i++) {
				if ( !pref.getValue("fri"+i, "null").equals("null") && !pref.getValue("fri"+i, "").equals(pref.getValue("fri"+(i+1), ""))) {
					tmp = i;
					while(tmp!=0){
						if (!pref.getValue("fri"+tmp, "").equals(pref.getValue("fri"+(tmp-1), ""))) break;
						tmp--;
					}
					time = TIME(getActivity(), tmp, i);
					list.add(new ListViewData(time, pref.getValue("fri"+i, "").replace("null", "").split(" ")[0],pref.getValue("fri"+i, "").replace("null", "").split(" ")[1],color));
				}
			}
			if (list.size() == 0) list.add(new ListViewData(null, gonggangmsg ,null,color));
		}
		
		CustomListAdapter adapter = new CustomListAdapter(getActivity(), R.layout.listview_layout, list);
		
		ListView v = new ListView(getActivity());
		params.setMargins(LRmargin, Topmargin, LRmargin, margin);
		v.setLayoutParams(params);
        v.setAdapter(adapter);
        // 테마 리스트뷰 구분선 색상.
        Drawable divider_drawable = IS_DARK_THEME ? new ColorDrawable(getResources().getColor(R.color.background_main_dark)) : new ColorDrawable(getResources().getColor(R.color.background_main));
        v.setDivider(divider_drawable);
        v.setDividerHeight(28);
		v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView subjecttv = (TextView) arg1.findViewById(R.id.listview_textview_subject);
                TextView timetv = (TextView) arg1.findViewById(R.id.listview_textview_time);
                TextView classtv = (TextView) arg1.findViewById(R.id.listview_textview_class);
                dialog = new InfoCustomDialog(getActivity(), null, position);
                dialog.setSubject(subjecttv.getText());
                //강의실 설정
                dialog.setClass(classtv.getText());
                //시간 얻기
                CharSequence time = timetv.getText().toString();
                dialog.setTime(time);

                android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(params);
                dialog.show();
            }
        });
		fl.addView(v);
        return fl;
	}
	
	public String TIME(Context mContext, int tmp, int i) {
			ControlSharedPref pref = new ControlSharedPref(mContext, "timetable.pref");
			String Starttime = pref.getValue("time"+tmp, "").split("-")[0];
			String Lasttime = pref.getValue("time"+i, "").split("-")[1];
			String time = Starttime+"-"+Lasttime;
	    	return time;
	    }
}

class CustomListAdapter extends ArrayAdapter<ListViewData> {
	 
    private ArrayList<ListViewData> items;
    private ControlSharedPref settingpref;
    private boolean IS_DARK_THEME = false;

    public CustomListAdapter(Context context, int textViewResourceId,
            ArrayList<ListViewData> items) {
        super(context, textViewResourceId, items);
        this.items = items;
		settingpref = new ControlSharedPref(context, "Setting.pref");
        IS_DARK_THEME = settingpref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1 ? true : false;
        }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
             
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_layout, null);
        }
        ListViewData custom_list_data = items.get(position);

        if (custom_list_data != null) {
            TextView tv_Time = (TextView) v.findViewById(R.id.listview_textview_time);
            TextView tv_Subject = (TextView) v.findViewById(R.id.listview_textview_subject);
            TextView tv_class = (TextView) v.findViewById(R.id.listview_textview_class);
            View color_view = (View) v.findViewById(R.id.listview_ColorView);
            RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.listview_relativelayout);

            int timesize = settingpref.getValue(MainAppSettingActivity.TTB_TIME_SIZE_data, 16);
            int subjectsize = settingpref.getValue(MainAppSettingActivity.TTB_SUBJECT_SIZE_data, 18);
            int classtsize = settingpref.getValue(MainAppSettingActivity.TTB_CLASS_SIZE_data, 14);

            tv_Time.setText(custom_list_data.gettime());
            tv_Time.setTextSize(TypedValue.COMPLEX_UNIT_SP, timesize);
            tv_Subject.setText(custom_list_data.getsubject());
            tv_Subject.setTextSize(TypedValue.COMPLEX_UNIT_SP, subjectsize);
            tv_class.setText(custom_list_data.getClassName());
            tv_class.setTextSize(TypedValue.COMPLEX_UNIT_SP, classtsize);
            color_view.setBackgroundColor(custom_list_data.getColor());

            /**
             *  리스트뷰 레이아웃 테두리 처리.
             */
            RectShape rect = new RectShape();
            ShapeDrawable rectShapeDrawable = new ShapeDrawable(rect);
            Paint paint = rectShapeDrawable.getPaint();
            paint.setColor(custom_list_data.getColor());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            if (Build.VERSION.SDK_INT >= 16 ) {
                layout.setBackground(rectShapeDrawable);
            } else {
                layout.setBackgroundDrawable(rectShapeDrawable);
            }

            /**
             * 다크 테마 처리.
             */
            if(IS_DARK_THEME) {
                tv_Time.setTextColor(v.getResources().getColor(R.color.fontcolor_main_dark));
                tv_class.setTextColor(v.getResources().getColor(R.color.fontcolor_main_dark));
                tv_Subject.setTextColor(v.getResources().getColor(R.color.fontcolor_main_dark));
            }
        }
 
        return v;
    }
    
}

class ListViewData {
	private String time;
	private String classname;
	private String subject;
    private int color;
	
	public ListViewData(String time, String subject, String classname, int color) {
		this.time = time;
		this.classname = classname;
		this.subject = subject;
        this.color = color;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setClassName(String classname) {
		this.classname = classname;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
    public void setColor(int color) {
        this.color = color;
    }
 	public String gettime() {
		return time;
	}
	public String getClassName() {
		return classname;
	}
	public String getsubject() {
		return subject;
	}
    public int getColor() {
        return color;
    }
}