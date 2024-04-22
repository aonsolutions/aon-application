package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class BookingResume implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Domain> childs;
	
	
	private Map<DomainType, DomainTypeInfo> domainTypes;
	private Map<UserType, Long> userTypes;
	private Map<AonApp, Long> childApps;
	
	private Integer childDefinedUsers;
	private Integer childBillingUsers;
	
	private Integer totalChilds;
	
	public BookingResume() {}

	public List<Domain> getChilds() {
		if(childs == null) {
			childs = new LinkedList<>();
		}
		return childs;
	}
	
	public BookingResume setChilds(List<Domain> childs) {
		this.childs = childs;
		return this;
	}
	
	public Map<DomainType, DomainTypeInfo> getDomainTypes() {
		return domainTypes;
	}
	
	public BookingResume setDomainTypes(Map<DomainType, DomainTypeInfo> domainTypes) {
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

	public Integer getTotalChilds() {
		return this.totalChilds;
	}
	
	public BookingResume setTotalChilds(Integer totalChilds) {
		this.totalChilds = totalChilds;
		return this;
	}
}
