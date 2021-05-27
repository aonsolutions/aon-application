package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ContactData implements Serializable{

	String address;
	String cellularPhone;
	String city;
	String contactstate;
	String country;
	Integer domain;
	String email;
	String fax;
	Integer id;
	String name;
	String note;
	String organization;
	String organizationAddress;
	String organizationCity;
	String organizationFax;
	String organizationPhone;
	String organizationPostalCode;
	String organizationstate;
	String phone;
	String postalCode;
	String surname;
	String title;
	String web;
	
	public ContactData() {}

	public String getAddress() {
		return address;
	}

	public ContactData setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getCellularPhone() {
		return cellularPhone;
	}

	public ContactData setCellularPhone(String cellularPhone) {
		this.cellularPhone = cellularPhone;
		return this;
	}

	public String getCity() {
		return city;
	}

	public ContactData setCity(String city) {
		this.city = city;
		return this;
	}

	public String getContactstate() {
		return contactstate;
	}

	public ContactData setContactstate(String contactstate) {
		this.contactstate = contactstate;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public ContactData setCountry(String country) {
		this.country = country;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ContactData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public ContactData setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFax() {
		return fax;
	}

	public ContactData setFax(String fax) {
		this.fax = fax;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public ContactData setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public ContactData setName(String name) {
		this.name = name;
		return this;
	}

	public String getNote() {
		return note;
	}

	public ContactData setNote(String note) {
		this.note = note;
		return this;
	}

	public String getOrganization() {
		return organization;
	}

	public ContactData setOrganization(String organization) {
		this.organization = organization;
		return this;
	}

	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public ContactData setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
		return this;
	}

	public String getOrganizationCity() {
		return organizationCity;
	}

	public ContactData setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity;
		return this;
	}

	public String getOrganizationFax() {
		return organizationFax;
	}

	public ContactData setOrganizationFax(String organizationFax) {
		this.organizationFax = organizationFax;
		return this;
	}

	public String getOrganizationPhone() {
		return organizationPhone;
	}

	public ContactData setOrganizationPhone(String organizationPhone) {
		this.organizationPhone = organizationPhone;
		return this;
	}

	public String getOrganizationPostalCode() {
		return organizationPostalCode;
	}

	public ContactData setOrganizationPostalCode(String organizationPostalCode) {
		this.organizationPostalCode = organizationPostalCode;
		return this;
	}

	public String getOrganizationstate() {
		return organizationstate;
	}

	public ContactData setOrganizationstate(String organizationstate) {
		this.organizationstate = organizationstate;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public ContactData setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public ContactData setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public ContactData setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ContactData setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getWeb() {
		return web;
	}

	public ContactData setWeb(String web) {
		this.web = web;
		return this;
	}
	
}
