package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.registry.IAddress;
import com.esferalia.aon.entity.master.InvoiceAddressDB;

@Entity
@Table(name="invoice_address")
public class InvoiceAddress extends InvoiceAddressDB implements IAddress {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getAddress3() {
		return null;
	}

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

    @Transient
    public String getShortAddress() {
  		return StringUtils.abbreviate(getFullAddress(), 25);
    }
    
}