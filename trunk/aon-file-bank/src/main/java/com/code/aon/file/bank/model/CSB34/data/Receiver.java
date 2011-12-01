package com.code.aon.file.bank.model.CSB34.data;

public class Receiver{
	
	private String code;
	private String name;
	private String address;
	private String zip;
	private String city;
	private String province;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressPart1() {
		if (address.length() > 36)
			return address.substring(0, 36);
		return address;
	}
	public String getAddressPart2() {
		if (address.length() > 36) {
			if (address.length() > 72)
				return address.substring(36, 72);
			return address.substring(36);
		}
		return null;
	}

	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipAndCity() {
		return ((zip!=null) ? zip + " - " : "") + city;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

}
