package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Enterprise implements Serializable {

	private static final long serialVersionUID = 8795104450454476889L;
	
	private Integer id;
	private int domain;
	
	private DocumentType documentType;
	private Country documentCountry;
	private String document;

	private String name;
	private String surname;
	private String alias;
	
	private StreetType streetType;
	private String address;
	private String number;
	private String address2;
	private String address3;
	private Province province;
	private String zip;
	private String town;
	private String city;

	private String phone;
	private String fax;
	private String email;
	private String web;
	
	private Integer scope;
	
	public Integer getId() {
		return id;
	}
	
	public Enterprise setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}
	
	public Enterprise setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	
	public Enterprise setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	
	public Enterprise  setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}

	public String getDocument() {
		return document;
	}
	
	public Enterprise setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Enterprise setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public Enterprise setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	
	public Enterprise setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public StreetType getStreetType() {
		return streetType;
	}
	
	public Enterprise setStreetType(StreetType streetType) {
		this.streetType = streetType;
		return this;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Enterprise setAddress(String address) {
		this.address = address;
		return this;
	}
	
	public String getNumber() {
		return number;
	}
	
	public Enterprise setNumber(String number) {
		this.number = number;
		return this;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public Enterprise setAddress2(String address2) {
		this.address2 = address2;
		return this;
	}
	
	public String getAddress3() {
		return address3;
	}
	
	public Enterprise setAddress3(String address3) {
		this.address3 = address3;
		return this;
	}
	
	public Province getProvince() {
		return province;
	}
	
	public Enterprise setProvince(Province province) {
		this.province = province;
		return this;
	}
	
	public String getZip() {
		return zip;
	}
	
	public Enterprise setZip(String zip) {
		this.zip = zip;
		return this;
	}
	
	public String getTown() {
		return town;
	}
	
	public Enterprise setTown(String town) {
		this.town = town;
		return this;
	}
	
	public String getCity() {
		return city;
	}
	
	public Enterprise setCity(String city) {
		this.city = city;
		return this;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public Enterprise setPhone(String phone) {
		this.phone = phone;
		return this;
	}
	
	public String getFax() {
		return fax;
	}
	
	public Enterprise setFax(String fax) {
		this.fax = fax;
		return this;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Enterprise setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public String getWeb() {
		return web;
	}
	
	public Enterprise setWeb(String web) {
		this.web = web;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	
	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
	public String toString() {
		return AonStringUtils.join(
				AonStringUtils.trimToEmpty(name)				
				,AonStringUtils.isNotBlank(surname)?AonStringUtils.SPACE:AonStringUtils.EMPTY
				,AonStringUtils.trimToEmpty(surname)
				,AonStringUtils.isNotBlank(document)?AonStringUtils.SPACE:AonStringUtils.EMPTY
				,AonStringUtils.isNotBlank(document)?'[':AonStringUtils.EMPTY
				,AonStringUtils.trimToEmpty(document)
				,AonStringUtils.isNotBlank(document)?']':AonStringUtils.EMPTY
				);
	}
}
