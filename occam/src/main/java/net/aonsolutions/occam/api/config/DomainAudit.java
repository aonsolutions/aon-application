package net.aonsolutions.occam.api.config;

import java.util.Date;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.watson.server.AonObjectUtils;

public class DomainAudit extends Audit implements HasDirtyFlag<DomainAudit>{

	private static final long serialVersionUID = 6995049003419969497L;
	
	private String lastAccessUser;
	private Date lastAccessDate;
	
	private boolean dirty;

	public Optional<String> getLastAccessUser() {
		return Optional.ofNullable(lastAccessUser);
	}

	public DomainAudit setLastAccessUser(String lastAccessUser) {
		this.dirtyMark( AonObjectUtils.notEquals(this.lastAccessUser,lastAccessUser) );
		this.lastAccessUser = lastAccessUser;
		return this;
	}

	public Optional<Date> getLastAccessDate() {
		return Optional.ofNullable(lastAccessDate);
	}

	public DomainAudit setLastAccessDate(Date lastAccessDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.lastAccessDate,lastAccessDate) );
		this.lastAccessDate = lastAccessDate;
		return this;
	}

	@Override
	public DomainAudit setCreationUser(String creationUser) {
		super.setCreationUser(creationUser);
		return this;
	}
	@Override
	public DomainAudit setCreationDate(Date creationDate) {
		super.setCreationDate(creationDate);
		return this;
	}
	@Override
	public DomainAudit setModificationUser(String modificationUser) {
		super.setModificationUser(modificationUser);
		return this;
	}
	@Override
	public DomainAudit setModificationDate(Date modificationDate) {
		super.setModificationDate(modificationDate);
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public DomainAudit setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
}
