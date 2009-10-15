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

import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "modular_page_option")
public class ModularPageOption implements ITransferObject, IPositionObject {

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

}
