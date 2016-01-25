package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.IsSerializable;


public class D2DepositTreeObject implements IsSerializable{
	
	private String domainName;
	private Integer domain;
	private Enterprise enterprise;
	private Map<String, String> map;
	private Map<String, String> mapDraft;
	private Boolean modify = false;
	private Integer year;

	public D2DepositTreeObject() {

	}
	
	public D2DepositTreeObject(Enterprise enterprise) {
		this.enterprise = enterprise;
		this.domain = enterprise.getDomain();
	}
	
	public D2DepositTreeObject(Enterprise enterprise, Integer year) {
		this.enterprise = enterprise;
		this.domain = enterprise.getDomain();
		this.year = year;
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
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
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
		this.mapDraft = new HashMap<String, String>();
		this.mapDraft = mapDraft;
	}
	
	public Boolean getModify() {
		return modify;
	}
	
	public void setModify(Boolean modify) {
		this.modify = modify;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
}
