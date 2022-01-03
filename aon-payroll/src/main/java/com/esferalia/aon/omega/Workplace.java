package com.esferalia.aon.omega;

public class Workplace {
	
	Integer workplace; 		// Workplace Id (se rellena en el proceso)
	
	String description;		// Descipcion centro de trabajo
	Address address;		// Direccion centro de trabajo
	
	protected Workplace() {
		super();
	}
	
	public Integer getWorkplace() {
		return workplace;
	}

	public Workplace setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Workplace setDescription(String description) {
		this.description = description;
		return this;
	}

	public Address getAddress() {
		return address;
	}

	public Workplace setAddress(Address address) {
		this.address = address;
		return this;
	}

}
