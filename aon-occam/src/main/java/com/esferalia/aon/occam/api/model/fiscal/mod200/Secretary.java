package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;
import java.util.Date;

public class Secretary implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String document;
	private Date irnr;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public Date getIrnr() {
		return irnr;
	}
	public void setIrnr(Date irnr) {
		this.irnr = irnr;
	}
	
}
