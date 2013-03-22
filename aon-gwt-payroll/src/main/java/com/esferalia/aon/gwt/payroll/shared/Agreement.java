package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Agreement implements Serializable, HasId<Integer> {

	private int 			id;
	private String 			description;

	
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
