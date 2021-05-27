package com.esferalia.aon.occam.api.model.type;

public enum AonUrlApi {
	
	GITHUB("https://api.github.com/"),
	AON("http://api.aonsolutions.net/"),
	AONTEST("http://api.aonsolutions.net/aon-aio/");
	
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
