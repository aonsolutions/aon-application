package com.code.aon.webmail;

import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

@EntryObject(mainObjectClass=IAonObjectClasses.CONTACT, objectClasses={IAonObjectClasses.TOP})
public class Contact implements ILdapTransferObject {

	private static final long serialVersionUID = 7825997921660369372L;

	private Name id;
	
	private String displayName;
	
	private String name;
	
	private String outlookName;
	
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
	
	private Boolean contactGroup = Boolean.FALSE;
	
	private List<GroupContact> contacts;
	
	@Id
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
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

	@Attribute(name="cn")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Attribute(name="gn")
	public String getOutlookName() {
		return outlookName;
	}

	public void setOutlookName(String outlookName) {
		this.outlookName = outlookName;
	}

	@Attribute(name="sn")
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

	@Attribute(name="contactGroup")
	public Boolean getContactGroup() {
		return contactGroup;
	}

	public void setContactGroup(Boolean contactGroup) {
		this.contactGroup = contactGroup;
	}
		
	@BaseDN("{this}")	
	@Attribute(name="member",baseClass="com.code.aon.webmail.GroupContact")
	public List<GroupContact> getContacts() {
		return contacts;
	}

	public void setContacts(List<GroupContact> contacts) {
		this.contacts = contacts;
	}
	
	private String getEmailLarge( String displayName, String email ) {
		if ( email != null ) {
			String name = displayName;
			if (! StringUtils.isAsciiPrintable(name) ) {
				name = "\"" + name + "\"";
			}
			return name + " &lt;" + email + "&gt;";			
		}
		return null;
	}

	public String getEmailLarge() {
		if ( getContactGroup() ) {
			List<String> list = new LinkedList<String>();
			if ( getContacts() != null ) {
				for( GroupContact gc : getContacts() ) {
					if ( ! StringUtils.isBlank(gc.getEmail()) ) {
						String email = getEmailLarge( gc.getDisplayName(), gc.getEmail() );
						list.add( email );						
					}
				}				
			}
			String emails = StringUtils.join( list, ", " );
			return StringUtils.trimToNull( emails );			
		}
		return getEmailLarge( getDisplayName(), getEmail() );
	}
	
	public String getEmailSummary() {
		String emails = getEmails();
		return StringUtils.abbreviate( emails, 80 );
	}

	public String getEmails() {
		if ( getContactGroup() ) {
			List<String> list = new LinkedList<String>();
			if ( getContacts() != null ) {
				for( GroupContact gc : getContacts() ) {
					if ( ! StringUtils.isBlank(gc.getEmail()) ) {
						list.add( gc.getEmail() );						
					}
				}				
			}
			String emails = StringUtils.join( list, ", " );
			return StringUtils.trimToNull( emails );
		} else {
			return getEmail();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Contact o = (Contact) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.address, o.address)
				.append(this.category, o.category)				
				.append(this.cellularPhone, o.cellularPhone)
				.append(this.city, o.city)		
				.append(this.contactGroup, o.contactGroup)
				.append(this.country, o.country)
				.append(this.displayName, o.displayName)
				.append(this.email, o.email)				
				.append(this.fax, o.fax)
				.append(this.name, o.name)				
				.append(this.note, o.note)
				.append(this.organization, o.organization)
				.append(this.organizationAddress, o.organizationAddress)				
				.append(this.organizationCity, o.organizationCity)
				.append(this.organizationFax, o.organizationFax)				
				.append(this.organizationPhone, o.organizationPhone)
				.append(this.organizationPostalCode, o.organizationPostalCode)
				.append(this.organizationState, o.organizationState)				
				.append(this.outlookCity, o.outlookCity)
				.append(this.outlookName, o.outlookName)				
				.append(this.phone, o.phone)
				.append(this.postalCode, o.postalCode)				
				.append(this.state, o.state)
				.append(this.surname, o.surname)				
				.append(this.title, o.title)
				.append(this.web, o.web)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(category)
			.append(cellularPhone)
			.append(city)
			.append(contactGroup)
			.append(country)
			.append(displayName)
			.append(email)
			.append(fax)
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
			.append(outlookName)
			.append(phone)			
			.append(postalCode)
			.append(state)
			.append(surname)
			.append(title)
			.append(web)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}