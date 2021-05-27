package com.code.aon.ui.mailing;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;


public class MailData implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String SEPARATOR = ",";
	
	private static final String QUOTE = "\"";
	
	private String id;
	
	private String document;
	
	private String name;
	
	private String address;
	
	private String city;
	
	private String zip;
	
	private String geoZone;
	
	private String phone;
	
	private String cellular;
	
	private String fax;
	
	private String email;

	public MailData( Registry registry ) throws ManagerBeanException {
		this.id = parseValue(registry.getId());
    	this.document = parseValue(registry.getDocument());
    	this.name = parseValue(registry.getName());

    	RegistryAddress rAddress = registry.getDefaultAddress();
    	if (rAddress != null) {
        	address = rAddress.getFullAddress();
        	this.city = parseValue(rAddress.getCity());
        	this.zip = parseValue(rAddress.getZip());
        	if (rAddress.getGeozone() != null) {
        		this.geoZone = parseValue(rAddress.getGeozone().getName());
        	}
    	}

    	RegistryMedia rPhone = registry.getPhone();
    	if (rPhone != null) {
    		this.phone = parseValue(rPhone.getValue());
    	}

    	RegistryMedia rCellular = registry.getCellular();
    	if (rCellular != null) {
    		cellular = parseValue(rCellular.getValue());
    	}

    	RegistryMedia rFax = registry.getFax();
    	if (rFax != null) {
    		fax = parseValue(rFax.getValue());
    	}

    	RegistryMedia rEmail = registry.getEmail();
    	if (rEmail != null) {
    		email = parseValue(rEmail.getValue());
    	}		
	}

	private String parseValue(Object value){
		try{
			return StringUtils.trimToNull(value.toString());
		}catch (Throwable e) {
			return null;
		}
	}	
	public String getId() {
		return id;
	}

	public String getDocument() {
		return document;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getZip() {
		return zip;
	}

	public String getGeoZone() {
		return geoZone;
	}

	public String getPhone() {
		return phone;
	}

	public String getCellular() {
		return cellular;
	}

	public String getFax() {
		return fax;
	}

	public String getEmail() {
		return email;
	}
	
	private void append( StringBuffer buffer, String value ) {
		if ( value != null ) {
			buffer.append(QUOTE).append(value).append(QUOTE);
		}
		buffer.append(SEPARATOR);		
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		append(buffer, id);
		append(buffer, document);
		append(buffer, name);
		append(buffer, address);
		append(buffer, city);
		append(buffer, zip);
		append(buffer, geoZone);
		append(buffer, phone);
		append(buffer, cellular);
		append(buffer, fax);
		append(buffer, email);

    	return buffer.toString();
	}
	
}
