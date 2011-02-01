package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "menu")
public class Menu implements ITransferObject {

	private static final long serialVersionUID = -8489778407102690539L;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Menu o = (Menu) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alias, o.alias)
				.append(this.defaultMenu, o.defaultMenu)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(defaultMenu)
			.append(id)	
			.append(type)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
