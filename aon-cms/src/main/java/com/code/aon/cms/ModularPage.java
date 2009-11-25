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

import com.code.aon.cms.enumeration.ModularType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "modular_page")
public class ModularPage implements ITransferObject {

	private static final long serialVersionUID = 2746610997632935339L;

	private Integer id;

	private String alias;
	
	private ModularType modularType;
	
	private boolean homepage;
	
	private boolean active = true;

	private Section section;
	
	private Set<ModularPageOption> options;
	
	private Set<ModularPageDetail> details;

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

	@Column(name = "type")
	public ModularType getModularType() {
		return modularType;
	}

	public void setModularType(ModularType modularType) {
		this.modularType = modularType;
	}

	@Column(name = "homepage")
	public boolean isHomepage() {
		return homepage;
	}

	public void setHomepage(boolean homepage) {
		this.homepage = homepage;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "modular_page", cascade={CascadeType.REMOVE})
	public Set<ModularPageOption> getOptions() {
		return this.options;
	}

	public void setOptions( Set<ModularPageOption> options ) {
		this.options = options;
	}
	
	@OneToMany(mappedBy = "modular_page", cascade={CascadeType.REMOVE})
	public Set<ModularPageDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ModularPageDetail> details ) {
		this.details = details;
	}	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ModularPage o = (ModularPage) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.homepage, o.homepage)
				.append(this.modularType, o.modularType)
				.append(this.section, o.section)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(homepage)
			.append(id)	
			.append(modularType)			
			.append(section)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

}
