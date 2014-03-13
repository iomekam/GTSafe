package com.example.gtsafe.model;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class CrimeData 
{
	private LatLng location;
	private String locationName;
	private Date date;
	private OffenseType offense;
	private ZoneData zone;
	
	public CrimeData(LatLng location, String locationName, Date date, OffenseType offense, ZoneData zone)
	{
		this.location = location;
		this.locationName = locationName;
		this.date = date;
		this.offense = offense;
		this.zone = zone;
	}
	
	public LatLng getLocation() {
		return location;
	}

	public String getLocationName() {
		return locationName;
	}

	public Date getDate() {
		return date;
	}

	public OffenseType getOffense() {
		return offense;
	}

	public ZoneData getZone() {
		return zone;
	}
	
	public String toString(){
		return date.toString();
	}
}
