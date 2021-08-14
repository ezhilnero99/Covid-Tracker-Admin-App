package com.example.corona_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.corona_admin.models.UserLocationDetails;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserHistoryActivity extends AppCompatActivity {
    private static final String TAG = "history_tag";
    TextView nameTV, ageTV;
    ImageView filterIV;
    RecyclerView detailsRV;
    String name, phone, age;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collref = db.collection("userLocationData");
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_user_history);
        } else {
            setContentView(R.layout.activity_user_history_tamil);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initUI();
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        age = getIntent().getStringExtra("age");
        nameTV.setText(name);
        ageTV.setText(age);

        filterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHistoryActivity.this, HistoryFilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("location", "");
                intent.putExtra("date", "");
                startActivity(intent);
            }
        });

        setupFireStore();

    }

    private void initUI() {
        nameTV = findViewById(R.id.nameTV);
        ageTV = findViewById(R.id.ageTV);
        filterIV = findViewById(R.id.filterIV);
        detailsRV = findViewById(R.id.DetailsRV);
        detailsRV.setLayoutManager(new LinearLayoutManager(this));
    }

    void setupFireStore() {
        Query query = collref
                .whereEqualTo(CommonUtils.userName, name)
                .whereEqualTo(CommonUtils.userPhone, phone).orderBy(CommonUtils.userDate, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserLocationDetails> options = new FirestoreRecyclerOptions.Builder<UserLocationDetails>().setQuery(query, new SnapshotParser<UserLocationDetails>() {
            @NonNull
            @Override
            public UserLocationDetails parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                final UserLocationDetails post = (UserLocationDetails) snapshot.toObject(UserLocationDetails.class);
                return post;
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<UserLocationDetails, UserHistoryActivity.FormsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserHistoryActivity.FormsViewHolder holder, int i, @NonNull final UserLocationDetails post) {
                holder.locationTV.setText(post.getLocation());
                holder.timeTV.setText(post.getTime());
                holder.dateTV.setText(post.getDate());
                holder.tempTV.setText(post.getTemperature());

                holder.userHistoryCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserHistoryActivity.this, HistoryFilterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("location", post.getLocation());
                        intent.putExtra("date", post.getDate());
                        intent.putExtra("name", post.getName());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public UserHistoryActivity.FormsViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view;
                if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
                     view = LayoutInflater.from(group.getContext()).inflate(R.layout.userhistory_item, group, false);
                } else {
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.userhistory_item_tamil, group, false);
                }
                return new UserHistoryActivity.FormsViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };

        detailsRV.setAdapter(adapter);
    }

    public class FormsViewHolder extends RecyclerView.ViewHolder {
        TextView locationTV, tempTV, dateTV, timeTV;
        CardView userHistoryCV;

        public FormsViewHolder(View itemView) {
            super(itemView);
            locationTV = itemView.findViewById(R.id.locationTV);
            tempTV = itemView.findViewById(R.id.tempTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            userHistoryCV = itemView.findViewById(R.id.userHistoryCV);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}