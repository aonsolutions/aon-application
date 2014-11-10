package com.esferalia.aon.master.impl.server.jooq;

public class DAOSecurityContext {

	int domainId;
	
	public DAOSecurityContext(int domainId) {
		this.domainId = domainId;
	}
	public int getDomainId() {
		return domainId;
	}
	
}
