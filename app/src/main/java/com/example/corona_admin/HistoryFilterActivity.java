package com.example.corona_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.corona_admin.adapters.UsersDataAdapter;
import com.example.corona_admin.adapters.UsersFilterAdapter;
import com.example.corona_admin.models.UserDetails;
import com.example.corona_admin.models.UserLocationDetails;
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

public class HistoryFilterActivity extends AppCompatActivity {

    AutoCompleteTextView locationET;
    EditText dateET;
    Button searchBT;
    RecyclerView historyRV;
    GifImageView progressIV;
    ArrayList<UserLocationDetails> userData = new ArrayList<>();
    UsersFilterAdapter adapter;
//    ArrayList<String> locations = new ArrayList<>();

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userLocationData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_history_filter);
        } else {
            setContentView(R.layout.activity_history_filter_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();
//        fetchData();
        locationET.setText(getIntent().getStringExtra("location"));
        dateET.setText(getIntent().getStringExtra("date"));
        final String name = getIntent().getStringExtra("name");

        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(HistoryFilterActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    if (locationET.getText().toString().isEmpty()) {
                        Toast.makeText(HistoryFilterActivity.this, "Please Enter A Valid Location", Toast.LENGTH_SHORT).show();
                    } else if (dateET.getText().toString().isEmpty()) {
                        Toast.makeText(HistoryFilterActivity.this, "Please Enter A Valid Date", Toast.LENGTH_SHORT).show();
                    } else {
                        progressIV.setVisibility(View.VISIBLE);
                        setUpRecyclerView(locationET.getText().toString(), dateET.getText().toString(), name);
                    }
                }
            }
        });

    }

//    private void fetchData() {
//        userCollRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
//                    final UserLocationDetails userDetails = snapshot.toObject(UserLocationDetails.class);
//                    locations.add(userDetails.getLocation());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(HistoryFilterActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    void setUpRecyclerView(String location, String date, final String name) {
        userCollRef.whereEqualTo(CommonUtils.userLocation, location).whereEqualTo(CommonUtils.userDate, date).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressIV.setVisibility(View.GONE);
                userData.clear();
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    final UserLocationDetails userDetails = snapshot.toObject(UserLocationDetails.class);
                    if (!userDetails.getName().equals(name)) {
                        userData.add(userDetails);
                    }
                }
                adapter = new UsersFilterAdapter(getBaseContext(), userData, HistoryFilterActivity.this);
                historyRV.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressIV.setVisibility(View.GONE);
                Toast.makeText(HistoryFilterActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUI() {
        locationET = findViewById(R.id.locationET);
        dateET = findViewById(R.id.dateET);
        searchBT = findViewById(R.id.searchBT);
        historyRV = findViewById(R.id.historyRV);
        progressIV = findViewById(R.id.progressIV);
        historyRV.setLayoutManager(new LinearLayoutManager(this));
    }
}