package com.example.recommendfood;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.RoadViewRequest;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.LabelTextStyle;
import com.kakao.vectormap.label.LabelTransition;
import com.kakao.vectormap.label.LodLabelLayer;
import com.kakao.vectormap.label.Transition;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {

    private KakaoMap kakaoMap;
    private LabelLayer labelLayer;
    private LodLabelLayer lodLabelLayer;
    private LatLng curLocation;
    private MapView mapView;
    private String foodTitle;

    int num = 0;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String BASE_URL = "https://dapi.kakao.com/";
    private static final String API_KEY = "KakaoAK d0b385bcedda58d7417e55bcbdf2d7de";  // REST API 키
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        KakaoMapSdk.init(this, "57466a70abb00a62cfa5302e42a5b09b");

        mapView = findViewById(R.id.map_view);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        foodTitle = getIntent().getStringExtra("foodtitle");

        // 권한 확인
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {

            }

            @Override
            public void onMapError(Exception e) {

            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap map) {
                kakaoMap = map;
                labelLayer = kakaoMap.getLabelManager().getLayer();

                // 지도 준비 완료 후 위치 업데이트 및 라벨 표시
                getCurrentLocation();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    // 사용자의 현재 위치 반환
    private void getCurrentLocation() {
        LatLng HBNU = LatLng.from(36.35114007573212, 127.30095002293778);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    curLocation = LatLng.from(location.getLatitude(), location.getLongitude());
                    // 위치 업데이트 후 라벨 표시 및 searchKeyword 메소드 실행
                    showCurLocationLabel("curloclabel");
                    if (foodTitle != null && !foodTitle.isEmpty()) {
                        searchKeyword(foodTitle);
                    }
                }
            }
        });
    }

    // 음식 이름을 키워드로 주변 검색
    private void searchKeyword(String keyword) {
        if (curLocation == null) {
            Toast.makeText(this, "Current location is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApiService api = retrofit.create(KakaoApiService.class);

        String longitude = String.valueOf(curLocation.getLongitude());
        String latitude = String.valueOf(curLocation.getLatitude());
        int radius = 1000;  // 검색 반경 (단위: 미터)

        Call<KakaoSearchResponse> call = api.searchLocation(API_KEY, keyword, longitude, latitude, radius);

        call.enqueue(new Callback<KakaoSearchResponse>() {
            @Override
            public void onResponse(Call<KakaoSearchResponse> call, Response<KakaoSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Document> places = response.body().getDocuments();
                    if (places.isEmpty()) {
                        Toast.makeText(MapActivity.this, "주변에 해당 음식점이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    for (Document place : places) {
                        double latitude = Double.parseDouble(place.getY());
                        double longitude = Double.parseDouble(place.getX());
                        LatLng placeLocation = LatLng.from(latitude, longitude);
                        String place_name = String.valueOf(place.getPlaceName());

                        showIconLabel("placelabel" + num, place_name ,placeLocation);
                        num += 1;
                    }
                }
                else {
                    Log.d("KakaoAPI", "No search results found.");
                    Toast.makeText(MapActivity.this, "No search results found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KakaoSearchResponse> call, Throwable t) {
                Log.w("KakaoAPI", "통신 실패: " + t.getMessage());
                Toast.makeText(MapActivity.this, "Failed to fetch search results.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIconLabel(String labelId, String placeName, LatLng placeLocation) {
        // 라벨 스타일 생성
        LabelStyles styles = kakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker).setTextStyles(
                                LabelTextStyle.from(getBaseContext(), R.style.labelTextStyle_1))
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))));

        labelLayer.addLabel(LabelOptions.from(labelId, placeLocation).setStyles(styles)
                .setTexts(placeName));
    }

    private void showCurLocationLabel(String labelId) {
        LabelStyles styles = kakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.cur_location_marker)
                        .setTextStyles(LabelTextStyle.from(getBaseContext(), R.style.labelTextStyle_1))
                        .setIconTransition(LabelTransition.from(Transition.None, Transition.None))));

        labelLayer.addLabel(LabelOptions.from(labelId, curLocation).setStyles(styles)
                .setTexts("현재 위치"));

        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(curLocation, 14));
    }
}
