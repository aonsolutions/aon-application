package com.esferalia.aon.gwt.api.client;

public enum AonUrlApi {
	
	GITHUB("https://api.github.com/"),
	AON("https://api.aonsolutions.net/");
	
	String url;
	
	private AonUrlApi(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	public AonUrlApi setUrl(String url) {
		this.url = url;
		return this;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
}
