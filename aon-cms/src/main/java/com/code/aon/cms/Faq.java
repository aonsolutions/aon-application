package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "faq")
public class Faq implements IActivableObject, IPositionObject {

	private static final long serialVersionUID = 4305040592552684635L;

	private Integer id;

	private String alias;

	private boolean active = true;

	private Integer position = new Integer(0);
	
	private FaqCategory faqCategory;

	private Set<FaqDetail> details;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}


	@OneToMany(mappedBy = "faq", cascade={CascadeType.REMOVE})
	public Set<FaqDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<FaqDetail> details ) {
		this.details = details;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "faq_category", nullable = false)
	public FaqCategory getFaqCategory() {
		return faqCategory;
	}

	public void setFaqCategory(FaqCategory faqCategory) {
		this.faqCategory = faqCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Faq o = (Faq) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.faqCategory, o.faqCategory)
				.append(this.position, o.position)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(faqCategory)
			.append(id)	
			.append(position)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
