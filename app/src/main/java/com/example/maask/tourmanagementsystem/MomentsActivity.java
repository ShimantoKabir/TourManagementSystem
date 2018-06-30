package com.example.maask.tourmanagementsystem;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.EventFile.ExpenseInfo;
import com.example.maask.tourmanagementsystem.EventFile.ImageInfo;
import com.example.maask.tourmanagementsystem.EventFile.ShowExpenseAdapter;
import com.example.maask.tourmanagementsystem.EventFile.ShowMomentsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MomentsActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    SharedPreferences sharedPreferences;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    private ArrayList<ImageInfo> imageInfos = new ArrayList<>();
    private RecyclerView recyclerView;
    private ShowMomentsAdapter showMomentsAdapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    String parentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 220);

        toolbar = findViewById(R.id.custom_toolbar);
        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        parentName = getIntent().getStringExtra("parent_name");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = findViewById(R.id.rv_for_show_moments);

        toolbar.setTitle("Moments");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                .child(parentName).child("moment_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageInfos.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ImageInfo imageInfo = snapshot.getValue(ImageInfo.class);
                    imageInfos.add(imageInfo);
                }
                Collections.reverse(imageInfos);
                showMomentsAdapter = new ShowMomentsAdapter(imageInfos,MomentsActivity.this);
                recyclerView.setAdapter(showMomentsAdapter);
                showMomentsAdapter.setOnDeleteIconClickListener(new ShowMomentsAdapter.OnDeleteIconClickListener() {
                    @Override
                    public void onDeleteClick(final String momentParentName) {

                        if (isNetworkAvailable()){
                            momentDelete(momentParentName,parentName);
                        }else {
                            Toast.makeText(MomentsActivity.this, "ERROR : Network is not available ... !", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                showMomentsAdapter.setOnDownloadIconClickListener(new ShowMomentsAdapter.OnDownloadIconClickListener() {
                    @Override
                    public void onDownloadClick(String momentParentName) {

                        if (isNetworkAvailable()){
                            momentDownload(momentParentName,parentName);
                        }else {
                            Toast.makeText(MomentsActivity.this, "ERROR : Network is not available ... !", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Moment : ", "onCancelled : "+ databaseError.getMessage() );
            }
        });

    }

    private void momentDelete(final String momentParentName, final String parentName) {


        AlertDialog.Builder builder = new AlertDialog.Builder(MomentsActivity.this);
        builder.setMessage("WARNING : Sir/Mam are you want to delete this moment ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                final ProgressDialog progressDialog = new ProgressDialog(MomentsActivity.this);
                progressDialog.setMessage("Deleting ....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                        .child(parentName).child("moment_info").child(momentParentName).removeValue();

                storageReference.child(auth.getCurrentUser().getUid()+"/moment_picture/"+parentName+"/"+momentParentName+".jpg")
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.setMessage("Delete Complete ... ");
                        progressDialog.dismiss();
                    }
                });

            }
        })
                .setNegativeButton("NO",null);
        builder.show();

    }

    private void momentDownload(String momentParentName, String parentName) {

        final ProgressDialog progressDialog = new ProgressDialog(MomentsActivity.this);
        progressDialog.setMessage("Downloading ....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        storageReference.child(auth.getCurrentUser().getUid()+"/moment_picture/"+parentName+"/"+momentParentName+".jpg")
                .getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                String imageName = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_a").format(Calendar.getInstance().getTime());

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                File path = Environment.getExternalStorageDirectory();
                File createDir = new File(path+"/tmsbookDownloads/");
                createDir.mkdir();

                File emptyImageFile = new File(createDir,imageName+".jpg");

                try {

                    FileOutputStream out = new FileOutputStream(emptyImageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                    progressDialog.setMessage("Download Complete ....");
                    progressDialog.dismiss();
                    Toast.makeText(MomentsActivity.this, "SUCCESS : Please check your storage in tmsbookDownloads folder ! ", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Moment download : ", "onCancelled : "+ e.getMessage() );
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
                Intent goHome = new Intent(MomentsActivity.this,EventManagerActivity.class);
                goHome.putExtra("parent_name",parentName);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(MomentsActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(MomentsActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
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
