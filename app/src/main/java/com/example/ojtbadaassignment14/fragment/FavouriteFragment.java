package com.example.ojtbadaassignment14.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.adapter.MovieAdapter;
import com.example.ojtbadaassignment14.databinding.FragmentFavouriteBinding;
import com.example.ojtbadaassignment14.livedata.FavouriteMovieViewModel;
import com.example.ojtbadaassignment14.models.ItemMovieOnListener;
import com.example.ojtbadaassignment14.models.Movie;

import java.util.List;

public class FavouriteFragment extends Fragment {
    private FavouriteMovieViewModel movieViewModel;
    private FragmentFavouriteBinding binding;
    private AppDatabase db;
    private MovieAdapter movieAdapter;

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(requireActivity());
        movieAdapter = new MovieAdapter(requireContext());
        initList();
        setSwipeRefresh();
        movieViewModel = new ViewModelProvider(requireActivity()).get(FavouriteMovieViewModel.class);

        movieViewModel.getFavouriteMovies().observe(getViewLifecycleOwner(), favouriteMovies -> {
            new Thread(() -> {
                List<Movie> favouriteMovieList = db.favouriteMovieDao().getAllFavouriteMovies();
                requireActivity().runOnUiThread(() -> movieAdapter.setList(favouriteMovieList));
            }).start();
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread(() -> {
                    List<Movie> filteredMovies;
                    if (s.toString().isEmpty()) {
                        filteredMovies = db.favouriteMovieDao().getAllFavouriteMovies();
                    } else {
                        filteredMovies = db.favouriteMovieDao().searchFavouriteMovies(s.toString());
                    }
                    requireActivity().runOnUiThread(() -> movieAdapter.setList(filteredMovies));
                }).start();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setSwipeRefresh() {
        binding.mSwipeRefresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        binding.mSwipeRefresh.setOnRefreshListener(() -> {
            new Thread(() -> {
                List<Movie> favouriteMovies = db.favouriteMovieDao().getAllFavouriteMovies();
                requireActivity().runOnUiThread(() -> {
                    movieAdapter.setList(favouriteMovies);
                    binding.mSwipeRefresh.setRefreshing(false);
                });
            }).start();
        });
    }

    private void initList() {
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setAdapter(movieAdapter);
        movieAdapter.setItemMovieOnListener(new ItemMovieOnListener() {
            @Override
            public void onItemClick(int p, Movie movie) {
                // Handle item click
            }

            @Override
            public void onFavouriteChange(int p, Movie movie) {
                movieViewModel.setFavouriteMovies(movie);
            }
        });
        new Thread(() -> {
            List<Movie> favouriteMovies = db.favouriteMovieDao().getAllFavouriteMovies();
            requireActivity().runOnUiThread(() -> movieAdapter.setList(favouriteMovies));
        }).start();
    }
}
