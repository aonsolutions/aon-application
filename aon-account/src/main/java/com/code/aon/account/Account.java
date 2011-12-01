package com.code.aon.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "account")
public class Account implements ITransferObject {

	private static final long serialVersionUID = 6518379784581410771L;

	private Integer id;
	private String code;
	private String description;
	private String alias;
	private boolean entryEnabled;

	private int level;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(nullable = false, length = 12)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@Column(nullable = false, length = 128)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = 32)
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(nullable = true)
	public boolean isEntryEnabled() {
		return entryEnabled;
	}
	public void setEntryEnabled(boolean entryEnabled) {
		this.entryEnabled = entryEnabled;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Transient
	public String getFullDescription() {
		return (getCode() + " " + getDescription());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Account o = (Account) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.code, o.code)
			.append(this.alias, o.alias)
			.append(this.description, o.description)
			.append(this.entryEnabled, o.entryEnabled)
			.append(this.level, o.level)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.code)
			.append(this.alias)
			.append(this.description)
			.append(this.entryEnabled)
			.append(this.id)
			.append(this.level)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}