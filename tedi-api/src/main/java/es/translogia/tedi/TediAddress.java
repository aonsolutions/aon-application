package es.translogia.tedi;

import org.json.JSONObject;

public class TediAddress {
	
	private String address;
	private String city;
	private String country;
	private String province;
	private String postalCode;

	public TediAddress() {
		
	}
	
	public TediAddress(JSONObject json) {
		if(json != null) {
			this.address = json.optString("address");
			this.city = json.optString("city");
			this.country = json.optString("country");
			this.province = json.optString("province");
			this.postalCode = json.optString("postal_code");
		}
	}

	public String getAddress() {
		return address;
	}

	public TediAddress setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getCity() {
		return city;
	}

	public TediAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public TediAddress setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getProvince() {
		return province;
	}

	public TediAddress setProvince(String province) {
		this.province = province;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public TediAddress setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getFullAddress() {
		return getAddress() + ", " + getPostalCode() + " " + getCity() 
			+ ", " + getProvince() + ", " + getCountry();
	}
}
