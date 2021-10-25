package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

public class AonData implements Serializable {

	private static final long serialVersionUID = -4746030973585760037L;
	
	private User user;
	private Integer userOperator;
	private String md5;
	private Domain domain;
	private Company company;
	private Boolean betaEnabled;
	private Boolean alphaEnabled;
	private Boolean aonSolutions;
	private boolean customerCheckEnabled;
	private String certificateDocument;
	private String certificateName;
	private String rootPanel;
	
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
	
	public Boolean isAonSolutions() {
		return aonSolutions;
	}
	
	public boolean isCustomerCheckEnabled() {
		return customerCheckEnabled;
	}
	public AonData setCustomerCheckEnabled(boolean customerCheckEnabled) {
		this.customerCheckEnabled = customerCheckEnabled;
		return this;
	}
	
	public AonData setAonSolutions(Boolean aonSolutions) {
		this.aonSolutions = aonSolutions;
		return this;
	}
	public String getRootPanel() {
		return rootPanel;
	}
	public AonData setRootPanel(String rootPanel) {
		this.rootPanel = rootPanel;
		return this;
	}
	
	public String getCertificateDocument() {
		return certificateDocument;
	}
	public AonData setCertificateDocument(String certificateDocument) {
		this.certificateDocument = certificateDocument;
		return this;
	}

	public String getCertificateName() {
		return certificateName;
	}
	public AonData setCertificateName(String certificateName) {
		this.certificateName= certificateName;
		return this;
	}
	
}
