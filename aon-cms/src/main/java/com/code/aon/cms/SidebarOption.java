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
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "sidebar_option")
public class SidebarOption implements ITransferObject, IPositionObject {

	private Integer id;

	private String alias;

	private Sidebar sidebar;

	private SidebarType type;
	
	private ContentLevel level;

	private Integer ident;

	private boolean active = true;

	private Integer position = new Integer(0);

	private SidebarSide side;
	
	private Set<SidebarOptionDetail> details;

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
	@JoinColumn(name = "sidebar", nullable = false)
	public Sidebar getSidebar() {
		return sidebar;
	}

	public void setSidebar(Sidebar sidebar) {
		this.sidebar = sidebar;
	}

	@Column(name = "type")
	public SidebarType getType() {
		return this.type;
	}

	public void setType(SidebarType type) {
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

	@OneToMany(mappedBy = "sidebar_option", cascade={CascadeType.REMOVE})
	public Set<SidebarOptionDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<SidebarOptionDetail> details ) {
		this.details = details;
	}

	@Column(name = "side")
	public SidebarSide getSide() {
		return side;
	}

	public void setSide(SidebarSide side) {
		this.side = side;
	}

	
}
