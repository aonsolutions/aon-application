package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "menu")
public class Menu implements ITransferObject {

	private Integer id;

	private String alias;

	private MenuType type;

	private boolean defaultMenu;

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
	
	@Column(name = "type", nullable = false)
	public MenuType getType() {
		return this.type;
	}

	public void setType(MenuType type) {
		this.type = type;
	}

	@Column(name = "defaultMenu", nullable = false)
	public boolean isDefaultMenu() {
		return defaultMenu;
	}

	public void setDefaultMenu(boolean defaultMenu) {
		this.defaultMenu = defaultMenu;
	}

	@OneToMany(mappedBy = "menu", cascade={CascadeType.REMOVE})
	public Set<MenuOption> getOptions() {
		return this.options;
	}

	public void setOptions( Set<MenuOption> options ) {
		this.options = options;
	}

}
