package net.aonsolutions.occam.api.accounting;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.metadata.AccountMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Account extends OccamEntity<AccountMetadata> {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private String code;
	private String description;
	private String alias;
	private boolean active;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Account markAsClean() {
		super.markAsClean();
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public Account setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AccountMetadata.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Account setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AccountMetadata.DOMAIN));
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Account setCode(String code) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.code,code), () -> markAsDirty(AccountMetadata.CODE));
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Account setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(AccountMetadata.DESCRIPTION));
		this.description = description;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public Account setAlias(String alias) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.alias,alias), () -> markAsDirty(AccountMetadata.ALIAS));
		this.alias = alias;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Account setActive(boolean active) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.active,active), () -> markAsDirty(AccountMetadata.ACTIVE));
		this.active = active;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Account other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}
