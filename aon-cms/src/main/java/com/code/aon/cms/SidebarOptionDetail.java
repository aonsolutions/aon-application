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
@Table(name = "sidebar_option_i18n")
public class SidebarOptionDetail implements ITransferObject {

	private static final long serialVersionUID = 1451682786365251940L;

	private Integer id;

	private SidebarOption sidebar_option;
	
	private Language language;

	private String label;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sidebar_option", nullable = false)
	public SidebarOption getSidebar_option() {
		return sidebar_option;
	}

	public void setSidebar_option(SidebarOption sidebar_option) {
		this.sidebar_option = sidebar_option;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "label", nullable = false, length = 64)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SidebarOptionDetail o = (SidebarOptionDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.label, o.label)
				.append(this.sidebar_option, o.sidebar_option)
				.append(this.language, o.language)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(label)	
			.append(language)
			.append(sidebar_option)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}		
		
}
