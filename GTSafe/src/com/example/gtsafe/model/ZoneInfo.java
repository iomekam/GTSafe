package com.example.gtsafe.model;

import java.util.List;

public class ZoneInfo implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 972680375283944451L;
	
	private int zoneID;
	private List<String> description;
	
	public ZoneInfo(int zoneID, List<String> description){
		this.description = description;
	}

	public List<String> getDescription(){
		return this.description;
	}
	
	public int getZoneID()
	{
		return zoneID;
	}
}
