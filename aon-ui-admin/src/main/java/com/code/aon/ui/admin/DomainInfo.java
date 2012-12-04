package com.code.aon.ui.admin;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.util.AonUtil;

public class DomainInfo {
	
	private String name;
	
	private String url;
	
	private DomainType type;
	
	private Domain parent;
	
	private Integer numberOfUsers;
	
	private int maxTotalDocumentSize;
	
	private boolean domainManagement;

	private List<Module> modules;

	private boolean active;
	
	private AuditLevel auditLevel; 
	
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

	public Integer getParentId() {
		return (parent != null) ? parent.getId() : null;
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

	public boolean[] getModuleArray() {
		boolean[] array = new boolean[Module.values().length];
		for( int i = 0; i < array.length; i++ ) {
			array[i] = this.modules.contains(Module.values()[i]);
		}
		return array;
	}

	public String getModuleList() {
		Set<String> modules = new TreeSet<String>();
		Locale locale = AonUtil.getCurrentLocale();
		for( Module module : this.modules ) {
			modules.add( module.getName(locale) );
		}
		return StringUtils.join(modules, ", ");
	}	
	
	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public AuditLevel getAuditLevel() {
		return auditLevel;
	}

	public void setAuditLevel(AuditLevel auditLevel) {
		this.auditLevel = auditLevel;
	}
	
}
