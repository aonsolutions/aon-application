package com.code.aon.file.bank.model.CSB34.data;

import com.code.aon.file.bank.model.SEPA.Address;
import com.code.aon.file.bank.model.SEPA.Entity;

public class Receiver implements Entity {
	
	private String code;
	private String name;
	private String address;
	private String zip;
	private String city;
	private String province;
	private String referenceCode; 
	private Address SEPAAddress;
	private boolean organisation;
	private String documentType;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressPart1() {
		if (address != null && address.length() > 36)
			return address.substring(0, 36);
		return address;
	}
	public String getAddressPart2() {
		if (address != null && address.length() > 36) {
			if (address.length() > 72)
				return address.substring(36, 72);
			return address.substring(36);
		}
		return null;
	}

	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipAndCity() {
		return ((zip!=null) ? zip + " - " : "") + city;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the referenceCode
	 */
	public String getReferenceCode() {
		return referenceCode;
	}
	/**
	 * @param referenceCode the referenceCode to set
	 */
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	@Override
	public String getDocument() {
		return getCode();
	}

	@Override
	public Address getSEPAAddress() {
		return SEPAAddress;
	}

	public void setSEPAAddress(Address sEPAAddress) {
		SEPAAddress = sEPAAddress;
	}

	@Override
	public boolean isOrganisation() {
		return organisation;
	}

	public void setOrganisation(boolean organisation) {
		this.organisation = organisation;
	}

	@Override
	public String getDocumentType() {
		return documentType;
	}
	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
}