package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Offer implements Serializable {
	
	private static final long serialVersionUID = -204612166516482195L;
	
	private Integer id;
	private int domain;
	private String series;
	private int number;
	private int version;
	private Date issueDate;
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private String registryTown;
	private String registryZIP;
	private String registryProvinceCode;
	private String registryProvince;
	private String scope;
	private Seller seller;
	private String project;
	private String workPlace;
	private OfferStatus status;
	private OfferType type;
	
	public Integer getId() {
		return id;
	}
	public Offer setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Offer setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Offer setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Offer setNumber(int number) {
		this.number = number;
		return this;
	}
	public int getVersion() {
		return version;
	}
	public Offer setVersion(int version) {
		this.version = version;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Offer setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public Offer setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Offer setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Offer setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Offer setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Offer setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public String getRegistryTown() {
		return registryTown;
	}
	public Offer setRegistryTown(String registryTown) {
		this.registryTown = registryTown;
		return this;
	}
	public String getRegistryZIP() {
		return registryZIP;
	}
	public Offer setRegistryZIP(String registryZIP) {
		this.registryZIP = registryZIP;
		return this;
	}
	public String getRegistryProvinceCode() {
		return registryProvinceCode;
	}
	public Offer setRegistryProvinceCode(String registryProvinceCode) {
		this.registryProvinceCode = registryProvinceCode;
		return this;
	}
	public String getRegistryProvince() {
		return registryProvince;
	}
	public Offer setRegistryProvince(String registryProvince) {
		this.registryProvince = registryProvince;
		return this;
	}
	public String getScope() {
		return scope;
	}
	public Offer setScope(String scope) {
		this.scope = scope;
		return this;
	}
	public Seller getSeller() {
		return seller;
	}
	public Offer setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	public String getProject() {
		return project;
	}
	public Offer setProject(String project) {
		this.project = project;
		return this;
	}
	public String getWorkPlace() {
		return workPlace;
	}
	public Offer setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
		return this;
	}
	public OfferStatus getStatus() {
		return status;
	}
	public Offer setStatus(OfferStatus status) {
		this.status = status;
		return this;
	}
	public OfferType getType() {
		return type;
	}
	public Offer setType(OfferType type) {
		this.type = type;
		return this;
	}
	
    public String getReferenceCode() {
    	String referenceCode = AonStringUtils.leftPad(Integer.toString(getNumber()), 6, '0');
    	referenceCode += '/' + getVersion();
		if (!AonStringUtils .isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }
}

