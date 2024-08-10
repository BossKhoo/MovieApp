package com.example.ojtbadaassignment14.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.ojtbadaassignment14.Pref;
import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.database.FirebaseHelper;
import com.example.ojtbadaassignment14.databinding.ActivityEditBinding;
import com.example.ojtbadaassignment14.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
public class EditActivity extends AppCompatActivity {
    private ActivityEditBinding binding;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseHelper = new FirebaseHelper();
        String userId = Pref.getString(this, Pref.KEY_USER_ID, null);
        if (userId != null) {
            fetchUser(userId);
        }
        setupListeners();
    }
    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> saveData());
        binding.image.setOnClickListener(v -> checkAndRequestPermissions());
        binding.tvAge.setOnClickListener(v -> showDatePicker());
        binding.tvGender.setOnClickListener(v -> showBottomSheetGenderChooser());
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void fetchUser(String userId) {
        firebaseHelper.getUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    populateFields(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void populateFields(User user) {
        binding.edtName.setText(user.name);
        binding.tvGender.setText(user.gender);
        binding.tvAge.setText(Pref.convertDate(user.dateOfBirth));
        if (user.photoBase64 != null) {
            bitmap = Pref.decodeBase64ToImage(user.photoBase64);
            Glide.with(this).load(bitmap)
                    .error(R.drawable.bg_default_circle_avatar)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop().into(binding.image);
        }
    }

    private void saveData() {
        User user = new User();
        user.name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
        user.gender = Objects.requireNonNull(binding.tvGender.getText()).toString().trim();
        user.dateOfBirth = cal.getTimeInMillis();

        if (bitmap != null) {
            user.photoBase64 = Pref.encodeImageToBase64(bitmap);
        }

        firebaseHelper.saveUser(user, this); // Save user and store user ID in SharedPreferences
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void checkAndRequestPermissions() {
        String[] permissions = getPermissions();

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            requestPermissionsLauncher.launch(permissionsArray);
        } else {
            showBottomSheetImageChooser();
        }
    }

    private void showBottomSheetImageChooser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_bottom_photo);
        dialog.setCancelable(true);
        dialog.show();

        View btnCamera = dialog.findViewById(R.id.tv_camera);
        View btnGallery = dialog.findViewById(R.id.tv_gallery);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnCamera).setOnClickListener(v -> {
            getPhoto("camera");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnGallery).setOnClickListener(v -> {
            getPhoto("gallery");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    @NonNull
    private static String[] getPermissions() {
        String[] newSDKPermissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_MEDIA_IMAGES};
        String[] oldSDKPermissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = newSDKPermissions;
        } else {
            permissions = oldSDKPermissions;
        }
        return permissions;
    }


    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean allPermissionsGranted = true;
        for (Boolean isGranted : result.values()) {
            if (!isGranted) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (allPermissionsGranted) {
            showBottomSheetImageChooser();
        } else {
        }
    });

    private void showDatePicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                binding.tvAge.setText(Pref.convertDate(cal.getTimeInMillis()));
                Pref.setLong(EditActivity.this, Pref.KEY_USER_AGE, cal.getTimeInMillis());
            }
        };

        new DatePickerDialog(EditActivity.this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Calendar cal = Calendar.getInstance();

    @SuppressLint("SetTextI18n")
    private void showBottomSheetGenderChooser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_bottom_gender);
        dialog.setCancelable(true);
        dialog.show();

        TextView btnMale = dialog.findViewById(R.id.tvMale);
        TextView btnFemale = dialog.findViewById(R.id.tvFemale);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnMale).setText("Male");
        Objects.requireNonNull(btnFemale).setText("Female");

        Objects.requireNonNull(btnMale).setOnClickListener(v -> {
            binding.tvGender.setText("Male");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnFemale).setOnClickListener(v -> {
            binding.tvGender.setText("Female");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void getPhoto(String from) {
        try {
            Intent intent = new Intent(this, CropImageActivity.class);
            intent.putExtra("maxSize", 600);
            intent.putExtra("maxQuality", 100);
            intent.putExtra("photoFrom", from);
            croppedStartForResult.launch(intent);
        } catch (Exception exp) {
            Toast.makeText(this, exp.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    Bitmap bitmap;

    private ActivityResultLauncher<Intent> croppedStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent intent = result.getData();
            if (intent != null) {
                String path = intent.getStringExtra("path");

                Glide.with(this).load(path).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).circleCrop().into(binding.image);
                bitmap = BitmapFactory.decodeFile(path);

            }
        }
    });
}