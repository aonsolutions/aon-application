package com.code.aon.infoweb;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.infoweb.enumeration.WebInfoPageType;

@Entity
@Table(name="web_info_page")
public class WebInfoPage implements ITransferObject {
	
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

	@Column(name="type")
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

	@Column(name="active")
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

}