package com.example.ojtbadaassignment14.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.adapter.ReminderAdapter;

import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.databinding.ActivityReminderBinding;
import com.example.ojtbadaassignment14.livedata.ReminderViewModel;
import com.example.ojtbadaassignment14.models.ItemReminderOnListener;
import com.example.ojtbadaassignment14.models.Reminder;
import com.example.ojtbadaassignment14.reminder.AlarmService;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class ReminderActivity extends AppCompatActivity {
    private ActivityReminderBinding binding;
    private ReminderViewModel reminderViewModel;
    private ReminderAdapter reminderAdapter;
    private AlarmService alarmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        alarmService = new AlarmService(this);

        initToolbar();
        initList();
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initList() {
        reminderAdapter = new ReminderAdapter();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.recyclerview.setAdapter(reminderAdapter);

        // Observe changes in reminders from ViewModel
        reminderViewModel.getAllReminders().observe(this, reminders -> {
            reminderAdapter.setData(reminders);
        });

        reminderAdapter.setItemReminderOnListener(new ItemReminderOnListener() {
            @Override
            public void onLongClick(Reminder reminder) {
                showBottomSheetMenu(reminder);
            }
        });
    }

    private void showBottomSheetMenu(Reminder reminder) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_reminder_listener);
        dialog.setCancelable(true);
        dialog.show();

        View btnDelete = dialog.findViewById(R.id.tvDelete);
        View btnCancel = dialog.findViewById(R.id.cancel);

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                dialog.dismiss();
                showDeleteConfirmationDialog(reminder);
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }
    }

    private void showDeleteConfirmationDialog(Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete")
                .setMessage("Are you sure to delete ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    reminderViewModel.deleteReminder(reminder);
                    Toast.makeText(this, "Delete successfully", Toast.LENGTH_SHORT).show();
                    alarmService.cancelAlarm(this, String.valueOf(reminder.getId()).hashCode());
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}