package com.example.ojtbadaassignment14.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ojtbadaassignment14.fragment.AboutFragment;
import com.example.ojtbadaassignment14.fragment.FavouriteFragment;
import com.example.ojtbadaassignment14.fragment.HomeFragment;
import com.example.ojtbadaassignment14.fragment.SettingsFragment;


public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fm;
        switch (position) {
            case 0:
                fm = HomeFragment.newInstance();
                break;
            case 1:
                fm = FavouriteFragment.newInstance();
                break;
            case 2:
                fm = SettingsFragment.newInstance();
                break;
            case 3:
                fm = AboutFragment.newInstance();
                break;
            default:
                fm = new Fragment();
                break;
        }
        return fm;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
