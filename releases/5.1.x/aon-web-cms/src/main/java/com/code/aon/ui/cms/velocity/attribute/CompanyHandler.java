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
	
	private String postal_code;
	
	private String web;
	
	private String logo;
	
	private String coordinates;
	
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
		this.coordinates=obj.getCoordinates();
	}

	public String getName() {
		return name;
	}

	public String getTelephone() {
		return telephone==null?"":telephone;
	}

	public String getFax() {
		return fax==null?"":fax;
	}

	public String getEmail() {
		return email==null?"":email;
	}

	public String getAddress() {
		return address==null?"":address;
	}

	public String getLocality() {
		return locality==null?"":locality;
	}

	public String getProvince() {
		return province==null?"":province;
	}

	public String getPostal_code() {
		return postal_code==null?"":postal_code;
	}

	public String getWeb() {
		return web==null?"":web;
	}

	public String getLogo() {
		return logo==null?"":logo;
	}

	public String getCoordinates() {
		return coordinates==null?"":coordinates;
	}
	
}
