package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

@Entity
@Table(name="contact")
@EntryObject(mainObjectClass=IAonObjectClasses.CONTACT, objectClasses={IAonObjectClasses.TOP})
public class Contact implements ITransferObject {

	private static final long serialVersionUID = 7825997921660369372L;

	private String id;
	
	private String displayName;
	
	private String name;
	
	private String surname;
	
	private String phone;
	
	private String cellularPhone;
	
	private String fax;
	
	private String email;
	
	private String address;
	
	private String postalCode;
	
	private String city;
	
	private String state;
	
	private String note;

	private String organization;
	
	private String category;
	
	private String organizationPhone;
	
	private String organizationFax;

	private String organizationAddress;
	
	private String organizationPostalCode;
	
	private String organizationCity;
	
	private String organizationState;
	
	private String web;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@RDN
	@Attribute(name="displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(length=64)
	@Attribute(name="cn", length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	@Attribute(name="sn", length=64)
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Column(length=64)
	@Attribute(name="o", length=64)
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(length=20)
	@Attribute(name="homePhone", length=20)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name="cellular_phone", length=20)
	@Attribute(name="mobile", length=20)
	public String getCellularPhone() {
		return cellularPhone;
	}

	public void setCellularPhone(String cellularPhone) {
		this.cellularPhone = cellularPhone;
	}

	@Column(length=20)
	@Attribute(name="facsimileTelephoneNumber", length=20)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(length=64)
	@Attribute(name="mail",length=64)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length=128)
	@Attribute(name="postalAddress",length=128)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Transient
	@Attribute(name="postalCode",length=16)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	@Column(length=65535)
	@Attribute(name="info",length=2048)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(length=64)
	@Attribute(name="city",length=64)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(length=64)
	@Attribute(name="st",length=64)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(length=128)
	@Attribute(name="businessCategory",length=128)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(length=20)
	@Attribute(name="telephoneNumber",length=20)
	public String getOrganizationPhone() {
		return organizationPhone;
	}

	public void setOrganizationPhone(String organizationPhone) {
		this.organizationPhone = organizationPhone;
	}

	@Column(length=20)
	@Attribute(name="oFacsimileTelephoneNumber",length=20)
	public String getOrganizationFax() {
		return organizationFax;
	}

	public void setOrganizationFax(String organizationFax) {
		this.organizationFax = organizationFax;
	}

	@Column(length=128)
	@Attribute(name="oPostalAddress",length=128)
	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}

	@Transient
	@Attribute(name="oPostalCode",length=16)
	public String getOrganizationPostalCode() {
		return organizationPostalCode;
	}

	public void setOrganizationPostalCode(String organizationPostalCode) {
		this.organizationPostalCode = organizationPostalCode;
	}

	@Column(length=64)
	@Attribute(name="oCity",length=64)
	public String getOrganizationCity() {
		return organizationCity;
	}

	public void setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity;
	}

	@Column(length=64)
	@Attribute(name="oSt",length=64)
	public String getOrganizationState() {
		return organizationState;
	}

	public void setOrganizationState(String organizationState) {
		this.organizationState = organizationState;
	}

	@Column(length=128)
	@Attribute(name="labeledURI",length=128)
	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}
	
	@Transient
	public String getEmailLarge() {
		if ( getEmail() != null ) {
			String name = getDisplayName();
			if (! StringUtils.isAsciiPrintable(name) ) {
				name = "\"" + name + "\"";
			}
			return name + " &lt;" + getEmail() + "&gt;";			
		}
		return null;
	}

}