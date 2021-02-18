package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.MediaType;

public class RegistryMedia implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private MediaType media;
	private String value;
	private String comment;
	private boolean administrative;
	private boolean commercial;
	private boolean technical;
	private Integer raddress;
	
	public Integer getId() {
		return id;
	}
	public RegistryMedia setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public RegistryMedia setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public RegistryMedia setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public MediaType getMedia() {
		return media;
	}
	public RegistryMedia setMedia(MediaType media) {
		this.media = media;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public RegistryMedia setValue(String value) {
		this.value = value;
		return this;
	}
	
	public String getComment() {
		return comment;
	}
	public RegistryMedia setComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public boolean isAdministrative() {
		return administrative;
	}
	public RegistryMedia setAdministrative(boolean administrative) {
		this.administrative = administrative;
		return this;
	}

	public boolean isCommercial() {
		return commercial;
	}
	public RegistryMedia setCommercial(boolean commercial) {
		this.commercial = commercial;
		return this;
	}

	public boolean isTechnical() {
		return technical;
	}
	public RegistryMedia setTechnical(boolean technical) {
		this.technical = technical;
		return this;
	}
	
	public Integer getRaddress() {
		return raddress;
	}
	public RegistryMedia setRaddress(Integer raddress) {
		this.raddress = raddress;
		return this;
	}
}
