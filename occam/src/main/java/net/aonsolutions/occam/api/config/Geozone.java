package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Geozone implements Serializable, HasSelector<Geozone>, HasDirtyFlag<Geozone> {

	private static final long serialVersionUID = 100071842215890945L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String code;
	private boolean system;
	
	private boolean dirty;
	private boolean selected;
	

	public Integer getId() {
		return id;
	}
	public Geozone setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Geozone setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Geozone setCode(String code) {
		this.dirtyMark( AonObjectUtils.notEquals(this.code,code) );
		this.code = code;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Geozone setName(String name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}
	
	public boolean isSystem() {
		return system;
	}
	public Geozone setSystem(boolean system) {
		this.dirtyMark( AonObjectUtils.notEquals(this.system,system) );
		this.system = system;
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Geozone setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Geozone setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Geozone other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
