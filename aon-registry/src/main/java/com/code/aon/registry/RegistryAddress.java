package com.code.aon.registry;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.entity.master.RegistryAddressDB;

@Entity
@Table(name="raddress")
public class RegistryAddress extends RegistryAddressDB implements IAddress {

    @Override
	@Transient
    public String getFullAddress() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(getStreetType()==null?"":getStreetType());
    	buf.append(getStreetType()==null?"":". ");
    	buf.append(getAddress());
    	buf.append(StringUtils.isEmpty(getNumber())?"":" ");
    	buf.append(StringUtils.isEmpty(getNumber())?"":getNumber());
    	buf.append(StringUtils.isEmpty(getAddress2())?"":", ");
    	buf.append(StringUtils.isEmpty(getAddress2())?"":getAddress2());
    	buf.append(StringUtils.isEmpty(getAddress3())?"":" (");
    	buf.append(StringUtils.isEmpty(getAddress3())?"":getAddress3());
    	buf.append(StringUtils.isEmpty(getAddress3())?"":")");
    	return buf.toString();
    }

    @Override
	@Transient
    public String getShortAddress() {
  		return StringUtils.isEmpty(getAlias())?StringUtils.abbreviate(getFullAddress(), 25): getAlias();
    }

    @Transient
    public boolean isMainAddress() {
  		return getAddressType() == AddressType.MAIN;
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
				.append(this.alias, o.alias)
				.append(this.city, o.city)				
				.append(this.geozone, o.geozone)
				.append(this.number, o.number)
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
			.append(alias)	
			.append(city)			
			.append(geozone)
			.append(id)
			.append(number)
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
