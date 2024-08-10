package com.example.ojtbadaassignment14.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.databinding.FragmentHomeBinding;
import com.google.gson.Gson;


public class HomeFragment extends Fragment {


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListMovieFragment fragment = ListMovieFragment.newInstance();
        replaceFragment(fragment, "Movie");
        Gson gson = new Gson();
        fragment.setOnFragmentListener(movie -> replaceFragment(DetailsFragment.newInstance(gson.toJson(movie)),"Details"));

    }


    private void replaceFragment(Fragment selectedFragment, String title) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("movie")
                .commit();

    }


}