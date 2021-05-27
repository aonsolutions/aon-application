package com.code.aon.file.bank.model.CSB34.data;

import com.code.aon.file.bank.model.SEPA.Address;
import com.code.aon.file.bank.model.SEPA.Entity;

public class Orderer implements Entity {
	
	private String code;
	private String name;
	private String address;
	private String city;
	private String id;
	private String document;
	private Address SEPAAddress;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getDocumentType() {
		return null;
	}

	public boolean isOrganisation() {
		return true;
	}

	public Address getSEPAAddress() {
		return SEPAAddress;
	}

	public void setSEPAAddress(Address sEPAAddress) {
		SEPAAddress = sEPAAddress;
	}
	
}