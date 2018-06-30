package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.LikelihoodFile.LikelihoodPlaceShowingAdapter;
import com.example.maask.tourmanagementsystem.LikelihoodFile.MarkerItem;
import com.example.maask.tourmanagementsystem.LikelihoodFile.StoreLikelihoodPlaces;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import java.util.ArrayList;
import java.util.List;

public class PlaceLikelihoodActivity extends AppCompatActivity implements OnMapReadyCallback {

    android.support.v7.widget.Toolbar toolbar;

    BottomNavigationView bottomNavigationView;

    GoogleMap map;
    private GoogleMapOptions googleMapOptions;

    private FusedLocationProviderClient client;

    private Location lastLocation;

    private GeoDataClient geoDataClient;
    private PlaceDetectionClient detectionClient;

    private ClusterManager<MarkerItem> clusterManager;

    private ArrayList<StoreLikelihoodPlaces> placesArrayList;

    private final int PLACE_PICKER_REQUEST_CODE = 1;

    ListView showLikelihoodPlacesList;
    private LikelihoodPlaceShowingAdapter adapter;
    private Bitmap bitmap;

    private LatLng mypos;

    private MarkerItem markerItem;
    private ArrayList<MarkerItem> items = new ArrayList<>();

    ImageView pickerIV;
    TextView pickerNameTV,pickerAddressTV,pickerPhoNumberTV,pikcerRatingTV;

    private ProgressDialog progressDialog;
    private ProgressBar pickerImgLoader;

    SharedPreferences sharedPreferences;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_likelihood);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        toolbar = findViewById(R.id.custom_toolbar);
        bottomNavigationView = findViewById(R.id.likelihood_bottom_navigation);

        progressDialog = new ProgressDialog(this);

        googleMapOptions = new GoogleMapOptions();
        client = LocationServices.getFusedLocationProviderClient(this);

        placesArrayList = new ArrayList<>();

        geoDataClient   = Places.getGeoDataClient(this,null);
        detectionClient = Places.getPlaceDetectionClient(this,null);

        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true);

        toolbar.setTitle("Likelihood Places");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);

        bottomNavigationListener();

    }

    private void getLikelihoodPlacesList() {

        progressDialog.setMessage("Getting likelihood place list ... ");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }

        detectionClient.getCurrentPlace(null).addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                if (!task.getResult().equals(null)){
                    PlaceLikelihoodBufferResponse response = task.getResult();
                    int countResponse = response.getCount();

                        for (int i = 0; i < countResponse; i++) {
                            PlaceLikelihood place = response.get(i);
                            placesArrayList.add(new StoreLikelihoodPlaces(
                                (String) place.getPlace().getName(),
                                (String) place.getPlace().getAddress(),
                                (String) place.getPlace().getPhoneNumber(),
                                place.getPlace().getLatLng(),
                                place.getPlace().getRating()

                            ));
                        }
                    showCurrentPlacesList(placesArrayList);
                    response.release();


                }

            }
        });



    }

    private void showCurrentPlacesList(final ArrayList<StoreLikelihoodPlaces> placesArrayList) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflateView = getLayoutInflater().inflate(R.layout.show_likelihood_places_lv,null);

        showLikelihoodPlacesList = inflateView.findViewById(R.id.lv_for_single_likelihood_places);
        adapter = new LikelihoodPlaceShowingAdapter(this,placesArrayList);
        showLikelihoodPlacesList.setAdapter(adapter);

        builder.setView(inflateView);

        showLikelihoodPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                markerItem = new MarkerItem(placesArrayList.get(i).getPlaceLatLng(),
                        placesArrayList.get(i).getPlaceName(),placesArrayList.get(i).getPlaceAddress());
                items.add(markerItem);
                clusterManager.addItems(items);
                clusterManager.cluster();

                Toast.makeText(PlaceLikelihoodActivity.this, "INFO : " + placesArrayList.get(i).getPlaceName() + " -- has been marked in the map !", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setPositiveButton("See In Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getLastLocation();
            }
        });
        progressDialog.dismiss();
        builder.show();


    }

    private void bottomNavigationListener() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id) {

                    case R.id.location:
                        getLastLocation();
                        map.clear();
                        break;

                    case R.id.recent:
                        if (isNetworkAvailable()){
                            getLikelihoodPlacesList();
                        }else {
                            Toast.makeText(PlaceLikelihoodActivity.this, "ERROR : Network is not Available !", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.pick:
                        map.clear();
                        if (isNetworkAvailable()){
                            showPlacePickerWidget();
                        }else {
                            Toast.makeText(PlaceLikelihoodActivity.this, "ERROR : Network is not Available !", Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

                return true;

            }
        });

    }

    private void showPlacePickerWidget() {

        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sub_toolbar, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                Intent goHome = new Intent(PlaceLikelihoodActivity.this,HomeActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(PlaceLikelihoodActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(PlaceLikelihoodActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        getLastLocation();

        clusterManager = new ClusterManager<MarkerItem>(this, map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }

        map.setMyLocationEnabled(true);

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
                    lastLocation = task.getResult();
                    mypos = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(mypos, 12.0f));

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PLACE_PICKER_REQUEST_CODE:
                if(resultCode == RESULT_OK){

                    Place place = PlacePicker.getPlace(this,data);
                    showPlacePickerInfo(place);

                }
            break;
        }
    }

    private void showPlacePickerInfo(Place place) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(PlaceLikelihoodActivity.this);
        View inflateView = getLayoutInflater().inflate(R.layout.place_picker_result,null);

        pickerIV = inflateView.findViewById(R.id.picker_image);
        pickerNameTV = inflateView.findViewById(R.id.picker_name);
        pickerAddressTV = inflateView.findViewById(R.id.picker_address);
        pickerPhoNumberTV = inflateView.findViewById(R.id.picker_pho);
        pikcerRatingTV = inflateView.findViewById(R.id.picker_rating);
        pickerImgLoader = inflateView.findViewById(R.id.picker_image_loader);

        setImage(place.getId());

        pickerNameTV.setText("Name : "+place.getName());
        pickerAddressTV.setText("Address : "+place.getAddress());
        pickerPhoNumberTV.setText("Phone Number : "+place.getPhoneNumber());
        pikcerRatingTV.setText("Rating : "+String.valueOf(place.getRating()));

        builder.setView(inflateView);
        builder.setPositiveButton("OK",null);
        builder.show();


    }

    public void setImage(String placeID){

        geoDataClient.getPlacePhotos(placeID).addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                if(task.isComplete() && task.getResult() != null){

                    PlacePhotoMetadataResponse responses = task.getResult();
                    PlacePhotoMetadataBuffer buffer = responses.getPhotoMetadata();
                    if (buffer.getCount() > 0) {

                        PlacePhotoMetadata metadata = buffer.get(0);

                        geoDataClient.getPhoto(metadata).addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                if (task.isComplete() && task.getResult() != null) {
                                    PlacePhotoResponse response = task.getResult();
                                    bitmap = response.getBitmap();
                                    pickerIV.setImageBitmap(bitmap);
                                    pickerImgLoader.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                    buffer.release();

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

}
