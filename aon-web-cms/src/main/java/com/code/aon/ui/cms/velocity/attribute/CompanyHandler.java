package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.Company;

public class CompanyHandler {

	private String name;
	
	private String telephone;
	
	private String fax;
	
	private String email;
	
	private String address;
	
	private String locality;
	
	private String province;
	
	private Integer postal_code;
	
	private String web;
	
	private String logo; 	
	
	public CompanyHandler (Company obj) {
		this.name=obj.getName();
		this.telephone=obj.getTelephone();
		this.fax=obj.getFax();
		this.email=obj.getEmail();
		this.address=obj.getAddress();
		this.locality=obj.getLocality();
		this.province=obj.getProvince();
		this.postal_code=obj.getPostal_code();
		this.web=obj.getWeb();
		this.logo=obj.getLogo(); 	
	}

	public String getName() {
		return name;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getFax() {
		return fax;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getLocality() {
		return locality;
	}

	public String getProvince() {
		return province;
	}

	public Integer getPostal_code() {
		return postal_code;
	}

	public String getWeb() {
		return web;
	}

	public String getLogo() {
		return logo;
	}

	
	
}
