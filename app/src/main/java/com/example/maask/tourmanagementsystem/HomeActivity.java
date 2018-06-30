package com.example.maask.tourmanagementsystem;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maask.tourmanagementsystem.EventFile.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;

    CardView likelihood,location,nearby,event,weather,geoFence;
    TextView welcomeTV;
    ImageView profileImgIV,downArrowIV;

    private FirebaseAuth auth;
    private DatabaseReference drForShowData;

    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    SharedPreferences sharedPreferences;

    private RelativeLayout clickToMore;
    private LinearLayout showMore,downLayout;
    int moreClick = 0;

    ProgressBar proImgDwnProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        if (!sharedPreferences.getString(VISIT_LOGIN, "").equals("Y")) {

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else {

            likelihood = findViewById(R.id.likelihood);
            location = findViewById(R.id.location);
            nearby = findViewById(R.id.nearby);
            geoFence = findViewById(R.id.geo_fence);
            weather = findViewById(R.id.weather);
            welcomeTV = findViewById(R.id.show_welcome);
            profileImgIV = findViewById(R.id.profile_image);
            event = findViewById(R.id.event);
            clickToMore = findViewById(R.id.click_to_more);
            downArrowIV = findViewById(R.id.down_arrow);
            showMore = findViewById(R.id.show_more);
            downLayout = findViewById(R.id.down_layout);
            proImgDwnProgress = findViewById(R.id.pro_img_dwn_progress);

            toolbar = findViewById(R.id.custom_toolbar);
            toolbar.setTitle("Home");
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            auth = FirebaseAuth.getInstance();
            FirebaseUser updateAuth = auth.getCurrentUser();

            drForShowData = FirebaseDatabase.getInstance().getReference(updateAuth.getUid());
            drForShowData.keepSynced(true);

            drForShowData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildrenCount()>0){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final UserInfo info = snapshot.getValue(UserInfo.class);
                            welcomeTV.setText("Welcome - " + info.getUserName());
                            Picasso.with(HomeActivity.this)
                            .load(info.getImgUrl())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(profileImgIV, new Callback() {
                                @Override
                                public void onSuccess() {
                                    proImgDwnProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                    Picasso.with(HomeActivity.this)
                                            .load(info.getImgUrl())
                                            .into(profileImgIV);

                                }
                            });
                        }
                    }else {
                        proImgDwnProgress.setVisibility(View.GONE);
                        profileImgIV.setImageResource(R.drawable.profile_icon);
                        welcomeTV.setText("Edit Your Profile");
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.e("ERROR : Massage", "onCancelled: " + databaseError.getMessage());
                }
            });

            getLastLocation();

            likelihood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, PlaceLikelihoodActivity.class);
                    startActivity(intent);
                }
            });

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, DirectionActivity.class);
                    startActivity(intent);
                }
            });

            nearby.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, NearbyActivity.class);
                    startActivity(intent);
                }
            });

            weather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, WeatherActivity.class);
                    startActivity(intent);
                }
            });

            welcomeTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });

            event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, EventActivity.class);
                    startActivity(intent);
                }
            });

            profileImgIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });

            showMore.setVisibility(View.GONE);

            clickToMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moreClick++;
                    if (moreClick == 1) {
                        downArrowIV.setRotation(180);
                        downThisLayout(downLayout);
                        showMore.setVisibility(View.VISIBLE);
                        slideCome(showMore);
                    } else {
                        moreClick = 0;
                        downArrowIV.setRotation(0);
                        upThisLayout(downLayout);
                        slideGo(showMore);
                    }
                }
            });

            geoFence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, GeoFenceActivity.class);
                    startActivity(intent);
                }
            });

        }

    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},133);
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.profile:

                Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                auth.signOut();
                Intent goLogin = new Intent(HomeActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

            case R.id.changed_pass:

                changePassword();

                break;

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View inflateView = layoutInflater.inflate(R.layout.forgot_password,null);

        final EditText pass = inflateView.findViewById(R.id.email);
        final TextView heading = inflateView.findViewById(R.id.heading);
        pass.setHint("Enter New Password");
        heading.setText("Change Password");

        builder.setView(inflateView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String getPass = pass.getText().toString();
                        if (getPass.length() < 6){
                            Toast.makeText(HomeActivity.this, "ERROR : Password should be six character !", Toast.LENGTH_SHORT).show();
                        }else {

                            if (isNetworkAvailable()){
                                auth.getCurrentUser().updatePassword(getPass);
                                Toast.makeText(HomeActivity.this, "SUCCESS : Password changed successfully !", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(HomeActivity.this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();

    }

    private void downThisLayout(View downLayout) {

        TranslateAnimation animate = new TranslateAnimation(0,0,-350,0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        downLayout.startAnimation(animate);

    }

    private void upThisLayout(View downLayout) {

        TranslateAnimation animate = new TranslateAnimation(0,0,350,0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        downLayout.startAnimation(animate);
    }

    private void slideGo(View showMore) {

        TranslateAnimation animate = new TranslateAnimation(0,showMore.getWidth(),0,0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        showMore.startAnimation(animate);
        showMore.setVisibility(View.GONE);

    }

    private void slideCome(View showMore) {

        TranslateAnimation animate = new TranslateAnimation(showMore.getWidth(),0,0,0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        showMore.startAnimation(animate);

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
