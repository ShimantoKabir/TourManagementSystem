package com.example.maask.tourmanagementsystem;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.maask.tourmanagementsystem.GeoFenceFile.GeoFenceResponse;
import com.example.maask.tourmanagementsystem.GeoFenceFile.GeoFenceService;
import com.example.maask.tourmanagementsystem.GeoFenceFile.GeoFencingIntentService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoFenceActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    private GoogleMapOptions googleMapOptions;

    android.support.v7.widget.Toolbar toolbar;

    private static final String PREFERENCES_KEY = "tms_preferences";
    private static final String VISIT_LOGIN = "visit_login";

    SharedPreferences sharedPreferences;

    private GeofencingClient client;
    private PendingIntent pendingIntent;

    private ArrayList<Geofence> geofences = new ArrayList<>();

    private EditText howFarET,expiredDurationET;

    private int radius = 300;
    private int duration = 24*60*60*1000;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private GeoFenceService geoFenceService;
    String getAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        howFarET = findViewById(R.id.how_far);
        expiredDurationET = findViewById(R.id.expired_duration);

        // toolbar ka locha ........
        toolbar = findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Place Notify");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        howFarET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeoFenceActivity.this);
                builder.setTitle("Select Radius - Unit meter");
                builder.setSingleChoiceItems(R.array.radius, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] array = getResources().getStringArray(R.array.radius);
                        howFarET.setText(array[i]);
                        radius = Integer.parseInt(array[i]);

                    }
                });
                builder.setPositiveButton("OK",null);
                builder.show();
            }
        });

        final CharSequence[] array = {"1 Hour", "6 Hour", "12 Hour","1 Day","2 Day","3 Day","5 Day"};

        expiredDurationET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeoFenceActivity.this);
                builder.setTitle("Select Duration");
                builder.setSingleChoiceItems(array, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] arrayFromResource = getResources().getStringArray(R.array.duration);
                        expiredDurationET.setText(array[i]);
                        duration = Integer.parseInt(arrayFromResource[i]);

                    }
                });
                builder.setPositiveButton("OK",null);
                builder.show();
            }
        });

        client = LocationServices.getGeofencingClient(this);
        pendingIntent = null;

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
                Intent goHome = new Intent(GeoFenceActivity.this,HomeActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(GeoFenceActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(GeoFenceActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }

        map.setMyLocationEnabled(true);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                geoFenceService = retrofit.create(GeoFenceService.class);

                String foundAddress = findLocationAddress(latLng.latitude,latLng.longitude);

                if (foundAddress.isEmpty()){

                    Toast.makeText(GeoFenceActivity.this, "WARNING : Address not found please try again ... !", Toast.LENGTH_SHORT).show();

                }else{

                    setLatLngAndAddressToNotify(latLng,foundAddress);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).title(foundAddress);
                    map.addMarker(markerOptions);

                }


            }
        });

    }

    private String findLocationAddress(double latitude, double longitude) {

        String api_key  = getString(R.string.geo_code_api);

        String end_url = String.format("json?latlng=%f,%f&key=%s",latitude,longitude,api_key);

        Call<GeoFenceResponse> responseCall = geoFenceService.getAddressFromLatLng(end_url);
        responseCall.enqueue(new Callback<GeoFenceResponse>() {
            @Override
            public void onResponse(Call<GeoFenceResponse> call, Response<GeoFenceResponse> response) {
                if (response.code() == 200 && response.isSuccessful()){

                    int size = response.body().getResults().size();
                    if (size > 0){
                        getAddress = response.body().getResults().get(0).getFormattedAddress();
                    }

                }
            }

            @Override
            public void onFailure(Call<GeoFenceResponse> call, Throwable t) {
                Log.e("Massage - > ", "onFailure: " + t.getMessage() );
            }
        });

        return getAddress;

    }

    private GeofencingRequest getGeoFencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofences);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder.build();
    }

    private PendingIntent getPendingIntent(){
        if (pendingIntent!=null){
            return pendingIntent;
        }else {
            Intent intent = new Intent(GeoFenceActivity.this,GeoFencingIntentService.class);
            pendingIntent = PendingIntent.getService(this,101,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
    }

    private void setLatLngAndAddressToNotify(LatLng latLng, String foundAddress) {

        Double lat = latLng.latitude;
        Double lng = latLng.longitude;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(foundAddress)
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(duration)
                .build();

        geofences.add(geofence);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},10);
            return;
        }

        client.addGeofences(getGeoFencingRequest(),getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(GeoFenceActivity.this, "INFO : Place will be notified !", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
