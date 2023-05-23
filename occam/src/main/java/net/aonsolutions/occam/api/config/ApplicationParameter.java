package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class ApplicationParameter implements Serializable,HasDirtyFlag<ApplicationParameter> {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private AppParam name;
	private String value;

	private boolean dirty;

	public Integer getId() {
		return id;
	}
	public ApplicationParameter setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public ApplicationParameter setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}

	public AppParam getName() {
		return name;
	}
	public ApplicationParameter setName(AppParam name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}
	public ApplicationParameter setValue(String value) {
		this.dirtyMark( AonObjectUtils.notEquals(this.value,value) );
		this.value = value;
		return this;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public ApplicationParameter setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof ApplicationParameter other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
	
}