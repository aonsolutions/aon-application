package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class LegalRepresentative implements Serializable, IsSerializable {

	private String name;
	private String document;
	private String notaryDate;
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
	public String getNotaryDate() {
		return notaryDate;
	}
	public void setNotaryDate(String notaryDate) {
		this.notaryDate = notaryDate;
	}
	public String getNotary() {
		return notary;
	}
	public void setNotary(String notary) {
		this.notary = notary;
	}
	
	

}
