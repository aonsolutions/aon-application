package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.google.gwt.user.client.ui.HasWidgets;

public class CheckItModuleOptions implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private String domainName;
	private int domain;
	private String user;
	private CheckItConfiguration configuration;

	private HasWidgets parentWidget;

	public String getDomainName() {
		return domainName;
	}

	public CheckItModuleOptions setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public CheckItModuleOptions setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public CheckItModuleOptions setUser(String user) {
		this.user = user;
		return this;
	}

	public CheckItConfiguration getConfiguration() {
		return configuration;
	}

	public CheckItModuleOptions setConfiguration(CheckItConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	public CheckItModuleOptions setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return this;
	}

}
