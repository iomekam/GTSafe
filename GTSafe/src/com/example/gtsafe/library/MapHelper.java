package com.example.gtsafe.library;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.Color;

import com.example.gtsafe.R;
import com.example.gtsafe.library.listeners.interfaces.OnDBGetListener;
import com.example.gtsafe.model.CrimeData;
import com.example.gtsafe.model.ZoneData;
import com.example.gtsafe.model.ZoneInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
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
				    Polygon polygon = this.map.addPolygon(new PolygonOptions()
		            .addAll(zones.get(x).getLocation())
		            .strokeColor(Color.BLACK)
		            .fillColor(Color.argb(50, 200, 0, 0)));
				    
			    	double xcentroid = 0; 
			    	double ycentroid = 0;
				    List<LatLng> coords = zones.get(x).getLocation();
				    for(int i = 0; i < coords.size(); i++){
				    	xcentroid+= coords.get(i).latitude;
				    	ycentroid+= coords.get(i).longitude;
				    }
				    xcentroid = xcentroid/coords.size();
				    ycentroid = ycentroid/coords.size();
				    Marker zoneIterator = this.map.addMarker(new MarkerOptions()
                    .position(new LatLng(xcentroid, ycentroid))
                    .title("Zone: " + x)
                    .snippet("" + zones.get(x).getZoneInformation().getDescription()));
				}
			}
		}
		
		return this.map;
	}
	
	public List<ZoneData> getZones(){
		return zones;
	}
	
	public GoogleMap populateCrimes(){
		db.getAllCrimeData(new OnDBGetListener<CrimeData>(){

			@Override
			public void OnGet(List<CrimeData> list) {
				crimes = list;
				for(int x = 0; x < crimes.size(); x++){
					if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("AGG ASSULT").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.assult)));
					}
					else if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("BURGLARY").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.burglary)));
					}
					else if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("LARCENY").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.larceny)));
					}
					else if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("AUTO THEFT").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorvehicle)));
					}
					else if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("HOMICIDE").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.murder)));
					}
					else if(crimes.get(x).getOffense().getPart() == crimes.get(x).getOffense().getOffenseType("RAPE").getPart()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.rape)));
					}
					else if(crimes.get(x).getOffense().getName() == crimes.get(x).getOffense().getOffenseType("ROBBERY").toString()){
						  Marker tempMarker = map.addMarker(new MarkerOptions()
	                      .position(crimes.get(x).getLocation())
	                      .title(crimes.get(x).getOffense().toString())
	                      .snippet(crimes.get(x).getLocationName())
	                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.robbery)));
					}

				}
			}
		});
		 Circle circle = this.map.addCircle(new CircleOptions()
	     .center(new LatLng(33.775618,-84.396285))
	     .radius(10)
	     .strokeColor(Color.RED)
	     .fillColor(Color.BLUE));	
		 return this.map; 
	}
}
