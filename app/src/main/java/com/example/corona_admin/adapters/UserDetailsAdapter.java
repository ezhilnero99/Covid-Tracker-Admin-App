package com.example.corona_admin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.corona_admin.R;
import com.example.corona_admin.UserHistoryActivity;
import com.example.corona_admin.models.UserDetails;
import com.example.corona_admin.models.UserLocationDetails;
import com.example.corona_admin.utils.CommonUtils;
import com.example.corona_admin.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.SearchViewHolder> {

    private ArrayList<UserDetails> userData;
    private Context context;
    private Activity parent;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userLocationData");

    public UserDetailsAdapter(Context context, ArrayList<UserDetails> userData, Activity parent) {
        this.context = context;
        this.userData = userData;
        this.parent = parent;
    }

    @NonNull
    @Override
    public UserDetailsAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (SharedPref.getString(context, "sp_language") == null || !SharedPref.getString(context, "sp_language").equals("tam")) {
            listItem = layoutInflater.inflate(R.layout.userdetails_item, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.userdetails_item_tamil, parent, false);
        }
        return new UserDetailsAdapter.SearchViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserDetailsAdapter.SearchViewHolder holder, int position) {
        final UserDetails userDetails = userData.get(position);
        holder.ageTV.setText(userDetails.getAge());
        holder.nameTV.setText(userDetails.getName());
        holder.bloodTV.setText(userDetails.getBlood());
        holder.genderTV.setText(userDetails.getGender());
        holder.phoneTV.setText(userDetails.getPhone());

        holder.userDetailsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.alerter(context)) {
                    Intent intent = new Intent(context, UserHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", userDetails.getName());
                    intent.putExtra("phone", userDetails.getPhone());
                    intent.putExtra("age", userDetails.getAge());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, genderTV, ageTV, bloodTV, phoneTV;
        CardView userDetailsCV;

        public SearchViewHolder(View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameTV);
            genderTV = itemView.findViewById(R.id.genderTV);
            ageTV = itemView.findViewById(R.id.ageTV);
            bloodTV = itemView.findViewById(R.id.bloodTV);
            phoneTV = itemView.findViewById(R.id.phoneTV);
            userDetailsCV = itemView.findViewById(R.id.userDetailsCV);
        }
    }
}
