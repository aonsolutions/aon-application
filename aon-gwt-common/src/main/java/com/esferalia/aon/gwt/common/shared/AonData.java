package com.esferalia.aon.gwt.common.shared;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AonData implements IsSerializable{

	User user;
	Integer userOperator;
	String md5;
	Domain domain;
	Company company;
	Boolean betaEnabled;
	Boolean alphaEnabled;
	
	public User getUser() {
		return user;
	}
	public AonData setUser(User user) {
		this.user = user;
		return this;
	}
	public String getMd5() {
		return md5;
	}
	public AonData setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public AonData setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Integer getUserOperator() {
		return userOperator;
	}
	public AonData setUserOperator(Integer userOperator) {
		this.userOperator = userOperator;
		return this;
	}
	public Boolean isBetaEnabled() {
		return betaEnabled;
	}
	public AonData setBetaEnabled(Boolean betaEnabled) {
		this.betaEnabled = betaEnabled;
		return this;
	}
	public Boolean isAlphaEnabled() {
		return alphaEnabled;
	}
	public AonData setAlphaEnabled(Boolean alphaEnabled) {
		this.alphaEnabled = alphaEnabled;
		return this;
	}
	public Company getCompany() {
		return company;
	}
	public AonData setCompany(Company company) {
		this.company = company;
		return this;
	}
}
