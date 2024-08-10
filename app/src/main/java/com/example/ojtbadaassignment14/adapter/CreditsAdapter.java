package com.example.ojtbadaassignment14.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.models.Cast;

import com.example.ojtbadaassignment14.models.Crew;

import java.util.List;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {
    private List<Cast> castList;
    private List<Crew> crewList;
    private Context context;

    public CreditsAdapter(Context context, List<Cast> castList, List<Crew> crewList) {
        this.context = context;
        this.castList = castList;
        this.crewList = crewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < castList.size()) {
            Cast cast = castList.get(position);
            holder.nameTextView.setText(cast.getName());

            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/original" + cast.getProfilePath())

                    .into(holder.profileImageView);
        } else {
            int crewPosition = position - castList.size();
            Crew crew = crewList.get(crewPosition);
            holder.nameTextView.setText(crew.getName());
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/original" + crew.getProfilePath())

                    .into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        return castList.size() + crewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}