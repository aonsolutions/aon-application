package com.code.aon.file.tax.model.MOD115.data;

public class Declaration {

	private String document;
	private Integer startPeriod;
	private Integer endPeriod;
	private Double result;
	private String name;
	private String address;
	private Integer addressNumber;
	private String entity;
	private String city;
	private String province;
	private Integer zip;
	private Integer telephone;
	private String amounts;

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public Integer getStartPeriod() {
		return startPeriod;
	}
	public void setStartPeriod(Integer startPeriod) {
		this.startPeriod = startPeriod;
	}

	public Integer getEndPeriod() {
		return endPeriod;
	}
	public void setEndPeriod(Integer endPeriod) {
		this.endPeriod = endPeriod;
	}

	public Double getResult() {
		return result;
	}
	public void setResult(Double result) {
		this.result = result;
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

	public Integer getAddressNumber() {
		return addressNumber;
	}
	public void setAddressNumber(Integer addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getZip() {
		return zip;
	}
	public void setZip(Integer zip) {
		this.zip = zip;
	}

	public Integer getTelephone() {
		return telephone;
	}
	public void setTelephone(Integer telephone) {
		this.telephone = telephone;
	}

	public String getAmounts() {
		return amounts;
	}
	public void setAmounts(String amounts) {
		this.amounts = amounts;
	}

	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
}
