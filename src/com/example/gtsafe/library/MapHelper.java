package com.example.gtsafe.library;

import java.util.List;

import android.graphics.Color;

import com.example.gtsafe.model.ZoneData;
import com.example.gtsafe.model.ZoneInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapHelper {
	final DBManager db = DBManager.getInstance();
	private List<ZoneData> zones;
	private GoogleMap map;
	
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
				}
			}
		}
		return this.map;
	}
}
