package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;

public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	private Domain domain;
	private Company company;
	private List<AonApp> apps;
	private List<AonApp> parentApps;
	private Integer numberOfUsers;
	private String payer;

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
		if(apps == null) {
			apps = new LinkedList<>();
		}
		return apps;
	}
	
	public Booking setApps(List<AonApp> apps) {
		this.apps = apps;
		return this;
	}
	
	public Booking addApp(AonApp app) {
		getApps().add(app);
		return this;
	}
	
	public List<AonApp> getParentApps() {
		if(parentApps == null) {
			parentApps = new LinkedList<>();
		}
		return parentApps;
	}
	
	public Booking setParentApps(List<AonApp> parentApps) {
		this.parentApps = parentApps;
		return this;
	}
	
	public Booking addParentApp(AonApp parentApp) {
		getParentApps().add(parentApp);
		return this;
	}
	
	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}
	
	public Booking setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
		return this;
	}
	
	public String getPayer() {
		return payer;
	}
	
	public Booking setPayer(String payer) {
		this.payer = payer;
		return this;
	}
}
