package com.esferalia.aon.occam.api.model.security;

import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;

public class DomainTypeInfo {
	
	Integer number;
	Map<AonApp, Long> childApps;
	List<Domain> childs;

	public DomainTypeInfo() {
	
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public DomainTypeInfo setNumber(Integer number) {
		this.number = number;
		return this;
	}
	
	public Map<AonApp, Long> getChildApps() {
		return childApps;
	}
	
	public DomainTypeInfo setChildApps(Map<AonApp, Long> childApps) {
		this.childApps = childApps;
		return this;
	}
	
	public List<Domain> getChilds() {
		return childs;
	}
	
	public DomainTypeInfo setChilds(List<Domain> childs) {
		this.childs = childs;
		return this;
	}
}