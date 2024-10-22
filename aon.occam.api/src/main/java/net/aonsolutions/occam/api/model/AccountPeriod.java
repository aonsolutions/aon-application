package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.AccountPeriodMetadata;
import net.aonsolutions.occam.api.model.type.AccountPeriodStatus;

public class AccountPeriod extends AonEntity<AccountPeriodMetadata> implements Serializable, HasAudit {

	private static final long serialVersionUID = -1686197262238560337L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date initiationDate;
	private Date deadline;
	private AccountPeriodStatus status;
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	private boolean defaultPeriod;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public AccountPeriod markAsClean() {
		super.markAsClean();
		return this; 
	}
	@Override
	public AccountPeriod setSelected(boolean selected) {
		super.setSelected(selected);
		return this;
	}
	@Override
	public AccountPeriod setDeleted(boolean deleted) {
		super.setDeleted(deleted);
		return this;
	}
	
	public Integer getId() {
		return this.id;
	}
	public AccountPeriod setId(Integer id) {
		checkIfDirty( this.id,id, AccountPeriodMetadata.ID);
		this.id = id;
		return this; 
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountPeriod setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, AccountPeriodMetadata.DOMAIN);
		this.domain = domain;
		return this; 
	}

	public String getName() {
		return this.name;
	}
	public AccountPeriod setName(String name) {
		checkIfDirty( this.name,name, AccountPeriodMetadata.NAME);
		this.name = name;
		return this; 
	}

	public Date getInitiationDate() {
		return this.initiationDate;
	}
	public AccountPeriod setInitiationDate(Date initiationDate) {
		checkIfDirty( this.initiationDate,initiationDate, AccountPeriodMetadata.INITIATION_DATE);
		this.initiationDate = initiationDate;
		return this; 
	}

	public Date getDeadline() {
		return this.deadline;
	}
	public AccountPeriod setDeadline(Date deadline) {
		checkIfDirty( this.deadline,deadline, AccountPeriodMetadata.DEADLINE);
		this.deadline = deadline;
		return this; 
	}

	public AccountPeriodStatus getStatus() {
		return this.status;
	}
	public AccountPeriod setStatus(AccountPeriodStatus status) {
		checkIfDirty( this.status,status, AccountPeriodMetadata.STATUS);
		this.status = status;
		return this; 
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public AccountPeriod setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public AccountPeriod setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public AccountPeriod setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public AccountPeriod setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public boolean isActive() {
		return this.status == null || this.status.isActive();
	}
	public boolean isClosed() {
		return this.status == AccountPeriodStatus.CLOSED;
	}
	
	public boolean isDefaultPeriod() {
		return defaultPeriod;
	}
	public AccountPeriod setDefaultPeriod(boolean defaultPeriod) {
		this.defaultPeriod = defaultPeriod;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AccountPeriod other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
