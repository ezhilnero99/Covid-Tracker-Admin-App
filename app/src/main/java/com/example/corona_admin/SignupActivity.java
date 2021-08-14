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

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "signup_tag";
    EditText emailET, passwordET, passwordConET;
    Button signupBT;
    GifImageView progressIV;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference supervisorCollRef = db.collection("supervisorData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_signup);
        } else {
            setContentView(R.layout.activity_signup_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();

        signupBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(SignupActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    String email = emailET.getText().toString().trim();
                    String password = passwordET.getText().toString().trim();
                    String passwordCon = passwordConET.getText().toString().trim();
                    signUp(email, password, passwordCon);
                }
            }
        });
    }

    private void signUp(final String email, final String password, String passwordCon) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        } else if (passwordCon.isEmpty()) {
            Toast.makeText(this, "Please Re-enter Your Password", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordCon)) {
            Toast.makeText(this, "Invalid Re-entry of password", Toast.LENGTH_SHORT).show();
        } else {
            progressIV.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        SupervisorData signupData = new SupervisorData(email, "ASV");

                        supervisorCollRef.add(signupData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    progressIV.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "SignUp Successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressIV.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "SignUp Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressIV.setVisibility(View.GONE);
                        Log.i(TAG, "onComplete: " + task.getException());
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void initUI() {
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordConET = findViewById(R.id.passwordConET);
        signupBT = findViewById(R.id.signupBT);
        progressIV = findViewById(R.id.progressIV);
    }
}