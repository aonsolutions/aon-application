package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum OfferType implements Serializable {

	NORMAL("Normal")
	, INTERNET("Internet")
	, PROFORMA("Proforma")
	, DEALERSHIP("Representación")
	, AUDATEX("AudaTex");

	private String description;

	private OfferType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
