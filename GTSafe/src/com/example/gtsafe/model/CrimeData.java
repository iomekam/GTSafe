package com.example.gtsafe.model;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class CrimeData 
{
	private LatLng location;
	private String locationName;
	private Date date;
	private OffenseType offense;
	private String offenseDesc;
	private ZoneData zone;
	
	public CrimeData(LatLng location, String locationName, Date date, OffenseType offense, String offenseDesc, ZoneData zone)
	{
		this.location = location;
		this.locationName = locationName;
		this.date = date;
		this.offense = offense;
		this.zone = zone;
		this.offenseDesc = offenseDesc;
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
	
	public String getOffenseDescription()
	{
		return offenseDesc;
	}

	public ZoneData getZone() {
		return zone;
	}
	
	public String toString(){
		return "Crime Date "+date.toString();
	}
}
