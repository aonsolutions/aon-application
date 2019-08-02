package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediAddress implements Serializable {

	private static final long serialVersionUID = 5751328895318267735L;
	
	private String address;
	private String city;
	private String country;
	private String province;
	private String postalCode;

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

}
