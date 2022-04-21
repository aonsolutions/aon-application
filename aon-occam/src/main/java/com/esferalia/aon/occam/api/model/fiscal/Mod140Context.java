package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.CompanyFull;

public class Mod140Context implements Serializable {

	private static final long serialVersionUID = 1945390780992784104L;
	
	private CompanyFull company;
	private String document;
	private String epigraph;

	
	
	public CompanyFull getCompany() {
		return company;
	}
	
	public void setCompany(CompanyFull company) {
		this.company = company;
	}
	
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getEpigraph() {
		return epigraph;
	}
	public void setEpigraph(String epigraph) {
		this.epigraph = epigraph;
	}
	
}
