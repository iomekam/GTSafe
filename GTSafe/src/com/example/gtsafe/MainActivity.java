package com.example.gtsafe;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gtsafe.library.DBHelper;
import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.gcm.Gcm;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;

public class MainActivity extends Activity {
	private String s = "";
	private TextView view;
	private Button view_Button;
	private Button data_Button;
	private Button help_Button; // Omar created this.
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DBManager.initializeInstance(new DBHelper(getApplicationContext()), this);
		Context context = getApplicationContext();
		view =  (TextView)findViewById(R.id.textView1);
		view.setMovementMethod(new ScrollingMovementMethod());
		
		
		Gcm gcm = new Gcm(context, this);
		gcm.initGCM();
		
		final DBManager db = DBManager.getInstance();
		
		db.setOnAllZoneUpdateEventListener(new OnDBUpdateListener<List<ZoneData>>()
		{
			@Override
			public void OnUpdate(List<ZoneData> data) {
				s = s + "Zones: Done\n";
				view.setText(s);
			}
		});
		
		db.setOnZoneUpdateEventListener(new OnDBUpdateListener<ZoneData>()
		{
			@Override
			public void OnUpdate(ZoneData data) 
			{
					s = s + data.getZoneID() + "\n";
					view.setText(s);
			}	
		});
		
		db.setOnAllCrimeUpdateEventListener(new OnDBUpdateListener<List<CrimeData>>()
		{
			@Override
			public void OnUpdate(List<CrimeData> data) 
			{
				s = s + "Crimes: Done\n";
				view.setText(s);
			}
		});
		
		db.setOnCrimeUpdateEventListener(new OnDBUpdateListener<CrimeData>()
		{
			@Override 
			public void OnUpdate(CrimeData data) {
				view.setText("Adding crime: " + data.getDate() + " -- " + data.getLocationName());
			}	
		});
		
		//view.setText(db.getCrimeData(1).getLocationName());
		
		db.getAllCrimeData(new OnDBGetListener<CrimeData>(){

			@Override
			public void OnGet(List<CrimeData> list) {
				view.setText("Crimes: " + list.size());
			}
		});
		
		Calendar cal = Calendar.getInstance();//gives current date at given time
		cal.set(Calendar.YEAR, 2013 );
	    cal.set(Calendar.MONTH, Calendar.JULY );
	    cal.set(Calendar.DATE, 28 );
	    
	    Calendar calB = Calendar.getInstance();
		calB.set(Calendar.YEAR, 2014 );
	    calB.set(Calendar.MONTH, Calendar.JANUARY );
	    calB.set(Calendar.DATE, 5 );

		db.getCrimesByDate(new java.sql.Date(cal.getTimeInMillis()), new java.sql.Date(calB.getTimeInMillis()),  new OnDBGetListener<CrimeData>(){

			@Override
			public void OnGet(List<CrimeData> list) {
				view.setText("Crimes by Date: " + list.size());
				Toast.makeText(getApplicationContext(), "Crimes Length: " + list.size(),
						   Toast.LENGTH_LONG).show();
			}
		});
		db.getCrimesByType(OffenseType.NON_CRIME,  new OnDBGetListener<CrimeData>(){

			@Override
			public void OnGet(List<CrimeData> list) {
				view.setText("Crimes by Type: " + list.size());
				Toast.makeText(getApplicationContext(), "Crimes Length: " + list.size(),
						   Toast.LENGTH_LONG).show();
			}
		});
		
//		
//		db.getCrimesByZone(1, new OnDBGetListener<CrimeData>(){
//
//			@Override
//			public void OnGet(List<CrimeData> list) {
//				view.setText("Crimes by Zone: " + list.size());
//			}
//		});
		
		//view.setText(db.getCrimeData(19).getZone().getZoneInformation().getDescription().get(0));
		//view.setText(""  + db.getZone(1).getZoneInformation().getDescription().size());
		//view.setText("" + db.getCrimesByZone(1).size());
		
		view_Button = (Button)findViewById(R.id.map_button);
		view_Button.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
			        Intent myIntent=new Intent(view.getContext(),CrimeMapActivity.class);
			        startActivity(myIntent);
			 }
			 });
		
		//db.updateAllCleryAct();
		//view.setText("" + Html.fromHtml(db.getCleryAct(2).getText()));
		data_Button = (Button)findViewById(R.id.data_button2);
		data_Button.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
			        Intent myIntent=new Intent(MainActivity.this,DataActivity.class);
			        startActivity(myIntent);
			 }
			 });
		
		help_Button = (Button)findViewById(R.id.help);
		help_Button.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
			        Intent myIntent=new Intent(MainActivity.this,HelpActivity.class);
			        startActivity(myIntent);
			 }
			 });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
