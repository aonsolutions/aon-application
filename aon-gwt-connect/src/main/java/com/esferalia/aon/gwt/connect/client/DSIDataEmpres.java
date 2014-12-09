package com.esferalia.aon.gwt.connect.client;

import java.util.Date;

public class DSIDataEmpres {

	// document: NIF empresa
	private String document;
	private String razonSocial;
	private Date firstContract;
	private Date lastContract;
	
	public DSIDataEmpres() {
		
	}
	
	public DSIDataEmpres setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public DSIDataEmpres setRazonSocial (String razonSocial) {
		this.razonSocial = razonSocial;
		return this;
	}
	
	public DSIDataEmpres setFirstContract (Date fistContract) {
		this.firstContract = fistContract;
		return this;
	}
	
	public DSIDataEmpres setLastContract (Date lastContract) {
		this.lastContract = lastContract;
		return this;
	}
	
	public String getDocument() {
		return this.document;
	}
	
	public String getRazonSocial() {
		return this.razonSocial;
	}
	
	public Date getFirstContract() {
		return this.firstContract;
	}
	
	public Date getLastContract() {
		return this.lastContract;
	}

}
