package com.code.aon.ui.admin.controller;

import org.apache.commons.io.FileUtils;

public class ServiceAccount {

	String client_id;
	String domain;
	String email_address;
	String size;
	String sizestr;
	String limit;
	String limitstr;
	String public_key;
	String google_account;
	byte[] data;
	
	public ServiceAccount(String client_id,String domain,String email, String size, String limit, String public_key, byte[] data, String google_account) {
	
		this.client_id=client_id;
		this.domain=domain;
		this.email_address= email;
		this.sizestr= size;
		this.limitstr=limit;
		this.public_key=public_key;
		this.data= data;
		this.google_account=google_account;
	}
	
	public ServiceAccount(){
		
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getPublic_key() {
		return public_key;
	}
	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getEmail_address() {
		return email_address;
	}
	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}
	public String getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size.toString();
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = limit.toString();

	}
	public String getSizestr() {
		return sizestr;
	}
	public void setSizestr(Long size) {
		this.sizestr = FileUtils.byteCountToDisplaySize(size);
	}
	public String getLimitstr() {
		return limitstr;
	}
	public void setLimitstr(Long limit) {
		this.limitstr = FileUtils.byteCountToDisplaySize(limit);
	}

	public String getGoogle_account() {
		return google_account;
	}

	public void setGoogle_account(String google_account) {
		this.google_account = google_account;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSizestr(String sizestr) {
		this.sizestr = sizestr;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public void setLimitstr(String limitstr) {
		this.limitstr = limitstr;
	}
	
	
	
}
