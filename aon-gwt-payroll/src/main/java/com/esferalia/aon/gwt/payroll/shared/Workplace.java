package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Workplace implements Serializable, HasId<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3342539954330850324L;
	
	private int				id;
	private String 			description;
	private Agreement		agreement;
	
	public Workplace() {
	}
	
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
	
	public Agreement getAgreement() {
		return agreement;
	}
	
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	
	
	
}
