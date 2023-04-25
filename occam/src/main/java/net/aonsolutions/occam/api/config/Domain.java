package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.occam.api.HasAudit;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;

public class Domain implements Serializable, HasAudit<Domain>, HasSelector<Domain> {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private String name;
	private String description;
	private String owner;
	private Integer parent;
	private DomainType type;
	private String subDomainSuffix;
	
	private boolean enableHeredity;
	private boolean domainManagement;
	private boolean disableDomainManagement;
	private boolean active;
	private Integer scope;
	private Integer maxDefinedUsers;
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
	
	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Domain setId(Integer id) {
		this.dirtyMark( AonUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}
	public Domain setName(String name) {
		this.dirtyMark( AonUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Domain setDescription(String description) {
		this.dirtyMark( AonUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}

	public String getOwner() {
		return owner;
	}
	public Domain setOwner(String owner) {
		this.dirtyMark( AonUtils.notEquals(this.owner,owner) );
		this.owner = owner;
		return this;
	}
	
	public Integer getParent() {
		return parent;
	}
	public Domain setParent(Integer parent) {
		this.dirtyMark( AonUtils.notEquals(this.parent,parent) );
		this.parent = parent;
		return this;
	}	
	public boolean isParent() {
		return parent == null && isDomainManagement();
	}
	public boolean isChild() {
		return parent != null;
	}
	public boolean isStandalone() {
		return parent == null && !isDomainManagement();
	}
	
	public DomainType getType() {
		return type;
	}
	public Domain setType(DomainType type) {
		this.dirtyMark( AonUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public String getSubDomainSuffix() {
		return subDomainSuffix;
	}
	public Domain setSubDomainSuffix(String subDomainSuffix) {
		this.dirtyMark( AonUtils.notEquals(this.subDomainSuffix,subDomainSuffix) );
		this.subDomainSuffix = subDomainSuffix;
		return this;
	}
	
	public boolean isEnableHeredity() {
		return enableHeredity;
	}
	public Domain setEnableHeredity(boolean enableHeredity) {
		this.dirtyMark( AonUtils.notEquals(this.enableHeredity,enableHeredity) );
		this.enableHeredity = enableHeredity;
		return this;
	}
	
	public boolean isDomainManagement() {
		return domainManagement;
	}
	public Domain setDomainManagement(boolean domainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.domainManagement,domainManagement) );
		this.domainManagement = domainManagement;
		return this;
	}
	
	public boolean isDisableDomainManagement() {
		return disableDomainManagement;
	}
	public Domain setDisableDomainManagement(boolean disableDomainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.disableDomainManagement,disableDomainManagement) );
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public Domain setActive(boolean active) {
		this.dirtyMark( AonUtils.notEquals(this.active,active) );
		this.active = active;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	public Domain setScope(Integer scope) {
		this.dirtyMark( AonUtils.notEquals(this.scope,scope) );
		this.scope = scope;
		return this;
	}
	
	public Integer getMaxDefinedUsers() {
		return maxDefinedUsers;
	}
	public Domain setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.dirtyMark( AonUtils.notEquals(this.maxDefinedUsers,maxDefinedUsers) );
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Integer getMaxDocumentSize() {
		return maxDocumentSize;
	}
	public Domain setMaxDocumentSize(Integer maxDocumentSize) {
		this.dirtyMark( AonUtils.notEquals(this.maxDocumentSize,maxDocumentSize) );
		this.maxDocumentSize = maxDocumentSize;
		return this;
	}
	
	public Integer getMaxTotalDocumentSize() {
		return maxTotalDocumentSize;
	}
	public Domain setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.dirtyMark( AonUtils.notEquals(this.maxTotalDocumentSize,maxTotalDocumentSize) );
		this.maxTotalDocumentSize = maxTotalDocumentSize;
		return this;
	}
	
	public String getLastAccessUser() {
		return lastAccessUser;
	}
	public Domain setLastAccessUser(String lastAccessUser) {
		this.dirtyMark( AonUtils.notEquals(this.lastAccessUser,lastAccessUser) );
		this.lastAccessUser = lastAccessUser;
		return this;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public Domain setLastAccessDate(Date lastAccessDate) {
		this.dirtyMark( AonUtils.notEquals(this.lastAccessDate,lastAccessDate) );
		this.lastAccessDate = lastAccessDate; 
		return this;
	}
	
	public Date getExpirationDate() {
		return expirationDate;  
	}
	public Domain setExpirationDate(Date expirationDate) {
		this.dirtyMark( AonUtils.notEquals(this.expirationDate,expirationDate) );
		this.expirationDate = expirationDate;
		return this;
	}

	public Integer getAonCustomer() {
		return aonCustomer;
	}
	public Domain setAonCustomer(Integer aonCustomer) {
		this.dirtyMark( AonUtils.notEquals(this.aonCustomer,aonCustomer) );
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public AonStatus getAonStatus() {
		return aonStatus;
	}
	public Domain setAonStatus(AonStatus aonStatus) {
		this.dirtyMark( AonUtils.notEquals(this.aonStatus,aonStatus) );
		this.aonStatus = aonStatus;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public Domain setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	public Domain dirtyMark(boolean dirty) {
		this.dirty = isDirty() || dirty;
		return this;
	}

	// ---------------------------------------------------------- HasSelector<Invoice>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Domain setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	// ---------------------------------------------------------- HasAudit<Domain>
	public String getCreationUser() {
		return creationUser;
	}

	public Domain setCreationUser(String creationUser) {
		this.dirtyMark( AonUtils.notEquals(this.creationUser,creationUser) );
		this.creationUser = creationUser;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Domain setCreationDate(Date creationDate) {
		this.dirtyMark( AonUtils.notEquals(this.creationDate,creationDate) );
		this.creationDate = creationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}
	public Domain setModificationUser(String modificationUser) {
		this.dirtyMark( AonUtils.notEquals(this.modificationUser,modificationUser) );
		this.modificationUser = modificationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
	public Domain setModificationDate(Date modificationDate) {
		this.dirtyMark( AonUtils.notEquals(this.modificationDate,modificationDate) );
		this.modificationDate = modificationDate;
		return this;
	}
	
}
