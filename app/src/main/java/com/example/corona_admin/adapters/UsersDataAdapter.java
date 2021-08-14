package com.example.corona_admin.adapters;

import android.app.Activity;
import android.content.Context;
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

public class UsersDataAdapter extends RecyclerView.Adapter<UsersDataAdapter.SearchViewHolder> {

    private ArrayList<UserDetails> userData;
    private Context context;
    private Activity parent;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userLocationData");

    public UsersDataAdapter(Context context, ArrayList<UserDetails> userData, Activity parent) {
        this.context = context;
        this.userData = userData;
        this.parent = parent;
    }

    @NonNull
    @Override
    public UsersDataAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (SharedPref.getString(context, "sp_language") == null || !SharedPref.getString(context, "sp_language").equals("tam")) {
            listItem = layoutInflater.inflate(R.layout.userdetails_item, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.userdetails_item_tamil, parent, false);
        }
        return new UsersDataAdapter.SearchViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersDataAdapter.SearchViewHolder holder, int position) {
        final UserDetails userDetails = userData.get(position);
        holder.ageTV.setText(userDetails.getAge());
        holder.nameTV.setText(userDetails.getName());
        holder.bloodTV.setText(userDetails.getBlood());
        holder.genderTV.setText(userDetails.getGender());
        holder.phoneTV.setText(userDetails.getPhone());

        holder.userDetailsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(parent);
                LayoutInflater inflater = parent.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_temp, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView cancelTV = dialogView.findViewById(R.id.cancelTV);
                TextView submitTV = dialogView.findViewById(R.id.submitTV);
                TextView nameTV = dialogView.findViewById(R.id.nameTV);
                final EditText tempET = dialogView.findViewById(R.id.tempET);
                final GifImageView progressIV = dialogView.findViewById(R.id.progressIV);

                nameTV.setText(userDetails.getName());

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();


                submitTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.hideKeyboard(parent);
                        if (!CommonUtils.alerter(context) && SharedPref.getString(context, "sp_location") != null) {
                            progressIV.setVisibility(View.VISIBLE);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            String Location = SharedPref.getString(context, "sp_location") + ", " + SharedPref.getString(context, "sp_city") + ", " + SharedPref.getString(context, "sp_pincode");
                            String temperature = tempET.getText().toString();
                            if (temperature.isEmpty() || (temperature.length() < 4 && temperature.length() > 1)) {
                                UserLocationDetails userLocationDetails = new UserLocationDetails();
                                userLocationDetails.setAge(userDetails.getAge());
                                userLocationDetails.setAsvEmail(SharedPref.getString(context, "sp_email"));
                                userLocationDetails.setBlood(userDetails.getBlood());
                                userLocationDetails.setGender(userDetails.getGender());
                                userLocationDetails.setLocation(Location);
                                userLocationDetails.setName(userDetails.getName());
                                userLocationDetails.setPhone(userDetails.getPhone());
                                userLocationDetails.setDate(formatter.format(date).substring(0, 10));
                                userLocationDetails.setTime(formatter.format(date).substring(11, 19));
                                userLocationDetails.setTemperature(temperature);

                                userCollRef.add(userLocationDetails).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        progressIV.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(parent, "Successfull", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(parent, "Failed:", Toast.LENGTH_SHORT).show();
                                        }
                                        alertDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressIV.setVisibility(View.GONE);
                                        Toast.makeText(parent, "Failed:", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                });
                            } else {
                                progressIV.setVisibility(View.GONE);
                                Toast.makeText(parent, "Invalid Temperature", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Location Details needed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

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
