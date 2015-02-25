package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod140Context implements Serializable {

	private String document;
	private String epigraph;
	

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
