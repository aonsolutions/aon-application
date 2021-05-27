package net.aonsolutions.aon.tedi;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;

public class TediContext {
	
	private AONContext aonContext;
	private String domainName;
	private Integer domain;
	private String user;
	private AonConfiguration aonConfiguration;

	public AONContext getAONContext() {
		return aonContext;
	}

	public TediContext setAONContext(AONContext aonContext) {
		this.aonContext = aonContext;
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public TediContext setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public TediContext setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}

	public TediContext setUser(String user) {
		this.user = user;
		return this;
	}

	public AonConfiguration getAonConfiguration() {
		return aonConfiguration;
	}

	public TediContext setAonConfiguration(AonConfiguration aonConfiguration) {
		this.aonConfiguration = aonConfiguration;
		return this;
	}
	
	public Company getCompany() {
		return aonConfiguration != null ? aonConfiguration.getCompany() : null;
	}
	public String getCompanyDocument() {
		return getCompany() != null ? getCompany().getDocument() : null;
	}

	
}
