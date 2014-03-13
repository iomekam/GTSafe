package com.example.gtsafe.model;

import java.util.List;

public class ZoneInfo implements java.io.Serializable {
	private List<String> description;
	
	public ZoneInfo(List<String> description){
		this.description = description;
	}

	public List<String> getDescription(){
		return this.description;
	}
}
