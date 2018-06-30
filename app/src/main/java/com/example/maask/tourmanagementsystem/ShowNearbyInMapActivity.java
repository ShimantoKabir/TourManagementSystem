package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.NearbyDistance.DistanceResponse;
import com.example.maask.tourmanagementsystem.NearbyDistance.DistanceService;
import com.example.maask.tourmanagementsystem.NearbyFile.NearbyResponse;
import com.example.maask.tourmanagementsystem.NearbyFile.NearbyService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowNearbyInMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    android.support.v7.widget.Toolbar toolbar;
    GoogleMap map;
    private GoogleMapOptions googleMapOptions;

    private Polyline polyline;
    private Circle circle;

    Double nearbyLat,nearbyLng,myLat,myLng;
    String nearbyName,nearbyAddress;

    private TextView address,distance,duration;

    // base url for fetch distance matrix data
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private DistanceService distanceService;

    SharedPreferences sharedPreferences;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_nearby_in_map);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        nearbyLat = getIntent().getExtras().getDouble("nearbyLat");
        nearbyLng = getIntent().getExtras().getDouble("nearbyLng");

        myLat = getIntent().getExtras().getDouble("myLat");
        myLng = getIntent().getExtras().getDouble("myLng");


        nearbyName = getIntent().getExtras().getString("nearbyName");
        nearbyAddress = getIntent().getExtras().getString("nearbyAddress");

        // custom toolbar ...
        toolbar = findViewById(R.id.custom_toolbar);
        address = findViewById(R.id.address);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);

        // toolbar ka locha ...
        toolbar.setTitle(nearbyName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        address.setText("Address : "+ nearbyAddress);

        // google option ka locha ...
        googleMapOptions = new GoogleMapOptions();
        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .zoomControlsEnabled(true);

        // map load in fragment ...
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment);
        ft.commit();

        // internally sync map in the fragment ...
        mapFragment.getMapAsync(this);

        // retrofit ka locha ...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        distanceService = retrofit.create(DistanceService.class);

        // get distance and duration
        showDistanceMatrixData();

    }

    private void showDistanceMatrixData() {

        String api_key  = getString(R.string.distance_api);
        String end_url = String.format("distancematrix/json?units=metric&origins=%f,%f&destinations=%f,%f&key=%s",myLat,myLng,nearbyLat,nearbyLng,api_key);

        Call<DistanceResponse> responseCall = distanceService.getDistancData(end_url);
        responseCall.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                if (response.code() == 200 && response.isSuccessful()){

                    String getAddress = response.body().getDestinationAddresses().get(0);
                    String getDistance = response.body().getRows().get(0).getElements().get(0).getDistance().getText();
                    String getDuration = response.body().getRows().get(0).getElements().get(0).getDuration().getText();
                    address.setText("Address : "+getAddress);
                    distance.setText("Distance : " + getDistance);
                    duration.setText("Duration : "+getDuration);

                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                Log.e("Massage - > ", "onFailure: " + t.getMessage() );
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sub_toolbar,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                Intent goHome = new Intent(ShowNearbyInMapActivity.this,NearbyActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(ShowNearbyInMapActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(ShowNearbyInMapActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }

        map.setMyLocationEnabled(true);

        LatLng start = new LatLng(nearbyLat,nearbyLng);
        LatLng end   = new LatLng(myLat,myLng);

        map.addMarker(new MarkerOptions().position(start).title(nearbyName).snippet(nearbyAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat,myLng), 13.0f));


        circle = map.addCircle(new CircleOptions()
                .center(new LatLng(nearbyLat, nearbyLng))
                .radius(20)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowNearbyInMapActivity.this,NearbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
