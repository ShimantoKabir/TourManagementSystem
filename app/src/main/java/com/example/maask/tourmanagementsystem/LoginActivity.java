package com.example.maask.tourmanagementsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button loginBT;
    private EditText userPassET,userEmailET;
    private TextView userRegTV,forgotPass;
    private CheckBox rememberMe;
    private ProgressDialog progressDialog;

    private SignInButton googleLoginBT;

    private static final String PREFERENCES_KEY  = "tms_preferences";
    private static final String VISIT_LOGIN      = "visit_login";

    SharedPreferences sharedPreferences;

    GoogleApiClient googleApiClient;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Google login fail - ";

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);

        loginBT = findViewById(R.id.button_signin);
        userEmailET = findViewById(R.id.user_email);
        userPassET = findViewById(R.id.user_pass);
        userRegTV = findViewById(R.id.user_reg);
        rememberMe = findViewById(R.id.remember_pass);
        forgotPass = findViewById(R.id.forgot_pass);
        googleLoginBT = findViewById(R.id.google_login);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "ERROR : Sorry sir/man some thing wrong ... !", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        progressDialog = new ProgressDialog(this);

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userEmailET.getText().toString().trim();
                String pass = userPassET.getText().toString().trim();
                loginOperation(email, pass);

            }
        });

        userRegTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rememberMe.isChecked()) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(VISIT_LOGIN, "Y");
                    editor.commit();

                }
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewPassword();
            }
        });

        googleLoginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
                progressDialog.setMessage("Sign in with google ... ");
                progressDialog.show();
            }
        });

    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setNewPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View inflateView = layoutInflater.inflate(R.layout.forgot_password,null);

        final EditText email = inflateView.findViewById(R.id.email);

        builder.setView(inflateView)
        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String getEmail = email.getText().toString();

                if ( getEmail.isEmpty() ){

                    Toast.makeText(LoginActivity.this, "ERROR : Field must not be Empty !", Toast.LENGTH_SHORT).show();

                } else {

                    auth.fetchProvidersForEmail(getEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.getResult().getProviders().isEmpty()){
                                Toast.makeText(LoginActivity.this, "ERROR : Sir/Mam email address not found !", Toast.LENGTH_SHORT).show();
                            }else {

                                progressDialog.setMessage("Please wait ...");
                                progressDialog.show();

                                auth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "SUCCESS : Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "ERROR : Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        })
        .setNegativeButton("cancel", null).show();

    }

    private void loginOperation(String email, String pass) {

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)){
            Toast.makeText(LoginActivity.this, "WARNING : Field must not be empty !", Toast.LENGTH_SHORT).show();
        }else {

            progressDialog.setMessage("Sign in ...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && task.isComplete()){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(VISIT_LOGIN, "Y");
                        editor.commit();

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "SUCCESS : Login successful !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "ERROR : Email and password not match !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {



        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(VISIT_LOGIN, "Y");
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "ERROR : Sorry Sir/Mam some thing wrong .. !", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
