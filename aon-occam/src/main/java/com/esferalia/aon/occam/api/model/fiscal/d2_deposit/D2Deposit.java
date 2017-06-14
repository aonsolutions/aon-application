package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import java.io.Serializable;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;

@SuppressWarnings("serial")
public class D2Deposit implements Serializable{
	private Integer id;
	private Domain domain;
	private Enterprise enterprise;
	private Map<String, String> map;
	private Map<String, String> mapDraft;
	private Boolean modify = false;
	private Integer year;
	private String type;
	
	public D2Deposit() {

	}
	
	public D2Deposit(Domain domain, String cif, String razonSocial){
		this.domain = domain;
	}

	//-------------------- Getters & Setters
	
	public Integer getId() {
		return id;
	}

	public D2Deposit setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}

	public D2Deposit setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public String getCif() {
		return enterprise.getDocument();
	}

	public String getRazonSocial() {
		return enterprise.getName();
	}

	public Map<String, String> getMap() {
		return map;
	}

	public D2Deposit setMap(Map<String, String> map) {
		this.map = map;
		return this;
	}

	public Map<String, String> getMapDraft() {
		return mapDraft;
	}

	public D2Deposit setMapDraft(Map<String, String> mapDraft) {
		this.mapDraft = mapDraft;
		return this;
	}

	public Boolean getModify() {
		return modify;
	}

	public D2Deposit setModify(Boolean modify) {
		this.modify = modify;
		return this;
	}

	public Integer getYear() {
		return year;
	}

	public D2Deposit setYear(Integer year) {
		this.year = year;
		return this;
	}

	public String getType() {
		return type;
	}

	public D2Deposit setType(String type) {
		this.type = type;
		return this;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public D2Deposit setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
		return this;
	}
}
