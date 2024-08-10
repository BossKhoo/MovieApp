package com.example.ojtbadaassignment14.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ojtbadaassignment14.models.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders ORDER BY datetime DESC")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminders ORDER BY datetime DESC LIMIT 3")
    List<Reminder> getTop3RecentReminders();

    @Query("SELECT * FROM reminders WHERE id = :id")
    Reminder getReminderById(long id);
}
