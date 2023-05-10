package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.occam.api.HasSelector;

public class Scope implements Serializable, HasSelector<Scope> {

	private static final long serialVersionUID = 2356532157666489635L;
	
	private Integer id;
	private Integer domain;
	private String description;

	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Scope setId(Integer id) {
		this.dirtyMark( AonUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Scope setDomain(Integer domain) {
		this.dirtyMark( AonUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Scope setDescription(String description) {
		this.dirtyMark( AonUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}
	public Scope setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	public Scope dirtyMark(boolean dirty) {
		this.dirty = isDirty() || dirty;
		return this;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Scope setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Scope other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
