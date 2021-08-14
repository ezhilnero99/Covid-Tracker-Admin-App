package com.example.corona_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corona_admin.models.SupervisorData;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "login_tag";
    EditText emailET, passwordET;
    TextView signupTV;
    Button loginBT;
    GifImageView progressIV;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference supervisorCollRef = db.collection("supervisorData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.activity_login_tamil);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();
        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(LoginActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    String email = emailET.getText().toString().trim();
                    String password = passwordET.getText().toString().trim();
                    login(email, password);
                }

            }
        });
        signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void login(final String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        } else {
            progressIV.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        supervisorCollRef.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        SupervisorData supervisorData = task.getResult().getDocuments().get(0).toObject(SupervisorData.class);
                                        SharedPref.putBoolean(getApplicationContext(), "sp_log", true);
                                        SharedPref.putString(getApplicationContext(), "sp_email", supervisorData.getEmail());
                                        SharedPref.putString(getApplicationContext(), "sp_desId", supervisorData.getDesId());
                                        if (supervisorData.getDesId().equals("ASV")) {
                                            Intent intent = new Intent(LoginActivity.this, UserUpdateActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else if (supervisorData.getDesId().equals("SV")) {
                                            Intent intent = new Intent(LoginActivity.this, StatisticsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please Contact Admin", Toast.LENGTH_SHORT).show();
                                        }
                                        progressIV.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    SharedPref.putBoolean(getApplicationContext(), "sp_log", false);
                                    progressIV.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressIV.setVisibility(View.GONE);
                        SharedPref.putBoolean(getApplicationContext(), "sp_log", false);
                        Log.i(TAG, "onComplete: " + task.getException());
                        Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initUI() {
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBT = findViewById(R.id.loginBT);
        signupTV = findViewById(R.id.signupTV);
        progressIV = findViewById(R.id.progressIV);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(startMain);
        finishAffinity();
        finish();
    }
}