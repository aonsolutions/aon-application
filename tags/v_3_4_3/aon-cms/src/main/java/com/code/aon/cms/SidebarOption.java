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

import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "sidebar_option")
public class SidebarOption implements ITransferObject, IPositionObject {

	private static final long serialVersionUID = 7985304255268260197L;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SidebarOption o = (SidebarOption) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.ident, o.ident)
				.append(this.level, o.level)
				.append(this.position, o.position)
				.append(this.side, o.side)
				.append(this.sidebar, o.sidebar)
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
			.append(level)
			.append(position)
			.append(side)
			.append(sidebar)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}		
	
}
