package com.example.corona_admin.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    public static final String userName = "name";
    public static final String userAge = "age";
    public static final String userGender = "gender";
    public static final String userPhone = "phone";
    public static final String userBlood = "blood";
    public static final String userLocation = "location";
    public static final String userTemperature = "temperature";
    public static final String userDate = "date";
    public static final String usertime = "time";

    //Hides Keyboard
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Checks for Internet Connectivity
    public static boolean alerter(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            Boolean flag = !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
            if (flag)
                Toast.makeText(context, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return flag;
        }
        return false;
    }

}
