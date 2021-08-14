package com.example.corona_admin.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.corona_admin.R;
import com.example.corona_admin.models.UserLocationDetails;
import com.example.corona_admin.utils.SharedPref;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersFilterAdapter extends RecyclerView.Adapter<UsersFilterAdapter.SearchViewHolder> {

    private ArrayList<UserLocationDetails> userData;
    private Context context;
    private Activity parent;

    //firestore Initialization
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollRef = db.collection("userLocationData");

    public UsersFilterAdapter(Context context, ArrayList<UserLocationDetails> userData, Activity parent) {
        this.context = context;
        this.userData = userData;
        this.parent = parent;
    }

    @NonNull
    @Override
    public UsersFilterAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (SharedPref.getString(context, "sp_language") == null || !SharedPref.getString(context, "sp_language").equals("tam")) {
            listItem = layoutInflater.inflate(R.layout.userfilter_item, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.userfilter_item_tamil, parent, false);
        }        return new UsersFilterAdapter.SearchViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersFilterAdapter.SearchViewHolder holder, int position) {
        final UserLocationDetails UserLocationDetails = userData.get(position);
        holder.ageTV.setText(UserLocationDetails.getAge());
        holder.nameTV.setText(UserLocationDetails.getName());
        holder.bloodTV.setText(UserLocationDetails.getBlood());
        holder.genderTV.setText(UserLocationDetails.getGender());
        holder.phoneTV.setText(UserLocationDetails.getPhone());
        holder.timeTV.setText(UserLocationDetails.getTime());
        holder.tempTV.setText(UserLocationDetails.getTemperature());

    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, genderTV, ageTV, bloodTV, phoneTV, timeTV,tempTV;


        public SearchViewHolder(View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameTV);
            genderTV = itemView.findViewById(R.id.genderTV);
            ageTV = itemView.findViewById(R.id.ageTV);
            tempTV = itemView.findViewById(R.id.tempTV);
            bloodTV = itemView.findViewById(R.id.bloodTV);
            phoneTV = itemView.findViewById(R.id.phoneTV);
            timeTV = itemView.findViewById(R.id.timeTV);
        }
    }
}
