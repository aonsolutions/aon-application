package com.code.aon.company;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="agreement_level_category")
public class AgreementLevelCategory implements ITransferObject {

	private static final long serialVersionUID = -2038737275830609769L;

	private Integer id;
	private AgreementLevel level;
	private String description;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn( name="agreement_level", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CATEGORY_AGREEMENT_LEVEL")
	@Index(name = "FK_CATEGORY_AGREEMENT_LEVEL")
	public AgreementLevel getLevel() {
		return level;
	}
	public void setLevel(AgreementLevel level) {
		this.level = level;
	}
	
	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementLevelCategory o = (AgreementLevelCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.level, o.level)			
				.append(this.description, o.description)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(level)
			.append(description)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
