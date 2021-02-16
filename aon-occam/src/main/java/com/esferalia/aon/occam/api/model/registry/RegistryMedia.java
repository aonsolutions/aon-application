package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.MediaType;

public class RegistryMedia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Registry registry;
	private MediaType media;
	private String value;
	private String comment;
	private Boolean administrative;
	private Boolean commercial;
	private Boolean technical;
	private Integer raddress;
	
	public RegistryMedia() {
	
	}
	
	public RegistryMedia setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public RegistryMedia setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public RegistryMedia setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	
	public RegistryMedia setMedia(MediaType media) {
		this.media = media;
		return this;
	}
	
	public RegistryMedia setValue(String value) {
		this.value = value;
		return this;
	}
	
	public RegistryMedia setComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public Integer getDomain() {
		return domain;
	}
	
	public Registry getRegistry() {
		return registry;
	}
	public MediaType getMedia() {
		return media;
	}
	public String getValue() {
		return value;
	}
	public String getComment() {
		return comment;
	}

	public Boolean getAdministrative() {
		return administrative;
	}

	public byte getAdministrativeValue() {
		return getAdministrative() ? (byte) 1 : 0;
	}

	public RegistryMedia setAdministrative(Boolean administrative) {
		this.administrative = administrative;
		return this;
	}

	public Boolean getCommercial() {
		return commercial;
	}
	
	public byte getCommercialValue() {
		return getCommercial() ? (byte) 1 : 0;
	}

	public RegistryMedia setCommercial(Boolean commercial) {
		this.commercial = commercial;
		return this;
	}

	public Boolean getTechnical() {
		return technical;
	}
	
	public byte getTechnicalValue() {
		return getTechnical() ? (byte) 1 : 0;
	}

	public RegistryMedia setTechnical(Boolean technical) {
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
