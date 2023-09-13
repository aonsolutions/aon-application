package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class RegistryAddress implements Serializable {

	private static final long serialVersionUID = 5907689386464778213L;
	
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
	private Integer geozone;
	private String geozoneCode;
	private String geozoneName;
	private String alias;	
	private String municipalityCode;

	private GeoZone child;
	private GeoZone parent;
	private String province;
	private Country country;
	
	private boolean dirty;
	private boolean removed;
	private boolean global;
	
	public Integer getId() {
		return id;
	}
	
	public RegistryAddress setId(Integer id) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.id , id));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public RegistryAddress setDomain(Integer domain) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.domain , domain));
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	
	public RegistryAddress setRegistry(Integer registry) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.registry, registry));
		this.registry = registry;
		return this;
	}

	public boolean isMain() {
		return main;
	}
	
	public RegistryAddress setMain(boolean main) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.main, main));
		this.main = main;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}
	
	public RegistryAddress setRecipient(String recipient) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.recipient, recipient));
		this.recipient = recipient;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	
	public RegistryAddress setStreetType(StreetType streetType) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.streetType, streetType));
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	
	public RegistryAddress setAddress(String address) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.address, address));
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	
	public RegistryAddress setNumber(String number) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.number, number));
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	
	public RegistryAddress setAddress2(String address2) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.address2, address2));
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}
	
	public RegistryAddress setAddress3(String address3) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.address3, address3));
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}
	
	public RegistryAddress setZip(String zip) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.zip, zip));
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	
	public RegistryAddress setCity(String city) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.city, city));
		this.city = city;
		return this;
	}

	public Integer getGeozone() {
		return geozone;
	}
	
	public RegistryAddress setGeozone(Integer geozone) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.geozone, geozone));
		this.geozone = geozone;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	
	public RegistryAddress setAlias(String alias) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.alias, alias));
		this.alias = alias;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}
	
	public RegistryAddress setMunicipalityCode(String municipalityCode) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.municipalityCode, municipalityCode));
		this.municipalityCode = municipalityCode;
		return this;
	}

	public String getGeozoneCode() {
		return geozoneCode;
	}
	
	public RegistryAddress setGeozoneCode(String geozoneCode) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.geozoneCode, geozoneCode));
		this.geozoneCode = geozoneCode;
		return this;
	}
	
	public String getGeozoneName() {
		return geozoneName;
	}
	
	public RegistryAddress setGeozoneName(String geozoneName) {
		this.setDirty(isDirty() || AonUtils.notEquals(this.geozoneName, geozoneName));
		setProvince(geozoneName);
		this.geozoneName = geozoneName;
		return this;
	}
	
	public GeoZone getChild() {
		return child;
	}

	public RegistryAddress setChild(GeoZone child) {
		setProvince(child.getName());
		this.child = child;
		return this;
	}

	public GeoZone getParent() {
		return parent;
	}

	public RegistryAddress setParent(GeoZone parent) {
		setCountry(Country.safeValueOf(parent.getCode()));
		this.parent = parent;
		return this;
	}
	
	public String getProvince() {
		return province;
	}
	
	public RegistryAddress setProvince(String province) {
		this.province = province;
		return this;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public RegistryAddress setCountry(Country country) {
		this.country = country;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	public RegistryAddress setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistryAddress setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public String getFullAddress() {
		String st = getStreetType() == null 
				|| StreetType.XX.equals(getStreetType())
				|| StreetType.ZZ.equals(getStreetType())
			? "": getStreetType().getDescription().substring(0, 1) + getStreetType().getDescription().substring(1).toLowerCase();
    	StringBuilder buf = new StringBuilder();
    	buf.append(AonStringUtils.defaultString(st));
    	buf.append(!AonStringUtils.isBlank(st) ? ". " : "");
    	buf.append(AonStringUtils.isEmpty(getAddress())? "":getAddress());
    	buf.append(AonStringUtils.isEmpty(getNumber())?"":" ");
    	buf.append(AonStringUtils.isEmpty(getNumber())?"":getNumber());
    	buf.append(AonStringUtils.isEmpty(getAddress2())?"":", ");
    	buf.append(AonStringUtils.isEmpty(getAddress2())?"":getAddress2());
    	buf.append(AonStringUtils.isEmpty(getAddress3())?"":" (");
    	buf.append(AonStringUtils.isEmpty(getAddress3())?"":getAddress3());
    	buf.append(AonStringUtils.isEmpty(getAddress3())?"":")");
    	return buf.toString();
    }
	
	public String getFullAddress2() {
    	StringBuilder buf = new StringBuilder();
    	buf.append(AonStringUtils.isBlank(getZip()) ? "" : " " + getZip());
    	buf.append(AonStringUtils.isBlank(getProvince()) ? "" : ", " + getProvince());
    	return getFullAddress() + buf.toString();
    }
	
	public String getFullAddress(AonLanguage language) {
		
		String description = getStreetType() != null ? getStreetType().getDescription(language) : null;
		
		String st = getStreetType() == null 
				|| StreetType.XX.equals(getStreetType())
				|| StreetType.ZZ.equals(getStreetType())
				|| AonStringUtils.isBlank(description)
				? "": description.substring(0, 1) + description.substring(1).toLowerCase();
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.defaultString(st));
		buf.append(!AonStringUtils.isBlank(st) ? ". " : "");
		buf.append(AonStringUtils.isEmpty(getAddress())? "":getAddress());
		buf.append(AonStringUtils.isEmpty(getNumber())?"":" ");
		buf.append(AonStringUtils.isEmpty(getNumber())?"":getNumber());
		buf.append(AonStringUtils.isEmpty(getAddress2())?"":", ");
		buf.append(AonStringUtils.isEmpty(getAddress2())?"":getAddress2());
		buf.append(AonStringUtils.isEmpty(getAddress3())?"":" (");
		buf.append(AonStringUtils.isEmpty(getAddress3())?"":getAddress3());
		buf.append(AonStringUtils.isEmpty(getAddress3())?"":")");
		return buf.toString();
	}

	public boolean isEmpty() {
		return id == null && domain == null && registry == null
			&&  AonStringUtils.isBlank(recipient) && streetType == null && AonStringUtils.isBlank(address)
			&&  AonStringUtils.isBlank(zip) &&  AonStringUtils.isBlank(city) && geozone == null;
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	public RegistryAddress setGlobal(boolean global) {
		this.global = global;
		return this;
	}
	
}
