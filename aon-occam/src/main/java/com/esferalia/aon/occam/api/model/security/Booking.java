package com.esferalia.aon.occam.api.model.security;

import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;

public class Booking {
	
	Domain domain;
	Company company;
	List<AonApp> apps;
	Integer numberOfUsers;
	
	public Domain getDomain() {
		return domain;
	}
	
	public Booking setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public Booking setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public List<AonApp> getApps() {
		return apps;
	}
	
	public Booking setApps(List<AonApp> apps) {
		this.apps = apps;
		return this;
	}
	
	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}
	
	public Booking setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
		return this;
	}
}
