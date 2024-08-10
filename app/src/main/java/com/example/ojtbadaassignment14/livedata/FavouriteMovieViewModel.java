package com.example.ojtbadaassignment14.livedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ojtbadaassignment14.models.Movie;

import java.util.ArrayList;
import java.util.List;


public class FavouriteMovieViewModel extends ViewModel {
    private final MutableLiveData<Movie> favouriteMovies = new MutableLiveData<>();

    public LiveData<Movie> getFavouriteMovies() {
        return favouriteMovies;
    }

    public void setFavouriteMovies(Movie movies) {

        favouriteMovies.setValue(movies);
    }
}
