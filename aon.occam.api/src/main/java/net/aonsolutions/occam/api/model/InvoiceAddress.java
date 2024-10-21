package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.StreetType;

public class InvoiceAddress implements Serializable {

	private static final long serialVersionUID = 5907689386464778213L;
	
	private boolean deleted;

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
	
	public boolean isDeleted() {
		return deleted;
	}
	public InvoiceAddress setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Integer getId() {
		return id;
	}
	public InvoiceAddress setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoiceAddress setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceAddress setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}

	public StreetType getStreetType() {
		return streetType;
	}
	public InvoiceAddress setStreetType(StreetType streetType) {
		this.streetType = streetType;
		return this;
	}

	public String getAddress() {
		return address;
	}
	public InvoiceAddress setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getNumber() {
		return number;
	}
	public InvoiceAddress setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getAddress2() {
		return address2;
	}
	public InvoiceAddress setAddress2(String address2) {
		this.address2 = address2;
		return this;
	}

	public String getZip() {
		return zip;
	}
	public InvoiceAddress setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}
	public InvoiceAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public Optional<Geozone> getGeozone() {
		return Optional.ofNullable(geozone);
	}
	public InvoiceAddress setGeozone(Geozone geozone) {
		this.geozone = geozone;
		return this;
	}

	public Optional<Geozone> getParent() {
		return Optional.ofNullable(parent);
	}
	public InvoiceAddress setParent(Geozone parent) {
		this.parent = parent;
		return this;
	}
	
	public String getProvince() {
		return province;
	}
	public InvoiceAddress setProvince(String province) {
		this.province = province;
		return this;
	}
	
	public static InvoiceAddress from(RegistryAddress ra) {
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
