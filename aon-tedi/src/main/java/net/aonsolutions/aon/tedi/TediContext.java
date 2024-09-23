package net.aonsolutions.aon.tedi;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;

public class TediContext {
	
	private AONContext aonContext;
	private Occam occam;
	private AonConfiguration aonConfiguration;

	public AONContext getAONContext() {
		return aonContext;
	}

	public TediContext setAONContext(AONContext aonContext) {
		this.aonContext = aonContext;
		return this;
	}
	
	public Occam getOccam() {
		return occam;
	}
	public TediContext setOccam(Occam occam) {
		this.occam = occam;
		return this;
	}

	public String getDomainName() {
		return occam.getDomainName();
	}
	public Integer getDomain() {
		return occam.getDomain();
	}
	public String getUser() {
		return occam.getUser();
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
