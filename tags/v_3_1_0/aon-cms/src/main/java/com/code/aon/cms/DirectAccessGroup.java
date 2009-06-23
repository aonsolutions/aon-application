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

import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "direct_access_group")
public class DirectAccessGroup implements ITransferObject {

	private Integer id;

	private String alias;

	private boolean active = true;

	private Section section;
	
	private Set<MenuOption> options;

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}
	
	@OneToMany(mappedBy = "menu", cascade={CascadeType.REMOVE})
	public Set<MenuOption> getOptions() {
		return this.options;
	}

	public void setOptions( Set<MenuOption> options ) {
		this.options = options;
	}

}
