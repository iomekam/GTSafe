package com.example.gtsafe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
	String bla;

    
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private List<RowItem> rowItems;
	private CustomAdapter adapter2;
	private String[] menutitles;
	private TypedArray menuIcons;
	private String [] mTitles;

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
	        Intent myIntent=new Intent(MainActivity.this,CrimeMapActivity.class);
	        startActivity(myIntent);
	        finish();
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
					//bla = "" + list.size();
				}
			});
			/*Context context = getApplicationContext();
			
			CharSequence text = "" + bla;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();*/
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
		
	    mTitle = mDrawerTitle = getTitle();

	    menutitles = getResources().getStringArray(R.array.titles);
	    menuIcons = getResources().obtainTypedArray(R.array.icons);

	    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);

	    rowItems = new ArrayList<RowItem>();

	    for (int i = 0; i < menutitles.length; i++) {
	     RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
	       i, -1));
	     rowItems.add(items);
	    }

	    menuIcons.recycle();

	    adapter2 = new CustomAdapter(getApplicationContext(), rowItems);

	    mDrawerList.setAdapter(adapter2);
	    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
	    	    R.drawable.ic_launcher, R.string.app_name,R.string.app_name) {
	    	       public void onDrawerClosed(View view) {
	    	         getActionBar().setTitle(mTitle);
	    	         // calling onPrepareOptionsMenu() to show action bar icons
	    	         invalidateOptionsMenu();
	    	       }

	    	        public void onDrawerOpened(View drawerView) {
	    	              getActionBar().setTitle(mDrawerTitle);
	    	               // calling onPrepareOptionsMenu() to hide action bar icons
	    	              invalidateOptionsMenu();
	    	         }
	    	        @Override
	    	        public void onDrawerSlide(View drawerView, float slideOffset)
	    	        {
	    	            super.onDrawerSlide(drawerView, slideOffset);
	    	            mDrawerLayout.bringChildToFront(drawerView);
	    	            mDrawerLayout.requestLayout();
	    	        }
	    	  };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
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
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	private void selectItem(int position) {
	    // Create a new fragment and specify the planet to show based on position
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	    
	    Intent myIntent;
	    switch (position) {
	    case 0:
	        myIntent=new Intent(MainActivity.this,MainActivity.class);
	        startActivity(myIntent);
	                 break;
	    case 1:
	        myIntent=new Intent(MainActivity.this,CrimeLogActivity.class);
	        startActivity(myIntent);
	                break;
	    case 2:
	        myIntent=new Intent(MainActivity.this,MainActivity.class);
	        startActivity(myIntent);
	                break;
	    case 3:
	        myIntent=new Intent(MainActivity.this,HelpActivity.class);
	        startActivity(myIntent);
	                break;
	    case 4:
	        myIntent=new Intent(MainActivity.this,MainActivity.class);
	        startActivity(myIntent);
	                break;
	   default:
	              break;
	    }
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
