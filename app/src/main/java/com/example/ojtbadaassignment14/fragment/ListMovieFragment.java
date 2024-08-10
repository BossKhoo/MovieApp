package com.example.ojtbadaassignment14.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.adapter.MovieAdapter;
import com.example.ojtbadaassignment14.client.MovieApi;
import com.example.ojtbadaassignment14.client.MovieClient;
import com.example.ojtbadaassignment14.databinding.FragmentListMovieBinding;
import com.example.ojtbadaassignment14.livedata.FavouriteMovieViewModel;
import com.example.ojtbadaassignment14.models.ItemMovieOnListener;
import com.example.ojtbadaassignment14.models.Movie;
import com.example.ojtbadaassignment14.models.MovieResponse;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListMovieFragment extends Fragment {
    public static int type = 1;
    private FavouriteMovieViewModel movieViewModel;

    private FragmentListMovieBinding binding;
    private MovieAdapter movieAdapter;
    public static int viewMode = 1;
    private Disposable disposable;

    private List<Movie> listMovie = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public static ListMovieFragment newInstance() {
        return new ListMovieFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListMovieBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieAdapter = new MovieAdapter(requireContext());
        movieViewModel = new ViewModelProvider(requireActivity()).get(FavouriteMovieViewModel.class);
        initList();
        changeViewMode();
        callData(currentPage);
        binding.mSwipeRefresh.setRefreshing(true);
        setSwipeRefresh();

        movieViewModel.getFavouriteMovies().observe(getViewLifecycleOwner(), favouriteMovies -> {
            movieAdapter.changeData(favouriteMovies);
        });
    }

    private void showReload() {
        binding.mSwipeRefresh.setRefreshing(true);
    }

    private void setSwipeRefresh() {
        binding.mSwipeRefresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        binding.mSwipeRefresh.setOnRefreshListener(this::loadData);
    }

    public void loadData() {
        showReload();
        currentPage = 1;
        isLastPage = false;
        listMovie.clear();
        movieAdapter.setList(listMovie);

        switch (type) {
            case 2:
                callData2(currentPage);
                break;
            case 3:
                callData3(currentPage);
                break;
            case 4:
                callData4(currentPage);
                break;
            default:
                callData(currentPage);
                break;
        }
    }

    private void initList() {
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setAdapter(movieAdapter);
        movieAdapter.setItemMovieOnListener(new ItemMovieOnListener() {
            @Override
            public void onItemClick(int p, Movie movie) {
                mListener.onFunctionInParentCalled(movie);
            }

            @Override
            public void onFavouriteChange(int p, Movie movie) {
                movieViewModel.setFavouriteMovies(movie);
            }
        });

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == listMovie.size() - 1) {
                    if (!isLoading && !isLastPage) {
                        currentPage++;
                        switch (type) {
                            case 2:
                                callData2(currentPage);
                                break;
                            case 3:
                                callData3(currentPage);
                                break;
                            case 4:
                                callData4(currentPage);
                                break;
                            default:
                                callData(currentPage);
                                break;
                        }
                    }
                }
            }
        });
    }

    private OnFragmentListener mListener;

    public interface OnFragmentListener {
        void onFunctionInParentCalled(Movie movie);
    }

    public void setOnFragmentListener(OnFragmentListener mListener) {
        this.mListener = mListener;
    }

    public void changeViewMode() {
        if (viewMode == 2) {
            binding.recyclerview.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        } else {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        }
        onViewModeChanged(viewMode);
    }

    private void onViewModeChanged(int mode) {
        movieAdapter.setType(mode);
        setLayoutManager(mode);
    }

    private void setLayoutManager(int type) {
        movieAdapter.setType(type);
    }

    public void callData(int page) {
        isLoading = true;
        MovieApi client = MovieClient.getClient();
        disposable = client.getPopularMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                    @Override
                    public void onSuccess(MovieResponse movieResponse) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        if (movieResponse != null) {
                            List<Movie> movies = movieResponse.getResults();
                            if (movies.isEmpty()) {
                                isLastPage = true;
                            } else {
                                listMovie.addAll(movies);
                                movieAdapter.setList(listMovie);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        Log.d("__haha", "onError: " + e.getMessage());
                    }
                });
    }

    public void callData2(int page) {
        isLoading = true;
        MovieApi client = MovieClient.getClient();
        disposable = client.getTopRated(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                    @Override
                    public void onSuccess(MovieResponse movieResponse) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        if (movieResponse != null) {
                            List<Movie> movies = movieResponse.getResults();
                            if (movies.isEmpty()) {
                                isLastPage = true;
                            } else {
                                listMovie.addAll(movies);
                                movieAdapter.setList(listMovie);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        Log.d("__haha", "onError: " + e.getMessage());
                    }
                });
    }

    public void callData3(int page) {
        isLoading = true;
        MovieApi client = MovieClient.getClient();
        disposable = client.getUpcoming(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                    @Override
                    public void onSuccess(MovieResponse movieResponse) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        if (movieResponse != null) {
                            List<Movie> movies = movieResponse.getResults();
                            if (movies.isEmpty()) {
                                isLastPage = true;
                            } else {
                                listMovie.addAll(movies);
                                movieAdapter.setList(listMovie);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        Log.d("__haha", "onError: " + e.getMessage());
                    }
                });
    }

    public void callData4(int page) {
        isLoading = true;
        MovieApi client = MovieClient.getClient();
        disposable = client.getNowPlaying(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                    @Override
                    public void onSuccess(MovieResponse movieResponse) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        if (movieResponse != null) {
                            List<Movie> movies = movieResponse.getResults();
                            if (movies.isEmpty()) {
                                isLastPage = true;
                            } else {
                                listMovie.addAll(movies);
                                movieAdapter.setList(listMovie);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        binding.mSwipeRefresh.setRefreshing(false);
                        Log.d("__haha", "onError: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

