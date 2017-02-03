package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class RAddress implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String registryName;
	private Byte type;	
	private String recipient;
	private String street_type;
	private String address;
	private String number;
	private String address2;
	private String address3;
	private String zip;	
	private String city;
	private Integer geozone;
	private String geozoneName;
	private String alias;	
	private String municipality_code;
	
	public RAddress() {
	
	}

	public Integer getId() {
		return id;
	}

	public RAddress setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RAddress setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RAddress setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Byte getType() {
		return type;
	}

	public RAddress setType(Byte type) {
		this.type = type;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}

	public RAddress setRecipient(String recipient) {
		this.recipient = recipient;
		return this;
	}

	public String getStreet_type() {
		return street_type;
	}

	public RAddress setStreet_type(String street_type) {
		this.street_type = street_type;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public RAddress setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public RAddress setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}

	public RAddress setAddress2(String address2) {
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}

	public RAddress setAddress3(String address3) {
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}

	public RAddress setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}

	public RAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public Integer getGeozone() {
		return geozone;
	}

	public RAddress setGeozone(Integer geozone) {
		this.geozone = geozone;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public RAddress setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getMunicipality_code() {
		return municipality_code;
	}

	public RAddress setMunicipality_code(String municipality_code) {
		this.municipality_code = municipality_code;
		return this;
	}

	public String getRegistryName() {
		return registryName;
	}

	public RAddress setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public String getGeozoneName() {
		return geozoneName;
	}

	public RAddress setGeozoneName(String geozoneName) {
		this.geozoneName = geozoneName;
		return this;
	}

}
