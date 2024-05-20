package net.aonsolutions.invofox.model;

import java.io.Serializable;

public class OCRApiKey implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String key;
	private String creation;
	private boolean active;

	public OCRApiKey() {

	}
	
	public String getId() {
		return id;
	}
	
	public OCRApiKey setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public OCRApiKey setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getKey() {
		return key;
	}
	
	public OCRApiKey setKey(String key) {
		this.key = key;
		return this;
	}
	
	public String getCreation() {
		return creation;
	}
	
	public OCRApiKey setCreation(String creation) {
		this.creation = creation;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public OCRApiKey setActive(boolean active) {
		this.active = active;
		return this;
	}
	
}
