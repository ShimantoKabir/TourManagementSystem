package com.example.maask.tourmanagementsystem;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.EventFile.ShowEventAdapter;
import com.example.maask.tourmanagementsystem.EventFile.UserEventInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class EventActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    private Button addNewEventBT;
    private EditText takeEventNameET, takeStartLocationET, takeEndLocationET,takeBudgetET, takeDepartureDateET;

    private Calendar calendar;
    private int year,month,day;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    private ArrayList<UserEventInfo> userEventInfos = new ArrayList<>();
    private RecyclerView recyclerView;
    private ShowEventAdapter showEventAdapter;

    private TextView alertDialogTitle;

    SharedPreferences sharedPreferences;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        toolbar  = findViewById(R.id.custom_toolbar);
        addNewEventBT = findViewById(R.id.add_new_event);
        recyclerView = findViewById(R.id.rv_for_show_events);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        toolbar.setTitle("Create Event");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userEventInfos.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserEventInfo userEventInfo = snapshot.getValue(UserEventInfo.class);
                    userEventInfos.add(userEventInfo);

                }

                Collections.reverse(userEventInfos);
                showEventAdapter = new ShowEventAdapter(userEventInfos,EventActivity.this);
                recyclerView.setAdapter(showEventAdapter);
                showEventAdapter.setOnDeleteIconClickListener(new ShowEventAdapter.OnDeleteIconClickListener() {
                    @Override
                    public void onDeleteClick(String parentName) {

                        progressDialog.setMessage("Deleting ....");
                        progressDialog.setCancelable(false);
                        deleteThisChild(parentName);
                    }
                });

                showEventAdapter.setOnEditIconClickListener(new ShowEventAdapter.OnEditIconClickListener() {
                    @Override
                    public void onEditClick(String parentName) {
                        editThisChild(parentName);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addNewEventBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View inflateView = layoutInflater.inflate(R.layout.add_new_event,null);

                takeEventNameET     = inflateView.findViewById(R.id.event_name);
                takeStartLocationET = inflateView.findViewById(R.id.start_location);
                takeEndLocationET = inflateView.findViewById(R.id.end_location);
                takeBudgetET        = inflateView.findViewById(R.id.event_budget);
                takeDepartureDateET = inflateView.findViewById(R.id.departure_date);

                progressDialog.setMessage("Saving ... ");
                progressDialog.show();
                builder.setView(inflateView).setNegativeButton("Cancle",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String currentTime = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_a").format(Calendar.getInstance().getTime());
                        String eventStartData = new SimpleDateFormat("dd/MMM/yyyy hh.mm.ss a").format(Calendar.getInstance().getTime());
                        String currentData = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

                        String eventName = takeEventNameET.getText().toString();
                        String eventStartLocation = takeStartLocationET.getText().toString();
                        String eventDestination = takeEndLocationET.getText().toString();
                        String eventBudget = takeBudgetET.getText().toString();
                        String departureDate = takeDepartureDateET.getText().toString();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date recentDate = null;
                        Date pickedDate = null;

                        if ( eventBudget.isEmpty() || eventName.isEmpty() || eventStartLocation.isEmpty() || eventDestination.isEmpty() || departureDate.isEmpty()) {

                            Toast.makeText(EventActivity.this, "ERROR : Field must not be Empty !", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        } else {

                            try {
                                recentDate = format.parse(currentData);
                                pickedDate = format.parse(departureDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = pickedDate.getTime() - recentDate.getTime();
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            if (diffDays < 0) {
                                Toast.makeText(EventActivity.this, "ERROR : Sir/Mam you can not pick old Date ! ", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {

                                UserEventInfo userEventInfo = new UserEventInfo(currentTime,eventStartData, eventName, eventStartLocation, eventDestination, departureDate, eventBudget,"0");

                                databaseReference.child(auth.getUid()).child("user_event_info").child(currentTime).setValue(userEventInfo);
                                progressDialog.dismiss();
                            }

                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.dismiss();
                    }
                });

                takeDepartureDateET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       setDepartureDate();
                    }
                });

                builder.show();

            }
        });

    }

    private void editThisChild(final String parentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0){
                    UserEventInfo userEventInfo = dataSnapshot.getValue(UserEventInfo.class);

                     // show alert dialogue

                    AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View inflateView = layoutInflater.inflate(R.layout.add_new_event,null);

                    takeEventNameET     = inflateView.findViewById(R.id.event_name);
                    takeStartLocationET = inflateView.findViewById(R.id.start_location);
                    takeEndLocationET   = inflateView.findViewById(R.id.end_location);
                    takeBudgetET        = inflateView.findViewById(R.id.event_budget);
                    takeDepartureDateET = inflateView.findViewById(R.id.departure_date);
                    alertDialogTitle    = inflateView.findViewById(R.id.set_title);

                    alertDialogTitle.setText("Edit Event");
                    takeEventNameET.setText(userEventInfo.getEventName());
                    takeStartLocationET.setText(userEventInfo.getEventStartLocation());
                    takeEndLocationET.setText(userEventInfo.getEventDestination());
                    takeBudgetET.setText(userEventInfo.getEventBudget());
                    takeDepartureDateET.setText(userEventInfo.getDepartureDate());

                    builder.setView(inflateView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        String currentData = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

                        String eventName = takeEventNameET.getText().toString();
                        String eventStartLocation = takeStartLocationET.getText().toString();
                        String eventDestination = takeEndLocationET.getText().toString();
                        String eventBudget = takeBudgetET.getText().toString();
                        String departureDate = takeDepartureDateET.getText().toString();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date recentDate = null;
                        Date pickedDate = null;

                        if ( eventBudget.isEmpty() || eventName.isEmpty() || eventStartLocation.isEmpty() || eventDestination.isEmpty() || departureDate.isEmpty()) {

                            Toast.makeText(EventActivity.this, "ERROR : Field must not be Empty !", Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                recentDate = format.parse(currentData);
                                pickedDate = format.parse(departureDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = pickedDate.getTime() - recentDate.getTime();
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            if (diffDays < 0) {

                                Toast.makeText(EventActivity.this, "ERROR : Sir/Mam you can not pick old Date ! ", Toast.LENGTH_SHORT).show();

                            } else {

                                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventName").setValue(eventName);
                                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventStartLocation").setValue(eventStartLocation);
                                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventDestination").setValue(eventDestination);
                                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("departureDate").setValue(departureDate);
                                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("eventBudget").setValue(eventBudget);

                            }

                        }

                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();

                    takeDepartureDateET.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setDepartureDate();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Event Activity : ", "onCancelled -> "+ databaseError.getMessage() );
            }
        });


    }

    private void deleteThisChild(final String parentName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
        builder.setMessage("WARNING : Sir/Mam are you want to delete this event ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).removeValue();
            }
        }).setNegativeButton("NO",null);
        builder.show();

    }

    private void setDepartureDate() {

        calendar = Calendar.getInstance();
        year     = calendar.get(Calendar.YEAR);
        month    = calendar.get(Calendar.MONTH);
        day      = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String departureDate = sdf.format(calendar.getTime());
                takeDepartureDateET.setText(departureDate);

            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,dateListener,year,month,day);
        datePickerDialog.show();

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
                Intent goHome = new Intent(EventActivity.this,HomeActivity.class);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(EventActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(EventActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

}

