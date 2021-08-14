package com.example.corona_admin;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.corona_admin.models.UserDetails;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private static final String TAG = "otp_tag";
    EditText otpET;
    Button submitBT;
    ImageView verificationIV;
    RelativeLayout progressRL;

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userData");
    PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();
    PhoneAuthCredential credentials;
    String verificationId;
    UserDetails userObj = new UserDetails();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_otp);
        } else {
            setContentView(R.layout.activity_otp_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();

        userObj.setName(getIntent().getStringExtra(CommonUtils.userName));
        userObj.setAge(getIntent().getStringExtra(CommonUtils.userAge));
        userObj.setGender(getIntent().getStringExtra(CommonUtils.userGender));
        userObj.setBlood(getIntent().getStringExtra(CommonUtils.userBlood));
        userObj.setPhone(getIntent().getStringExtra(CommonUtils.userPhone));

        Log.i(TAG, "onCreate: " + userObj.getAge());

        phoneAuth.verifyPhoneNumber("+91" + userObj.getPhone(),
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.i(TAG, "onVerificationCompleted: Verified");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.i(TAG, "onVerificationFailed: " + e.toString());
                        Log.i(TAG, "onVerificationCompleted: Failed");
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                    }
                });

        submitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(OtpActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    progressRL.setVisibility(View.VISIBLE);
                    onSubmit();
                }
            }
        });

    }

    private void initUI() {
        otpET = findViewById(R.id.otpET);
        submitBT = findViewById(R.id.submitBT);
        verificationIV = findViewById(R.id.verificationIV);
        progressRL = findViewById(R.id.progressRL);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onSubmit() {
        String otp = otpET.getText().toString();
        if (otp.isEmpty() || otp == null) {
            Toast.makeText(this, "Please Enter A Valid OTP", Toast.LENGTH_SHORT).show();
        } else {
            try {
                credentials = PhoneAuthProvider.getCredential(verificationId, otp);
                mAuth.signInWithCredential(credentials).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userCollRef.add(userObj).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        progressRL.setVisibility(View.GONE);
                                        verificationIV.setImageResource(R.drawable.circle_green_asset);
                                        Toast.makeText(OtpActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "onComplete: success");
                                    } else {
                                        progressRL.setVisibility(View.GONE);
                                        verificationIV.setImageResource(R.drawable.circle_red_asset);
                                        Log.i(TAG, "onComplete: " + task.getException());
                                        Toast.makeText(OtpActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            progressRL.setVisibility(View.GONE);
                            verificationIV.setImageResource(R.drawable.circle_red_asset);
                            Log.i(TAG, "onComplete: " + task.getException());
                            Toast.makeText(OtpActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } catch (Exception e) {
                progressRL.setVisibility(View.GONE);
                Log.i(TAG, "onSubmit: " + e.toString());
                Toast.makeText(OtpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}