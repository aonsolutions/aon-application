package net.aonsolutions.occam.api.accounting;

import java.util.Objects;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Account extends OccamEntity {

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
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Account setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AonNames.DOMAIN));
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Account setCode(String code) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.code,code), () -> markAsDirty(AonNames.CODE));
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Account setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(AonNames.DESCRIPTION));
		this.description = description;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public Account setAlias(String alias) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.alias,alias), () -> markAsDirty(AonNames.ALIAS));
		this.alias = alias;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Account setActive(boolean active) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.active,active), () -> markAsDirty(AonNames.ACTIVE));
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
