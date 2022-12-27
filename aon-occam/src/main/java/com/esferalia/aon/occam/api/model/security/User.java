package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AonRole;


public class User implements Serializable {
	
	private static final long serialVersionUID = -5850188463638573104L;
	
	private Integer id;
	private Domain domain;
	private UserType type;
	private String name;
	private String login;
	private boolean active;
    private Registry registry;
    private boolean allowConcurrent;
    private String locale;
    private Integer pageLimit;
    private Integer linesPageLimit;
    private String initAction;
    private Date expirationDate;
    private Date lastAccess;
    private UserToolbar toolbar;
    private List<Workgroup> workgroups;
    private String auth;
    
    private AonRole[] userRoles;
    
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
	public Domain getDomain() {
	    if(domain == null) {
	        domain = new Domain();
	    }
		return domain;
	}
	public User setDomain(Domain domain) {
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
	
	public AonRole[] getUserRoles() {
		return userRoles;
	}
	public User setRoles(AonRole[] userRoles) {
		this.userRoles = userRoles;
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
	
	public String getAuth() {
		return auth;
	}

	public User setAuth(String auth) {
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

	public boolean isAllowConcurrent() {
        return allowConcurrent;
    }
	
	public User setAllowConcurrent(boolean allowConcurrent) {
        this.allowConcurrent = allowConcurrent;
        return this;
    }
	
	public String getLocale() {
        return locale;
    }
	
	public User setLocale(String locale) {
        this.locale = locale;
        return this;
    }

	public Integer getPageLimit() {
        return pageLimit;
    }

	public User setPageLimit(Integer pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

	public Integer getLinesPageLimit() {
	    return linesPageLimit;
	}
	
	public User setLinesPageLimit(Integer linesPageLimit) {
        this.linesPageLimit = linesPageLimit;
        return this;
    }

	public String getInitAction() {
        return initAction;
    }
	
	public User setInitAction(String initAction) {
        this.initAction = initAction;
        return this;
    }
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public User setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}
	
	public Date getLastAccess() {
        return lastAccess;
    }
    
    public User setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
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
		return getEnterprise() != null || UserType.PORTAL.equals(getType());
	}
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null
				&& getLogin() == null;
	}
}
