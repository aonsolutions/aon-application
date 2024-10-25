package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.StreetType;

public class RegistryAddress implements Serializable {

	private static final long serialVersionUID = 5907689386464778213L;
	
	private boolean deleted;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private boolean main;	
	private String recipient;
	private StreetType streetType;
	private String address;
	private String number;
	private String address2;
	private String address3;
	private String zip;	
	private String city;
	private String alias;	
	private String municipalityCode;
	private Geozone geozone;
	private Geozone parent;

	
	public boolean isDeleted() {
		return deleted;
	}
	public RegistryAddress setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	public boolean isNotDeleted() {
		return !deleted;
	}
	public void delete() {
		this.deleted = true;
	}
	public void restore() {
		this.deleted = false;
	}

	public Integer getId() {
		return id;
	}
	public RegistryAddress setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RegistryAddress setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	public RegistryAddress setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public boolean isMain() {
		return main;
	}
	public RegistryAddress setMain(boolean main) {
		this.main = main;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}
	public RegistryAddress setRecipient(String recipient) {
		this.recipient = recipient;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public RegistryAddress setStreetType(StreetType streetType) {
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public RegistryAddress setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	public RegistryAddress setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public RegistryAddress setAddress2(String address2) {
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}
	public RegistryAddress setAddress3(String address3) {
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public RegistryAddress setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public RegistryAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public RegistryAddress setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}
	public RegistryAddress setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
		return this;
	}

	public Optional<Geozone> getGeozone() {
		return Optional.ofNullable(geozone);
	}
	public RegistryAddress setGeozone(Geozone geozone) {
		this.geozone = geozone;
		return this;
	}

	public Optional<Geozone> getParent() {
		return Optional.ofNullable(parent);
	}
	public RegistryAddress setParent(Geozone parent) {
		this.parent = parent;
		return this;
	}
	
}
