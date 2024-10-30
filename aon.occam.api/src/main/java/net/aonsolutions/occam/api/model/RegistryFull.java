package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;

public class RegistryFull<R extends Registry> implements Serializable {

	private static final long serialVersionUID = 1169435863658845445L;
	private R registry;
	private LinkedList<RegistryBank> banks;
	private LinkedList<RegistryAddress> addresses;
	private LinkedList<RegistryMedia> medias;
	
	// ------------------------------------------ REGISTRY
	
	public R getRegistry() {
		return registry;
	}
	public R get() {
		return getRegistry();
	}
	public RegistryFull<R> setRegistry(R registry) {
		this.registry = registry;
		return this;
	}
	public Integer getId() {
		return registry==null?null:registry.getId();
	}

	public Integer getDomain() {
		return registry==null? null : registry.getDomain();
	}
	
	// ------------------------------------------ REGISTRY BANK
	
	public Stream<RegistryBank> bankStream() {
		return AonCollectionUtils.stream(banks);
	}
	public RegistryFull<R> addBank(RegistryBank bank) {
		if (this.banks == null) this.banks =new LinkedList<>();
		this.banks.add(bank);
		return this;
	}

	// ------------------------------------------ REGISTRY ADDRESS
	
	public Stream<RegistryAddress> addressStream() {
		return AonCollectionUtils.stream(addresses);
	}
	public RegistryFull<R> addAddress(RegistryAddress address) {
		if (this.addresses == null) this.addresses = new LinkedList<>(); 
		this.addresses.add(address);
		return this;
	}
	public Optional<RegistryAddress> getMainAddress() {
		return addressStream()
			.filter(a -> a.isMain())
			.findFirst();
	}

	// ------------------------------------------ REGISTRY MEDIA
	
	public Stream<RegistryMedia> mediaStream() {
		return AonCollectionUtils.stream(medias);
	}
	public RegistryFull<R> addMedia(RegistryMedia media) {
		if (this.medias == null) this.medias = new LinkedList<>();
		this.medias.add(media);
		return this;
	}
	
	protected void initializeChilds() {
		// Empty method
	}
}
