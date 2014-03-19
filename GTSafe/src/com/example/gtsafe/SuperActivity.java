package com.example.gtsafe;

import android.app.Activity;

import com.example.gtsafe.library.DBManager;

public abstract class SuperActivity extends Activity
{
	static DBManager manager = DBManager.getInstance();
}
