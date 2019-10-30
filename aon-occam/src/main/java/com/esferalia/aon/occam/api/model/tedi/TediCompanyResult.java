package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Company;

public class TediCompanyResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	Company company;
	Integer inboxCount;
	
	public TediCompanyResult() {

	}

	public TediCompanyResult(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Integer getInboxCount() {
		return inboxCount;
	}

	public void setInboxCount(Integer inboxCount) {
		this.inboxCount = inboxCount;
	}
	
}
