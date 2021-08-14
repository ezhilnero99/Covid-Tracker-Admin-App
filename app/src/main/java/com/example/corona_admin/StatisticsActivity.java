package com.example.corona_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.corona_admin.models.UserLocationDetails;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "stats_tag";
    TextView dateTV, overallTV, todayTV;
    ImageView logoutIV, searchIV,languageTV;
    ArrayList<UserLocationDetails> userData = new ArrayList<>();
    AnyChartView chartviewAC;
    GifImageView progressIV;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userLocationData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_statistics);
        } else {
            setContentView(R.layout.activity_statistics_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();
        if (!CommonUtils.alerter(getApplicationContext())) {
            fetchAndset();
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        languageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StatisticsActivity.this);
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
                        SharedPref.putString(getApplicationContext(), "sp_language", "en");
                        alertDialog.dismiss();
                    }
                });

                tamilTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPref.putString(getApplicationContext(), "sp_language", "tam");
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

    private void setUpUI() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);
        dateTV.setText(today);
        int count = 0;

        overallTV.setText(String.valueOf(userData.size()));
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i).getDate().equals(today)) {
                count++;
            }
        }
        todayTV.setText(String.valueOf(count));
        setUpChartView();
    }

    private void setUpChartView() {
        Set<String> set = new HashSet<>();
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < userData.size(); i++) {
            set.add(userData.get(i).getDate());
        }
        dates.addAll(set);
        Collections.sort(dates);
        Log.i(TAG, "setUpChartView: " + dates.toString());
        for (int i = 0; i < dates.size(); i++) {
            int dayCount = 0;
            for (int j = 0; j < userData.size(); j++) {

                if (userData.get(j).getDate().equals(dates.get(i))) {
                    dayCount++;
                }
            }
            data.add(new ValueDataEntry(dates.get(i).substring(0, 5), dayCount));
        }

        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}");

        cartesian.animation(true);
        cartesian.title("Number of users recorded");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.xAxis(0).title("Dates");
        cartesian.yAxis(0).title("Users");

        chartviewAC.setChart(cartesian);
    }

    private void fetchAndset() {
        progressIV.setVisibility(View.VISIBLE);
        userCollRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    final UserLocationDetails userDetails = snapshot.toObject(UserLocationDetails.class);
                    userData.add(userDetails);
                }
                setUpUI();
                progressIV.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressIV.setVisibility(View.GONE);
                Toast.makeText(StatisticsActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        dateTV = findViewById(R.id.dateTV);
        overallTV = findViewById(R.id.overallTV);
        todayTV = findViewById(R.id.todayTV);
        logoutIV = findViewById(R.id.logoutIV);
        searchIV = findViewById(R.id.searchIV);
        chartviewAC = findViewById(R.id.chartViewCV);
        progressIV = findViewById(R.id.progressIV);
        languageTV = findViewById(R.id.languageTV);

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
