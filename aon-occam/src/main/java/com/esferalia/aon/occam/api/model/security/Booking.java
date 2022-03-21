package com.esferalia.aon.occam.api.model.security;

import java.util.List;

import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;

public class Booking {
	
	List<AonApp> apps;
	Integer numberOfUsers;
	
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
