package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.OldAonRole;


public class User implements Serializable {
	
	private static final long serialVersionUID = -5850188463638573104L;
	
	private Integer id;
	private Integer domain;
	private UserType type;
	private String name;
	private String login;
	private boolean active;
    private Registry registry;
    private OldAonRole[] userRoles;
    private Date expirationDate;
    private UserToolbar toolbar;
    private List<Workgroup> workgroups;
    private Auth auth;
    private List<TaskHolder> taskHolders;
    
    @Deprecated
	private boolean shared;
	@Deprecated
	private Integer enterprise;
	
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
	
	public UserType getType() {
		if(type == null) {
			type = UserType.NORMAL;
		}
		return type;
	}
	
	public byte getTypeValue() {
		return getType() != null
			? getType().value()
			: UserType.NORMAL.value();
	}
	
	public User setType(UserType type) {
		this.type = type;
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
		return shared || UserType.SHARED.equals(getType());
	}
	
	public User setShared(boolean shared) {
		this.shared = shared;
		setType(UserType.SHARED);
		return this;
	}
	
	public OldAonRole[] getUserRoles() {
		return userRoles;
	}
	public User setRoles(OldAonRole[] userRoles) {
		this.userRoles = userRoles;
		return this;
	}
	public User setRoles(List<OldAonRole> roles) {
		this.userRoles = roles.toArray(OldAonRole[]::new);
		return this;
	}

	public Registry getRegistry() {
	    if(registry == null) {
	        return new Registry();
	    }
	    return registry;
	}
	public User setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}	

	@Deprecated
	public Integer getEnterprise() {
		return enterprise;
	}
	
    @Deprecated
	public User setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	
	public Auth getAuth() {
		if(auth == null) {
			auth = new Auth();
		}
		return auth;
	}

	public User setAuth(Auth auth) {
		this.auth = auth;
		return this;
	}
	
	public UserToolbar getToolbar() {
		return toolbar;
	}
	
	public User setToolbar(UserToolbar toolbar) {
		this.toolbar = toolbar;
		return this;
	}
	
	public List<Workgroup> getWorkgroups() {
		if(workgroups == null) {
			workgroups = new LinkedList<>();
		}
		return workgroups;
	}
	
	public User setWorkgroups(List<Workgroup> workgroups) {
		this.workgroups = workgroups;
		return this;
	}
	
	public User addWorkgroup(Workgroup workgroup) {
		getWorkgroups().add(workgroup);
		return this;
	}

	public List<TaskHolder> getTaskHolders() {
		if(taskHolders == null) 
			taskHolders = new LinkedList<>();
		return taskHolders;
	}
	
	public User setTaskHolders(List<TaskHolder> taskHolders) {
		this.taskHolders = taskHolders;
		return this;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public User setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}
	
	public boolean hasAuth() {
		return getAuth() != null;  
	}
	
	public boolean hasAdminRole() {
		return hasRole(OldAonRole.ADMIN);  
	}
	public boolean hasGuestRole() {
		return hasRole(OldAonRole.GUEST);  
	}
	public boolean hasConfigRole() {
		return hasAdminRole() || hasRole(OldAonRole.CONFIG);  
	}
	public boolean hasAuditorRole() {
		return hasAdminRole() || hasRole(OldAonRole.AUDITOR);  
	}
	public boolean hasConfidentialityRole() {
		return hasAdminRole() || hasRole(OldAonRole.CONFIDENTIALITY);  
	}
	public boolean hasProductRole() {
		return hasAdminRole() || hasRole(OldAonRole.PRODUCT);  
	}
	public boolean hasCommercialRole() {
		return hasAdminRole() || hasRole(OldAonRole.COMMERCIAL);  
	}
	public boolean hasSaleRole() {
		return hasAdminRole() || hasRole(OldAonRole.SALE);  
	}
	public boolean hasPurchaseRole() {
		return hasAdminRole() || hasRole(OldAonRole.PURCHASE);  
	}
	public boolean hasWarehouseRole() {
		return hasAdminRole() || hasRole(OldAonRole.WAREHOUSE);  
	}
	public boolean hasAccountingRole() {
		return hasAdminRole() || hasRole(OldAonRole.ACCOUNTING);  
	}
	public boolean hasFinanceRole() {
		return hasAdminRole() || hasRole(OldAonRole.FINANCE);  
	}
	public boolean hasStatisticsRole() {
		return hasAdminRole() || hasRole(OldAonRole.STATISTICS);  
	}
	public boolean hasTaskMonitoringRole() {
		return hasAdminRole() || hasRole(OldAonRole.TASK_MONITORING);  
	}
	public boolean hasESignatureRole() {
		return hasAdminRole() || hasRole(OldAonRole.E_SIGNATURE);  
	}
	public boolean hasSysAdminRole() {
		return hasAdminRole() || hasRole(OldAonRole.SYS_ADMIN);  
	}
	public boolean hasTGCRole() {
		return hasAdminRole() || hasRole(OldAonRole.TGC);  
	}
	public boolean hasDOCUMENTRole() {
		return hasAdminRole() || hasRole(OldAonRole.DOCUMENT);  
	}
	public boolean hasDocumentManagerRole() {
		return hasAdminRole() || hasRole(OldAonRole.DOCUMENT_MANAGER);  
	}
	public boolean hasPayrollRole() {
		return hasAdminRole() || hasRole(OldAonRole.PAYROLL);  
	}
	public boolean hasFiscalRole() {
		return hasAdminRole() || hasRole(OldAonRole.FISCAL);  
	}
	public boolean hasAccountingManagerRole() {
		return hasAdminRole() || hasRole(OldAonRole.ACCOUNTING_MANAGER);  
	}
	
	public boolean hasRole(OldAonRole role) {
		if (getUserRoles() != null) {
			for (OldAonRole r : getUserRoles()) {
				if (r == role) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isPortal() {
		return getEnterprise() != null || UserType.PORTAL.equals(getType());
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
				&& getLogin() == null;
	}
}
