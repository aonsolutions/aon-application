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

import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "modular_page_option")
public class ModularPageOption implements ITransferObject, IPositionObject {

	private static final long serialVersionUID = 8111919153303825510L;

	private Integer id;

	private String alias;

	private ModularPage modular_page;

	private ModularPageOptionType type;
	
	private Integer ident;

	private boolean active = true;

	private Integer position = new Integer(0);
	
	private Set<ModularPageOptionDetail> details;

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "modular_page", nullable = false)
	public ModularPage getModular_page() {
		return modular_page;
	}

	public void setModular_page(ModularPage modular_page) {
		this.modular_page = modular_page;
	}

	@Column(name = "type")
	public ModularPageOptionType getType() {
		return this.type;
	}

	public void setType(ModularPageOptionType type) {
		this.type = type;
	}

	@Column(name = "ident")
	public Integer getIdent() {
		return this.ident;
	}

	public void setIdent(Integer ident) {
		this.ident = ident;
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

	@OneToMany(mappedBy = "modular_page_option", cascade={CascadeType.REMOVE})
	public Set<ModularPageOptionDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ModularPageOptionDetail> details ) {
		this.details = details;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ModularPageOption o = (ModularPageOption) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.ident, o.ident)
				.append(this.modular_page, o.modular_page)
				.append(this.position, o.position)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(id)	
			.append(ident)			
			.append(modular_page)			
			.append(position)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

}
