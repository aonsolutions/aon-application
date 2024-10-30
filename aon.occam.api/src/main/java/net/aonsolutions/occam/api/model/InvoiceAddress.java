package net.aonsolutions.occam.api.model;

import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceAddressMetadata;
import net.aonsolutions.occam.api.model.type.StreetType;

public class InvoiceAddress extends AonEntity<InvoiceAddressMetadata> {

	private static final long serialVersionUID = 5907689386464778213L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private StreetType streetType;
	private String address;
	private String number;
	private String address2;
	private String zip;	
	private String city;
	private String province;
	private Geozone geozone;
	private Geozone parent;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	
	@Override
	public InvoiceAddress markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public InvoiceAddress setDeleted(boolean deleted) {
		super.setDeleted(deleted);
		return this;
	}
	@Override
	public InvoiceAddress setSelected(boolean selected) {
		super.setSelected(selected);
		return this;
	}

	public Integer getId() {
		return id;
	}
	public InvoiceAddress setId(Integer id) {
		checkIfDirty( this.id,id, InvoiceAddressMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoiceAddress setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, InvoiceAddressMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceAddress setInvoice(Integer invoice) {
		checkIfDirty( this.invoice,invoice, InvoiceAddressMetadata.INVOICE);
		this.invoice = invoice;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public InvoiceAddress setStreetType(StreetType streetType) {
		checkIfDirty( this.streetType,streetType, InvoiceAddressMetadata.STREET_TYPE);
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public InvoiceAddress setAddress(String address) {
		checkIfDirty( this.address,address, InvoiceAddressMetadata.ADDRESS);
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	public InvoiceAddress setNumber(String number) {
		checkIfDirty( this.number,number, InvoiceAddressMetadata.NUMBER);
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public InvoiceAddress setAddress2(String address2) {
		checkIfDirty( this.address2,address2, InvoiceAddressMetadata.ADDRESS2);
		this.address2 = address2;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public InvoiceAddress setZip(String zip) {
		checkIfDirty( this.zip,zip, InvoiceAddressMetadata.ZIP);
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public InvoiceAddress setCity(String city) {
		checkIfDirty( this.city,city, InvoiceAddressMetadata.CITY);
		this.city = city;
		return this;
	}

	public String getProvince() {
		return province;
	}
	public InvoiceAddress setProvince(String province) {
		checkIfDirty( this.province,province, InvoiceAddressMetadata.PROVINCE);
		this.province = province;
		return this;
	}

	public Optional<Geozone> getGeozone() {
		return Optional.ofNullable(geozone);
	}
	public InvoiceAddress setGeozone(Geozone geozone) {
		checkIfDirty( this.geozone,geozone, InvoiceAddressMetadata.GEOZONE);
		this.geozone = geozone;
		return this;
	}

	public Optional<Geozone> getParent() {
		return Optional.ofNullable(parent);
	}
	public InvoiceAddress setParent(Geozone parent) {
		checkIfDirty( this.parent,parent, InvoiceAddressMetadata.PARENT);
		this.parent = parent;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceAddress other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}

	public static InvoiceAddress from(RegistryAddress ra) {
		if (ra == null) return null;
		return new InvoiceAddress()
			.setDomain(ra.getDomain())
			.setStreetType(ra.getStreetType())
			.setAddress(ra.getAddress())
			.setNumber(ra.getNumber())
			.setAddress2(ra.getAddress2())
			.setZip(ra.getZip())
			.setCity(ra.getCity())
			.setProvince(ra.getGeozone().map(Geozone::getName).orElse(null))
			.setGeozone(ra.getGeozone().orElse(null))
			.setParent(ra.getParent().orElse(null))
		;				
	}
}
