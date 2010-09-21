package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.AMAVIS_ACCOUNT;
import static com.code.aon.ldap.IAonObjectClasses.INET_ORG_PERSON;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_PERSON;
import static com.code.aon.ldap.IAonObjectClasses.PERSON;
import static com.code.aon.ldap.IAonObjectClasses.POSIX_ACCOUNT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.ldap.IAonObjectClasses.USER;

import java.util.Date;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.dao.ldap.util.IPerson;

@EntryObject(mainObjectClass=USER, objectClasses={TOP, POSIX_ACCOUNT, PERSON, ORGANIZATIONAL_PERSON, INET_ORG_PERSON, AMAVIS_ACCOUNT})
public class DomainUser implements IPerson {

	private static final long serialVersionUID = -4210439762962373670L;

	private String uid;

	private byte[] password;
	
	private Date passwordExpirationTimestamp;
	
	private boolean active;
	
	private Integer gidNumber;
	
	private Integer uidNumber;
	
	private String homeDirectory;
	
	private Name id;
	
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

	private String outlookCity;
		
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

	private String country;
	
	private String title;
		
	public DomainUser() {
		this.active = true;
		this.gidNumber = 1;
		this.uidNumber = 1;
		this.homeDirectory = "/home/";
		this.passwordExpirationTimestamp = new Date();
	}
	
	@Id
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}	

	@RDN
	@Attribute(name="uid", length=256, nullable=false)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}	

	@Attribute(name="userPassword",length=128)
	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	public String getPasswordString() {
		return (password != null) ? new String( password ) : null;
	}
	
	public void setPasswordString( String value ) {
		this.password = (value != null) ? value.getBytes() : null;
	}
	
	@Attribute(name="passwordExpirationTimestamp")
	public Date getPasswordExpirationTimestamp() {
		return passwordExpirationTimestamp;
	}

	public void setPasswordExpirationTimestamp(Date passwordExpirationTimestamp) {
		this.passwordExpirationTimestamp = passwordExpirationTimestamp;
	}

	@Attribute(name="active",nullable=false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Attribute(name="gidNumber",nullable=false)
	public Integer getGidNumber() {
		return gidNumber;
	}

	public void setGidNumber(Integer gidNumber) {
		this.gidNumber = gidNumber;
	}

	@Attribute(name="uidNumber",nullable=false)
	public Integer getUidNumber() {
		return uidNumber;
	}

	public void setUidNumber(Integer uidNumber) {
		this.uidNumber = uidNumber;
	}

	@Attribute(name="homeDirectory",nullable=false)
	public String getHomeDirectory() {
		return homeDirectory;
	}

	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}

	@Attribute(name="displayName")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Attribute(name="cn", nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Attribute(name="givenName")
	public String getOutlookName() {
		return getName();
	}

	public void setOutlookName(String outlookName) {
		setName(outlookName);
	}

	@Attribute(name="sn", nullable=false)
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Attribute(name="o")
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Attribute(name="homePhone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Attribute(name="mobile")
	public String getCellularPhone() {
		return cellularPhone;
	}

	public void setCellularPhone(String cellularPhone) {
		this.cellularPhone = cellularPhone;
	}

	@Attribute(name="facsimileTelephoneNumber")
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Attribute(name="mail",length=256)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Attribute(name="postalAddress")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Attribute(name="postalCode",length=40)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	@Attribute(name="info",length=2048)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Attribute(name="city",length=64)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Attribute(name="l")
	public String getOutlookCity() {
		return outlookCity;
	}

	public void setOutlookCity(String outlookCity) {
		this.outlookCity = outlookCity;
	}

	@Attribute(name="st")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Attribute(name="businessCategory",length=128)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Attribute(name="telephoneNumber",length=32)
	public String getOrganizationPhone() {
		return organizationPhone;
	}

	public void setOrganizationPhone(String organizationPhone) {
		this.organizationPhone = organizationPhone;
	}

	@Attribute(name="oFacsimileTelephoneNumber")
	public String getOrganizationFax() {
		return organizationFax;
	}

	public void setOrganizationFax(String organizationFax) {
		this.organizationFax = organizationFax;
	}

	@Attribute(name="oPostalAddress")
	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}

	@Attribute(name="oPostalCode")
	public String getOrganizationPostalCode() {
		return organizationPostalCode;
	}

	public void setOrganizationPostalCode(String organizationPostalCode) {
		this.organizationPostalCode = organizationPostalCode;
	}

	@Attribute(name="oCity",length=64)
	public String getOrganizationCity() {
		return organizationCity;
	}

	public void setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity;
	}

	@Attribute(name="oSt")
	public String getOrganizationState() {
		return organizationState;
	}

	public void setOrganizationState(String organizationState) {
		this.organizationState = organizationState;
	}

	@Attribute(name="labeledURI")
	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}
	
	@Attribute(name="co")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Attribute(name="title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFullName() {
    	return ((StringUtils.isEmpty(surname)) ? "" : surname + ", ") + ((StringUtils.isEmpty(name)) ? "" : name);
    }	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DomainUser o = (DomainUser) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.address, o.address)
				.append(this.category, o.category)				
				.append(this.cellularPhone, o.cellularPhone)
				.append(this.city, o.city)		
				.append(this.country, o.country)
				.append(this.displayName, o.displayName)
				.append(this.email, o.email)				
				.append(this.fax, o.fax)
				.append(this.gidNumber, o.gidNumber)
				.append(this.homeDirectory, o.homeDirectory)
				.append(this.name, o.name)				
				.append(this.password, o.password)
				.append(this.passwordExpirationTimestamp, o.passwordExpirationTimestamp)
				.append(this.note, o.note)
				.append(this.organization, o.organization)
				.append(this.organizationAddress, o.organizationAddress)				
				.append(this.organizationCity, o.organizationCity)
				.append(this.organizationFax, o.organizationFax)				
				.append(this.organizationPhone, o.organizationPhone)
				.append(this.organizationPostalCode, o.organizationPostalCode)
				.append(this.organizationState, o.organizationState)				
				.append(this.outlookCity, o.outlookCity)		
				.append(this.phone, o.phone)
				.append(this.postalCode, o.postalCode)				
				.append(this.state, o.state)
				.append(this.surname, o.surname)				
				.append(this.title, o.title)
				.append(this.web, o.web)
				.append(this.uid, o.uid)
				.append(this.uidNumber, o.uidNumber)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()	
			.append(active)
			.append(address)
			.append(category)
			.append(cellularPhone)
			.append(city)
			.append(country)
			.append(displayName)
			.append(email)
			.append(fax)
			.append(gidNumber)
			.append(homeDirectory)
			.append(id)	
			.append(name)			
			.append(note)
			.append(organization)			
			.append(organizationAddress)
			.append(organizationCity)			
			.append(organizationFax)
			.append(organizationPhone)			
			.append(organizationPostalCode)
			.append(organizationState)			
			.append(outlookCity)			
			.append(password)
			.append(passwordExpirationTimestamp)
			.append(phone)			
			.append(postalCode)
			.append(state)
			.append(surname)
			.append(title)
			.append(web)
			.append(uid)
			.append(uidNumber)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);		
	}
	
}