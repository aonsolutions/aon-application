package com.esferalia.aon.occam.api.model.finance.nordigen;

import com.esferalia.aon.watson.error.AonCoreException;

public class NordigenException extends AonCoreException {
	
	private static final long serialVersionUID = -6384010753448554063L;

	private String summary;
	private String detail;
	private String type;
	private String country;
	private Integer statusCode;

	public NordigenException() {
		this(null,null,null,null,null);
	}
	
	public NordigenException(String message) {
		this(null,message,null,null,null);
	}

	public NordigenException(String summary, String detail, String type, String country, Integer statusCode) {
		super(detail);
		this.summary = summary;
		this.detail = detail;
		this.type = type;
		this.country = country;
		this.statusCode = statusCode;
	}

	public String getSummary() {
		return summary;
	}

	public String getDetail() {
		return detail;
	}

	public String getType() {
		return type;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getCountry() {
		return country;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
