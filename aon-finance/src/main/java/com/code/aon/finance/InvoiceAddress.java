package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.registry.IAddress;
import com.esferalia.aon.entity.master.InvoiceAddressDB;

@Entity
@Table(name="invoice_address")
public class InvoiceAddress extends InvoiceAddressDB implements IAddress {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getAddress3() {
		return null;
	}
	public void setAddress3(String address3) {
	}

	@Transient
	public String getAlias() {
		return null;
	}
	public void setAlias(String alias) {
	}

	@Transient
    public String getFullAddress() {
    	StringBuffer buf = new StringBuffer();
    	buf.append((getStreetType()!=null) ? getStreetType() : "");
    	buf.append((getStreetType()!=null) ? ". " : "");
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

    @Transient
    public String getShortAddress() {
  		return StringUtils.abbreviate(getFullAddress(), 25);
    }
    
	@Transient
    public String getLocation() {
    	StringBuffer buf = new StringBuffer();
    	buf.append((getCity()!=null) ? getCity()+" " : "");
    	buf.append((getGeozone()!=null && getGeozone().getId()!=null) ? "("+getGeozone().getName()+")" : "");
    	buf.append((getGeozone()==null || getGeozone().getId()==null) ? "("+getProvince()+")" : "");
    	return buf.toString();
    }

}