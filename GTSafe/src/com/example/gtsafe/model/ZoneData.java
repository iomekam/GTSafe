package com.example.gtsafe.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.util.LinkedList;
import java.util.List;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

public class ZoneData implements java.io.Serializable
{
	private transient List<LatLng> location;
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
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(location.size());
        for(int count=0; count < location.size(); count++)
        {
        	out.writeDouble(location.get(count).latitude);
            out.writeDouble(location.get(count).longitude);
        }
    }

    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        
        List<LatLng> locList = new LinkedList<LatLng>();
        
        for(int count = 0; count < size; count++)
        {
        	locList.add(new LatLng(in.readDouble(), in.readDouble()));
        }
        
        location = locList;
        
    }
}
