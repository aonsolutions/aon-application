package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AonRole;


public class User implements Serializable {
	
	private static final long serialVersionUID = -5850188463638573104L;
	
	private Integer id;
	private int domain;
	private String name;
	private String login;
	private boolean active;
	private AonRole[] userRoles;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public AonRole[] getUserRoles() {
		return userRoles;
	}
	public void setRoles(AonRole[] userRoles) {
		this.userRoles = userRoles;
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
}
