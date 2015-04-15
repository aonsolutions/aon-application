package com.esferalia.aon.gwt.template.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TemplateList implements IsSerializable{

	public TemplateList() {

	}
	
	Vector<TemplateInfo> list;
	String domain;
	Integer domainId;
	
	public Vector<TemplateInfo> getList() {
		return list;
	}
	public void setList(Vector<TemplateInfo> list) {
		this.list = list;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	 
	
	
}
