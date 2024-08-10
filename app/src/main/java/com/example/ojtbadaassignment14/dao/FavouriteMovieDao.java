package com.example.ojtbadaassignment14.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ojtbadaassignment14.models.Movie;

import java.util.List;

@Dao
public interface FavouriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie favouriteMovie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Movie favouriteMovie);

    @Delete
    void delete(Movie favouriteMovie);

    @Query("SELECT * FROM favourite_movies")
    List<Movie> getAllFavouriteMovies();

    @Query("SELECT * FROM favourite_movies WHERE id = :id")
    Movie getFavouriteMovieById(long id);

    @Query("SELECT * FROM favourite_movies WHERE title LIKE '%' || :searchTerm || '%'")
    List<Movie> searchFavouriteMovies(String searchTerm);

    @Query("SELECT COUNT(*) FROM favourite_movies")
    int getMovieCount();

    @Query("SELECT COUNT(*) > 0 FROM favourite_movies WHERE id = :id")
    boolean isMovieExists(long id);

}
