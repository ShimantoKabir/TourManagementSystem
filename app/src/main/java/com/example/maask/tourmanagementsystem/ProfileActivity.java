package com.example.maask.tourmanagementsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private EditText userEmailET,userPhoET,userNameET;
    private ImageView userPicIV;
    private Button editUserBT;
    private TextView tellUpload;

    private DatabaseReference drForShowData,drForUpdateData;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userEmailET = findViewById(R.id.user_email);
        userPhoET   = findViewById(R.id.user_pho);
        userNameET  = findViewById(R.id.user_name);
        userPicIV   = findViewById(R.id.user_pic);
        editUserBT  = findViewById(R.id.edit_user);
        tellUpload  = findViewById(R.id.tell_upload);

        auth = FirebaseAuth.getInstance();
        drForUpdateData  = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        final FirebaseUser updateAuth = auth.getCurrentUser();
        userEmailET.setText(updateAuth.getEmail());
        drForShowData = FirebaseDatabase.getInstance().getReference(updateAuth.getUid());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting User Information ... ");
        progressDialog.show();
        storageReference.child(updateAuth.getUid()+"/profile_picture/pic.jpg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Picasso.with(ProfileActivity.this)
                .load(imageURL)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.profile_icon)
                .into(userPicIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(ProfileActivity.this, "SORRY : Can't show profile image ! ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "INFO : Image and information did not updated yet !", Toast.LENGTH_SHORT).show();
            }
        });

        if (!isNetworkAvailable()){
            progressDialog.dismiss();
            Toast.makeText(this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
        }

        drForShowData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserInfo info = snapshot.getValue(UserInfo.class);
                    userNameET.setText(info.getUserName());
                    userPhoET.setText(info.getUserPho());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR : Massage", "onCancelled: " + databaseError.getMessage() );
            }
        });

        editUserBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()){

                    String name  = userNameET.getText().toString().trim();
                    String pho   = userPhoET.getText().toString().trim();
                    String email = userEmailET.getText().toString().trim();

                    uploadFile(updateAuth,name,pho,email);

                }else {
                    Toast.makeText(ProfileActivity.this, "ERROR : Network is not available !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        userPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });


    }

    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userPicIV.setImageBitmap(bitmap);
                tellUpload.setText("");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadFile(final FirebaseUser updateAuth, final String name, final String pho, final String email) {

        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference riversRef = storageReference.child(auth.getCurrentUser().getUid()+"/profile_picture/pic.jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String profileImageUrl = taskSnapshot.getDownloadUrl().toString();

                            UserInfo userInfo = new UserInfo(name,pho,profileImageUrl);
                            drForUpdateData.child(updateAuth.getUid()).child("user_information").setValue(userInfo);

                            updateAuth.updateEmail(email);

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "SUCCESS : Information saved !", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.setMessage("Uploading...");

                        }
                    });
        }else {
            Toast.makeText(this, "WARNING : Sir/Mam image has not been changed !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
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
