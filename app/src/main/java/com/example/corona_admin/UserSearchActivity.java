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

import com.example.corona_admin.adapters.UserDetailsAdapter;
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

public class UserSearchActivity extends AppCompatActivity {

    private static final String TAG = "update_tag";
    GifImageView progressIV;
    EditText phoneET;
    Button searchBT;
    RecyclerView userDetailsRV;
    ArrayList<UserDetails> userData = new ArrayList<>();
    UserDetailsAdapter adapter;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_user_search);
        } else {
            setContentView(R.layout.activity_user_search_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();


        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(UserSearchActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    String phoneNumber = phoneET.getText().toString().trim();
                    if (phoneNumber.isEmpty()) {
                        Toast.makeText(UserSearchActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    } else {
                        progressIV.setVisibility(View.VISIBLE);
                        setUpRecyclerView(phoneNumber);
                    }
                }
            }
        });

    }

    private void initUI() {
        progressIV = findViewById(R.id.progressIV);
        phoneET = findViewById(R.id.phoneET);
        searchBT = findViewById(R.id.searchBT);
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
                adapter = new UserDetailsAdapter(getBaseContext(), userData, UserSearchActivity.this);
                userDetailsRV.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressIV.setVisibility(View.GONE);
                Toast.makeText(UserSearchActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}