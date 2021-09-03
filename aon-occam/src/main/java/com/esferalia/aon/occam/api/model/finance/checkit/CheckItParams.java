package com.esferalia.aon.occam.api.model.finance.checkit;

public class CheckItParams {
	private String domainName;
	private Integer domainId;
	private String user;
	private Integer checkitEmpresaId;
	private String iban;
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Integer getCheckitEmpresaId() {
		return checkitEmpresaId;
	}
	public void setCheckitEmpresaId(Integer checkitEmpresaId) {
		this.checkitEmpresaId = checkitEmpresaId;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	
	
}
