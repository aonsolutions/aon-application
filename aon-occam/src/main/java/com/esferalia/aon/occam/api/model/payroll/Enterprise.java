package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class Enterprise  implements Serializable {

	private static final long serialVersionUID = 8795104450454476889L;

	private Integer id;
	private Integer domain;
	
	private Integer calendar;
	
	private DocumentType documentType;
	private Country documentCountry;
	private String document;

	private String name;
	private String surname;
	private String alias;
	
	private RegistryAddress address;
	
	private List<RegistryMedia> medias;
	private List<EnterpriseData> datas;
	
	private Integer scope;
	
	public Enterprise() {
		this.medias = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}

	public Enterprise setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Enterprise setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getCalendar() {
		return calendar;
	}

	public Enterprise setCalendar(Integer calendar) {
		this.calendar = calendar;
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

	public Enterprise setDocumentCountry(Country documentCountry) {
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

	public RegistryAddress getAddress() {
		return address;
	}

	public Enterprise setAddress(RegistryAddress address) {
		this.address = address;
		return this;
	}

	public List<RegistryMedia> getMedias() {
		return medias;
	}

	public Enterprise setMedias(List<RegistryMedia> medias) {
		this.medias = medias;
		return this;
	}

	public List<EnterpriseData> getDatas() {
		return datas;
	}

	public Enterprise setDatas(List<EnterpriseData> datas) {
		this.datas = datas;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public Enterprise setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
}
