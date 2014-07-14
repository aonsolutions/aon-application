package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class LegalRepresentative implements Serializable, IsSerializable {

	private String name;
	private String document;
	private Date notaryDate;
	private String notary;
	
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
	public Date getNotaryDate() {
		return notaryDate;
	}
	public void setNotaryDate(Date notaryDate) {
		this.notaryDate = notaryDate;
	}
	public String getNotary() {
		return notary;
	}
	public void setNotary(String notary) {
		this.notary = notary;
	}
	
	

}
