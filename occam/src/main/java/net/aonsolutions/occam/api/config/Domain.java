package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonNumberUtils;
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
	private Domain parent;
	private DomainType type;
	private String subDomainSuffix;
	
	private Boolean enableHeredity;
	private Boolean domainManagement;
	private Boolean disableDomainManagement;
	private Boolean active;
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
	
	private List<User> users; 
	
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

	public Optional<String> getOwner() {
		return Optional.ofNullable(owner);
	}
	public Domain setOwner(String owner) {
		this.dirtyMark( AonUtils.notEquals(this.owner,owner) );
		this.owner = owner;
		return this;
	}
	
	public Optional<Domain> getParent() {
		return Optional.ofNullable(parent);
	}
	public Domain setParent(Domain parent) {
		this.dirtyMark( AonUtils.notEquals(this.parent,parent) );
		this.parent = parent;
		return this;
	}
	
	public Optional<DomainType> getType() {
		return Optional.ofNullable(type);
	}
	public Domain setType(DomainType type) {
		this.dirtyMark( AonUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public Optional<String> getSubDomainSuffix() {
		return Optional.ofNullable(subDomainSuffix);
	}
	public Domain setSubDomainSuffix(String subDomainSuffix) {
		this.dirtyMark( AonUtils.notEquals(this.subDomainSuffix,subDomainSuffix) );
		this.subDomainSuffix = subDomainSuffix;
		return this;
	}
	
	public Optional<Boolean> isEnableHeredity() {
		return Optional.ofNullable(enableHeredity);
	}
	public Domain setEnableHeredity(Boolean enableHeredity) {
		this.dirtyMark( AonUtils.notEquals(this.enableHeredity,enableHeredity) );
		this.enableHeredity = enableHeredity;
		return this;
	}
	
	public Optional<Boolean> isDomainManagement() {
		return Optional.ofNullable(domainManagement);
	}
	public Domain setDomainManagement(Boolean domainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.domainManagement,domainManagement) );
		this.domainManagement = domainManagement;
		return this;
	}
	
	public Optional<Boolean> isDisableDomainManagement() {
		return Optional.ofNullable(disableDomainManagement);
	}
	public Domain setDisableDomainManagement(Boolean disableDomainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.disableDomainManagement,disableDomainManagement) );
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}
	
	public Optional<Boolean> isActive() {
		return Optional.ofNullable(active);
	}
	public Domain setActive(Boolean active) {
		this.dirtyMark( AonUtils.notEquals(this.active,active) );
		this.active = active;
		return this;
	}
	
	public Optional<Integer> getScope() {
		return Optional.ofNullable(scope);
	}
	public Domain setScope(Integer scope) {
		this.dirtyMark( AonUtils.notEquals(this.scope,scope) );
		this.scope = scope;
		return this;
	}
	
	public Optional<Integer> getMaxDefinedUsers() {
		return Optional.ofNullable(maxDefinedUsers);
	}
	public Domain setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.dirtyMark( AonUtils.notEquals(this.maxDefinedUsers,maxDefinedUsers) );
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Optional<Integer> getMaxDocumentSize() {
		return Optional.ofNullable(maxDocumentSize);
	}
	public Domain setMaxDocumentSize(Integer maxDocumentSize) {
		this.dirtyMark( AonUtils.notEquals(this.maxDocumentSize,maxDocumentSize) );
		this.maxDocumentSize = maxDocumentSize;
		return this;
	}
	
	public Optional<Integer> getMaxTotalDocumentSize() {
		return Optional.ofNullable(maxTotalDocumentSize);
	}
	public Domain setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.dirtyMark( AonUtils.notEquals(this.maxTotalDocumentSize,maxTotalDocumentSize) );
		this.maxTotalDocumentSize = maxTotalDocumentSize;
		return this;
	}
	
	public Optional<String> getLastAccessUser() {
		return Optional.ofNullable(lastAccessUser);
	}
	public Domain setLastAccessUser(String lastAccessUser) {
		this.dirtyMark( AonUtils.notEquals(this.lastAccessUser,lastAccessUser) );
		this.lastAccessUser = lastAccessUser;
		return this;
	}

	public Optional<Date> getLastAccessDate() {
		return Optional.ofNullable(lastAccessDate);
	}
	public Domain setLastAccessDate(Date lastAccessDate) {
		this.dirtyMark( AonUtils.notEquals(this.lastAccessDate,lastAccessDate) );
		this.lastAccessDate = lastAccessDate; 
		return this;
	}
	
	public Optional<Date> getExpirationDate() {
		return Optional.ofNullable(expirationDate);  
	}
	public Domain setExpirationDate(Date expirationDate) {
		this.dirtyMark( AonUtils.notEquals(this.expirationDate,expirationDate) );
		this.expirationDate = expirationDate;
		return this;
	}

	public Optional<Integer> getAonCustomer() {
		return Optional.ofNullable(aonCustomer);
	}
	public Domain setAonCustomer(Integer aonCustomer) {
		this.dirtyMark( AonUtils.notEquals(this.aonCustomer,aonCustomer) );
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public Optional<AonStatus> getAonStatus() {
		return Optional.ofNullable(aonStatus);
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
	public Optional<String> getCreationUser() {
		return Optional.ofNullable(creationUser);
	}

	public Domain setCreationUser(String creationUser) {
		this.dirtyMark( AonUtils.notEquals(this.creationUser,creationUser) );
		this.creationUser = creationUser;
		return this;
	}

	public Optional<Date> getCreationDate() {
		return Optional.ofNullable(creationDate);
	}

	public Domain setCreationDate(Date creationDate) {
		this.dirtyMark( AonUtils.notEquals(this.creationDate,creationDate) );
		this.creationDate = creationDate;
		return this;
	}

	public Optional<String> getModificationUser() {
		return Optional.ofNullable(modificationUser);
	}
	public Domain setModificationUser(String modificationUser) {
		this.dirtyMark( AonUtils.notEquals(this.modificationUser,modificationUser) );
		this.modificationUser = modificationUser;
		return this;
	}

	public Optional<Date> getModificationDate() {
		return Optional.ofNullable(modificationDate);
	}
	public Domain setModificationDate(Date modificationDate) {
		this.dirtyMark( AonUtils.notEquals(this.modificationDate,modificationDate) );
		this.modificationDate = modificationDate;
		return this;
	}
	// ---------------------------------------------------------- 
	public Optional<List<User>> getUsers() {
		return Optional.ofNullable(users);
	}
	public Domain setUsers(List<User> users) {
		this.users = users;
		return this;
	}
	public Domain addUsers(List<User> users) {
		if ( getUsers().isPresent() ) {
			getUsers().get().addAll(users);
		} else {
			setUsers(users);
		}
		return this;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Domain other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + (id == null ? 0 : id.hashCode());
	}
}
