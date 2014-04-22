package com.example.gtsafe.library;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.gtsafe.CrimeLogActivity;
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
	private ArrayAdapter<CrimeData> adapter;
	
	public MapHelper(GoogleMap mappy)
	{
		this.map = mappy;
	}
	
	public GoogleMap populateZones(){
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
				    
				}
				}
			}
		
		
		return this.map;
	}
	
	public List<ZoneData> getZones(){
		return zones;
	}
	
	public void getCrimesDB(){
	    Calendar currCal = Calendar.getInstance();
	    int days = (int) (currCal.getActualMaximum(Calendar.DAY_OF_MONTH) / 1.5);
	    currCal.add(Calendar.DATE, -1 * days);
	    db.getCrimesByDate(new java.sql.Date(currCal.getTimeInMillis()), new OnDBGetListener<CrimeData>(){
			public void OnGet(List<CrimeData> list) {
				crimes = list;
				populateCrimes();
			}
	    });
	    }
	public void updateCrimesCheck(){
	    Calendar currCal = Calendar.getInstance();
	    int days = (int) (currCal.getActualMaximum(Calendar.DAY_OF_MONTH) / 1.5);
	    currCal.add(Calendar.DATE, -1 * days);
	    db.getCrimesByDate(new java.sql.Date(currCal.getTimeInMillis()), new OnDBGetListener<CrimeData>(){
			public void OnGet(List<CrimeData> list) {
				crimes = list;
				map.clear();
			    populateZones();
		        populateCrimes();
			}
	    });
	}
	public void getZonesDB(){
		zones = db.getAllZones();
	}
	public void updateCrimes(List<CrimeData> newAdapter){
		this.crimes = newAdapter;
	}
	public GoogleMap populateCrimes(){
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
					return this.map; 
	}
	public void clearMap(){
		this.map.clear();
	}
	
	public List getCrimeCount(){
		return crimes;
	}
}
