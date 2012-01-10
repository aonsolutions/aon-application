package com.code.aon.registry;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.registry.enumeration.AddressType;
import com.esferalia.aon.entity.master.RegistryAddressDB;

@Entity
@Table(name="raddress")
public class RegistryAddress extends RegistryAddressDB implements IAddress {

	private static final long serialVersionUID = 1L;

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

}
