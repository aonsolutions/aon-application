package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class RegistryMedia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int domain;
	private Registry registry;
	private byte media;
	private String value;
	private String comment;
	private Byte administrative;
	private Byte commercial;
	private Byte technical;
	private Integer raddress;
	
	public RegistryMedia() {
	
	}
	
	public RegistryMedia setId(int id) {
		this.id = id;
		return this;
	}
	
	public RegistryMedia setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public RegistryMedia setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	
	public RegistryMedia setMedia(byte media) {
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
	public int getDomain() {
		return domain;
	}
	
	public Registry getRegistry() {
		return registry;
	}
	public byte getMedia() {
		return media;
	}
	public String getValue() {
		return value;
	}
	public String getComment() {
		return comment;
	}

	public Byte getAdministrative() {
		return administrative;
	}

	public RegistryMedia setAdministrative(Byte administrative) {
		this.administrative = administrative;
		return this;
	}

	public Byte getCommercial() {
		return commercial;
	}

	public RegistryMedia setCommercial(Byte commercial) {
		this.commercial = commercial;
		return this;
	}

	public Byte getTechnical() {
		return technical;
	}

	public RegistryMedia setTechnical(Byte technical) {
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
