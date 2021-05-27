package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ActivityGroup implements Serializable {
	  GROUP1("1")
	, GROUP2("2")
	, GROUP3("2")
	, GROUP4("3")
	, GROUP5("4")
	, GROUP6("5")
	, GROUP7("6");

	String key;

	private ActivityGroup(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
