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
	
	public int getId() {
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
	

}
