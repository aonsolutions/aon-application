package com.code.aon.infoweb;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.infoweb.enumeration.WebInfoPageType;

@Entity
@Table(name="web_info_page")
public class WebInfoPage implements ITransferObject {
	
	private static final long serialVersionUID = -4628246222559589905L;

	private Integer id;
	
	private String name;
	
	private WebInfoPageType type;
		
	private Integer position = 0;
	
	private boolean active = true;

	private Set<WebInfoPageDetail> details;

	private Set<WebInfoPageResource> resources;

	@Id
	@GeneratedValue
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="name",length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="type", nullable=false)
	public WebInfoPageType getType() {
		return type;
	}

	public void setType(WebInfoPageType type) {
		this.type = type;
	}

	@Column(name="position")
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Column(name="active", nullable=false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "webInfoPage", cascade={CascadeType.REMOVE})
	public Set<WebInfoPageDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<WebInfoPageDetail> details) {
		this.details = details;
	}

	@OneToMany(mappedBy = "webInfoPage", cascade={CascadeType.REMOVE})
	public Set<WebInfoPageResource> getResources() {
		return resources;
	}

	public void setResources(Set<WebInfoPageResource> resources) {
		this.resources = resources;
	}

    @Transient
	public boolean isDetailed() {
		if (getType()== WebInfoPageType.GALLERY) return true;
		if (getType()== WebInfoPageType.GENERIC) return true;
		if (getType()== WebInfoPageType.LOCATION) return true;
		return false;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WebInfoPage o = (WebInfoPage) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.name, o.name)
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
			.append(id)
			.append(name)
			.append(position)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}