package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;

public class CheckItParams implements Serializable{

	private static final long serialVersionUID = 6932297276176218369L;
	
	private String domainName;
	private Integer domainId;
	private String user;
	private Integer checkitEmpresaId;
	private String iban;
	
	public String getDomainName() {
		return domainName;
	}
	public CheckItParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public CheckItParams setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	public String getUser() {
		return user;
	}
	public CheckItParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Integer getCheckitEmpresaId() {
		return checkitEmpresaId;
	}
	public CheckItParams setCheckitEmpresaId(Integer checkitEmpresaId) {
		this.checkitEmpresaId = checkitEmpresaId;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public CheckItParams setIban(String iban) {
		this.iban = iban;
		return this;
	}
	
	
}
