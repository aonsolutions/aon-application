package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
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
	public Optional<AonConfiguration> optConfiguration() {
		return Optional.ofNullable( configuration );
	}

	public Optional<InvoiceCommunicationConfiguration> getCommunicationConfiguration() {
		return optConfiguration().flatMap( c -> c.optCommunicationConfig());
	}

	public Optional<InvoiceCommunicationConfiguration> optCommunicationConfig() {
		return optConfiguration()
			.map( c-> c.getCommunicationConfig() );
	}
	public boolean isCertificateNeededForCommunication() {
		return isCertificateNeededForCommunication(InvoiceType.SALES);
	}
	public boolean isCertificateNeededForCommunication(InvoiceType type) {
		return optCommunicationConfig()
			.map( icc -> icc.isCertificateNeeded(type) )
			.orElse(false)
		;
	}
	public boolean hasCommunication(Date atDate) {
		return optConfiguration()
			.map( c-> c.getCommunicationConfig() )
			.map( icc -> icc.hasCommunication( atDate) )
			.orElse(false)
		;
	}
	
	public boolean hasCommunication() {
		return optConfiguration()
			.map( c-> c.getCommunicationConfig() )
			.map( icc -> icc.hasCommunication() )
			.orElse(false)
		;
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
