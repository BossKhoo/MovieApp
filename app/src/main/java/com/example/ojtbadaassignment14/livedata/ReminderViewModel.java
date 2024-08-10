package com.example.ojtbadaassignment14.livedata;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ojtbadaassignment14.dao.ReminderDao;
import com.example.ojtbadaassignment14.database.AppDatabase;
import com.example.ojtbadaassignment14.models.Reminder;

import java.util.List;
public class ReminderViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final MutableLiveData<List<Reminder>> top3RecentReminders;
    private final MutableLiveData<List<Reminder>> allReminders;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        top3RecentReminders = new MutableLiveData<>();
        allReminders = new MutableLiveData<>();
        loadTop3RecentReminders();
        loadAllReminders();
    }

    private void loadTop3RecentReminders() {
        new Thread(() -> {
            List<Reminder> reminders = db.reminderDao().getTop3RecentReminders();
            top3RecentReminders.postValue(reminders);
        }).start();
    }

    private void loadAllReminders() {
        new Thread(() -> {
            List<Reminder> reminders = db.reminderDao().getAllReminders();
            allReminders.postValue(reminders);
        }).start();
    }

    public LiveData<List<Reminder>> getTop3RecentReminders() {
        return top3RecentReminders;
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }

    public void deleteReminder(Reminder reminder) {
        new Thread(() -> {
            db.reminderDao().delete(reminder);
            loadAllReminders();
        }).start();
    }
}
