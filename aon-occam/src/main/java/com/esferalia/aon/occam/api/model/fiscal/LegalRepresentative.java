package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class LegalRepresentative implements Serializable  {

	private String name;
	private String document;
	private Date notaryDate;
	private String notary;
	
	public String getName() {
		return name;
	}
	public LegalRepresentative setName(String name) {
		this.name = name;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public LegalRepresentative setDocument(String document) {
		this.document = document;
		return this;
	}
	public Date getNotaryDate() {
		return notaryDate;
	}
	public LegalRepresentative setNotaryDate(Date notaryDate) {
		this.notaryDate = notaryDate;
		return this;
	}
	public String getNotary() {
		return notary;
	}
	public LegalRepresentative setNotary(String notary) {
		this.notary = notary;
		return this;
	}
	
	

}
