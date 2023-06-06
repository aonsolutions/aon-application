package net.aonsolutions.occam.api.config;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.metadata.ApplicationParameterMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class ApplicationParameter extends OccamEntity<ApplicationParameterMetadata> {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private AppParam name;
	private String value;


	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public ApplicationParameter markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public ApplicationParameter setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(ApplicationParameterMetadata.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public ApplicationParameter setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(ApplicationParameterMetadata.DOMAIN));
		this.domain = domain;
		return this;
	}

	public AppParam getName() {
		return name;
	}
	public ApplicationParameter setName(AppParam name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(ApplicationParameterMetadata.NAME));
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}
	public ApplicationParameter setValue(String value) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.value,value), () -> markAsDirty(ApplicationParameterMetadata.VALUE));
		this.value = value;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof ApplicationParameter other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}