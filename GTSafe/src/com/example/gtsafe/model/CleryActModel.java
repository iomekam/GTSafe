package com.example.gtsafe.model;

import java.util.Date;

public class CleryActModel 
{
	private String title;
	private Date date;
	private String text;
	
	public CleryActModel(String title, Date date, String text)
	{
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
}
