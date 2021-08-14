package com.example.corona_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corona_admin.adapters.UsersDataAdapter;
import com.example.corona_admin.models.UserDetails;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class UserUpdateActivity extends AppCompatActivity {
    private static final String TAG = "update_tag";
    ImageView addIV, logoutIV, locationIV;
    GifImageView progressIV;
    TextView languageTV;
    EditText phoneET;
    Button searchBT;
    RecyclerView userDetailsRV;
    ArrayList<UserDetails> userData = new ArrayList<>();
    UsersDataAdapter adapter;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_user_update);
        } else {
            setContentView(R.layout.activity_user_update_tamil);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();

        addIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserUpdateActivity.this, UserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(UserUpdateActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    String phoneNumber = phoneET.getText().toString().trim();
                    if (phoneNumber.isEmpty()) {
                        Toast.makeText(UserUpdateActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    } else {
                        progressIV.setVisibility(View.VISIBLE);
                        setUpRecyclerView(phoneNumber);
                    }
                }
            }
        });

        locationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserUpdateActivity.this, LocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        languageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserUpdateActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_language, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView englishTV = dialogView.findViewById(R.id.englishTV);
                TextView tamilTV = dialogView.findViewById(R.id.tamilTV);


                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();


                englishTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPref.putString(getApplicationContext(), "sp_language","en");
                        alertDialog.dismiss();
                    }
                });

                tamilTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPref.putString(getApplicationContext(), "sp_language","tam");
                        alertDialog.dismiss();
                    }
                });
            }
        });


        logoutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignoutDialog();
            }
        });

    }

    private void initUI() {
        addIV = findViewById(R.id.addIV);
        progressIV = findViewById(R.id.progressIV);
        phoneET = findViewById(R.id.phoneET);
        searchBT = findViewById(R.id.searchBT);
        logoutIV = findViewById(R.id.logoutIV);
        locationIV = findViewById(R.id.locationIV);
        languageTV = findViewById(R.id.languageTV);
        userDetailsRV = findViewById(R.id.userDetailsRL);
        userDetailsRV.setLayoutManager(new LinearLayoutManager(this));
    }

    void setUpRecyclerView(String phoneNumber) {
        Log.i(TAG, "setupFireStore: Fired");
        userCollRef.whereEqualTo(CommonUtils.userPhone, phoneNumber).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressIV.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: " + queryDocumentSnapshots.isEmpty());
                userData.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    final UserDetails userDetails = new UserDetails();
                    userDetails.setName(snapshot.getString(CommonUtils.userName));
                    userDetails.setAge(snapshot.getString(CommonUtils.userAge));
                    userDetails.setBlood(snapshot.getString(CommonUtils.userBlood));
                    userDetails.setGender(snapshot.getString(CommonUtils.userGender));
                    userDetails.setPhone(snapshot.getString(CommonUtils.userPhone));
                    userData.add(userDetails);
                }
                adapter = new UsersDataAdapter(getBaseContext(), userData, UserUpdateActivity.this);
                userDetailsRV.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressIV.setVisibility(View.GONE);
                Toast.makeText(UserUpdateActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //logout alert box Display
    void SignoutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_logout_item, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView noTV = dialogView.findViewById(R.id.noTV);
        TextView yesTV = dialogView.findViewById(R.id.yesTV);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();


        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.removeAll(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        noTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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