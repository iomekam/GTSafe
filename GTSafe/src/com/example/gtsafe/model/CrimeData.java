package com.example.gtsafe.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.gtsafe.library.listeners.interfaces.Listable;
import com.google.android.gms.maps.model.LatLng;

public class CrimeData implements java.io.Serializable, Listable
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
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		return formatter.format(date) + "\t" + offenseDesc;
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

	@Override
	public String listString() {
		return toString();
	}
}
