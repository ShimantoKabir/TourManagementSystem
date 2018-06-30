package com.example.maask.tourmanagementsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maask.tourmanagementsystem.EventFile.ExpenseInfo;
import com.example.maask.tourmanagementsystem.EventFile.ShowExpenseAdapter;
import com.example.maask.tourmanagementsystem.EventFile.UserEventInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ExpenseHistoryActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    private static final String PREFERENCES_KEY  = "tms_preferences";
    SharedPreferences sharedPreferences;
    private static final String VISIT_LOGIN      = "visit_login";

    private ArrayList<ExpenseInfo> expenseInfos = new ArrayList<>();
    private RecyclerView recyclerView;
    private ShowExpenseAdapter showExpenseAdapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    String parentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        toolbar = findViewById(R.id.custom_toolbar);
        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        parentName = getIntent().getStringExtra("parent_name");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.rv_for_show_expense_history);

        toolbar.setTitle("Expense History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info").child(parentName).child("expense_info").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenseInfos.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    ExpenseInfo expenseInfo = snapshot.getValue(ExpenseInfo.class);
                    expenseInfos.add(expenseInfo);
                }
                Collections.reverse(expenseInfos);
                showExpenseAdapter = new ShowExpenseAdapter(expenseInfos,ExpenseHistoryActivity.this);
                recyclerView.setAdapter(showExpenseAdapter);
                showExpenseAdapter.setOnDeleteIconClickListener(new ShowExpenseAdapter.OnDeleteIconClickListener() {
                    @Override
                    public void onDeleteClick(String expenseParentName) {
                        deleteThisRow(parentName,expenseParentName);
                    }
                });

                showExpenseAdapter.setOnEditIconClickListener(new ShowExpenseAdapter.OnEditIconClickListener() {
                    @Override
                    public void onEditClick(String expenseParentName) {
                        editThisRow(parentName,expenseParentName);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void editThisRow(final String parentName, final String expenseParentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
        .child(parentName).child("expense_info").child(expenseParentName)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ExpenseInfo expenseInfo = dataSnapshot.getValue(ExpenseInfo.class);

                AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseHistoryActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View inflateView = layoutInflater.inflate(R.layout.add_expense,null);

                final EditText title = inflateView.findViewById(R.id.expense_title);
                final EditText amount = inflateView.findViewById(R.id.expense_amount);
                TextView heading = inflateView.findViewById(R.id.expense_heading);
                heading.setText("Edit Expense");

                title.setText(expenseInfo.getExpenseTitle());
                amount.setText(expenseInfo.getExpenseAmount());

                builder.setView(inflateView)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String getTitle = title.getText().toString();
                        String getAmount = amount.getText().toString();

                        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                                .child(parentName).child("expense_info").child(expenseParentName).
                                child("expenseTitle").setValue(getTitle);

                        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                                .child(parentName).child("expense_info").child(expenseParentName).
                                child("expenseAmount").setValue(getAmount);

                    }
                })
                .setNegativeButton("cancel", null).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void deleteThisRow(String parentName, String expenseParentName) {

        databaseReference.child(auth.getCurrentUser().getUid()).child("user_event_info")
                .child(parentName).child("expense_info").child(expenseParentName).removeValue();

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
                Intent goHome = new Intent(ExpenseHistoryActivity.this,EventManagerActivity.class);
                goHome.putExtra("parent_name",parentName);
                goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;

            case R.id.profile:

                Intent intent = new Intent(ExpenseHistoryActivity.this,ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.logout:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(VISIT_LOGIN, "N");
                editor.commit();
                Intent goLogin = new Intent(ExpenseHistoryActivity.this,LoginActivity.class);
                goLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goLogin);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
