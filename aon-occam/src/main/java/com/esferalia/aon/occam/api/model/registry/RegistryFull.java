package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.LinkedList;

public class RegistryFull<R extends Registry> implements Serializable {

	private static final long serialVersionUID = 1169435863658845445L;
	private R registry;
	private LinkedList<RegistryAddress> addresses;
	private LinkedList<RegistryMedia> medias;
	
	// ------------------------------------------ REGISTRY
	public R getRegistry() {
		return registry;
	}
	public RegistryFull<R> setRegistry(R registry) {
		this.registry = registry;
		return this;
	}
	
	public Integer getId() {
		return registry==null?null:registry.getId();
	}

	public Integer getDomain() {
		return registry==null || registry.getDomain() == null? null : registry.getDomain().getId();
	}

	// ------------------------------------------ REGISTRY ADDRESS
	public LinkedList<RegistryAddress> getAddresses() {
		return addresses;
	}
	public RegistryFull<R> setAddresses(LinkedList<RegistryAddress> addresses) {
		this.addresses = addresses;
		return this;
	}
	private LinkedList<RegistryAddress> ensureAddresses() {
		if (this.addresses == null) this.addresses = new LinkedList<RegistryAddress>(); 
		return this.addresses;
	}
	public RegistryFull<R> addAddress(RegistryAddress address) {
		ensureAddresses().add(address);
		return this;
	}
	public boolean hasAddresses() {
		return this.addresses != null && this.addresses.size() > 0;
	}
	
	// ------------------------------------------ REGISTRY MEDIA
	public LinkedList<RegistryMedia> getMedias() {
		return medias;
	}
	public RegistryFull<R> setMedias(LinkedList<RegistryMedia> medias) {
		this.medias = medias;
		return this;
	}
	private LinkedList<RegistryMedia> ensureMedias() {
		if (this.medias == null) this.medias = new LinkedList<RegistryMedia>(); 
		return this.medias;
	}
	public RegistryFull<R> addMedia(RegistryMedia media) {
		ensureMedias().add(media);
		return this;
	}
	public boolean hasMedias() {
		return this.medias != null && !this.medias.isEmpty();
	}
	
	// ------------------------------------------ OTHER
	public boolean isNew() {
		return getId() == null;
	}
	
	protected void initializeChilds() {
//		ensureAddresses().add( new RegistryAddress() );
//		ensureMedias().add(new RegistryMedia().setMedia(MediaType.FIXED_PHONE));
//		ensureMedias().add(new RegistryMedia().setMedia(MediaType.CELLULAR));
//		ensureMedias().add(new RegistryMedia().setMedia(MediaType.FAX));
//		ensureMedias().add(new RegistryMedia().setMedia(MediaType.EMAIL));
//		ensureMedias().add(new RegistryMedia().setMedia(MediaType.WEB));
	}
	
	
	
}
