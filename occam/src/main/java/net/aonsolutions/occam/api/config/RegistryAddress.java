package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class RegistryAddress implements Serializable, HasSelector<RegistryAddress>,HasDirtyFlag<RegistryAddress> {

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
	private Geozone geozone;
	private Geozone parentGeozone;
	private String zip;	
	private String city;
	private String alias;	
	private String municipalityCode;

	
	private boolean dirty;
	private boolean selected;
	
	public Integer getId() {
		return id;
	}
	
	public RegistryAddress setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RegistryAddress setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	
	public RegistryAddress setRegistry(Integer registry) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registry,registry) );
		this.registry = registry;
		return this;
	}

	public boolean isMain() {
		return main;
	}
	public RegistryAddress setMain(boolean main) {
		this.dirtyMark( AonObjectUtils.notEquals(this.main,main) );
		this.main = main;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}
	public RegistryAddress setRecipient(String recipient) {
		this.dirtyMark( AonObjectUtils.notEquals(this.recipient,recipient) );
		this.recipient = recipient;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public RegistryAddress setStreetType(StreetType streetType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.streetType,streetType) );
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public RegistryAddress setAddress(String address) {
		this.dirtyMark( AonObjectUtils.notEquals(this.address,address) );
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	
	public RegistryAddress setNumber(String number) {
		this.dirtyMark( AonObjectUtils.notEquals(this.number,number) );
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public RegistryAddress setAddress2(String address2) {
		this.dirtyMark( AonObjectUtils.notEquals(this.address2,address2) );
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}
	public RegistryAddress setAddress3(String address3) {
		this.dirtyMark( AonObjectUtils.notEquals(this.address3,address3) );
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public RegistryAddress setZip(String zip) {
		this.dirtyMark( AonObjectUtils.notEquals(this.zip,zip) );
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public RegistryAddress setCity(String city) {
		this.dirtyMark( AonObjectUtils.notEquals(this.city,city) );
		this.city = city;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public RegistryAddress setAlias(String alias) {
		this.dirtyMark( AonObjectUtils.notEquals(this.alias,alias) );
		this.alias = alias;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}
	public RegistryAddress setMunicipalityCode(String municipalityCode) {
		this.dirtyMark( AonObjectUtils.notEquals(this.municipalityCode,municipalityCode) );
		this.municipalityCode = municipalityCode;
		return this;
	}

	public Optional<Geozone> getGeozone() {
		return Optional.ofNullable( geozone );
	}

	public RegistryAddress setGeozone(Geozone geozone) {
		this.dirtyMark( this.geozone, geozone);
		this.geozone = geozone;
		return this;
	}

	public Optional<Geozone> getParentGeozone() {
		return Optional.ofNullable( parentGeozone );
	}
	public RegistryAddress setParentGeozone(Geozone parentGeozone) {
		this.dirtyMark( this.parentGeozone, parentGeozone);
		this.parentGeozone = parentGeozone;
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public RegistryAddress setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public RegistryAddress setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof RegistryAddress other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
	
}
