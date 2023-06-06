package net.aonsolutions.occam.api.config;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.metadata.DomainAuditMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class DomainAudit extends OccamEntity<DomainAuditMetadata> {

	private static final long serialVersionUID = 6995049003419969497L;

	private Integer id;
	private String lastAccessUser;
	private Date lastAccessDate;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public DomainAudit markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public DomainAudit setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(DomainAuditMetadata.ID));
		this.id = id;
		return this;
	}

	public Optional<String> getLastAccessUser() {
		return Optional.ofNullable(lastAccessUser);
	}
	public DomainAudit setLastAccessUser(String lastAccessUser) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.lastAccessUser,lastAccessUser), () -> markAsDirty(DomainAuditMetadata.LAST_ACCESS_USER));
		this.lastAccessUser = lastAccessUser;
		return this;
	}

	public Optional<Date> getLastAccessDate() {
		return Optional.ofNullable(lastAccessDate);
	}

	public DomainAudit setLastAccessDate(Date lastAccessDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.lastAccessDate,lastAccessDate), () -> markAsDirty(DomainAuditMetadata.LAST_ACCESS_DATE));
		this.lastAccessDate = lastAccessDate;
		return this;
	}

	public Optional<String> getCreationUser() {
		return Optional.ofNullable(creationUser);
	}
	public DomainAudit setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Optional<Date> getCreationDate() {
		return Optional.ofNullable(creationDate);
	}
	public DomainAudit setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public Optional<String> getModificationUser() {
		return Optional.ofNullable(modificationUser);
	}
	public DomainAudit setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Optional<Date> getModificationDate() {
		return Optional.ofNullable(modificationDate);
	}
	public DomainAudit setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Registry other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
