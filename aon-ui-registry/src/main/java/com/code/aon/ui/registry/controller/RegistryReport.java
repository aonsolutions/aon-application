package com.code.aon.ui.registry.controller;

import com.code.aon.common.ITransferObject;

public class RegistryReport implements ITransferObject {

	private static final long serialVersionUID = -2726021028034361397L;
	
	private Integer id;
	private String document;
	private String name;
	private String phone;
	private String alias;
	private String status;
	private String advertising;
	
	public RegistryReport(Integer id, String document, String name,
			String phone, String alias, String status,
			String advertising) {
		this.id = id;
		this.document = document;
		this.name = name;
		this.phone= phone ;
		this.alias = alias;
		this.status = status;
		this.advertising = advertising;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAdvertising() {
		return advertising;
	}
	public void setAdvertising(String advertising) {
		this.advertising = advertising;
	}
	
}