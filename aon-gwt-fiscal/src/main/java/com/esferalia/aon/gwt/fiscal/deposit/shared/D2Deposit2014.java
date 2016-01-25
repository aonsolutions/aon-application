package com.esferalia.aon.gwt.fiscal.deposit.shared;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


public class D2Deposit2014 implements IsSerializable{
	
	private String domainName;
	private Integer domain;
	private String cif;
	private Map<String, String> map;
	private Map<String, String> mapDraft;
	private Boolean modify = false;
	private Integer year;
	
	public D2Deposit2014() {

	}
	
	public D2Deposit2014(Integer domain, String document) {
		this.domain = domain;
		this.cif = document;
	}
	
	
	//-------------------- Getters & Setters
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Map<String, String> getMap() {
		return map;
	}
	
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	public Map<String, String> getMapDraft() {
		return mapDraft;
	}
	
	public void setMapDraft(Map<String, String> mapDraft) {
		this.mapDraft = mapDraft;
	}
	
	public Boolean getModify() {
		return modify;
	}
	
	public void setModify(Boolean modify) {
		this.modify = modify;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	
}
