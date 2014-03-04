package com.example.gtsafe.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class ZoneData 
{
	private List<LatLng> location;
	private int zoneID;
	ZoneInfo zInfo;
	
	public ZoneData(List<LatLng> location, int zoneID, ZoneInfo zInfo)
	{
		this.location = location;
		this.zoneID = zoneID;
		this.zInfo= zInfo;
	}
	
	public List<LatLng> getLocation()
	{
		return location;
	}
	
	public int getZoneID()
	{
		return zoneID;
	}
	public ZoneInfo getZoneInformation()
	{
		return this.zInfo;
	}
}
