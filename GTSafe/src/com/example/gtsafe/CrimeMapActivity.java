package com.example.gtsafe;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gtsafe.library.DBManager;
import com.example.gtsafe.library.MapHelper;
import com.example.gtsafe.library.listeners.interfaces.Listable;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;

public class CrimeMapActivity extends FragmentActivity {
private GoogleMap mMap;
private MapHelper helper;
private Button filterButt;
private LatLngBounds coords = new LatLngBounds(
    new LatLng(33.770836, -84.407272), new LatLng(33.786638, -84.390492));
private List<ZoneData> zones;
private TextView date;
private String [] mTitles;
private RadioGroup radioGroup;
private int selectedId;
private ArrayAdapter<CrimeData> adapter;
private int choice_index; 
final DBManager manager = DBManager.getInstance();
Object selectedItem = null;

private CharSequence mDrawerTitle;
private CharSequence mTitle;

private DrawerLayout mDrawerLayout;
private ListView mDrawerList;
private ActionBarDrawerToggle mDrawerToggle;

private List<RowItem> rowItems;
private CustomAdapter adapter2;
private String[] menutitles;
private TypedArray menuIcons;


protected void onCreate(Bundle savedInstanceState) {

super.onCreate(savedInstanceState);
setContentView(R.layout.activity_map);
adapter = new ArrayAdapter<CrimeData>(CrimeMapActivity.this,
		android.R.layout.simple_list_item_1, android.R.id.text1,
		new LinkedList<CrimeData>());
getActionBar().setTitle("Crime Map");
if(manager.runningInit)
{
	manager.initDialog = ProgressDialog.show(this, "Initialzing", "Loading data into app. Please wait", true);
}
else
{
	//manager.loadingScreen = new ProgressDialog(this);
	//manager.deserializeTable();
}

try {
    // Loading map
    initilizeMap();
    

} catch (Exception e) {
    e.printStackTrace();
}
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
	              //getActionBar().setTitle(mDrawerTitle);
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
//////////////////////////////////////////////////////////////////

}

private void initilizeMap() {
if (mMap == null) {
    mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    mMap.setMyLocationEnabled(true);
    helper = new MapHelper(mMap);
    helper.getZonesDB();
    mMap = helper.populateZones();
    helper.getCrimesDB();
    mMap = helper.populateCrimes();
    
    mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker marker) {              
            return null;
        }           

        // Defines the contents of the InfoWindow
        @Override
        public View getInfoContents(Marker marker) {

            // Getting view from the layout file info_window_layout
            View v = getLayoutInflater().inflate(R.layout.infowindow, null);
            StringTokenizer tokens = new StringTokenizer(marker.getSnippet(), "|");
            String first = tokens.nextToken();// this will contain "Fruit"
            String second = tokens.nextToken();
            String third = tokens.nextToken();
            

            // Getting reference to the TextView to set title
            TextView titles = (TextView) v.findViewById(R.id.title);
            TextView dates = (TextView) v.findViewById(R.id.crime_date);
            TextView location = (TextView) v.findViewById(R.id.crime_location);
            TextView description = (TextView) v.findViewById(R.id.crime_details);
            
            titles.setText(marker.getTitle());
            dates.setText(first);
            location.setText(second);
            description.setText(third);
            //note.setText(marker.getTitle() );
            // Returning the view containing InfoWindow contents
            return v;
        }
    });  
    

    

    
    zones = helper.getZones();
    Calendar currCal = Calendar.getInstance();
    int days = (int) (currCal.getActualMaximum(Calendar.DAY_OF_MONTH) / 1.5);
    currCal.add(Calendar.DATE, -1 * days);
    date = (TextView)findViewById(R.id.currDate);
    date.setText("Date Range: " + (new java.sql.Date(currCal.getTimeInMillis()).toString()) + " - Today");
    mMap.setOnMapClickListener(new OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            int i;
            int j;
            int result = -1;
            boolean finished = false;
            for (int x = 0; x < zones.size() && !finished; x++){
	            for (i = 0, j = zones.get(x).getLocation().size() - 1; i < zones.get(x).getLocation().size(); j = i++) {
	              if ((zones.get(x).getLocation().get(i).latitude > point.latitude) != (zones.get(x).getLocation().get(j).latitude > point.latitude) &&
	                  (point.longitude < (zones.get(x).getLocation().get(j).longitude - zones.get(x).getLocation().get(i).longitude) * (point.latitude - zones.get(x).getLocation().get(i).latitude) / (zones.get(x).getLocation().get(j).latitude-zones.get(x).getLocation().get(i).latitude) +zones.get(x).getLocation().get(i).longitude)) {
	                finished = true;
	                result = x;
	                break;
	              }
	            }
            }
            if(result!=-1){
            	AlertDialog.Builder builder1 = new AlertDialog.Builder(CrimeMapActivity.this);
            	builder1.setTitle("Zone " + result + " information");
            	builder1.setMessage("Stuff will go here");
            	builder1.setCancelable(true);
            	builder1.setNeutralButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					} 
            	});
            	AlertDialog alert11 = builder1.create();
            	alert11.show();
            }
        }   
    }); 

    mMap.setOnCameraChangeListener(new OnCameraChangeListener(){

		@Override
		public void onCameraChange(CameraPosition pos) {
			// TODO Auto-generated method stub
			if (pos.zoom < 14){
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos.target, 14));
			}
			
		}
    	
    });
    if ( mMap == null) {
        Toast.makeText(getApplicationContext(),
                 "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                 .show();
    }
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 16));
}
    else{
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 16));
}

}
@Override
protected void onResume() {
super.onResume();
initilizeMap();
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
    //setTitle(mTitles[position]);
    mDrawerLayout.closeDrawer(mDrawerList);
    
    Intent myIntent;
    switch (position) {
    case 0:
        myIntent=new Intent(CrimeMapActivity.this,DataActivity.class);
        startActivity(myIntent);
                 break;
    case 1:
        myIntent=new Intent(CrimeMapActivity.this,CrimeLogActivity.class);
        startActivity(myIntent);
                break;
    case 2:
		PhoneCallListener phoneListener = new PhoneCallListener();
		TelephonyManager telephonyManager = (TelephonyManager) this
			.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
                break;
    case 3:
        myIntent=new Intent(CrimeMapActivity.this,HelpActivity.class);
        startActivity(myIntent);
                break;
   default:
              break;
    }
}


@Override
public void setTitle(CharSequence title) {
    getActionBar().setTitle(title);
}
public boolean onCreateOptionsMenu(Menu menu) {
    
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
    return true;
}
public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	    case R.id.map_filter:
	        AlertDialog.Builder builder = new AlertDialog.Builder(CrimeMapActivity.this);
	        // Set the dialog title
	        String[] arr = {"None", "Date Range", "Crime Type"};
	        choice_index = 0;
	        builder.setTitle("Choose Filter")
	        // Specify the list array, the items to be selected by default (null for none),
	        // and the listener through which to receive callbacks when items are selected
	               .setSingleChoiceItems(R.array.choices_arr, 0,
	                          new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						choice_index = which;
					}
	               })
	        // Set the action buttons
	               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                   @Override
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User clicked OK, so save the mSelectedItems results somewhere
	                       // or return them to the component that opened the dialog
	                       dialog.dismiss();
	               		AlertDialog.Builder b = new Builder(CrimeMapActivity.this);
	                       if(choice_index == 0){
	                    	   helper.updateCrimesCheck();
	                       }
	                       else if(choice_index == 1){
	   					    LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
	   					    View customView = inflater.inflate(R.layout.double_date_picker, null);
	   					    
	   					    final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);
	   					    final DatePicker dpEndDate = (DatePicker) customView.findViewById(R.id.dpEndDate);
	   					  
	   					    b.setTitle("Choose Dates");
	   					    b.setView(customView); // Set the view of the dialog to your custom layout
	   					    b.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	   					        @SuppressWarnings("deprecation")
	   							@Override
	   					        public void onClick(DialogInterface dialog, int which) {
	   					        	dialog.dismiss();
	   								adapter.clear();
	   					        	int startYear, startMonth, startDay, endYear, endMonth, endDay;
	   					            startYear = dpStartDate.getYear();
	   					            startMonth = dpStartDate.getMonth();
	   					            startDay = dpStartDate.getDayOfMonth();
	   					            endYear = dpEndDate.getYear();
	   					            endMonth = dpEndDate.getMonth();
	   					            endDay = dpEndDate.getDayOfMonth();
	   					            //java.sql.Date jsqlD = java.sql.Date.valueOf( "2010-01-31" );
	   					            @SuppressWarnings("deprecation")
	   								Date start= Date.valueOf(startYear+"-"+startMonth+"-"+startDay);
	   					            Date end= Date.valueOf(endYear+"-"+endMonth+"-"+endDay);
	   					            manager.getCrimesByDate(start, end, new OnDBGetListener<CrimeData>() {
	   
	   									@Override
	   									public void OnGet(List<CrimeData> list) {
	   										helper.updateCrimes(list);
	   										mMap.clear();
	   									    mMap = helper.populateZones();
	   									    mMap = helper.populateCrimes();
	   									}
	   								});
	   					    
	   					        }});  
	   					    b.show();
	                       }
	                       else if(choice_index == 2){
	   						List<String> list = new LinkedList<String>();
	   						for(Listable item: OffenseType.values())
	   						{
	   							list.add(((Listable)item).listString());
	   						}
	   						final String[] newlist = list.toArray(new String[list.size()]);
	   
	   						b.setItems(newlist, new OnClickListener() {
	   
	   							@Override
	   							public void onClick(DialogInterface dialog, int position) {
	   								dialog.dismiss();
	   								adapter.clear();
	   								final OffenseType offType = OffenseType.getOffenseType(newlist[position]);
	   								selectedItem = offType;
	   								manager.getCrimesByType(offType, new OnDBGetListener<CrimeData>() {
	   									@Override
	   									public void OnGet(List<CrimeData> list) {
	   										helper.updateCrimes(list);
	   										mMap.clear();
	   									    mMap = helper.populateZones();
	   									    mMap = helper.populateCrimes();
	   									}
	   								});
	   							}
	   						});
	   						b.show();
	                       }
	                   }
	                   
	               })
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   @Override
	                   public void onClick(DialogInterface dialog, int id) {
	                	   dialog.dismiss();
	                   }
	               });


			builder.show();
	    return true;
	    default:
	    return super.onOptionsItemSelected(item);
	}
	
 
}
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


