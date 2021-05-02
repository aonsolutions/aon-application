package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WorkplaceComunicaInfo implements Serializable {
	private Integer id;
	private String description;
	private Integer addressId;
	
	public WorkplaceComunicaInfo() {
		super();
	}

	public WorkplaceComunicaInfo(Integer id, String description, Integer addressId) {
		super();
		this.id = id;
		this.description = description;
		this.addressId = addressId;
	}

	public Integer getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Integer getAddressId() {
		return addressId;
	}
	
}