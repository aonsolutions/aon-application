package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.google.gwt.user.client.ui.HasWidgets;

public class NordigenModuleOptions implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private String domainName;
	private int domain;
	private String user;
	private NordigenConfiguration configuration;

	private HasWidgets parentWidget;

	public String getDomainName() {
		return domainName;
	}

	public NordigenModuleOptions setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public NordigenModuleOptions setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public NordigenModuleOptions setUser(String user) {
		this.user = user;
		return this;
	}

	public NordigenConfiguration getConfiguration() {
		return configuration;
	}

	public NordigenModuleOptions setConfiguration(NordigenConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	public NordigenModuleOptions setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return this;
	}

}
