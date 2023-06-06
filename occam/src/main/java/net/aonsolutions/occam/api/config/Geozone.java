package net.aonsolutions.occam.api.config;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.metadata.GeozoneMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Geozone extends OccamEntity<GeozoneMetadata> {

	private static final long serialVersionUID = 100071842215890945L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String code;
	private boolean system;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Geozone markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Geozone setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(GeozoneMetadata.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Geozone setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(GeozoneMetadata.DOMAIN));
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Geozone setCode(String code) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.code,code), () -> markAsDirty(GeozoneMetadata.CODE));
		this.code = code;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Geozone setName(String name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(GeozoneMetadata.NAME));
		this.name = name;
		return this;
	}
	
	public boolean isSystem() {
		return system;
	}
	public Geozone setSystem(boolean system) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.system,system), () -> markAsDirty(GeozoneMetadata.SYSTEM));
		this.system = system;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Geozone other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
