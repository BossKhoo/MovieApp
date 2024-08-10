package com.example.ojtbadaassignment14;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.ojtbadaassignment14.activity.EditActivity;
import com.example.ojtbadaassignment14.activity.ReminderActivity;
import com.example.ojtbadaassignment14.adapter.PagerAdapter;
import com.example.ojtbadaassignment14.adapter.ReminderAdapter;
import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.database.FirebaseHelper;
import com.example.ojtbadaassignment14.databinding.ActivityMainBinding;
import com.example.ojtbadaassignment14.fragment.AboutFragment;
import com.example.ojtbadaassignment14.fragment.DetailsFragment;
import com.example.ojtbadaassignment14.fragment.FavouriteFragment;
import com.example.ojtbadaassignment14.fragment.HomeFragment;
import com.example.ojtbadaassignment14.fragment.ListMovieFragment;
import com.example.ojtbadaassignment14.fragment.SettingsFragment;
import com.example.ojtbadaassignment14.livedata.FavouriteMovieViewModel;
import com.example.ojtbadaassignment14.livedata.ReminderViewModel;
import com.example.ojtbadaassignment14.models.ItemReminderOnListener;
import com.example.ojtbadaassignment14.models.Reminder;
import com.example.ojtbadaassignment14.models.User;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    MaterialButton btnEdit;
    MaterialButton btnShowAll;
    ImageView imvAvatar;
    TextView tvName, tvDate, tvGender;
    private ActivityMainBinding binding;
    private AppDatabase db;
    private Handler mHandle = new Handler(Looper.getMainLooper());
    private ReminderViewModel reminderViewModel;
    private FirebaseHelper firebaseHelper;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = AppDatabase.getInstance(this);
        firebaseHelper = new FirebaseHelper();
        movieViewModel = new ViewModelProvider(this).get(FavouriteMovieViewModel.class);

        initToolbar();
        initDrawerLayout();
        initViewPager();
        initBottomBar();
        mHandle.postDelayed(this::hideSplash, 1500);
        showPhotoPermission();


        binding.menu.setOnClickListener(this::showPopupMenu);

        movieViewModel.getFavouriteMovies().observe(this, favouriteMovies -> {
            badge.setNumber(db.favouriteMovieDao().getMovieCount());
        });

    }

    private PagerAdapter pagerAdapter;


    private void initBottomBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        AtomicInteger icon = new AtomicInteger(R.drawable.ic_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            String title = "";
            if (item.getItemId() == R.id.navigation_home) {
                binding.viewPager.setCurrentItem(0, false);
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment instanceof ListMovieFragment) {
                    icon.set(R.drawable.ic_menu);
                    binding.layoutOfMenu.setVisibility(View.VISIBLE);
                    title = "Movie";
                } else if (currentFragment instanceof DetailsFragment) {
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    title = ((DetailsFragment) currentFragment).getMovieName();
                    icon.set(R.drawable.back);
                }
            } else if (item.getItemId() == R.id.navigation_favourite) {
                binding.layoutOfMenu.setVisibility(View.GONE);
                icon.set(R.drawable.ic_menu);
                binding.viewPager.setCurrentItem(1, false);
                title = "Favourite";
            } else if (item.getItemId() == R.id.navigation_settings) {
                binding.layoutOfMenu.setVisibility(View.GONE);
                icon.set(R.drawable.ic_menu);
                binding.viewPager.setCurrentItem(2, false);
                title = "Settings";
            } else if (item.getItemId() == R.id.navigation_about) {
                binding.layoutOfMenu.setVisibility(View.GONE);
                icon.set(R.drawable.ic_menu);
                binding.viewPager.setCurrentItem(3, false);
                title = "About";
            }
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(icon.get());
            binding.toolbar.setTitle(title);

            return true;
        });


        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    }
                    case 1: {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_favourite);
                        break;
                    }
                    case 2: {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
                        break;
                    }
                    case 3: {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_about);
                        break;
                    }
                }

            }
        });


        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null) {
                String title = "";
                if (currentFragment instanceof HomeFragment) {
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    title = "Home";
                } else if (currentFragment instanceof FavouriteFragment) {
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

                    title = "Favourite";
                } else if (currentFragment instanceof SettingsFragment) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    title = "Settings";
                } else if (currentFragment instanceof AboutFragment) {
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    title = "About";
                } else if (currentFragment instanceof ListMovieFragment) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    binding.layoutOfMenu.setVisibility(View.VISIBLE);
                    title = "Movie";
                } else if (currentFragment instanceof DetailsFragment) {
                    binding.layoutOfMenu.setVisibility(View.GONE);
                    title = ((DetailsFragment) currentFragment).getMovieName();
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
                }
                binding.toolbar.setTitle(title);

            }
        });

        binding.toolbar.setNavigationOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();

            } else {
                performCustomAction();
            }
        });

        badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_favourite);
        badge.setVisible(true);
        badge.setNumber(db.favouriteMovieDao().getMovieCount());

    }

    private FavouriteMovieViewModel movieViewModel;
    BadgeDrawable badge;

    private void initViewPager() {
        pagerAdapter = new PagerAdapter(this);
        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setUserInputEnabled(true);
        binding.viewPager.setSaveEnabled(false);
        binding.viewPager.setAdapter(pagerAdapter);
    }

    private void initDrawerLayout() {
        NavigationView navigationView = binding.nav;
        View headerView = navigationView.getHeaderView(0);
        tvName = headerView.findViewById(R.id.tvMail);
        tvDate = headerView.findViewById(R.id.tvDate);
        tvGender = headerView.findViewById(R.id.tvGender);
        imvAvatar = headerView.findViewById(R.id.imvAvatar);
        btnEdit = headerView.findViewById(R.id.btnEdit);
        btnShowAll = headerView.findViewById(R.id.btnShowAll);
        recyclerView = headerView.findViewById(R.id.recyclerviewReminder);
        loadView();

        btnShowAll.setOnClickListener(v -> startForResult.launch(new Intent(MainActivity.this, ReminderActivity.class)));

        btnEdit.setOnClickListener(v -> {
            startForResult.launch(new Intent(this, EditActivity.class));
        });
        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();

                        if (intent == null) {
                            return;
                        }
                        loadView();

                    }
                }
        );

        if (ListMovieFragment.viewMode == 1) {
            binding.viewMode.setImageResource(R.drawable.filter);
        } else {
            binding.viewMode.setImageResource(R.drawable.filter_2);
        }
        binding.viewMode.setOnClickListener(v -> {
            if (ListMovieFragment.viewMode == 1) {
                ListMovieFragment.viewMode = 2;
                binding.viewMode.setImageResource(R.drawable.filter_2);

            } else {
                ListMovieFragment.viewMode = 1;
                binding.viewMode.setImageResource(R.drawable.filter);

            }

            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof ListMovieFragment) {
                ListMovieFragment movieFragment = (ListMovieFragment) currentFragment;
                movieFragment.changeViewMode();
            }

        });


    }

    RecyclerView recyclerView;

    private void initList() {
        ReminderAdapter reminderAdapter = new ReminderAdapter();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(reminderAdapter);

        reminderViewModel.getTop3RecentReminders().observe(this, reminders -> {
            reminderAdapter.setData(reminders);
        });

        reminderAdapter.setItemReminderOnListener(new ItemReminderOnListener() {
            @Override
            public void onLongClick(Reminder reminder) {

            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

    }


    public void showDrawerLayout() {
        binding.main.openDrawer(GravityCompat.START);
    }

    public void hideDrawerLayout() {
        binding.main.closeDrawer(GravityCompat.START);
    }

    private void hideSplash() {
        Log.d("__haha", "hideSplash: haha");
        binding.splashLayout.setVisibility(View.GONE);
        binding.containerLayout.setVisibility(View.VISIBLE);
    }


    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void performCustomAction() {
        showDrawerLayout();
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_home, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof ListMovieFragment) {
                ListMovieFragment movieFragment = (ListMovieFragment) currentFragment;
                if (item.getItemId() == R.id.menu_item1) {
                    ListMovieFragment.type = 1;
                } else if (item.getItemId() == R.id.menu_item2) {
                    ListMovieFragment.type = 2;
                } else if (item.getItemId() == R.id.menu_item3) {
                    ListMovieFragment.type = 3;

                } else if (item.getItemId() == R.id.menu_item4) {
                    ListMovieFragment.type = 4;

                }
                movieFragment.loadData();
                return true;
            }
            return false;


        });

        popup.show();
    }


    private ActivityResultLauncher<Intent> startForResult;

    public void loadView() {
        String userId = (Pref.getString(this, Pref.KEY_USER_ID, null));
        Log.d("MainActivity", "User ID: " + userId);
        if (userId != null) {
            firebaseHelper.getUser(userId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Log.d("MainActivity", "User data: " + user.toString());
                        updateUI(user);
                    } else {
                        Log.d("MainActivity", "User data is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("MainActivity", "Firebase error: " + error.getMessage());
                }
            });
        } else {
            Log.d("MainActivity", "User ID is null");
        }
    }

    private void updateUI(User user) {
        Glide.with(this).load(Pref.decodeBase64ToImage(user.photoBase64))
                .error(R.drawable.bg_default_circle_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop().into(imvAvatar);
        tvName.setText(user.name);
        tvDate.setText(Pref.convertDate(user.dateOfBirth));
        tvGender.setText(user.gender);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    private void showPhotoPermission() {
        int p3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        int p4 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (p3 != PackageManager.PERMISSION_GRANTED || p4 != PackageManager.PERMISSION_GRANTED) {
                requestPhotoPermission();
            }
        }
    }

    private void requestPhotoPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.READ_MEDIA_IMAGES
                },
                123
        );
    }


}