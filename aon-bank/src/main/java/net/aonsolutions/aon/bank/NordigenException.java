package net.aonsolutions.aon.bank;

import org.json.JSONObject;

public class NordigenException extends RuntimeException {
	
	private static final long serialVersionUID = -6384010753448554063L;

	private String summary;
	private String detail;
	private String type;
	private String country;
	private Integer statusCode;

	public NordigenException() {
		super();
	}
	
	public NordigenException(String message) {
		super(message);
	}

	private NordigenException(String summary, String detail, String type, String country, Integer statusCode) {
		super(detail);
		this.summary = summary;
		this.detail = detail;
		this.type = type;
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

	public static void throwNordigenException(JSONObject errJson) throws NordigenException {
		if (errJson != null) {
			String message = errJson.optString("detail");
			throw new NordigenException(
			errJson.optString("summary")
			,message
			,errJson.optString("type")
			,errJson.optString("country")
			,errJson.optInt("status_code"));
		}
		throw new NordigenException();
	}
	
}
