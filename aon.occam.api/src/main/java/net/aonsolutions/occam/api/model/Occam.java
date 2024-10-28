package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class Occam implements Serializable {

	private static final long serialVersionUID = 7841145705601632307L;
	
	private String domainName;
	private String user;

	public String getDomainName() {
		return domainName;
	}
	public Occam setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public String getUser() {
		return user;
	}
	public Occam setUser(String user) {
		this.user = user;
		return this;
	}

}
