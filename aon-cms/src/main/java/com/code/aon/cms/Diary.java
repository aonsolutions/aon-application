package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "diary")
public class Diary implements ITransferObject {

	private static final long serialVersionUID = 2700898975367880268L;

	private Integer id;

	private boolean categories;

	private boolean pastEvents;
	
	private Section section;
	
	private Section elementSection;	

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "is_categories", nullable = false)
	public boolean isCategories() {
		return categories;
	}

	public void setCategories(boolean categories) {
		this.categories = categories;
	}

	@Column(name = "is_past_events", nullable = false)
	public boolean isPastEvents() {
		return pastEvents;
	}

	public void setPastEvents(boolean pastEvents) {
		this.pastEvents = pastEvents;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "elementSection")
	public Section getElementSection() {
		return elementSection;
	}

	public void setElementSection(Section elementSection) {
		this.elementSection = elementSection;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Diary o = (Diary) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.categories, o.categories)
				.append(this.elementSection, o.elementSection)
				.append(this.pastEvents, o.pastEvents)
				.append(this.section, o.section)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(categories)
			.append(elementSection)
			.append(id)	
			.append(pastEvents)
			.append(section)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
