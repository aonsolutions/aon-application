package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Workplace implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3342539954330850324L;
	
	private int				id;
	private String 			description;
	
	public Workplace() {
	}
	
	public int getId() {
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
