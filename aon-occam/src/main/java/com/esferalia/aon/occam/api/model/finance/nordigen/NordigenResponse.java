package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;

public class NordigenResponse implements Serializable{
	
	private static final long serialVersionUID = -6384010753448554063L;

	private String summary;
	private String detail;
	private String type;
	private String country;
	private Integer statusCode;

	public String getSummary() {
		return summary;
	}
	public NordigenResponse setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public String getDetail() {
		return detail;
	}
	public NordigenResponse setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public String getType() {
		return type;
	}
	public NordigenResponse setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	public NordigenResponse setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getCountry() {
		return country;
	}
	public NordigenResponse setCountry(String country) {
		this.country = country;
		return this;
	}
	
}
