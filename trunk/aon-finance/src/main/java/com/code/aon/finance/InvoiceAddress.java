package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.IAddress;

@Entity
@Table(name="invoice_address")
public class InvoiceAddress implements ITransferObject, IAddress {

	private static final long serialVersionUID = 1873769542411802117L;

	private Integer id;
	private Invoice invoice;
	private String address;
	private String address2;
	private String zip;
	private String city;
	private GeoZone geozone;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

    @ManyToOne
    @JoinColumn(name="invoice", nullable = false)
    @ForeignKey(name="FK_INVOICE_ADDRESS_INVOICE")
    @Index(name="IDX_INVOICE_ADDRESS_INVOICE")                                
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Column(length=45)
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length=45)
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Column(length=16)
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	@Column(length=45)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

    @ManyToOne
    @JoinColumn(name="geozone", nullable = false)
    @ForeignKey(name="FK_INVOICE_ADDRESS_GEOZONE")
    @Index(name="IDX_INVOICE_ADDRESS_GEOZONE")                            
	public GeoZone getGeozone() {
		return geozone;
	}
	public void setGeozone(GeoZone geozone) {
		this.geozone = geozone;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoiceAddress o = (InvoiceAddress) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.address,o.address)
			.append(this.address2,o.address2)
			.append(this.city,o.city)
			.append(this.geozone,o.geozone)
			.append(this.invoice,o.invoice)
			.append(this.zip,o.zip)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.address)
			.append(this.address2)
			.append(this.city)
			.append(this.geozone)
			.append(this.invoice)
			.append(this.zip)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}