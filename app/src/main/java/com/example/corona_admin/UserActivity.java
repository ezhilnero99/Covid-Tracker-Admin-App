package com.example.corona_admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "user_tag";
    Button verifyBT;
    TextView nameET, ageET, phonenoET;
    Spinner genderSP, bloodSP;
    ArrayAdapter genderAdapter, bloodAdapter;
    NestedScrollView scrollView;

    private String name, phone, gender, blood;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_user);
        } else {
            setContentView(R.layout.activity_user_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        initUI();

        genderAdapter = new ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                new String[]{"", "Male", "Female", "Others"});
        genderSP.setAdapter(genderAdapter);

        bloodAdapter = new ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                new String[]{"", "O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"});
        bloodSP.setAdapter(bloodAdapter);

        bloodSP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.scrollTo(0, scrollView.getBottom());
                return false;
            }
        });

        verifyBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(UserActivity.this);
                if (!CommonUtils.alerter(getApplicationContext())) {
                    onSubmit();
                }
            }
        });
    }

    private void onSubmit() {
        name = nameET.getText().toString().trim();
        age = Integer.parseInt(ageET.getText().toString().isEmpty() ? "0" : (ageET.getText().toString().trim()));
        gender = genderSP.getSelectedItem().toString().trim();
        blood = bloodSP.getSelectedItem().toString().trim();
        phone = phonenoET.getText().toString().trim();
        if (name.isEmpty() || name == null) {
            Toast.makeText(this, "Please Enter A Name.", Toast.LENGTH_SHORT).show();
        } else if (age < 1 || age > 100) {
            Toast.makeText(this, "Please Enter A Valid Age.", Toast.LENGTH_SHORT).show();
        } else if (gender.isEmpty() || gender == null || gender.equals("")) {
            Toast.makeText(this, "Please Select A Gender.", Toast.LENGTH_SHORT).show();
        } else if (blood.isEmpty() || blood == null || blood.equals("")) {
            Toast.makeText(this, "Please Select A Blood Group.", Toast.LENGTH_SHORT).show();
        } else if (phone.isEmpty() || phone.length() != 10 || phone == null) {
            Toast.makeText(this, "Please Enter A Valid Number.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(UserActivity.this, OtpActivity.class);
            Log.i(TAG, "onSubmit: " + age);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CommonUtils.userName, name);
            intent.putExtra(CommonUtils.userAge, String.valueOf(age));
            intent.putExtra(CommonUtils.userBlood, blood);
            intent.putExtra(CommonUtils.userGender, gender);
            intent.putExtra(CommonUtils.userPhone, phone);
            startActivity(intent);
        }
    }

    private void initUI() {
        verifyBT = findViewById(R.id.verifyBT);
        nameET = findViewById(R.id.nameET);
        ageET = findViewById(R.id.ageET);
        genderSP = findViewById(R.id.genderET);
        phonenoET = findViewById(R.id.phoneET);
        bloodSP = findViewById(R.id.bloodET);
        scrollView = findViewById(R.id.scrollViewNS);
    }
}