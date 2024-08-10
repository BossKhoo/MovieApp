package com.example.ojtbadaassignment14.client;

import com.example.ojtbadaassignment14.models.CreditsResponse;
import com.example.ojtbadaassignment14.models.MovieDetail;
import com.example.ojtbadaassignment14.models.MovieResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieApi {
    @GET("movie/popular?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieResponse> getMovies();

    @GET("movie/popular?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieResponse> getPopularMovies(@Query("page") int page);

    @GET("movie/upcoming?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieResponse> getUpcoming(@Query("page") int page);

    @GET("movie/top_rated?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieResponse> getTopRated(@Query("page") int page);

    @GET("movie/now_playing?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieResponse> getNowPlaying(@Query("page") int page);

    @GET("movie/{movie_id}?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<MovieDetail> getMovieDetails(@Path("movie_id") long movieId);

    @GET("movie/{movie_id}/credits?api_key=e7631ffcb8e766993e5ec0c1f4245f93")
    Single<CreditsResponse> getMovieCredits(@Path("movie_id") long movieId);

}
