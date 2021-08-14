package com.example.corona_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;

public class LocationActivity extends AppCompatActivity {

    EditText locationTV, cityTV, pincodeTV;
    Button submitBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_location);
        } else {
            setContentView(R.layout.activity_location_tamil);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initUI();


        locationTV.setText(SharedPref.getString(getApplicationContext(),"sp_location"));
        cityTV.setText(SharedPref.getString(getApplicationContext(),"sp_city"));
        pincodeTV.setText(SharedPref.getString(getApplicationContext(),"sp_pincode"));

        submitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationTV.getText().toString().trim();
                String city = cityTV.getText().toString().trim();
                String pincode = pincodeTV.getText().toString().trim();
                if (location.isEmpty()) {
                    Toast.makeText(LocationActivity.this, "Please Enter A Location", Toast.LENGTH_SHORT).show();
                } else if (city.isEmpty()) {
                    Toast.makeText(LocationActivity.this, "Please Enter A City", Toast.LENGTH_SHORT).show();
                } else if (pincode.isEmpty() || pincode.length() < 6) {
                    Toast.makeText(LocationActivity.this, "Please Enter A Pincode", Toast.LENGTH_SHORT).show();
                } else {
                    CommonUtils.hideKeyboard(LocationActivity.this);
                    SharedPref.putString(getApplicationContext(), "sp_location", location);
                    SharedPref.putString(getApplicationContext(), "sp_city", city);
                    SharedPref.putString(getApplicationContext(), "sp_pincode", pincode);
                    onBackPressed();
                }
            }
        });

    }

    private void initUI() {
        locationTV = findViewById(R.id.locationET);
        cityTV = findViewById(R.id.cityET);
        pincodeTV = findViewById(R.id.pincodeET);
        submitBT = findViewById(R.id.submitBT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}