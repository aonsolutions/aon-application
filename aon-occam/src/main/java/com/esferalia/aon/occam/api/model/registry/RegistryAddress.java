package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

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

	private boolean dirty;
	private boolean removed;

	public Integer getId() {
		return id;
	}
	public RegistryAddress setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RegistryAddress setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain , domain) );
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	public RegistryAddress setRegistry(Integer registry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.registry, registry) );
		this.registry = registry;
		return this;
	}

	public boolean isMain() {
		return main;
	}
	public RegistryAddress setMain(boolean main) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.main, main) );
		this.main = main;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}
	public RegistryAddress setRecipient(String recipient) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.recipient, recipient) );
		this.recipient = recipient;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public RegistryAddress setStreetType(StreetType streetType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.streetType, streetType) );
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public RegistryAddress setAddress(String address) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.address, address) );
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	public RegistryAddress setNumber(String number) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.number, number) );
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public RegistryAddress setAddress2(String address2) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.address2, address2) );
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}
	public RegistryAddress setAddress3(String address3) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.address3, address3) );
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public RegistryAddress setZip(String zip) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.zip, zip) );
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public RegistryAddress setCity(String city) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.city, city) );
		this.city = city;
		return this;
	}

	public Integer getGeozone() {
		return geozone;
	}
	public RegistryAddress setGeozone(Integer geozone) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.geozone, geozone) );
		this.geozone = geozone;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public RegistryAddress setAlias(String alias) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.alias, alias) );
		this.alias = alias;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}
	public RegistryAddress setMunicipalityCode(String municipalityCode) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.municipalityCode, municipalityCode) );
		this.municipalityCode = municipalityCode;
		return this;
	}

	public String getGeozoneCode() {
		return geozoneCode;
	}
	public RegistryAddress setGeozoneCode(String geozoneCode) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.geozoneCode, geozoneCode) );
		this.geozoneCode = geozoneCode;
		return this;
	}
	
	public String getGeozoneName() {
		return geozoneName;
	}
	public RegistryAddress setGeozoneName(String geozoneName) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.geozoneName, geozoneName) );
		this.geozoneName = geozoneName;
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
		String streetType = getStreetType()==null?"":getStreetType().getDescription();
		if(getStreetType() == null 
			|| getStreetType() == StreetType.XX 		// TODO ????????????
			|| getStreetType() == StreetType.ZZ) { 		// TODO ????????????
			streetType = "";
		}
    	StringBuffer buf = new StringBuffer();
    	buf.append(AonStringUtils.defaultString(streetType));
    	buf.append(AonStringUtils.isBlank(streetType) ? ". " : "");
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

}
