package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.LinkedList;

public class RegistryFull implements Serializable {

	private static final long serialVersionUID = 1169435863658845445L;
	private Registry registry;
	private LinkedList<RegistryAddress> addresses;
	private LinkedList<RegistryMedia> medias;
	
	// ------------------------------------------ REGISTRY
	public Registry getRegistry() {
		return registry;
	}
	private Registry ensureRegistry() {
		if (this.registry == null) this.registry = new Registry(); 
		return this.registry;
	}
	public RegistryFull setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	
	public Integer getId() {
		return registry.getId();
	}
	public RegistryFull getId(Integer id) {
		ensureRegistry().setId(id);
		return this;
	}

	// ------------------------------------------ REGISTRY ADDRESS
	public LinkedList<RegistryAddress> getAddresses() {
		return addresses;
	}
	public RegistryFull setAddresses(LinkedList<RegistryAddress> addresses) {
		this.addresses = addresses;
		return this;
	}
	
	// ------------------------------------------ REGISTRY MEDIA
	public LinkedList<RegistryMedia> getMedias() {
		return medias;
	}
	public RegistryFull setMedias(LinkedList<RegistryMedia> medias) {
		this.medias = medias;
		return this;
	}
	private LinkedList<RegistryMedia> ensureMedias() {
		if (this.medias == null) this.medias = new LinkedList<RegistryMedia>(); 
		return this.medias;
	}
	public void addMedia(RegistryMedia media) {
		ensureMedias().add(media);
	}
	public boolean hasMedias() {
		return this.medias != null && !this.medias.isEmpty();
	}
	
	// ------------------------------------------ OTHER
	public boolean isNew() {
		return getId() == null;
	}
	
	
	
	
}
