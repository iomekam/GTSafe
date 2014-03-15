package com.example.gtsafe.model;

import com.example.gtsafe.library.listeners.interfaces.Listable;

public enum OffenseType implements Listable
{
	HOMICIDE("HOMICIDE", 1), RAPE("RAPE", 1), ROBBERY("ROBBERY", 1), AGG_ASSAULT("AGG ASLT", 1), BURGLARY("BURGLARY", 1), LARCENY("LARCENY", 1),
	AUTO_THEFT("AUTO THEFT", 1), ARSON("ARSON", 1), OTHER_ASSAULTS("OTHER ASSAULTS", 2), FORGERY("FORGERY", 2), FRAUD("FRAUD", 2), 
	EMBEZZLEMENT("EMBEZZLEMENT", 2), STOLEN_PROPERTY("STOLEN PROPERTY", 2), DAMAGE_TO_PROPERTY("DAMAGE TO PROPERTY", 2), 
	WEAPONS_OFFENSE("WEAPONS OFFENSE", 2), PROSTITUTION("PROSTITUTION", 2), SEX_OFFENSE("SEX OFFENSE", 2), DANGEROUS_DRUGS("DANGEROUS DRUGS", 2), 
	GAMBLING_OFFENSE("GAMBLING OFFENSE", 2), FAMILY_OFFENSE("FAMILY OFFENSE", 2), DUI("DUI", 2), LIQUOR_LAWS("LIQUOR LAWS", 2), 
	PUBLIC_PEACE_OFFENSE("PUBLIC PEACE OFFENSE", 2), VAGRANCY_OFFENSE("VAGRANCY OFFENSE", 2), ALL_OTHER_OFFENSES("ALL OTHER OFFENSES", 2), 
	NO_CRIME("NO_CRIME", 2), NON_CRIME("NON CRIME", 3);
	
	private String name;
	private int part;
	
	private OffenseType(String name, int part)
	{
		this.name = name;
		this.part = part;
	}
	
	public OffenseType getOffenseType(String name)
	{
		for(int i = 0; i < OffenseType.values().length; i++)
		{
			if(OffenseType.values()[i].name.equals(name))
			{
				return OffenseType.values()[i];
			}
		}
		
	
		
		return ALL_OTHER_OFFENSES;
	}
	
	public int getPart()
	{
		return part;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return name;
	}

	@Override
	public String listString() {
		return name;
	}
}
