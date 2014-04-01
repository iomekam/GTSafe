package com.example.gtsafe;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gtsafe.library.DBHelper;
import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.gcm.Gcm;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.library.listeners.interfaces.OnDBUpdateListener;
import com.example.gtsafe.model.CleryActModel;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.example.gtsafe.model.ZoneInfo;

public class MainActivity extends SuperActivity {
	private String s = "";
	private TextView view;
	private Button view_Button;
	private Button data_Button;
	private ImageButton help_Button; // Omar created this.
	private Button call_Button;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//DBManager.initializeInstance(new DBHelper(getApplicationContext()), this);
		if(manager.runningInit)
		{
			manager.initDialog = ProgressDialog.show(this, "Initialzing", "Loading data into app. Please wait", true);
		}
		else
		{
			//manager.loadingScreen = new ProgressDialog(this);
			//manager.deserializeTable();
		}
		
		view =  (TextView)findViewById(R.id.textView1);
		view.setMovementMethod(new ScrollingMovementMethod());
		
		manager.setOnAllCleryActUpdateEventListener(new OnDBUpdateListener<List<CleryActModel>>()
		{
			@Override
			public void OnUpdate(List<CleryActModel> updatedItem) {
				s = s + "Clery Acts: Done\n";
				view.setText(s);
			}
			
		});
		
		manager.setOnAllZoneInfoUpdateEventListener(new OnDBUpdateListener<List<ZoneInfo>>()
		{

			@Override
			public void OnUpdate(List<ZoneInfo> updatedItem) {
				s = s + "Zone Info: Done\n";
				view.setText(s);
			}

		});
		
		manager.setOnAllZoneUpdateEventListener(new OnDBUpdateListener<List<ZoneData>>()
		{
			@Override
			public void OnUpdate(List<ZoneData> data) {
				s = s + "Zones: Done\n";
				view.setText(s);
			}
		});
		
		manager.setOnZoneUpdateEventListener(new OnDBUpdateListener<ZoneData>()
		{
			@Override
			public void OnUpdate(ZoneData data) 
			{
					s = "Creating zone " + data.getZoneID() + "\n";
					view.setText(s);
			}	
		});
		
		manager.setOnAllCrimeUpdateEventListener(new OnDBUpdateListener<List<CrimeData>>()
		{
			@Override
			public void OnUpdate(List<CrimeData> data) 
			{
				s = s + "Crimes: Done\n";
				view.setText(s);
			}
		});
		
		manager.setOnCrimeUpdateEventListener(new OnDBUpdateListener<CrimeData>()
		{
			@Override 
			public void OnUpdate(CrimeData data) {
				view.setText("Adding crime: " + data.getDate() + " -- " + data.getLocationName());
			}	
		});
		
		//view.setText(db.getCrimeData(1).getLocationName());
		
		if(!manager.runningInit)
		{
			manager.getAllCrimeData(new OnDBGetListener<CrimeData>(){
	
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
	
			manager.getCrimesByDate(new java.sql.Date(cal.getTimeInMillis()), new java.sql.Date(calB.getTimeInMillis()),  new OnDBGetListener<CrimeData>(){
	
				@Override
				public void OnGet(List<CrimeData> list) {
					view.setText("Crimes by Date: " + list.size());
				}
			});
			manager.getCrimesByType(OffenseType.NON_CRIME,  new OnDBGetListener<CrimeData>(){
	
				@Override
				public void OnGet(List<CrimeData> list) {
					view.setText("Crimes by Type: " + list.size());
				}
			});
		}
		
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
		
		help_Button = (ImageButton)findViewById(R.id.helpbutton);
		help_Button.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
			        Intent myIntent=new Intent(MainActivity.this,HelpActivity.class);
			        startActivity(myIntent);
			 }
			 });
		// add PhoneStateListener
				PhoneCallListener phoneListener = new PhoneCallListener();
				TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
		call_Button = (Button) findViewById(R.id.callbutton);
		call_Button.setOnClickListener(new View.OnClickListener() {
			 
			@Override
			public void onClick(View v) {
 
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:4048942500"));
				startActivity(callIntent);
 
			}
 
		});

	}
	
	//monitor phone call activities
		private class PhoneCallListener extends PhoneStateListener {
	 
			private boolean isPhoneCalling = false;
	 
			String LOG_TAG = "LOGGING 123";
	 
			public void onCallStateChanged(int state, String incomingNumber) {
	 
				if (TelephonyManager.CALL_STATE_RINGING == state) {
					// phone ringing
					Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
				}
	 
				if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
					// active
					Log.i(LOG_TAG, "OFFHOOK");
	 
					isPhoneCalling = true;
				}
	 
				if (TelephonyManager.CALL_STATE_IDLE == state) {
					// run when class initial and phone call ended, 
					// need detect flag from CALL_STATE_OFFHOOK
					Log.i(LOG_TAG, "IDLE");
	 
					if (isPhoneCalling) {
	 
						Log.i(LOG_TAG, "restart app");
	 
						// restart app
						Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
								getBaseContext().getPackageName());
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
	 
						isPhoneCalling = false;
					}
	 
				}
			}
		}
}
