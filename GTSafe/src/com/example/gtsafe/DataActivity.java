package com.example.gtsafe;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class DataActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		if(getActionBar()!= null){
			getActionBar().setTitle("Crime Data");
		}
        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("Crime Alert");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Crime Stat");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Crime Log");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Crime Alert");
        tab1.setContent(new Intent(this,CleryActActivity.class));
        

        tab2.setIndicator("Crime Stat");
        tab2.setContent(new Intent(this,CrimeStatActivity.class));

        tab3.setIndicator("Crime Log");
        tab3.setContent(new Intent(this,CrimeLogActivity.class));

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data, menu);
		return true;
	}

}
