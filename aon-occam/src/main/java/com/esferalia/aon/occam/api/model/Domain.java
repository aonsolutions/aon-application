package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class Domain implements Serializable {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private String owner;
	private Integer parentId;
	private DomainType domainType;
	private boolean enableHeredity;
	private boolean domainManagement;
	private boolean disableDomainManagement;
	private boolean active;
	private Integer scope;
	private Integer maxDefinedUsers;
	private Integer definedUsers;
	private Integer maxDocumentSize;
	private Integer maxTotalDocumentSize;
	private String lastAccessUser;
	private Date lastAccessDate;
	private Date expirationDate;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	private Integer aonCustomer;
	private AonStatus aonStatus;
	
	private List<User> users;
	private List<DomainApp> apps;
	
	public List<User> getUsers() {
		return users;
	}
	
	public Domain setUsers(List<User> users) {
		this.users = users;
		return this;
	}
	
	public List<DomainApp> getApps() {
		return apps;
	}
	
	public Domain setApps(List<DomainApp> apps) {
		this.apps = apps;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public Domain setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}
	public Domain setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getParentId() {
		return parentId;
	}
	public Domain setParentId(Integer parentId) {
		this.parentId = parentId;
		return this;
	}	

	public boolean isParent() {
		return parentId == null && isDomainManagement();
	}
	public boolean isChild() {
		return parentId != null;
	}
	public boolean isStandalone() {
		return parentId == null && !isDomainManagement();
	}
	
	public boolean isConsultancy() {
		return DomainType.CONSULTANCY.equals(getDomainType());
	}

	public boolean isActive() {
		return active;
	}

	public Domain setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Domain setDescription(String description) {
		this.description = description;
		return this;
	}

	public DomainType getDomainType() {
		return domainType;
	}

	public Domain setDomainType(DomainType domainType) {
		this.domainType = domainType;
		return this;
	}

	public boolean isEnableHeredity() {
		return enableHeredity;
	}

	public Domain setEnableHeredity(boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
		return this;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}

	public Domain setDomainManagement(boolean domainManagement) {
		this.domainManagement = domainManagement;
		return this;
	}
	
	public boolean isDisableDomainManagement() {
		return disableDomainManagement;
	}
	
	public Domain setDisableDomainManagement(boolean disableDomainManagement) {
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public Domain setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public String getOwner() {
		return owner;
	}
	
	public Domain setOwner(String owner) {
		this.owner = owner;
		return this;
	}
	
	public Integer getMaxDefinedUsers() {
		return maxDefinedUsers;
	}
	
	public Domain setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Integer getDefinedUsers() {
		return definedUsers;
	}
	
	public Domain setDefinedUsers(Integer definedUsers) {
		this.definedUsers = definedUsers;
		return this;
	}
	
	public Integer getMaxDocumentSize() {
		return maxDocumentSize;
	}
	
	public Domain setMaxDocumentSize(Integer maxDocumentSize) {
		this.maxDocumentSize = maxDocumentSize;
		return this;
	}
	
	public Integer getMaxTotalDocumentSize() {
		if(maxTotalDocumentSize == null) {
			maxTotalDocumentSize = 0;
		}
		return maxTotalDocumentSize;
	}
	
	public Domain setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.maxTotalDocumentSize = maxTotalDocumentSize;
		return this;
	}

	public String getLastAccessUser() {
		return lastAccessUser;
	}
	public Domain setLastAccessUser(String lastAccessUser) {
		this.lastAccessUser = lastAccessUser;
		return this;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public Domain setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate; 
		return this;
	}

	public Date getExpirationDate() {
		return expirationDate;  
	}
	public Domain setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Domain setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Domain setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}
	public Domain setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
	public Domain setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Integer getAonCustomer() {
		return aonCustomer;
	}
	public Domain setAonCustomer(Integer aonCustomer) {
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public AonStatus getAonStatus() {
		return aonStatus;
	}
	public Domain setAonStatus(AonStatus aonStatus) {
		this.aonStatus = aonStatus;
		return this;
	}
	
}
