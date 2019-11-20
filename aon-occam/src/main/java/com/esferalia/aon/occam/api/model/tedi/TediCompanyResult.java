package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Company;

public class TediCompanyResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	Company company;
	Integer inboxCount;
	Boolean tedi;
	
	public TediCompanyResult() {

	}

	public TediCompanyResult(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}

	public TediCompanyResult setCompany(Company company) {
		this.company = company;
		return this;
	}

	public Integer getInboxCount() {
		return inboxCount;
	}

	public TediCompanyResult setInboxCount(Integer inboxCount) {
		this.inboxCount = inboxCount;
		return this;
	}

	public Boolean getTedi() {
		return tedi;
	}

	public TediCompanyResult setTedi(Boolean tedi) {
		this.tedi = tedi;
		return this;
	}
	
	
}
