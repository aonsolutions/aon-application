package com.esferalia.aon.gwt.fiscal.client.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.google.gwt.user.client.ui.HasWidgets;

public class FinanceModuleOptions implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration configuration;

	private HasWidgets parentWidget;

	public String getDomainName() {
		return domainName;
	}

	public FinanceModuleOptions setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public FinanceModuleOptions setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public FinanceModuleOptions setUser(String user) {
		this.user = user;
		return this;
	}

	public AonConfiguration getConfiguration() {
		return configuration;
	}

	public FinanceModuleOptions setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	public FinanceModuleOptions setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return this;
	}
}
