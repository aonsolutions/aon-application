package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.Map;

import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class BookingResume implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private Map<DomainType, Long> domainTypes;
	private Map<UserType, Long> userTypes;
	private Map<AonApp, Long> childApps;
	
	private Integer childDefinedUsers;
	private Integer childBillingUsers;
	
	
	public BookingResume() {

	}
	
	public Map<DomainType, Long> getDomainTypes() {
		return domainTypes;
	}
	
	public BookingResume setDomainTypes(Map<DomainType, Long> domainTypes) {
		this.domainTypes = domainTypes;
		return this;
	}
	
	public Map<UserType, Long> getUserTypes() {
		return userTypes;
	}
	
	public BookingResume setUserTypes(Map<UserType, Long> userTypes) {
		this.userTypes = userTypes;
		return this;
	}
	
	public Map<AonApp, Long> getChildApps() {
		return childApps;
	}
	
	public BookingResume setChildApps(Map<AonApp, Long> childApps) {
		this.childApps = childApps;
		return this;
	}
	
	public Integer getChildDefinedUsers() {
		return childDefinedUsers;
	}
	
	public BookingResume setChildDefinedUsers(Integer childDefinedUsers) {
		this.childDefinedUsers = childDefinedUsers;
		return this;
	}
	
	public Integer getChildBillingUsers() {
		return childBillingUsers;
	}
	
	public BookingResume setChildBillingUsers(Integer childBillingUsers) {
		this.childBillingUsers = childBillingUsers;
		return this;
	}
}
