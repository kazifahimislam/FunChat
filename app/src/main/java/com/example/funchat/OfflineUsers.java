package com.example.funchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class OfflineUsers extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
