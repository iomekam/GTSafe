package com.example.gtsafe.model;

import java.util.Date;

public class CleryActModel 
{
	private String title;
	private Date date;
	private String text;
	private int id;
	
	public CleryActModel(int id, String title, Date date, String text)
	{
		this.id = id;
		this.title = title;
		this.date = date;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String toString()
	{
		return date.toLocaleString() + " -- " + title;
	}
	
}
