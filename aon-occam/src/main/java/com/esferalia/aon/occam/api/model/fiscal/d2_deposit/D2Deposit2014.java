package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.Enterprise;

public class D2Deposit2014 {
	
	String domain;
	Enterprise enterprise;
	
	Map<String, String> map;
 	
	Map<D2DepositKey, String> memoryMap;
	Map<D2DepositHeaderKey, String> headerMap;
	Map<D2DepositFooterKey, String> footerMap;

	//-------------------- Constructors
	
	public D2Deposit2014() {
	
	}
	
	public D2Deposit2014(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public D2Deposit2014(String domainName, Enterprise enterprise) {
		this.domain = domainName;
		this.enterprise = enterprise;
	}
	
	
	//-------------------- Getters & Setters
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Map<D2DepositKey, String> getMemoryMap() {
		return memoryMap;
	}
	
	public void setMemoryMap(Map<D2DepositKey, String> memoryMap) {
		this.memoryMap = memoryMap;
	}
	
	public Map<D2DepositHeaderKey, String> getHeaderMap() {
		return headerMap;
	}
	
	public void setHeaderMap(Map<D2DepositHeaderKey, String> headerMap) {
		this.headerMap = headerMap;
	}
	
	public Map<D2DepositFooterKey, String> getFooterMap() {
		return footerMap;
	}
	
	public void setFooterMap(Map<D2DepositFooterKey, String> footerMap) {
		this.footerMap = footerMap;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	
	
}
