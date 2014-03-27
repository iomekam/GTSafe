package com.example.gtsafe.model;

public enum ZoneRank 
{
	LOW("Low"), NORMAL("Normal"), MEDIUM("Medium"), HIGH("High");
	private String name;
	
	private ZoneRank(String name)
	{
		this.name = name;
	}
	
	public static ZoneRank getZoneRank(String name)
	{
		for(int i = 0; i < OffenseType.values().length; i++)
		{
			if(ZoneRank.values()[i].name.equals(name))
			{
				return ZoneRank.values()[i];
			}
		}
		
		return LOW;
	}
	
	public String toString()
	{
		return name;
	}
}
