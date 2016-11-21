package com.esferalia.aon.gwt.common.shared;

import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AonData implements IsSerializable{

	String loggedUser;
	String md5;
	Domain domain;
	
	public String getLoggedUser() {
		return loggedUser;
	}
	public AonData setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
		return this;
	}
	public String getMd5() {
		return md5;
	}
	public AonData setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public AonData setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
}
