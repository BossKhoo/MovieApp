package com.example.ojtbadaassignment14.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ojtbadaassignment14.Pref;
import com.example.ojtbadaassignment14.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class FirebaseHelper {
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://ojt-bada-assignment14-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("users");
    }

    public void saveUser(User user, Context context) {
        String userId = Pref.getString(context, Pref.KEY_USER_ID, null);
        if (userId == null) {
            userId = databaseReference.push().getKey();
            if (userId != null) {
                Pref.setString(context, Pref.KEY_USER_ID, userId);
            }
        }
        if (userId != null) {
            databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                }
            });
        }
    }

    public void getUser(String userId, ValueEventListener valueEventListener) {
        databaseReference.child(userId).addListenerForSingleValueEvent(valueEventListener);
    }
}
