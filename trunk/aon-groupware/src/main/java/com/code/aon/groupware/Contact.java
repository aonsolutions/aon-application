package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	
	private String organization;
	
	private String phone;
	
	private String cellularPhone;
	
	private String fax;
	
	private String email;
	
	private String address;
	
	private String postalCode;
	
	private String note;

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
	@Attribute(name="organizationName", length=64)
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(length=64)
	@Attribute(name="homePhone", length=64)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name="cellular_phone", length=64)
	@Attribute(name="mobile", length=64)
	public String getCellularPhone() {
		return cellularPhone;
	}

	public void setCellularPhone(String cellularPhone) {
		this.cellularPhone = cellularPhone;
	}

	@Column(length=64)
	@Attribute(name="facsimileTelephoneNumber", length=64)
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
	@Attribute(name="postalCode",length=40)
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
}