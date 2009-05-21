package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents a Balance.
 */
@Entity
@Table(name = "balance")
public class Balance implements ITransferObject {
		
	private static final long serialVersionUID = -5577162507185474823L;
	
	private Integer id;
	private String name;
	private boolean removable;
	private Integer type;


	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="name", length=64, nullable=false)
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="removable")
	public boolean getRemovable() {
		return removable;
	}
	public void setRemovable(boolean removable) {
		this.removable = removable;
	}

	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Balance o = (Balance) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getName(), o.getName())
			.append(this.getRemovable(), o.getRemovable())
			.append(this.getType(), o.getType())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getName())
			.append(this.getRemovable())
			.append(this.getType())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}