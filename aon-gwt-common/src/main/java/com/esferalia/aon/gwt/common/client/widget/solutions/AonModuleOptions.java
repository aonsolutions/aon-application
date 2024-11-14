package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.ui.HasWidgets;

public class AonModuleOptions<T extends AonModuleOptions<T>> implements Serializable {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration configuration;
	private boolean advancedMode;

	private HasWidgets parentWidget;
	
	public Occam getOccam() {
		return new Occam()
			.setDomainName( getDomainName())
			.setDomain(getDomain())
			.setUser(getUser());
	}

	public String getDomainName() {
		return domainName;
	}

	@SuppressWarnings("unchecked")
	public T setDomainName(String domainName) {
		this.domainName = domainName;
		return (T) this;
	}

	public int getDomain() {
		return domain;
	}

	@SuppressWarnings("unchecked")
	public T setDomain(int domain) {
		this.domain = domain;
		return (T) this;
	}

	public String getUser() {
		return user;
	}

	@SuppressWarnings("unchecked")
	public T setUser(String user) {
		this.user = user;
		return (T) this;
	}

	public AonConfiguration getConfiguration() {
		return configuration;
	}

	@SuppressWarnings("unchecked")
	public T setConfiguration(AonConfiguration configuration) {
		this.configuration = configuration;
		return (T) this;
	}
	
	public boolean isAdvancedMode() {
		return advancedMode;
	}
	@SuppressWarnings("unchecked")
	public T setAdvancedMode(boolean advancedMode) {
		this.advancedMode = advancedMode;
		return (T) this;
	}
	
	public HasWidgets getParentWidget() {
		return parentWidget;
	}

	@SuppressWarnings("unchecked")
	public T setParentWidget(HasWidgets parentWidget) {
		this.parentWidget = parentWidget;
		return (T) this;
	}
	
	public boolean isGuest() {
		return getConfiguration() != null
			&& getConfiguration().getUser() != null		
			&& getConfiguration().getUser().hasGuestRole() 
			&& !getConfiguration().getUser().hasAdminRole();
	}
	
	public boolean isAccountingGuest() {
		return isGuest() || 
			(getConfiguration().getDur().isAccounting() 
				&& !getConfiguration().getDur().isAccountingManager());
	}
	
	public boolean hasConfidentialityRole() {
		return getConfiguration() != null
			&& getConfiguration().getUser() != null 
			&& getConfiguration().getUser().hasConfidentialityRole();
	}
}
