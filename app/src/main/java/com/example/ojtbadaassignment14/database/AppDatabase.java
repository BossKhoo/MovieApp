package com.example.ojtbadaassignment14.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.example.ojtbadaassignment14.dao.FavouriteMovieDao;
import com.example.ojtbadaassignment14.dao.ReminderDao;
import com.example.ojtbadaassignment14.models.Converters;
import com.example.ojtbadaassignment14.models.Movie;
import com.example.ojtbadaassignment14.models.Reminder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movie.class, Reminder.class}, version = 4)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavouriteMovieDao favouriteMovieDao();
    public abstract ReminderDao reminderDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "movies.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
