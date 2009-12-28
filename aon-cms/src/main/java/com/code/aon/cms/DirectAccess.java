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
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.util.IActivableObject;
import com.code.aon.cms.util.IReferenceObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "direct_access")
public class DirectAccess implements IActivableObject, IPositionObject, IReferenceObject {

	private static final long serialVersionUID = -2407857395297539362L;

	private Integer id;

	private String alias;

	private DirectAccessGroup directAccessGroup;

	private PageType type;
	
	private ContentLevel level;

	private Integer ident;

	private boolean active = true;

	private Integer position = new Integer(0);

	private String image;

	private Set<DirectAccessDetail> details;

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
	@JoinColumn(name = "direct_access_group", nullable = false)
	public DirectAccessGroup getDirectAccessGroup() {
		return directAccessGroup;
	}

	public void setDirectAccessGroup(DirectAccessGroup directAccessGroup) {
		this.directAccessGroup = directAccessGroup;
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

	@OneToMany(mappedBy = "directAccess", cascade={CascadeType.REMOVE})
	public Set<DirectAccessDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<DirectAccessDetail> details ) {
		this.details = details;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DirectAccess o = (DirectAccess) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.directAccessGroup, o.directAccessGroup)
				.append(this.ident, o.ident)				
				.append(this.image, o.image)
				.append(this.level, o.level)
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
			.append(directAccessGroup)
			.append(id)	
			.append(ident)
			.append(image)
			.append(level)
			.append(position)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
}
