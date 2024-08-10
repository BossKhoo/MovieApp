package com.example.ojtbadaassignment14.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ojtbadaassignment14.Pref;
import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.dao.FavouriteMovieDao;
import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.adapter.CreditsAdapter;
import com.example.ojtbadaassignment14.client.MovieApi;
import com.example.ojtbadaassignment14.client.MovieClient;
import com.example.ojtbadaassignment14.databinding.FragmentDetailsBinding;
import com.example.ojtbadaassignment14.livedata.FavouriteMovieViewModel;
import com.example.ojtbadaassignment14.models.CreditsResponse;
import com.example.ojtbadaassignment14.models.Movie;
import com.example.ojtbadaassignment14.models.MovieDetail;
import com.example.ojtbadaassignment14.reminder.AlarmService;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailsFragment extends Fragment {

    private FavouriteMovieViewModel movieViewModel;
    private static final String ARG_ID = "arg_id";


    private String movieString;
    private Movie movie;
    FavouriteMovieDao db;

    public String getMovieName() {
        return movie.getTitle();
    }

    public static DetailsFragment newInstance(String movieString) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, movieString);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieString = getArguments().getString(ARG_ID);
            movie = new Gson().fromJson(movieString, Movie.class);
        }
    }

    private FragmentDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(requireActivity()).favouriteMovieDao();
        alarmService = new AlarmService(requireActivity());
        movieViewModel = new ViewModelProvider(requireActivity()).get(FavouriteMovieViewModel.class);
        callData(movie.getId());
        initList(movie.getId());


        binding.favoriteIcon.setOnClickListener(v -> {
            boolean isFavourite = db.isMovieExists(movie.getId());
            if (isFavourite) {
                isFavourite = false;
                binding.favoriteIcon.setImageResource(R.drawable.star);
                db.delete(movie);
            } else {
                isFavourite = true;
                binding.favoriteIcon.setImageResource(R.drawable.star2);
                db.insert(movie);
            }
            movie.setFavourite(isFavourite);
            movieViewModel.setFavouriteMovies(movie);
        });

//        binding.setReminderButton.setOnClickListener(v -> showBottomReminder());

        movieViewModel.getFavouriteMovies().observe(getViewLifecycleOwner(), favouriteMovies -> {
            boolean isFavourite = db.isMovieExists(favouriteMovies.getId());
            if (isFavourite) {
                binding.favoriteIcon.setImageResource(R.drawable.star2);
            } else {
                binding.favoriteIcon.setImageResource(R.drawable.star);
            }
        });

    }

    private void initList(long id) {
        binding.castCrewRecyclerview.setHasFixedSize(true);
        binding.castCrewRecyclerview.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
        callData2(id);


    }


    public void callData(long id) {
        MovieApi client = MovieClient.getClient();
        client.getMovieDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<MovieDetail>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onSuccess(MovieDetail movieDetail) {
                        binding.overview.setText("Overview: \n" + Objects.requireNonNull(movieDetail).getOverview());
                        binding.releaseDate.setText("Release date: \n" + movieDetail.getReleaseDate());
                        binding.rating.setText("Rating: " + movieDetail.getVoteAverage() + "/10");

                        String url = "https://image.tmdb.org/t/p/original" + movieDetail.getPosterPath();

                        new Thread(() -> {
                            boolean isFavourite = db.isMovieExists(id);
                            requireActivity().runOnUiThread(() -> {
                                binding.favoriteIcon.setImageResource(isFavourite ? R.drawable.star2 : R.drawable.star);
                            });
                        }).start();

                        Glide.with(requireActivity())
                                .load(url)
                                .into(binding.poster);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("__haha", "onResponse: Error");
                    }
                });
    }

    public void callData2(long id) {
        MovieApi client = MovieClient.getClient();
        client.getMovieCredits(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CreditsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onSuccess(CreditsResponse creditsResponse) {
                        CreditsAdapter creditsAdapter = new CreditsAdapter(requireActivity(), Objects.requireNonNull(creditsResponse).getCast(), creditsResponse.getCrew());
                        binding.castCrewRecyclerview.setAdapter(creditsAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("__haha", "onResponse: Error");
                    }
                });
    }

    Calendar c = Calendar.getInstance();
    private long timeInMillis = 0;

    private void showDateTimePickerDialog(TextView v) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    final int selectedYear = year1;
                    final int selectedMonth = monthOfYear;
                    final int selectedDay = dayOfMonth;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);


                    TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(),
                            (view1, hourOfDay, minute1) -> {
                                Calendar selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute1);


                                if (selectedDateTime.getTimeInMillis() < System.currentTimeMillis()) {
                                    Toast.makeText(requireActivity(), "Không được chọn thời gian bé hơn hiện tại", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                timeInMillis = selectedDateTime.getTimeInMillis();
                                v.setText(Pref.convertDateTime(timeInMillis));
                            }, hour, minute, true);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }

    private AlarmService alarmService;

//    private void showBottomReminder() {
//        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
//        dialog.setContentView(R.layout.layout_bottom_reminder);
//        dialog.setCancelable(true);
//        dialog.show();
//
//        AppCompatEditText edtTitle = dialog.findViewById(R.id.edtTitle);
//        AppCompatEditText edtDescription = dialog.findViewById(R.id.edtDescription);
//        TextView tvDateTime = dialog.findViewById(R.id.tvDateTime);
//        TextView tvSave = dialog.findViewById(R.id.tvSave);
//        timeInMillis = System.currentTimeMillis();
//        Objects.requireNonNull(edtTitle).setText(movie.getTitle());
//        Objects.requireNonNull(tvDateTime).setText(Pref.convertDateTime(timeInMillis));
//        tvDateTime.setOnClickListener(v -> showDateTimePickerDialog(tvDateTime));
//        Objects.requireNonNull(tvSave).setOnClickListener(v -> {
//            Reminder reminder = new Reminder();
//            reminder.setMovieId(movie.getId());
//            reminder.setTitle(Objects.requireNonNull(edtTitle.getText()).toString().trim());
//            reminder.setDescription(Objects.requireNonNull(Objects.requireNonNull(edtDescription).getText()).toString().trim());
//            reminder.setPoster(movie.getPoster_path());
//            reminder.setDatetime(timeInMillis);
//            long id = db.addReminder(reminder);
//
//            alarmService.setExactAlarm(timeInMillis, String.valueOf(id).hashCode(), reminder.getTitle(), reminder.getDescription());
//
//            dialog.dismiss();
//            Toast.makeText(requireActivity(), "Đã thêm vào nhắc nhở", Toast.LENGTH_SHORT).show();
//            ((MainActivity) requireActivity()).loadView();
//        });
//
//
//    }


}

//
//    private void showBottomReminder() {
//        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
//        dialog.setContentView(R.layout.layout_bottom_reminder);
//        dialog.setCancelable(true);
//        dialog.show();
//
//        AppCompatEditText edtTitle = dialog.findViewById(R.id.edtTitle);
//        AppCompatEditText edtDescription = dialog.findViewById(R.id.edtDescription);
//        TextView tvDateTime = dialog.findViewById(R.id.tvDateTime);
//        TextView tvSave = dialog.findViewById(R.id.tvSave);
//        timeInMillis = System.currentTimeMillis();
//        Objects.requireNonNull(edtTitle).setText(movie.getTitle());
//        Objects.requireNonNull(tvDateTime).setText(Pref.convertDateTime(timeInMillis));
//        tvDateTime.setOnClickListener(v -> showDateTimePickerDialog(tvDateTime));
//        Objects.requireNonNull(tvSave).setOnClickListener(v -> {
//            Reminder reminder = new Reminder();
//            reminder.setMovieId(movie.getId());
//            reminder.setTitle(Objects.requireNonNull(edtTitle.getText()).toString().trim());
//            reminder.setDescription(Objects.requireNonNull(Objects.requireNonNull(edtDescription).getText()).toString().trim());
//            reminder.setPoster(movie.getPoster_path());
//            reminder.setDatetime(timeInMillis);
//            long id = db.addReminder(reminder);
//
//            alarmService.setExactAlarm(timeInMillis, String.valueOf(id).hashCode(), reminder.getTitle(), reminder.getDescription());
//
//            dialog.dismiss();
//            Toast.makeText(requireActivity(), "Add reminder successfully", Toast.LENGTH_SHORT).show();
//            ((MainActivity) requireActivity()).loadView();
//        });
//



