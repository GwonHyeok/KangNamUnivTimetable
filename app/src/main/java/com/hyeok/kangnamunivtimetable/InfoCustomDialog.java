package com.hyeok.kangnamunivtimetable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InfoCustomDialog extends Dialog {
	private int position=0;
    private boolean IS_DARK_THEME;
	private Button cancelbtn;
    private RelativeLayout bg_layout;
	private TextView subject_class_tv, subject_time, title_tv, class_tv;
	IButton map_btn, extratime_btn;

	final Context mContext;
	int  Map_index = 0;
	public InfoCustomDialog(Context context, String Nexttime, final int position) {
		super(context);
		mContext = context;
        ControlSharedPref settingepref = new ControlSharedPref(mContext, "setting.pref");
        IS_DARK_THEME =  settingepref.getValue(MainAppSettingActivity.TTB_THEME, 0) == 1 ?  true : false;
        settingepref = null;
        this.position = position;
        final String NORMAL = mContext.getString(R.string.CUSTOM_DIALOG_SELECT_MAP_NORMAL_MAP);
		final String CAMPUS = mContext.getString(R.string.CUSTOM_DIALOG_SELECT_MAP_CAMPUS_MAP);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_dialog);
		super.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 배경화면 레이아웃 초기화
        bg_layout = (RelativeLayout)findViewById(R.id.extratime_main_layout);
		// 제목 설정
		title_tv = (TextView)findViewById(R.id.dialog_title_tv);
		title_tv.setGravity(Gravity.CENTER);
		title_tv.setTypeface(null, Typeface.BOLD);
		// 강의실 설정
		class_tv = (TextView)findViewById(R.id.dialog_subject_class_tv);
		cancelbtn = (Button)findViewById(R.id.dialog_cancelbutton);
		subject_time = (TextView)findViewById(R.id.dialog_time_tv);
		subject_class_tv = (TextView)findViewById(R.id.dialog_class_tv);
		
		// 남은시간 버튼
        extratime_btn = (IButton)findViewById(R.id.dialog_extratime_button);
        extratime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ExtraTime.class);
                intent.putExtra("class", class_tv.getText());
                intent.putExtra("subject", subject_class_tv.getText());
                intent.putExtra("time", subject_time.getText());
                intent.putExtra("day", position);
                mContext.startActivity(intent);
            }
        });

        // 지도버튼
        map_btn = (IButton) findViewById(R.id.dialog_map_button);
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final String items[] = {NORMAL, CAMPUS};
                builder.setSingleChoiceItems(items, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map_index = which;
                            }
                        }
                ).setPositiveButton(mContext.getResources().getString(R.string.CUSTOM_DIALOG_SELECT_MAP_OK_MSG),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (Map_index) {
                                    case 0:
                                        try {
                                            String geo = class_tv.getText().toString().split(": ")[1].substring(0, 1);
                                            String tmp_uri = null;
                                            if (geo.equals("경")) {
                                                tmp_uri = Utils.GEO_GYEONCHEON;
                                            } else if (geo.equals("이")) {
                                                tmp_uri = Utils.GEO_EGONG;
                                            } else if (geo.equals("천")) {
                                                tmp_uri = Utils.GEO_CHEONN;
                                            } else if (geo.equals("교")) {
                                                tmp_uri = Utils.GEO_GOYUK;
                                            } else if (geo.equals("후")) {
                                                tmp_uri = Utils.GEO_HUSENG;
                                            } else if (geo.equals("우")) {
                                                tmp_uri = Utils.GEO_WUWON;
                                            } else if (geo.equals("예")) {
                                                tmp_uri = Utils.GEO_YESUL;
                                            } else if (geo.equals("목")) {
                                                tmp_uri = Utils.GEO_MOKYANG;
                                            } else if (geo.equals("인")) {
                                                tmp_uri = Utils.GEO_INSA;
                                            } else if (geo.equals("승")) {
                                                tmp_uri = Utils.GEO_SEUNGLEE;
                                            } else if (geo.equals("샬")) {
                                                tmp_uri = Utils.GEO_SHALROM;
                                            } else if (geo.equals("운")) {
                                                tmp_uri = Utils.GEO_UNDONG;
                                            }
                                            Intent intent = new Intent(mContext, MapsActivity.class);
                                            intent.putExtra("geo", tmp_uri);
                                            intent.putExtra("class_name", Utils.getFullClassName(geo));
                                            mContext.startActivity(intent);
                                        } catch (NullPointerException e) {
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.CUSTOM_DIALOG_NO_MAP_GEO), Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 1:
                                        mContext.startActivity(new Intent(mContext, CampusMap.class));
                                        break;
                                }
                            }
                        }
                ).setNegativeButton(mContext.getResources().getString(R.string.CUSTOM_DIALOG_SELECT_MAP_CANCEL_MSG),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                ).show();
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (IS_DARK_THEME) {
            setDarkTheme();
        }
    }


    private void setDarkTheme() {
        /**
         * 테마 구현.........
         * 하게된다면
         */
    }

	public void setClass(CharSequence msg) {
		class_tv.setGravity(Gravity.CENTER);
		String tmp_msg = msg.toString();
		
		if ( tmp_msg.contains("경")){
			tmp_msg = tmp_msg.replace("경", "경천관");
		} else if(tmp_msg.contains("이")){
			tmp_msg = tmp_msg.replace("이", "이공관");
		} else if(tmp_msg.contains("천")){
			tmp_msg = tmp_msg.replace("천", "천은관");
		} else if(tmp_msg.contains("교")){
			tmp_msg = tmp_msg.replace("교", "교육관");
		} else if(tmp_msg.contains("후")){
			tmp_msg = tmp_msg.replace("후", "후생관");
		} else if(tmp_msg.contains("우")){
			tmp_msg = tmp_msg.replace("우", "우원관");
		} else if(tmp_msg.contains("예")){
			tmp_msg = tmp_msg.replace("예", "예술관");
		} else if(tmp_msg.contains("목")){
			tmp_msg = tmp_msg.replace("목", "목양관");
		} else if(tmp_msg.contains("인")){
			tmp_msg = tmp_msg.replace("인", "인사관");
		} else if(tmp_msg.contains("승")){
			tmp_msg = tmp_msg.replace("승", "승리관");
		} else if(tmp_msg.contains("샬")){
			tmp_msg = tmp_msg.replace("샬", "샬롬관");
		} else if(tmp_msg.contains("운")){
            tmp_msg = tmp_msg.replace("운", "운동장");
        }
		class_tv.setText(mContext.getResources().getString(R.string.CUSTOM_DIALOG_CLASS_MSG)+" "+tmp_msg);
	}
	
	public void setSubject(CharSequence msg) {
		subject_class_tv.setGravity(Gravity.CENTER);
		subject_class_tv.setText(mContext.getResources().getString(R.string.CUSTOM_DIALOG_SUBJECT_MSG)+" "+msg);
	}
	
	public void setTime(CharSequence msg) {
		subject_time.setGravity(Gravity.CENTER);
		subject_time.setText(mContext.getResources().getString(R.string.CUSTOM_DIALOG_TIME_MSG)+" "+msg);
	}

	public int getCurrentColor(int pageposition) {
		int hex ;
		int newcolor;
		ControlSharedPref settingepref = new ControlSharedPref(mContext, "Setting.pref");
		switch(pageposition) {
		case 0:
			hex = Color.parseColor("#FF96AA39");
			newcolor = settingepref.getValue(MainAppSettingActivity.TTB_COLOR_MON_data, hex);
			break;
		case 1:
			hex = Color.parseColor("#FFC74B46");
			newcolor = settingepref.getValue(MainAppSettingActivity.TTB_COLOR_TUE_data, hex);
			break;
		case 2:
			hex = Color.parseColor("#FFF4842D");
			newcolor = settingepref.getValue(MainAppSettingActivity.TTB_COLOR_WEN_data, hex);
			break;
		case 3:
			hex = Color.parseColor("#FF3F9FE0");
			newcolor = settingepref.getValue(MainAppSettingActivity.TTB_COLOR_THUR_data, hex);
			break;
		case 4:
			hex = Color.parseColor("#FF5161BC");
			newcolor = settingepref.getValue(MainAppSettingActivity.TTB_COLOR_FRI_data, hex);
			break;
		default:
			hex =Color.parseColor("#FF666666");
			newcolor = hex;
			break;
		}
		return newcolor;
		}
}
