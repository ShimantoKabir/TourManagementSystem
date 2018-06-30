package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse;
import com.example.maask.tourmanagementsystem.DirectionFile.DirectionService;
import com.example.maask.tourmanagementsystem.DirectionFile.InstructionShowingAdapter;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener,GoogleMap.OnPolylineClickListener{

    android.support.v7.widget.Toolbar toolbar;
    GoogleMap map;
    private GoogleMapOptions googleMapOptions;
    ArrayList<LatLng> latLngs = new ArrayList<>();

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private DirectionService service;

    Button driveBT,walkBT,nextRouteBT;
    private String travel_mode = "driving";

    private int totalRoute = 0;
    private int routeIndex = 0;

    private Polyline polyline;
    private Circle circle;
    TextView durationTV,distanceTV,availableRouteTV;
    LinearLayout showDisDurLT;

    ImageView instructionIV;
    private ArrayList<String> storeInstruction = new ArrayList<>();

    private RecyclerView showInstructionRecyclerView;
    private InstructionShowingAdapter adapter;

    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;

    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        toolbar             = findViewById(R.id.custom_toolbar);
        driveBT             = findViewById(R.id.drive);
        walkBT              = findViewById(R.id.walk);
        nextRouteBT         = findViewById(R.id.next_route);
        durationTV          = findViewById(R.id.show_duration);
        availableRouteTV    = findViewById(R.id.show_available_route);
        distanceTV          = findViewById(R.id.show_distance);
        showDisDurLT        = findViewById(R.id.show_dis_dur);
        instructionIV       = findViewById(R.id.show_ins);

        progressDialog = new ProgressDialog(this);

        // toolbar ka locha ...
        toolbar.setTitle("Direction");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // retrofit ka locha ...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(DirectionService.class);

        // google option ka locha ...
        googleMapOptions = new GoogleMapOptions();
        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .zoomControlsEnabled(true);


        // map load in fragment ...
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);

        driveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                nextRouteBT.setEnabled(false);
                travel_mode = "driving";
                Toast.makeText(DirectionActivity.this, "INFO : Travel mode in Driving !", Toast.LENGTH_SHORT).show();
                driveBT.setTextColor(Color.GRAY);
                walkBT.setTextColor(Color.parseColor("#ffb300"));

            }
        });

        walkBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                nextRouteBT.setEnabled(false);
                travel_mode = "walking";
                Toast.makeText(DirectionActivity.this, "INFO : Travel mode in Cycling !", Toast.LENGTH_SHORT).show();
                walkBT.setTextColor(Color.GRAY);
                driveBT.setTextColor(Color.parseColor("#ffb300"));
            }
        });

        nextRouteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Getting next route ... ");
                progressDialog.show();

                storeInstruction.clear();
                routeIndex++;
                if (routeIndex == totalRoute){
                    routeIndex = 0;
                }
                getDirectionData(latLngs);
                map.clear();
                map.addMarker(new MarkerOptions().position(new LatLng(latLngs.get(0).latitude,latLngs.get(0).longitude)).title("Start Point").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                map.addMarker(new MarkerOptions().position(new LatLng(latLngs.get(1).latitude,latLngs.get(1).longitude)).title("Destination").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });

        instructionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Getting instruction ... ");
                progressDialog.show();

                if (storeInstruction.size()>0){

                    final AlertDialog.Builder builder = new AlertDialog.Builder(DirectionActivity.this);
                    View inflateView = getLayoutInflater().inflate(R.layout.show_instruction_lv,null);
                    showInstructionRecyclerView = inflateView.findViewById(R.id.rv_for_single_instruction);

                    LinearLayoutManager lm = new LinearLayoutManager(DirectionActivity.this);
                    lm.setOrientation(LinearLayoutManager.VERTICAL);
                    showInstructionRecyclerView.setLayoutManager(lm);

                    adapter = new InstructionShowingAdapter(storeInstruction);
                    showInstructionRecyclerView.setAdapter(adapter);
                    builder.setView(inflateView);
                    builder.setPositiveButton("OK",null);

                    progressDialog.dismiss();
                    builder.show();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(DirectionActivity.this, "ERROR : Start and destination did not marked yet OR may be network is not available ! ", Toast.LENGTH_LONG).show();
                }

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
                Intent goHome = new Intent(DirectionActivity.this,HomeActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(DirectionActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(DirectionActivity.this,LoginActivity.class);
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
        map.setOnMapLongClickListener(this);
        map.setOnPolylineClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if (latLngs.size() == 2){

            latLngs.clear();
            map.clear();
            nextRouteBT.setEnabled(false);

        }

        latLngs.add(latLng);

        if (latLngs.size() == 1){

            storeInstruction.clear();
            routeIndex = 0;
            map.addMarker(new MarkerOptions().position(latLng).title("Start Point").
                   icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }else {

            map.addMarker(new MarkerOptions().position(latLng).title("Destination").
                   icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            progressDialog.setMessage("Getting route ... ");
            progressDialog.show();
            if (!isNetworkAvailable()){
                progressDialog.dismiss();
                Toast.makeText(this, "ERROR : Network is not available ... !", Toast.LENGTH_SHORT).show();
            }
            getDirectionData(latLngs);
            nextRouteBT.setEnabled(true);
            if (isNetworkAvailable()){
                showDisDurLT.setVisibility(View.VISIBLE);
            }

        }

    }

    private void getDirectionData(ArrayList<LatLng> latLngs) {

        String api_key = getString(R.string.direction_api);

        String origin      = String.valueOf(latLngs.get(0).latitude+","+latLngs.get(0).longitude);
        String destination = String.valueOf(latLngs.get(1).latitude+","+latLngs.get(1).longitude);

        String end_url = String.format("directions/json?&mode=%s&origin=%s&destination=%s&alternatives=true&key=%s",travel_mode,origin,destination,api_key);
        Call<DirectionResponse> responseCall = service.getDirection(end_url);
        responseCall.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if (response.code() == 200 && response.isSuccessful() ){
                    DirectionResponse directionResponse = response.body();
                    totalRoute = directionResponse.getRoutes().size();

                        if (!directionResponse.getRoutes().isEmpty()){

                            availableRouteTV.setText("Routes : " + directionResponse.getRoutes().size());

                            durationTV.setText("Duration : " + directionResponse.getRoutes().get(routeIndex)
                            .getLegs().get(0).getDuration().getText());

                            distanceTV.setText("Distance : " + directionResponse.getRoutes().get(routeIndex)
                                    .getLegs().get(0).getDistance().getText());

                            List<DirectionResponse.Step> steps = directionResponse.getRoutes().get(routeIndex).getLegs().get(0).getSteps();

                            int z = 0;

                            for (int i = 0; i < steps.size() ; i++) {

                                z++;

                                double startLat = steps.get(i).getStartLocation().getLat();
                                double startLng = steps.get(i).getStartLocation().getLng();

                                double endLat = steps.get(i).getEndLocation().getLat();
                                double endLng = steps.get(i).getEndLocation().getLng();

                                LatLng start = new LatLng(startLat,startLng);
                                LatLng end   = new LatLng(endLat,endLng);

                                int color = chooseColor(z);

                                polyline = map.addPolyline(new PolylineOptions()
                                        .add(start)
                                        .add(end)
                                        .width(7)
                                        .color(color)
                                        .clickable(true));

                                circle = map.addCircle(new CircleOptions()
                                        .center(new LatLng(startLat, startLng))
                                        .radius(20)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.BLUE));

                                String instruction = String.valueOf(Html.fromHtml(steps.get(i).getHtmlInstructions()));
                                storeInstruction.add(instruction);
                                polyline.setTag(instruction);

                            }

                            double lstLat = steps.get(steps.size()-1).getEndLocation().getLat();
                            double lstLng = steps.get(steps.size()-1).getEndLocation().getLng();
                            circle = map.addCircle(new CircleOptions()
                                    .center(new LatLng(lstLat, lstLng))
                                    .radius(30)
                                    .strokeColor(Color.BLUE)
                                    .fillColor(Color.RED));

                            progressDialog.dismiss();

                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(DirectionActivity.this, "SORRY : No route available !", Toast.LENGTH_SHORT).show();
                        }

                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {
                Log.e("Error :" , t.getMessage());
            }
        });


    }

    private int chooseColor(int i) {

        if (i==11){
            i=0;
        }

        int color;

        if (i == 1){
            color = Color.MAGENTA;
        }else if (i == 2){
            color = Color.DKGRAY;
        }else if (i == 3){
            color = Color.GREEN;
        }else if(i == 4){
            color = Color.DKGRAY;
        }else if(i == 5){
            color = Color.BLUE;
        }else if(i == 6){
            color = Color.DKGRAY;
        }else if(i == 7){
            color = Color.RED;
        }else if(i == 8){
            color = Color.DKGRAY;
        }else if(i == 9){
            color = Color.YELLOW;
        }else if(i == 10){
            color = Color.DKGRAY;
        }else {
            color = Color.BLACK;
        }

        return color;

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        Toast.makeText(this, polyline.getTag().toString(), Toast.LENGTH_LONG).show();
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

}
