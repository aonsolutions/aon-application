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

import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "menu_option")
public class MenuOption implements ITransferObject, IPositionObject {

	private Integer id;

	private String alias;

	private Menu menu;

	private boolean separator = false;

	private PageType type;
	
	private ContentLevel level;

	private Integer ident;

	private boolean active = true;

	private Integer position = new Integer(0);

	private Set<MenuOptionDetail> details;

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
	@JoinColumn(name = "menu", nullable = false)
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Column(name = "sep")
	public boolean isSeparator() {
		return separator;
	}

	public void setSeparator(boolean separator) {
		this.separator = separator;
	}

	@Column(name = "type")
	public PageType getType() {
		return this.type;
	}

	public void setType(PageType type) {
		this.type = type;
	}

	@Column(name = "level")
	public ContentLevel getLevel() {
		return this.level;
	}

	public void setLevel(ContentLevel level) {
		this.level = level;
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

	@OneToMany(mappedBy = "menu_option", cascade={CascadeType.REMOVE})
	public Set<MenuOptionDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<MenuOptionDetail> details ) {
		this.details = details;
	}

}
