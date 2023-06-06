package net.aonsolutions.occam.api.config;

import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.watson.server.AonObjectUtils;

public class RegistryAddress extends OccamEntity {

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

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public RegistryAddress markAsClean() {
		super.markAsClean();
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	
	public RegistryAddress setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RegistryAddress setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AonNames.DOMAIN));
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	
	public RegistryAddress setRegistry(Integer registry) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.registry,registry), () -> markAsDirty(AonNames.REGISTRY));
		this.registry = registry;
		return this;
	}

	public boolean isMain() {
		return main;
	}
	public RegistryAddress setMain(boolean main) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.main,main), () -> markAsDirty(AonNames.MAIN));
		this.main = main;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}
	public RegistryAddress setRecipient(String recipient) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.recipient,recipient), () -> markAsDirty(AonNames.RECIPIENT));
		this.recipient = recipient;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public RegistryAddress setStreetType(StreetType streetType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.streetType,streetType), () -> markAsDirty(AonNames.STREET_TYPE));
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public RegistryAddress setAddress(String address) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.address,address), () -> markAsDirty(AonNames.ADDRESS));
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	
	public RegistryAddress setNumber(String number) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.number,number), () -> markAsDirty(AonNames.NUMBER));
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public RegistryAddress setAddress2(String address2) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.address2,address2), () -> markAsDirty(AonNames.ADDRESS2));
		this.address2 = address2;
		return this;
	}

	public String getAddress3() {
		return address3;
	}
	public RegistryAddress setAddress3(String address3) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.address3,address3), () -> markAsDirty(AonNames.ADDRESS3));
		this.address3 = address3;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public RegistryAddress setZip(String zip) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.zip,zip), () -> markAsDirty(AonNames.ZIP));
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public RegistryAddress setCity(String city) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.city,city), () -> markAsDirty(AonNames.CITY));
		this.city = city;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public RegistryAddress setAlias(String alias) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.alias,alias), () -> markAsDirty(AonNames.ALIAS));
		this.alias = alias;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}
	public RegistryAddress setMunicipalityCode(String municipalityCode) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.municipalityCode,municipalityCode), () -> markAsDirty(AonNames.MUNICIPALITY_CODE));
		this.municipalityCode = municipalityCode;
		return this;
	}

	public Optional<Geozone> getGeozone() {
		return Optional.ofNullable( geozone );
	}

	public RegistryAddress setGeozone(Geozone geozone) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.geozone,geozone), () -> markAsDirty(AonNames.GEOZONE));
		this.geozone = geozone;
		return this;
	}

	public Optional<Geozone> getParentGeozone() {
		return Optional.ofNullable( parentGeozone );
	}
	public RegistryAddress setParentGeozone(Geozone parentGeozone) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.parentGeozone,parentGeozone), () -> markAsDirty(AonNames.PARENT));
		this.parentGeozone = parentGeozone;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof RegistryAddress other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}
