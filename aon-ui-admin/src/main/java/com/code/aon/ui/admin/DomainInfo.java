package com.code.aon.ui.admin;

import java.util.List;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;

public class DomainInfo {
	
	private String name;
	
	private String url;
	
	private DomainType type;
	
	private Domain parent;
	
	private Integer numberOfUsers;
	
	private int maxTotalDocumentSize;
	
	private boolean domainManagement;

	private List<Module> modules;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
	}

	public Domain getParent() {
		return parent;
	}

	public void setParent(Domain parent) {
		this.parent = parent;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public int getMaxTotalDocumentSize() {
		return maxTotalDocumentSize;
	}

	public void setMaxTotalDocumentSize(int maxTotalDocumentSize) {
		this.maxTotalDocumentSize = maxTotalDocumentSize;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}

	public void setDomainManagement(boolean domainManagement) {
		this.domainManagement = domainManagement;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
}
