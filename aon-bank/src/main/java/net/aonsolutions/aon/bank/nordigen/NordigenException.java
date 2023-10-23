package net.aonsolutions.aon.bank.nordigen;

import org.json.JSONObject;

public class NordigenException extends RuntimeException {
	

	private static final long serialVersionUID = -6384010753448554063L;

	private String summary;
	private String detail;
	private String type;
	private String country;
	private Integer statusCode;

	
	
	public NordigenException(String summary, String detail, String type, Integer statusCode) {
		super(detail);
		this.summary = summary;
		this.detail = detail;
		this.type = type;
		this.statusCode = statusCode;
	}

	public NordigenException() {
		super();
	}

	public NordigenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NordigenException(String message, Throwable cause) {
		super(message, cause);
	}

	public NordigenException(String message) {
		super(message);
	}

	public NordigenException(Throwable cause) {
		super(cause);
	}
	
	

	public String getSummary() {
		return summary;
	}

	public NordigenException setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public String getDetail() {
		return detail;
	}

	public NordigenException setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public String getType() {
		return type;
	}

	public NordigenException setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public NordigenException setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	public String getCountry() {
		return country;
	}
	
	public NordigenException setCountry(String country) {
		this.country = country;
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static void throwNordigenException(JSONObject errJson) throws NordigenException {
		if (errJson != null) {
			String message = errJson.optString("detail");
			throw new NordigenException(message)
			.setSummary(errJson.optString("summary"))
			.setDetail(message)
			.setType(errJson.optString("type"))
			.setCountry(errJson.optString("country"))
			.setStatusCode(errJson.optInt("status_code"));
		}
		throw new NordigenException();
	}
	
}
