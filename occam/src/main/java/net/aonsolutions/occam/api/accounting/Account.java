package net.aonsolutions.occam.api.accounting;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Account implements Serializable, HasSelector<Account>,HasDirtyFlag<Account>{

	private static final long serialVersionUID = 3940903705871158256L;
//	private static final String EMPTY_CODE = "?????????";

	private Integer id;
	private Integer domain;
	private String code;
	private String description;
	private String alias;
	private boolean active;

	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Account setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Account setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Account setCode(String code) {
		this.dirtyMark( AonObjectUtils.notEquals(this.code,code) );
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Account setDescription(String description) {
		this.dirtyMark( AonObjectUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public Account setAlias(String alias) {
		this.dirtyMark( AonObjectUtils.notEquals(this.alias,alias) );
		this.alias = alias;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Account setActive(boolean active) {
		this.dirtyMark( AonObjectUtils.notEquals(this.active,active) );
		this.active = active;
		return this;
	}

//	public boolean isEntryEnabled() {
//		return getLevel() == 5;
//	}
//	public int getLevel() {
//		return AonStringUtils.length(getCode()) > 4 ? 5 : AonStringUtils.length(getCode());
//	}
//
//	public String getFullName() {
//		return AonStringUtils.defaultIfEmpty(getCode(), EMPTY_CODE) + " - " +
//				AonStringUtils.defaultIfEmpty(getDescription(), EMPTY_CODE);
//	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Account setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Account setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Account other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
