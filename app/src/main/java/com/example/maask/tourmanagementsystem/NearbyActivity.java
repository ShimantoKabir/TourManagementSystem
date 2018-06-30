package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.NearbyFile.NearbyResponse;
import com.example.maask.tourmanagementsystem.NearbyFile.NearbyService;
import com.example.maask.tourmanagementsystem.NearbyFile.NearbyShowingAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;

    EditText radiusET;
    ImageView otherPlaceIV;

    // fused location provide device last location
    private FusedLocationProviderClient client;
    private Double lat;
    private Double lng;

    // create nearby service instance
    private NearbyService nearbyService;

    // base url for fetch data
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    // Array list for nearby resutl
    List<NearbyResponse.Result> nearbyList;

    // asset for show nearby place list
    private ListView nearbyListView;
    private NearbyShowingAdapter adapter;

    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        client = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        radiusET     = findViewById(R.id.radius_edit);
        otherPlaceIV = findViewById(R.id.other_place);

        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Nearby Places");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radiusET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NearbyActivity.this);
                builder.setTitle("Select Radius - Unit meter");
                builder.setSingleChoiceItems(R.array.radius, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] array = getResources().getStringArray(R.array.radius);
                        radiusET.setText(array[i]);

                    }
                });
                builder.setPositiveButton("OK",null);
                builder.show();
            }
        });

        final CharSequence[] placeName = {"Mosque", "Museum", "Park","Pharmacy","Subway Station","Supermarket",
                "Train Station","Embassy","Gym","Hindu Temple","Local Government Office","Bus Station"};

        otherPlaceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NearbyActivity.this);
                builder.setTitle("Other Places");
                builder.setSingleChoiceItems(placeName, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] placesArray = getResources().getStringArray(R.array.places);

                        if (radiusET.getText().toString().isEmpty()){

                            if (isNetworkAvailable()){

                                progressDialog.setMessage("Getting nearby place list ... ");
                                progressDialog.show();
                                getDefaultNearbyPlaceData(placesArray[i]);

                            }else {
                                Toast.makeText(NearbyActivity.this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                            }

                        }else {

                            if (isNetworkAvailable()){
                                progressDialog.setMessage("Getting nearby place list ... ");
                                progressDialog.show();
                                getNearbyPlaceData(placesArray[i]);
                            }else {
                                Toast.makeText(NearbyActivity.this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
                builder.setPositiveButton("OK",null);
                builder.show();



            }
        });

        // retrofit ka locha ...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nearbyService = retrofit.create(NearbyService.class);

    }

    private void getNearbyPlaceData(String placeType) {

        String api_key  = getString(R.string.nearby_api);
        int radius = Integer.parseInt(radiusET.getText().toString());

        String end_url = String.format("nearbysearch/json?location=%f,%f&radius=%d&type=%s&key=%s",lat,lng,radius,placeType,api_key);

        Call<NearbyResponse> responseCall = nearbyService.getNearbyPlace(end_url);
        responseCall.enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(Call<NearbyResponse> call, Response<NearbyResponse> response) {
                if (response.code() == 200 && response.isSuccessful() ){

                    if (response.body().getResults().size() > 0){
                        nearbyList = response.body().getResults();
                        showNearbyPlaceList(nearbyList);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(NearbyActivity.this, "INFO : No nearby place found !", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {
                Log.e("Massage - > ", "onFailure: " + t.getMessage() );
            }
        });

    }

    private void getDefaultNearbyPlaceData(String placeType) {

        String api_key  = getString(R.string.nearby_api);
        int radius = 300;

        String end_url = String.format("nearbysearch/json?location=%f,%f&radius=%d&type=%s&key=%s",lat,lng,radius,placeType,api_key);

        Call<NearbyResponse> responseCall = nearbyService.getNearbyPlace(end_url);
        responseCall.enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(Call<NearbyResponse> call, Response<NearbyResponse> response) {
                if (response.code() == 200 && response.isSuccessful() ){

                    if (response.body().getResults().size() > 0){
                        nearbyList = response.body().getResults();
                        showNearbyPlaceList(nearbyList);
                    }else {
                        Toast.makeText(NearbyActivity.this, "INFO : No nearby place found !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }

            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {
                Log.e("Massage - > ", "onFailure: " + t.getMessage() );
            }
        });

    }

    private void showNearbyPlaceList(final List<NearbyResponse.Result> nearbyList) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NearbyActivity.this);
        View inflateView = getLayoutInflater().inflate(R.layout.show_nearby_lv,null);
        nearbyListView = inflateView.findViewById(R.id.rv_for_nearby_place);

        adapter = new NearbyShowingAdapter(this,nearbyList);
        nearbyListView.setAdapter(adapter);
        builder.setView(inflateView);
        builder.show();
        progressDialog.dismiss();
        nearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(NearbyActivity.this,ShowNearbyInMapActivity.class);

                intent.putExtra("nearbyName",nearbyList.get(i).getName());
                intent.putExtra("nearbyAddress",nearbyList.get(i).getVicinity());

                intent.putExtra("nearbyLat",nearbyList.get(i).getGeometry().getLocation().getLat());
                intent.putExtra("nearbyLng",nearbyList.get(i).getGeometry().getLocation().getLng());

                intent.putExtra("myLat",lat);
                intent.putExtra("myLng",lng);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

    }

    public void choosePlaceType(View view) {

        int id = view.getId();

        switch (id){

        case R.id.restaurant:

            if (radiusET.getText().toString().isEmpty()){

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getDefaultNearbyPlaceData("restaurant");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }else {

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getNearbyPlaceData("restaurant");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }

        break;

        case R.id.hospital:

            if (radiusET.getText().toString().isEmpty()){

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getDefaultNearbyPlaceData("hospital");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }else {

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getNearbyPlaceData("hospital");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }
            break;

        case R.id.travel_agency:

            if (radiusET.getText().toString().isEmpty()){

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getDefaultNearbyPlaceData("travel_agency");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }else {

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getNearbyPlaceData("bus_station");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        case R.id.atm_both:

            if (radiusET.getText().toString().isEmpty()){

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getDefaultNearbyPlaceData("atm");
                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }


            }else {

                if (isNetworkAvailable()){

                    progressDialog.setMessage("Getting nearby place list ... ");
                    progressDialog.show();
                    getNearbyPlaceData("atm");

                }else {
                    Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }


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
                Intent goHome = new Intent(NearbyActivity.this,HomeActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(NearbyActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(NearbyActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }

        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isComplete() && task.getResult() != null){
                    Location lastLocation = task.getResult();
                    lat = lastLocation.getLatitude();
                    lng = lastLocation.getLongitude();
                }
            }
        });

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NearbyActivity.this,HomeActivity.class);
        startActivity(intent);
    }

}
