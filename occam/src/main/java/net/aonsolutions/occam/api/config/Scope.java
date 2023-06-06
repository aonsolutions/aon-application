package net.aonsolutions.occam.api.config;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.metadata.ScopeMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Scope extends OccamEntity<ScopeMetadata>  {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private Integer domain;
	private String description;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Scope markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Scope setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(ScopeMetadata.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Scope setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(ScopeMetadata.DOMAIN));
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Scope setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(ScopeMetadata.DESCRIPTION));
		this.description = description;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Scope other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
