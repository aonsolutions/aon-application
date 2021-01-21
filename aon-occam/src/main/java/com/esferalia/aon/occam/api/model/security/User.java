package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AonRole;


public class User implements Serializable {
	
	private static final long serialVersionUID = -5850188463638573104L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String login;
	private boolean active;
	private boolean shared;
	private Integer registry;
	private Integer enterprise;
	private AonRole[] userRoles;
	private byte[] auth;
	
	public Integer getId() {
		return id;
	}
	public User setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public User setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getName() {
		return name;
	}
	public User setName(String name) {
		this.name = name;
		return this;
	}
	public String getLogin() {
		return login;
	}
	public User setLogin(String login) {
		this.login = login;
		return this;
	}
	public boolean isActive() {
		return active;
	}
	public User setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isShared() {
		return shared;
	}
	
	public User setShared(boolean shared) {
		this.shared = shared;
		return this;
	}
	
	public AonRole[] getUserRoles() {
		return userRoles;
	}
	public User setRoles(AonRole[] userRoles) {
		this.userRoles = userRoles;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public User setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}	
	
	public Integer getEnterprise() {
		return enterprise;
	}
	
	public User setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	
	public byte[] getAuth() {
		return auth;
	}
	public User setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}

	public boolean hasAuth() {
		return getAuth() != null;  
	}
	
	public boolean hasAdminRole() {
		return hasRole(AonRole.ADMIN);  
	}
	public boolean hasGuestRole() {
		return hasRole(AonRole.GUEST);  
	}
	public boolean hasConfigRole() {
		return hasAdminRole() || hasRole(AonRole.CONFIG);  
	}
	public boolean hasAuditorRole() {
		return hasAdminRole() || hasRole(AonRole.AUDITOR);  
	}
	public boolean hasConfidentialityRole() {
		return hasAdminRole() || hasRole(AonRole.CONFIDENTIALITY);  
	}
	public boolean hasProductRole() {
		return hasAdminRole() || hasRole(AonRole.PRODUCT);  
	}
	public boolean hasCommercialRole() {
		return hasAdminRole() || hasRole(AonRole.COMMERCIAL);  
	}
	public boolean hasSaleRole() {
		return hasAdminRole() || hasRole(AonRole.SALE);  
	}
	public boolean hasPurchaseRole() {
		return hasAdminRole() || hasRole(AonRole.PURCHASE);  
	}
	public boolean hasWarehouseRole() {
		return hasAdminRole() || hasRole(AonRole.WAREHOUSE);  
	}
	public boolean hasAccountingRole() {
		return hasAdminRole() || hasRole(AonRole.ACCOUNTING);  
	}
	public boolean hasFinanceRole() {
		return hasAdminRole() || hasRole(AonRole.FINANCE);  
	}
	public boolean hasStatisticsRole() {
		return hasAdminRole() || hasRole(AonRole.STATISTICS);  
	}
	public boolean hasTaskMonitoringRole() {
		return hasAdminRole() || hasRole(AonRole.TASK_MONITORING);  
	}
	public boolean hasESignatureRole() {
		return hasAdminRole() || hasRole(AonRole.E_SIGNATURE);  
	}
	public boolean hasSysAdminRole() {
		return hasAdminRole() || hasRole(AonRole.SYS_ADMIN);  
	}
	public boolean hasTGCRole() {
		return hasAdminRole() || hasRole(AonRole.TGC);  
	}
	public boolean hasDOCUMENTRole() {
		return hasAdminRole() || hasRole(AonRole.DOCUMENT);  
	}
	public boolean hasDocumentManagerRole() {
		return hasAdminRole() || hasRole(AonRole.DOCUMENT_MANAGER);  
	}
	public boolean hasPayrollRole() {
		return hasAdminRole() || hasRole(AonRole.PAYROLL);  
	}
	public boolean hasFiscalRole() {
		return hasAdminRole() || hasRole(AonRole.FISCAL);  
	}
	public boolean hasAccountingManagerRole() {
		return hasAdminRole() || hasRole(AonRole.ACCOUNTING_MANAGER);  
	}
	
	public boolean hasRole(AonRole role) {
		if (getUserRoles() != null) {
			for (AonRole r : getUserRoles()) {
				if (r == role) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isPortal() {
		return getEnterprise() != null;
	}
}
