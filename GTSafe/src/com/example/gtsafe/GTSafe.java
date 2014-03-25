package com.example.gtsafe;

import com.example.gtsafe.library.DBHelper;
import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.gcm.Gcm;

import android.app.Application;

public class GTSafe extends Application {
    @Override
    public void onCreate() {
        super.onCreate(); 
        
        DBManager.initializeInstance(new DBHelper(GTSafe.this), GTSafe.this);
        Gcm gcm = new Gcm(this);
		gcm.initGCM();
    }
}
