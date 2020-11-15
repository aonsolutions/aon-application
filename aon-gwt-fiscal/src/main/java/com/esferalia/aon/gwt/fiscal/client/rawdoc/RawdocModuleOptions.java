package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.google.gwt.user.client.ui.HasWidgets;

public class RawdocModuleOptions implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration configuration;
	private RawdocParams params;

	private HasWidgets parentWidget;

	public String getDomainName() {
		return domainName;
	}

	public RawdocModuleOptions setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public RawdocModuleOptions setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public RawdocModuleOptions setUser(String user) {
		this.user = user;
		return this;
	}

	public AonConfiguration getConfiguration() {
		return configuration;
	}

	public RawdocModuleOptions setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	public RawdocModuleOptions setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return this;
	}

	public RawdocParams getParams() {
		return params;
	}
	public RawdocModuleOptions setParams(RawdocParams params) {
		this.params = params;
		return this;
	}
	
}
