package com.code.aon.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class RawTarget {

	private String id;
	
	private String name;
	
	private String streetType;
	
	private String address;
	
	private String address2;
	
	private String addressNumber;
	
	private String address3;
	
	private String zip;
	
	private String city;
	
	private String geoZone;
	
	private String geoZoneTree;
	
	private String phone1;
	
	private String phone2;
	
	private String email1;
	
	private String email2;
	
	private String cnaeCode;
	
	private String cnae;
	
	public RawTarget( String[] values ) {
		this.id = values[0];
		this.name = values[1];
		this.streetType = values[2];
		this.address = values[3];
		this.address2 = values[4];
		this.addressNumber = values[5];
		this.address3 = values[6];
		this.zip = values[7];
		this.city = values[8];
		this.geoZone = values[9];
		this.geoZoneTree = values[10];
		this.phone1 = values[11];
		this.phone2 = values[12];
		this.email1 = values[13];
		this.email2 = values[14];
		this.cnaeCode = values[15];
		this.cnae = values[16];
		parseAddress();
	}
	
	private void parseAddress() {
		if ( StringUtils.isEmpty(this.streetType) ) {
			this.streetType = "CL";
			if ( StringUtils.startsWith(this.address, "C ") ) {
				this.address = StringUtils.substringAfter(this.address, "C ");
			} else if ( StringUtils.startsWith(this.address, "C/ ") ) {
				this.address = StringUtils.substringAfter(this.address, "C/ ");
			} else if ( StringUtils.startsWith(this.address, "CALLE ") ) {
				this.address = StringUtils.substringAfter(this.address, "CALLE ");
			} else if ( StringUtils.startsWith(this.address, "CARRER ") ) {
				this.address = StringUtils.substringAfter(this.address, "CARRER ");
			} else if ( StringUtils.startsWith(this.address, "PL ") ) {
				this.address = StringUtils.substringAfter(this.address, "PL ");
				this.streetType = "PZ";
			} else if ( StringUtils.startsWith(this.address, "PL. ") ) {
				this.address = StringUtils.substringAfter(this.address, "PL. ");
				this.streetType = "PZ";
			} else if ( StringUtils.startsWith(this.address, "PLAZA ") ) {
				this.address = StringUtils.substringAfter(this.address, "PLAZA ");
				this.streetType = "PZ"; 
			} else if ( StringUtils.startsWith(this.address, "PLAÇA ") ) {
				this.address = StringUtils.substringAfter(this.address, "PLAÇA ");
				this.streetType = "PZ"; 
			} else if ( StringUtils.startsWith(this.address, "AV ") ) {
				this.address = StringUtils.substringAfter(this.address, "AV ");
				this.streetType = "AV";
			} else if ( StringUtils.startsWith(this.address, "AV. ") ) {
				this.address = StringUtils.substringAfter(this.address, "AV. ");
				this.streetType = "AV";
			} else if ( StringUtils.startsWith(this.address, "AVDA ") ) {
				this.address = StringUtils.substringAfter(this.address, "AVDA ");
				this.streetType = "AV";
			} else if ( StringUtils.startsWith(this.address, "AVDA. ") ) {
				this.address = StringUtils.substringAfter(this.address, "AVDA. ");
				this.streetType = "AV";
			} else if ( StringUtils.startsWith(this.address, "AVENIDA ") ) {
				this.address = StringUtils.substringAfter(this.address, "AVENIDA ");
				this.streetType = "AV";
			} else if ( StringUtils.startsWith(this.address, "RBLA. ") ) {
				this.address = StringUtils.substringAfter(this.address, "RBLA. ");
				this.streetType = "RB";
			} else if ( StringUtils.startsWith(this.address, "RBLA ") ) {
				this.address = StringUtils.substringAfter(this.address, "RBLA ");
				this.streetType = "RB";
			} else if ( StringUtils.startsWith(this.address, "RAMBLA ") ) {
				this.address = StringUtils.substringAfter(this.address, "RAMBLA ");
				this.streetType = "RB";
			} else if ( StringUtils.startsWith(this.address, "VIA ") ) {
				this.address = StringUtils.substringAfter(this.address, "VIA ");
				this.streetType = "VI";
			} else if ( StringUtils.startsWith(this.address, "V. ") ) {
				this.address = StringUtils.substringAfter(this.address, "V. ");
				this.streetType = "VI";
			} else if ( StringUtils.startsWith(this.address, "RONDA ") ) {
				this.address = StringUtils.substringAfter(this.address, "RONDA ");
				this.streetType = "RD";
			} else if ( StringUtils.startsWith(this.address, "RDA. ") ) {
				this.address = StringUtils.substringAfter(this.address, "RDA. ");
				this.streetType = "RD";
			} else if ( StringUtils.startsWith(this.address, "ROND ") ) {
				this.address = StringUtils.substringAfter(this.address, "ROND ");
				this.streetType = "RD";
			}			
		}
		String number = StringUtils.trim(StringUtils.substringAfterLast(this.address, ","));
		if ( NumberUtils.isNumber(number) ) {
			this.address = StringUtils.substringBeforeLast(this.address, ", ");
			this.addressNumber = number;
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStreetType() {
		return streetType;
	}

	public String getAddress() {
		return address;
	}

	public String getAddress2() {
		return address2;
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public String getAddress3() {
		return address3;
	}

	public String getZip() {
		return zip;
	}

	public String getCity() {
		return city;
	}

	public String getGeoZone() {
		return geoZone;
	}

	public String getGeoZoneTree() {
		return geoZoneTree;
	}

	public String getPhone1() {
		return phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public String getEmail1() {
		return email1;
	}

	public String getEmail2() {
		return email2;
	}

	public String getCnaeCode() {
		return cnaeCode;
	}

	public String getCnae() {
		return cnae;
	}
	
}
