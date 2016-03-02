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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setDomain(int domain) {
		this.domain = domain;
	}
	
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public void setMedia(byte media) {
		this.media = media;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
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
