package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import net.aonsolutions.occam.api.model.type.AonStatus;
import net.aonsolutions.occam.api.model.type.DomainType;

public class Domain implements Serializable, HasAudit {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private String owner;
	private Integer parentId;
	private DomainType domainType;
	private boolean heredityEnabled;
	private boolean domainManagement;
	private boolean disableDomainManagement;
	private boolean active;
	private Integer scope;
	private Integer maxDefinedUsers;
	private Integer definedUsers;
	private Integer maxDocumentSize;
	private Integer maxTotalDocumentSize;
	private String lastAccessUser;
	private Timestamp lastAccessDate;
	private Date expirationDate;
	private Integer aonCustomer;
	private AonStatus aonStatus;
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
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
	
	public String getDescription() {
		return description;
	}
	public Domain setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getOwner() {
		return owner;
	}
	public Domain setOwner(String owner) {
		this.owner = owner;
		return this;
	}
	
	public Integer getParentId() {
		return parentId;
	}
	public Domain setParentId(Integer parentId) {
		this.parentId = parentId;
		return this;
	}	
	
	public boolean isHeredityEnabled() {
		return heredityEnabled;
	}
	public Domain setHeredityEnabled(boolean heredityEnabled) {
		this.heredityEnabled = heredityEnabled;
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

	public boolean isActive() {
		return active;
	}
	public Domain setActive(boolean active) {
		this.active = active;
		return this;
	}

	public Integer getScope() {
		return scope;
	}
	public Domain setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public DomainType getDomainType() {
		return domainType;
	}
	public Domain setDomainType(DomainType domainType) {
		this.domainType = domainType;
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

	public Timestamp getLastAccessDate() {
		return lastAccessDate;
	}
	public Domain setLastAccessDate(Timestamp lastAccessDate) {
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
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Domain setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Domain setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Domain setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Domain setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
}
