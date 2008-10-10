package com.code.aon.desktop.report;

import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAddress;

public class CompanyReport {

	private Company company;

	private RegistryAddress address;

	private String addressStr;

	private String phone;
	
	private String fax;
	
	private String cellular;

	private String email;
	
	private String web;

	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public RegistryAddress getAddress() {
		return address;
	}

	public void setAddress(RegistryAddress address) {
		this.address = address;
		addressStr = "";
		if (address.getStreetType()!=null)
			addressStr += address.getStreetType().toString()+" ";
		if (address.getAddress()!=null)
			addressStr += address.getAddress()+" ";
		if (address.getAddress2()!=null)
			addressStr += address.getAddress2()+" ";
		if (address.getAddress3()!=null)
			addressStr += address.getAddress3()+" ";
		if (address.getCity()!=null)
			addressStr += address.getCity()+" ";
		if (address.getZip()!=null)
			addressStr += address.getZip()+" ";
	}

	public String getAddressStr() {
		return addressStr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}


}
