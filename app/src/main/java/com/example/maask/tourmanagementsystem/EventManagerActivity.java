package com.example.maask.tourmanagementsystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.EventFile.ExpenseInfo;
import com.example.maask.tourmanagementsystem.EventFile.ImageInfo;
import com.example.maask.tourmanagementsystem.EventFile.UserEventInfo;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class EventManagerActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;

    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    SharedPreferences sharedPreferences;

    String moneyField[] = {"In","Out","Box"};
    ArrayList<PieEntry> entryList = new ArrayList();
    private PieChart chart;

    public static final int[] MY_COLORS = {rgb("#2ecc71"), rgb("#f42851"), rgb("#f1c40f")};

    private ImageView expenditureArrowIV;
    private ImageView momentArrowIV;

    private LinearLayout expenditureItemLL;
    private LinearLayout momentItemLL;

    private LinearLayout expenditureViewLL;
    private LinearLayout momentViewLL;

    int expenditureClick = 0;
    int momentClick = 0;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private TextView tourNameTV,tourBudgetTV,addBudgetTV,addExpenseTV,expenseHistoryTV,tourExpenseTV,tourDepositTV;
    private TextView captureUploadImageTV,viewMomentsTV;
    private EditText getNewBudgetET;

    private static final int CAMERA_REQUEST = 1888;
    private StorageReference storageReference;

    private String parentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manager);

        parentName = getIntent().getStringExtra("parent_name");

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReference();

        chart = findViewById(R.id.pie_chart);
        toolbar = findViewById(R.id.custom_toolbar);

        expenditureViewLL = findViewById(R.id.expenditure_view);
        expenditureArrowIV = findViewById(R.id.expenditure_arrow);
        expenditureItemLL = findViewById(R.id.expenditure_item);

        momentViewLL = findViewById(R.id.moments_view);
        momentArrowIV = findViewById(R.id.moment_arrow);
        momentItemLL = findViewById(R.id.moments_item);

        tourNameTV = findViewById(R.id.tour_name);
        tourBudgetTV = findViewById(R.id.tour_budget);
        addExpenseTV = findViewById(R.id.add_expense);
        expenseHistoryTV = findViewById(R.id.expense_history);
        tourExpenseTV = findViewById(R.id.tour_expense);
        tourDepositTV = findViewById(R.id.tour_deposit);
        viewMomentsTV = findViewById(R.id.view_moments);

        captureUploadImageTV = findViewById(R.id.capture_upload_img);

        addBudgetTV = findViewById(R.id.add_budget);

        toolbar.setTitle("Event Manager");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        entryList.add(new PieEntry(0,moneyField[0]));
        entryList.add(new PieEntry(0,moneyField[1]));
        entryList.add(new PieEntry(0,moneyField[2]));

        setExpense(parentName);

        setBudget(parentName);

        uiAnimationSetting();

        addExpenseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EventManagerActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View inflateView = layoutInflater.inflate(R.layout.add_expense,null);

                final EditText title = inflateView.findViewById(R.id.expense_title);
                final EditText amount = inflateView.findViewById(R.id.expense_amount);

                builder.setView(inflateView)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String getTitle = title.getText().toString();
                        String getAmount = amount.getText().toString();

                        String createDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(Calendar.getInstance().getTime());
                        String currentTime = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_a").format(Calendar.getInstance().getTime());

                        if ( getTitle.isEmpty() || getAmount.isEmpty() ){

                            Toast.makeText(EventManagerActivity.this, "ERROR : Field must not be Empty !", Toast.LENGTH_SHORT).show();

                        } else {

                            ExpenseInfo expenseInfo = new ExpenseInfo(getTitle,getAmount,currentTime,createDate);
                            checkBudgetEmpty(expenseInfo,parentName,currentTime);

                        }
                    }
                })
                .setNegativeButton("cancel", null).show();

            }
        });

        addBudgetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EventManagerActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View inflateView = layoutInflater.inflate(R.layout.add_budget,null);

                getNewBudgetET = inflateView.findViewById(R.id.add_new_budget);

                builder.setView(inflateView)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!getNewBudgetET.getText().toString().isEmpty()){

                            int newBudget = Integer.parseInt(getNewBudgetET.getText().toString());
                            setNewBudget(newBudget,parentName);

                        }else {
                            Toast.makeText(EventManagerActivity.this, "ERROR : Field must not be Empty !", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("cancel", null).show();

            }
        });

        expenseHistoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventManagerActivity.this, ExpenseHistoryActivity.class);
                intent.putExtra("parent_name",parentName);
                startActivity(intent);
            }
        });

        captureUploadImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()){
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }else {
                    Toast.makeText(EventManagerActivity.this, "ERROR : Network is not available ... !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewMomentsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventManagerActivity.this, MomentsActivity.class);
                intent.putExtra("parent_name",parentName);
                startActivity(intent);

            }
        });

    }

    private void checkBudgetEmpty(final ExpenseInfo expenseInfo, final String parentName, final String currentTime) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                .child(parentName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount()>0){

                    UserEventInfo userEventInfo = dataSnapshot.getValue(UserEventInfo.class);

                    int budget = Integer.parseInt(userEventInfo.getEventBudget());
                    int expense = Integer.parseInt(userEventInfo.getEventExpense());

                    int difference = budget - expense;

                    if (difference == 0 || difference < 0 ){
                        Toast.makeText(EventManagerActivity.this, "WARNING : Sir/Mam your budget is already empty,Please add more budget ! ", Toast.LENGTH_SHORT).show();
                    }else {
                        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("expense_info").child(currentTime).setValue(expenseInfo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Event Manager : ", "onCancelled -> "+ databaseError.getMessage() );
            }
        });


    }

    private void setBudget(String parentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                .child(parentName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0){

                    UserEventInfo userEventInfo = dataSnapshot.getValue(UserEventInfo.class);

                    int budget = Integer.parseInt(userEventInfo.getEventBudget());
                    int expense = Integer.parseInt(userEventInfo.getEventExpense());

                    int difference = budget - expense;

                    tourDepositTV.setText("Deposit : " + String.valueOf(difference) + " TK");

                    tourNameTV.setText(userEventInfo.getEventName());
                    entryList.set(0,new PieEntry(Integer.parseInt(userEventInfo.getEventBudget()),moneyField[0]));
                    entryList.set(2,new PieEntry(difference,moneyField[2]));
                    tourBudgetTV.setText("Budget : "+ userEventInfo.getEventBudget()+" TK");
                    setUpPieChart();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Event Manager : ", "onCancelled -> "+ databaseError.getMessage() );
            }
        });
    }

    private void setExpense(final String parentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
        .child(parentName).child("expense_info")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount()>0){

                    int sum = 0;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        ExpenseInfo expenseInfo = snapshot.getValue(ExpenseInfo.class);
                        sum = sum + Integer.parseInt(expenseInfo.getExpenseAmount());
                    }

                    entryList.set(1,new PieEntry(sum,moneyField[1]));
                    tourExpenseTV.setText("Total Expense : "+ String.valueOf(sum)+" TK");
                    databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventExpense").setValue(String.valueOf(sum));

                    setUpPieChart();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Event Manager : ", "onCancelled -> "+ databaseError.getMessage() );
            }
        });

    }

    private void setNewBudget(final int newBudget, final String parentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount()>0){

                    UserEventInfo userEventInfo = dataSnapshot.getValue(UserEventInfo.class);

                    int oldBudget = Integer.parseInt(userEventInfo.getEventBudget());

                    int totalBudget = newBudget + oldBudget;
                    databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventBudget").setValue(String.valueOf(totalBudget));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Event Manager : ", "onCancelled -> "+ databaseError.getMessage() );
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
                Intent goHome = new Intent(EventManagerActivity.this,EventActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(EventManagerActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(EventManagerActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpPieChart() {

        PieDataSet dataSet = new PieDataSet(entryList,"");
        dataSet.setColors(ColorTemplate.createColors(MY_COLORS));
        PieData data = new PieData(dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(8f);

        // set data to chart

        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText("Pie Chart");
        chart.setCenterTextColor(Color.GRAY);
        chart.setCenterTextSize(10);
        chart.setData(data);
        chart.animateX(500);
        chart.invalidate();

    }

    private void uiAnimationSetting() {

        expenditureViewLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenditureClick++;
                if (expenditureClick == 1){

                    expenditureArrowIV.setRotation(180);
                    slideGo(expenditureItemLL);

                }else {

                    expenditureClick = 0;
                    expenditureArrowIV.setRotation(0);
                    slideCome(expenditureItemLL);

                }

            }
        });


        momentViewLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                momentClick++;
                if (momentClick == 1){
                    momentArrowIV.setRotation(180);
                    slideGo(momentItemLL);
                }else {
                    momentClick = 0;
                    momentArrowIV.setRotation(0);
                    slideCome(momentItemLL);

                }

            }
        });

    }

    public void slideCome(View view){

        TranslateAnimation animate = new TranslateAnimation(view.getWidth(),0,0,0);
        animate.setDuration(700);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public void slideGo(View view){

        TranslateAnimation animate = new TranslateAnimation(0,view.getWidth(),0,0);
        animate.setDuration(0);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Starting ... ");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final String currentTime = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_a").format(Calendar.getInstance().getTime());

            Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] getImageByte = outputStream.toByteArray();

            StorageReference momentImageRef = storageReference.child(auth.getCurrentUser().getUid()+"/moment_picture/"+parentName+"/"+currentTime+".jpg");

            UploadTask uploadTask = momentImageRef.putBytes(getImageByte);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ImageInfo imageInfo = new ImageInfo(taskSnapshot.getDownloadUrl().toString(),currentTime);

                    databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("moment_info").child(currentTime).setValue(imageInfo);
                    progressDialog.setMessage("Upload Successful ... ");
                    progressDialog.dismiss();
                    Toast.makeText(EventManagerActivity.this, "SUCCESS : Image saved !", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setMessage("Uploading...");
                }
            });

        }

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
