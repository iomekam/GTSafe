package com.example.gtsafe.library;

import java.util.Calendar;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.example.gtsafe.R;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.OffenseType;
import com.example.gtsafe.model.ZoneData;
import com.example.gtsafe.model.ZoneRank;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapHelper {
	final DBManager db = DBManager.getInstance();
	private List<ZoneData> zones;
	private GoogleMap map;
	private List<CrimeData> crimes;
	
	public MapHelper(GoogleMap mappy)
	{
		this.map = mappy;
	}
	
	public GoogleMap populateZones(){
		zones = db.getAllZones();
		if (zones != null){
			if (zones.size() > 0){
				for(int x = 0; x < zones.size(); x++){
					int color = 0;
					
					if(zones.get(x).getRank() == ZoneRank.LOW)
					{
						color = Color.WHITE;
					}
					else if(zones.get(x).getRank() == ZoneRank.NORMAL)
					{
						color = Color.LTGRAY;
					}
					else if(zones.get(x).getRank() == ZoneRank.MEDIUM)
					{
						color = Color.DKGRAY; 
					}
					else if(zones.get(x).getRank() == ZoneRank.HIGH)
					{
						color = Color.RED;
					}
					
					
					color = Color.argb(150, Color.red(color), Color.green(color), Color.blue(color));
					
					if(zones.get(x).getZoneID() < 33)
					{
					    this.map.addPolygon(new PolygonOptions()
			            .addAll(zones.get(x).getLocation())
			            .strokeColor(Color.BLACK)
			            .fillColor(color));
					}
				    
//			    	double xcentroid = 0; 
//			    	double ycentroid = 0;
//				    List<LatLng> coords = zones.get(x).getLocation();
//				    for(int i = 0; i < coords.size(); i++){
//				    	xcentroid+= coords.get(i).latitude;
//				    	ycentroid+= coords.get(i).longitude;
//				    }
//				    xcentroid = xcentroid/coords.size();
//				    ycentroid = ycentroid/coords.size();
//				    Marker zoneIterator = this.map.addMarker(new MarkerOptions()
//                    .position(new LatLng(xcentroid, ycentroid))
//                    .title("Zone: " + x)
//                    .snippet("" + zones.get(x).getZoneInformation().getDescription()));
				}
				}
			}
		
		
		return this.map;
	}
	
	public List<ZoneData> getZones(){
		return zones;
	}
	
	public GoogleMap populateCrimes(){
	    Calendar currCal = Calendar.getInstance();
	    int days = (int) (currCal.getActualMaximum(Calendar.DAY_OF_MONTH) / 1.5);
	    currCal.add(Calendar.DATE, -1 * days);
	    Log.e("DATE", new java.sql.Date(currCal.getTimeInMillis()).toString());
	    db.getCrimesByDate(new java.sql.Date(currCal.getTimeInMillis()), new OnDBGetListener<CrimeData>(){
			@SuppressWarnings("deprecation")
			@Override
			public void OnGet(List<CrimeData> list) {
				crimes = list;
				for(int x = 0; x < crimes.size(); x++){
					if(crimes.get(x).getOffense() == OffenseType.AGG_ASSAULT){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.assult)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.BURGLARY){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.burglary)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.LARCENY){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.larceny)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.AUTO_THEFT){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorvehicle)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.HOMICIDE){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.murder)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.RAPE){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.rape)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.ROBBERY){
						  map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.robbery)));
					}
					else if(crimes.get(x).getOffense() == OffenseType.NON_CRIME){
						map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.noncrime)));
					}
					else{
						map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet("Date: " + crimes.get(x).getDate().toLocaleString() + "|" + "Location: " + crimes.get(x).getLocationName() + "|" + "Details: " + crimes.get(x).getOffenseDescription())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.part2)));
					}
				}
			}
		});
		 this.map.addCircle(new CircleOptions()
	     .center(new LatLng(33.775618,-84.396285))
	     .radius(10)
	     .strokeColor(Color.RED)
	     .fillColor(Color.BLUE));	
		 return this.map; 
	}
	
	public List getCrimeCount(){
		
		return crimes;
	}
}
