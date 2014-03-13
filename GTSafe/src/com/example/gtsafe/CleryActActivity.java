package com.example.gtsafe;

import com.example.gtsafe.library.DBManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class CleryActActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clery_act);
		
		String summary = DBManager.getInstance().getCleryAct(35).getText();
		WebView view = (WebView)findViewById(R.id.cleryActView);
		view.loadData(summary, "text/html", null);
		
		view.loadData(summary, "text/html", null);
	}
}
