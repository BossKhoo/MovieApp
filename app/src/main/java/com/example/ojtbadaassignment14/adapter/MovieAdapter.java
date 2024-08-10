package com.example.ojtbadaassignment14.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ojtbadaassignment14.dao.FavouriteMovieDao;
import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.models.ItemMovieOnListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> list = new ArrayList<>();

    private int type = 1;


    private FavouriteMovieDao  helper;
    private ItemMovieOnListener itemMovieOnListener;

    public void setItemMovieOnListener(ItemMovieOnListener itemMovieOnListener) {
        this.itemMovieOnListener = itemMovieOnListener;
    }

    public void setList(List<Movie> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public MovieAdapter(Context activity) {
        helper = AppDatabase.getInstance(activity).favouriteMovieDao();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false));
        } else {
            return new VH2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_2, parent, false));
        }

    }

    @Override
    public int getItemViewType(int position) {

        return type;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movie movie = this.list.get(holder.getAdapterPosition());

        if (type == 1) {
            VH hd = (VH) holder;
            hd.tvRating.setText("Rating: " + movie.getVote_average());
            hd.tvReleaseDate.setText("Release date: " + movie.getRelease_date());
            hd.tvOverview.setText(movie.getOverview());
            hd.tvMovieName.setText(movie.getTitle());

            String url = "https://image.tmdb.org/t/p/original" + movie.getPoster_path();
            Log.d("__haha", "onBindViewHolder: " + url);

            Picasso.get().load(url)
                    .centerCrop()
                    .fit()
                    .into(hd.imageView);

            AtomicBoolean isFavourite = new AtomicBoolean(helper.isMovieExists(movie.getId()));
            this.list.get(holder.getAdapterPosition()).setFavourite(isFavourite.get());
            if (isFavourite.get()) {
                hd.imvFavourite.setImageResource(R.drawable.star2);
            } else {
                hd.imvFavourite.setImageResource(R.drawable.star);
            }

            hd.imvFavourite.setOnClickListener(v -> {
                if (isFavourite.get()) {
                    isFavourite.set(false);
                    hd.imvFavourite.setImageResource(R.drawable.star);
                    helper.delete(movie);
                } else {
                    isFavourite.set(false);
                    hd.imvFavourite.setImageResource(R.drawable.star2);
                    helper.insert(movie);
                }
                movie.setFavourite(isFavourite.get());
                this.list.get(holder.getAdapterPosition()).setFavourite(isFavourite.get());
                itemMovieOnListener.onFavouriteChange(holder.getAdapterPosition(), movie);


            });

        } else {
            VH2 hd = (VH2) holder;
            hd.tvMovieName.setText(movie.getTitle());

            String url = "https://image.tmdb.org/t/p/original" + movie.getPoster_path();
            Log.d("__haha", "onBindViewHolder: " + url);

            Picasso.get().load(url)
                    .centerCrop()
                    .fit()
                    .into(hd.imageView);
        }

        holder.itemView.setOnClickListener(v -> itemMovieOnListener.onItemClick(holder.getAdapterPosition(), movie));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setType(int mode) {
        this.type = mode;
        notifyDataSetChanged();
    }

    public void changeData(Movie movie) {
        for (int i = 0; i < list.size(); i++) {
            if (this.list.get(i).getId() == movie.getId()) {
                this.list.set(i, movie);
                notifyItemChanged(i);
                return;
            }
        }

    }

    static class VH extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView tvMovieName;
        private TextView tvReleaseDate;
        private TextView tvRating;
        private TextView tvOverview;
        private ImageView imvFavourite;

        public VH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            tvReleaseDate = itemView.findViewById(R.id.tvDate);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvRating = itemView.findViewById(R.id.tvRate);
            tvOverview = itemView.findViewById(R.id.tvOverView);
            imvFavourite = itemView.findViewById(R.id.imvFavourite);
        }
    }

    static class VH2 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView tvMovieName;


        public VH2(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);

        }
    }

}
