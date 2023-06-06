package net.aonsolutions.occam.api.config;

import java.util.Objects;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Activity extends OccamEntity {

	private static final long serialVersionUID = 8605536086019198332L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private String epigraph;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Activity markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Activity setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Activity setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AonNames.DOMAIN));
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public Activity setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(AonNames.DESCRIPTION));
		this.description = description;
		return this;
	}
	
	public String getEpigraph() {
		return epigraph;
	}
	public Activity setEpigraph(String epigraph) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.epigraph,epigraph), () -> markAsDirty(AonNames.EPIGRAPH));
		this.epigraph = epigraph;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Activity other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}

