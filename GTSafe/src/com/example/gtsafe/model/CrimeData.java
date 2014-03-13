package com.example.gtsafe.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class CrimeData implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5194829320406807950L;
	private transient LatLng location;
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
		return date.toString();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(location.latitude);
        out.writeDouble(location.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        location = new LatLng(in.readDouble(), in.readDouble());
    }
}
