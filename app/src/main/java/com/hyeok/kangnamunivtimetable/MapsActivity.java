package com.hyeok.kangnamunivtimetable;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String geo, class_name; // Map Position Data
    private TextView map_classname_tv;
    private LinearLayout map_space_view;
    private Button btn_map_map, btn_map_share, btn_map_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mActionBarSize = ActionBarTransparent();
        setContentView(R.layout.activity_maps);
        map_classname_tv = (TextView) findViewById(R.id.map_classname_tv); // 강의실 이름 텍스트뷰.
        map_space_view = (LinearLayout) findViewById(R.id.mapview_spaceview); // 액션바 대신한 빈 뷰.
        btn_map_map = (Button) findViewById(R.id.btn_map_map); // 맵 여는 버튼.
        btn_map_share = (Button) findViewById(R.id.btn_map_share); // 맵 공유 버튼.
        btn_map_confirm = (Button) findViewById(R.id.btn_map_confirm); // 멥 확인 버튼.
        btn_map_map.setOnClickListener(this); // 버튼 리스너
        btn_map_confirm.setOnClickListener(this); // 버튼 리스너
        btn_map_share.setOnClickListener(this); // 버튼 리스너
        map_space_view.setPadding(0,mActionBarSize,0,0); // 액션바 오버레이때문에 LinearLayout padding top을 액션바 크기만큼 적용.
        geo = getIntent().getStringExtra("geo");
        class_name = getIntent().getStringExtra("class_name");
        map_classname_tv.setText(class_name);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        /**
         * 다이얼로그에서 지도 좌표를 받아서 넘기고 그 좌표값으로 마커 설정. (String geo)
         * geo = geo:0.000000,0.000000
         */
        String M_geo = geo.split("geo:")[1];
        double x_geo = Double.parseDouble(M_geo.split(",")[0]); //x좌표.
        double y_geo = Double.parseDouble(M_geo.split(",")[1]); //y좌표
        mMap.addMarker(new MarkerOptions().position(new LatLng(x_geo, y_geo)).title(class_name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(x_geo, y_geo), 16.0f));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this); //Map Click Listener
        mMap.setOnMyLocationButtonClickListener(this); //My Location Button Click Listener
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(getClass().getName(), "Map Clicked");
    }

    @Override
    public boolean onMyLocationButtonClick() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isOnGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isOnNet = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Check Location Provider (if Switch off GPS,Network Provider Show Toast.)
        if (!isOnGps || !isOnNet)
            Toast.makeText(this, getResources().getString(R.string.MAP_LOCATION_OFF), Toast.LENGTH_SHORT).show();
        return false;
    }

    private int ActionBarTransparent() {
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == btn_map_confirm.getId()) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if(id == btn_map_map.getId()) {
            mapIntent();
        } else if(id == btn_map_share.getId()) {
            mapShare();
        }
    }

    private void mapShare() {
        String GOOGLE_MAP_BASE_URL = "https://maps.google.com/?q=GEO&hl=ko&gl=kr";
        String M_geo = geo.split("geo:")[1];
        GOOGLE_MAP_BASE_URL = GOOGLE_MAP_BASE_URL.replace("GEO", M_geo);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.MAP_SHARE_TEXT), class_name, GOOGLE_MAP_BASE_URL));
        startActivity(i);
        Log.d(getClass().getName(), GOOGLE_MAP_BASE_URL);
    }

    private void mapIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
        startActivity(intent);
    }
}
