package com.code.aon.registry;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.StreetType;

/**
 * Transfer Object that represents the RegistryAddress.
 * 
 * @author Consulting & Development. Eugenio Castellano - 28-ene-2005
 * @since 1.0
 */
@Entity
@Table(name="raddress")
public class RegistryAddress implements ITransferObject, IAddress {

	private static final long serialVersionUID = -104685154793784862L;

	private Integer id;
    private Registry registry;
    private AddressType type;
    private String recipient;
    private StreetType streetType;
    private String address;
    private String number;
    private String address2;
    private String address3;
    private String zip;
    private String city;
    private GeoZone geozone;
    private String alias;

    @Id
    @GeneratedValue
	public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="registry", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_RADDRESS_REGISTRY")
    @Index(name = "IDX_RADDRESS_REGISTRY")
    public Registry getRegistry() {
        return registry;
    }
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Column(name="type", nullable = false)
    public AddressType getAddressType() {
        return type;
    }
    public void setAddressType(AddressType type) {
        this.type = type;
    }

    @Column(length=128)
	public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Column(name="street_type")
   	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.registry.enumeration.StreetType") })
    public StreetType getStreetType() {
        return streetType;
    }
    public void setStreetType(StreetType streetType) {
        this.streetType = streetType;
    }

    @Column(length=128)
	public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

	public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    @Column(length=128)
	public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @Column(length=128)
	public String getAddress3() {
        return address3;
    }
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    @Column(length=16)
	public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(length=64)
	public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

	@ManyToOne
    @JoinColumn( name="geozone" )
    @ForeignKey(name = "FK_RADDRESS_GEOZONE")
    @Index(name = "IDX_RADDRESS_GEOZONE")
    public GeoZone getGeozone() {
        return geozone;
    }
    public void setGeozone(GeoZone geozone) {
        this.geozone = geozone;
    }

    @Column(length=15)
	public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Transient
    public String getFullAddress() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(getStreetType()==null?"":getStreetType());
    	buf.append(getStreetType()==null?"":" ");
    	buf.append(getAddress());
    	buf.append(StringUtils.isEmpty(getNumber())?"":" ");
    	buf.append(StringUtils.isEmpty(getNumber())?"":getNumber());
    	buf.append(StringUtils.isEmpty(getAddress2())?"":" ");
    	buf.append(StringUtils.isEmpty(getAddress2())?"":getAddress2() );
    	buf.append(StringUtils.isEmpty(getAddress3())?"":" (");
    	buf.append(StringUtils.isEmpty(getAddress3())?"":getAddress3() );
    	buf.append(StringUtils.isEmpty(getAddress3())?"":")");
    	return buf.toString();
    }
    @Transient
    public String getShortAddress() {
  		return StringUtils.isEmpty(getAlias())?StringUtils.abbreviate(getFullAddress(), 20): getAlias();
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryAddress o = (RegistryAddress) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.address, o.address)
				.append(this.address2, o.address2)				
				.append(this.address3, o.address3)
				.append(this.city, o.city)				
				.append(this.geozone, o.geozone)
				.append(this.recipient, o.recipient)
				.append(this.registry, o.registry)				
				.append(this.streetType, o.streetType)
				.append(this.type, o.type)				
				.append(this.zip, o.zip)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(address2)
			.append(address3)	
			.append(city)			
			.append(geozone)
			.append(id)
			.append(recipient)	
			.append(registry)			
			.append(streetType)
			.append(type)			
			.append(zip)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}