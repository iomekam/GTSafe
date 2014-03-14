package com.example.gtsafe;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gtsafe.library.MapHelper;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.StringTokenizer;
public class CrimeMapActivity extends Activity {
private GoogleMap mMap;
private MapHelper helper;
private SlidingDrawer slider;
private Button filterButt;
private LatLngBounds coords = new LatLngBounds(
    new LatLng(33.770836, -84.407272), new LatLng(33.786638, -84.390492));
private List<ZoneData> zones;
private TextView date;

protected void onCreate(Bundle savedInstanceState) {


super.onCreate(savedInstanceState);
setContentView(R.layout.activity_map);

try {
    // Loading map
    initilizeMap();
    

} catch (Exception e) {
    e.printStackTrace();
}

}

private void initilizeMap() {
if (mMap == null) {
    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    mMap.setMyLocationEnabled(true);
    helper = new MapHelper(mMap);
    mMap = helper.populateZones();
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
            

            // Getting reference to the TextView to set title
            TextView titles = (TextView) v.findViewById(R.id.title);
            TextView location = (TextView) v.findViewById(R.id.crime_location);
            TextView description = (TextView) v.findViewById(R.id.crime_details);
            
            titles.setText(marker.getTitle());
            location.setText(first);
            description.setText(second);
            //note.setText(marker.getTitle() );
            // Returning the view containing InfoWindow contents
            return v;

        }

    });  
    
    zones = helper.getZones();
	int days = 14;
    Calendar currCal = Calendar.getInstance();
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
	                result = x;
	                finished = true;
	                break;
	              }
	            }
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
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 14));
}
    else{
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 14));
}
	filterButt = (Button)findViewById(R.id.ok);
	filterButt.setOnClickListener(new View.OnClickListener() {
	 @Override
	 public void onClick(View v) {

	 }
	 });
}
@Override
protected void onResume() {
super.onResume();
initilizeMap();
}
}
