package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.HiruOrganizerCentre;

public class HiruCenterHandler {

	private String address;

	private String email;

	private String fax;

	private String locality;

	private String name;

	private Integer postal_code;

	private String telephone;

	private String web;
	
	private ArrayList<HiruCourseHandler> list;

	public HiruCenterHandler (HiruOrganizerCentre hoc, ArrayList<HiruCourseHandler> list) {
		this.address = hoc.getAddress();
		this.email = hoc.getEmail();
		this.fax = hoc.getFax();
		this.locality = hoc.getLocality();
		this.name = hoc.getName();
		this.postal_code = hoc.getPostal_code();
		this.telephone = hoc.getTelephone();
		this.web = hoc.getWeb();
		this.list = list;
	}

	
	
	public String getAddress() {
		return address;
	}



	public String getEmail() {
		return email;
	}



	public String getFax() {
		return fax;
	}



	public String getLocality() {
		return locality;
	}



	public String getName() {
		return name;
	}



	public Integer getPostal_code() {
		return postal_code;
	}



	public String getTelephone() {
		return telephone;
	}



	public String getWeb() {
		return web;
	}



	public ArrayList<HiruCourseHandler> getList() {
		return list;
	}

}
