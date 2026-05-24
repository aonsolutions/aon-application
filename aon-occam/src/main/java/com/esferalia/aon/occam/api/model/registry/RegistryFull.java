package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RegistryFull<R extends Registry> implements Serializable {

	private static final long serialVersionUID = 1169435863658845445L;
	private R registry;
	private List<RegistryBank> banks;
	private LinkedList<RegistryAddress> addresses;
	private LinkedList<RegistryMedia> medias;
	private List<RecordData> recordData;
	
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
	
	// ------------------------------------------ REGISTRY BANK
	
	public List<RegistryBank> getBanks() {
		return banks;
	}
		
	public RegistryFull<R> setBanks(List<RegistryBank> banks) {
		this.banks = banks;
		return this;
	}
		
	private List<RegistryBank> ensureBanks() {
		if (this.banks == null) this.banks = new LinkedList<>(); 
		return this.banks;
	}
		
	public RegistryFull<R> addBank(RegistryBank bank) {
		ensureBanks().add(bank);
		return this;
	}
		
	public boolean hasBanks() {
		return this.banks != null && !this.banks.isEmpty();
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
		if (this.addresses == null) this.addresses = new LinkedList<>(); 
		return this.addresses;
	}
	public RegistryFull<R> addAddress(RegistryAddress address) {
		ensureAddresses().add(address);
		return this;
	}
	public boolean hasAddresses() {
		return this.addresses != null && !this.addresses.isEmpty();
	}
	public RegistryAddress getMainAddress() {
		return (hasAddresses()) 
			?this.addresses.stream().filter(addr -> addr.isMain()).findFirst().orElse(null)
			:null;
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
		if (this.medias == null) this.medias = new LinkedList<>(); 
		return this.medias;
	}
	public RegistryFull<R> addMedia(RegistryMedia media) {
		ensureMedias().add(media);
		return this;
	}
	public boolean hasMedias() {
		return this.medias != null && !this.medias.isEmpty();
	}
	public List<RegistryMedia> getEmailMedias() {
		return (hasMedias()) 
			?this.medias.stream().filter(med -> med.isEmail()).collect(Collectors.toCollection(LinkedList::new))
			:null;
	}
	public List<RegistryMedia> getPhoneMedias() {
		return (hasMedias()) 
			?this.medias.stream().filter(med -> med.isPhone()).collect(Collectors.toCollection(LinkedList::new))
			:null;
	}
	
	
	// ------------------------------------------ RECORD DATA

	public List<RecordData> getRecordDatas() {
		return recordData;
	}
	
	public RegistryFull<R> setRecordDatas(List<RecordData> recordData) {
		this.recordData = recordData;
		return this;
	}
	
	// ------------------------------------------ OTHER
	
	public boolean isNew() {
		return getId() == null;
	}
		
	protected void initializeChilds() {
		// Empty method
	}
	
}
