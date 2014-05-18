package com.hyeok.kangnamunivtimetable;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeveloperInformTab extends Activity implements View.OnClickListener,
        View.OnLongClickListener, ShakeDetector.OnShakeListener {
	private final String FaceBaseURL = "http://m.facebook.com/";
	private final String MHWANBLOGURL = "http://blog.naver.com/bmhjlovef";
	private final String[] id = { "Hyeok.G", "mhwanbae21" };
	private int shake_num = 0;
	private Handler s_check_handler;
	private Toast toast;
    private LinearLayout mainLinearLayout;
	private Bitmap savebitmap1, savebitmap2;
	ImageView iv1;
	ImageView iv2;
	TextView tv1, tv1_1, tv2, tv2_1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mActionBarSize = TransparentActionBar(); // TransParent ActionBar
        setContentView(R.layout.developeractivity);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		iv1 = (ImageView) findViewById(R.id.DeveloperImageView1);
		iv2 = (ImageView) findViewById(R.id.DeveloperImageView2);
		tv1 = (TextView) findViewById(R.id.DeveloperTextView1);
		tv1_1 = (TextView) findViewById(R.id.DeveloperTextView1_1);
		tv2 = (TextView) findViewById(R.id.DeveloperTextView2);
		tv2_1 = (TextView) findViewById(R.id.DeveloperTextView2_1);
        mainLinearLayout = (LinearLayout)findViewById(R.id.MainLayout);

		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv1.setOnLongClickListener(this);
		iv2.setOnLongClickListener(this);
		ShakeDetector.create(this, this);
		s_check_handler = new Handler();
		
		String msg = getResources().getString(R.string.DEVELOPER_INFO_HYEOK);
		String msg1 = getResources().getString(R.string.DEVELOPER_INFO_MHWAN);

        mainLinearLayout.setPadding(0,mActionBarSize,0,0);
        SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder(
				msg);
		mSpannableStringBuilder.setSpan(new AbsoluteSizeSpan(40), 0, 4,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		SpannableStringBuilder mSpannableStringBuilder1 = new SpannableStringBuilder(
				msg1);
		mSpannableStringBuilder1.setSpan(new AbsoluteSizeSpan(40), 0, 4,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv1.append(mSpannableStringBuilder);
		tv1_1.setText("- 위 학생은 멍청하고요.\n- 멍청합니다.\n- 만사가 귀찮고.\n- 모든것이 귀찮습니다.");
		tv2.append(mSpannableStringBuilder1);
		tv2_1.setText("- 위 학생은 멍청하고요.\n- 멍청합니다.\n- 만사가 귀찮고.\n- 모든것이 귀찮습니다.");

		/*
		 * Get Profile Image In FaceBook
		 */
		class SetProfileimage extends AsyncTask<String, Integer, Boolean> {
			private Bitmap myBitmap0;
			private Bitmap myBitmap1;

			@Override
			protected Boolean doInBackground(String... arg0) {

				if (Utils.NetWorkState(DeveloperInformTab.this)) {

					Document dc;
					try {
						// Hyeok000
						String Face_Book_Link = FaceBaseURL + id[0];
						dc = Jsoup.connect(Face_Book_Link).get();
						Elements a = dc.getElementsByTag("div");
						Element LINKELEMENT = a.get(9).getAllElements().get(1);
						String link = LINKELEMENT.toString().substring(10)
								.split("\"")[0];
						URL myImageURL = new URL(link);
						HttpURLConnection connection = (HttpURLConnection) myImageURL
								.openConnection();
						connection.setDoInput(true);
						connection.connect();
						InputStream input = connection.getInputStream();
						myBitmap0 = BitmapFactory.decodeStream(input);

						// MHWAN @@@@
						Face_Book_Link = FaceBaseURL + id[1];
						dc = Jsoup.connect(Face_Book_Link).get();
						a = dc.getElementsByTag("div");
						LINKELEMENT = a.get(9).getAllElements().get(1);
						link = LINKELEMENT.toString().substring(10).split("\"")[0];
						myImageURL = new URL(link);
						connection = (HttpURLConnection) myImageURL
								.openConnection();
						connection.setDoInput(true);
						connection.connect();
						input = connection.getInputStream();
						myBitmap1 = BitmapFactory.decodeStream(input);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);
				if (Utils.NetWorkState(DeveloperInformTab.this)) {
					iv1.setImageBitmap(myBitmap0);
					iv2.setImageBitmap(myBitmap1);
				} else {
					Toast.makeText(
							DeveloperInformTab.this,
							DeveloperInformTab.this.getResources().getString(
									R.string.Network_NotConnect_msg),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		SetProfileimage pf = new SetProfileimage();
		pf.execute();
	}

	/*
	 * Click Image go FACEBOOK Page
	 */
	@Override
	public void onClick(View arg0) {
		Intent intent = null;
		switch (arg0.getId()) {
		case R.id.DeveloperImageView1:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FaceBaseURL
					+ id[0]));
			break;
		case R.id.DeveloperImageView2:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MHWANBLOGURL));
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.DeveloperImageView1:
			iv1.setImageResource(R.drawable.ic_launcher);
			break;
		case R.id.DeveloperImageView2:
			iv2.setImageResource(R.drawable.ic_launcher);
			break;
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();
		ShakeDetector.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ShakeDetector.destroy();
		}
	
	@Override
	public void onStop() {
		super.onStop();
		ShakeDetector.stop(); 
	}
	@Override
	public void OnShake() {
		shake_num++;
		toast.setText(shake_num+"/3");
		toast.show();
		if (shake_num >= 3) {
			toast.setText("조건을 만족했다.");
			startActivity(new Intent(this, easteregg.class));
			toast.cancel();
			shake_num=0;
		}
		s_check_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				shake_num=0;
			}
		}, 1500);
	}

    private int TransparentActionBar() {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY); // 액션바 오바레이.
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar))); // 액션바 색상 설정.
        actionBar.setDisplayShowHomeEnabled(false); // 액션바 로고 제거
        View mview = getLayoutInflater().inflate(R.layout.action_bar_only_title, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mview, layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }); // 액션바 크기.
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0); // 액션바 크기.
        styledAttributes.recycle();
        return mActionBarSize;
    }
}