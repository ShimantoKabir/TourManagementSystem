package com.example.maask.tourmanagementsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userPassET,userEmailET;
    private Button userRegTV;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userEmailET = findViewById(R.id.user_email);
        userPassET  = findViewById(R.id.user_pass);
        userRegTV   = findViewById(R.id.button_signup);

        progressDialog = new ProgressDialog(this);

        userRegTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userEmailET.getText().toString().trim();;
                String pass  = userPassET.getText().toString().trim();

                if (pass.length() < 6 ){
                    Toast.makeText(RegistrationActivity.this, "INFO : Password should be al least six character !", Toast.LENGTH_SHORT).show();
                }else {
                    registrationOperation(email,pass);
                }

            }
        });

    }

    private void registrationOperation(String email, String pass) {

        auth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)){
            Toast.makeText(RegistrationActivity.this, "WARNING : Field must not be empty !", Toast.LENGTH_SHORT).show();
        }else {

            progressDialog.setMessage("Sign up ...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && task.isComplete()){

                        progressDialog.dismiss();

                        Toast.makeText(RegistrationActivity.this, "SUCCESS : Registration successful !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, "ERROR : Registration unsuccessful !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
