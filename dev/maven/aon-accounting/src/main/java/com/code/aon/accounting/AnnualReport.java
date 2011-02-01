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

@Entity
@Table(name = "annual_report")
public class AnnualReport implements ITransferObject {
		
	private static final long serialVersionUID = -5577162507185474823L;
	
	private Integer id;
	private String name;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final AnnualReport o = (AnnualReport) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getName(), o.getName())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getName())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append(this.getId())
		.append(this.getName()).toString();
	}

}