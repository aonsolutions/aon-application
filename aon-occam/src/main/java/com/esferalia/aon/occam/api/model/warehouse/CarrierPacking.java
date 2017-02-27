package com.esferalia.aon.occam.api.model.warehouse;

import java.util.Date;

public class CarrierPacking {

	private Integer id;
	private Integer domain;
	private String series;
	private Integer number;
	private CarrierPackingType type;
	private CarrierPackingStatus status;
	private Date issueDate;
	private Integer carrier;
	private String carrierName;
	private Date deliveryDate;
	private String carrierReference;
	private String numberPlate;
	private String driverName;
	private String driverDocument;
	private String comments;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	public CarrierPacking setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public CarrierPacking setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public CarrierPacking setSeries(String series) {
		this.series = series;
		return this;		
	}
	public Integer getNumber() {
		return number;
	}
	public CarrierPacking setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public CarrierPackingType getType() {
		return type;
	}
	public CarrierPacking setType(CarrierPackingType type) {
		this.type = type;
		return this;
	}
	public CarrierPackingStatus getStatus() {
		return status;
	}
	public CarrierPacking setStatus(CarrierPackingStatus status) {
		this.status = status;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public CarrierPacking setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Integer getCarrier() {
		return carrier;
	}
	public CarrierPacking setCarrier(Integer carrier) {
		this.carrier = carrier;
		return this;
	}
	public String getCarrierName() {
		return carrierName;
	}
	public CarrierPacking setCarrierName(String carrierName) {
		this.carrierName = carrierName;
		return this;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public CarrierPacking setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}
	public String getCarrierReference() {
		return carrierReference;
	}
	public CarrierPacking setCarrierReference(String carrierReference) {
		this.carrierReference = carrierReference;
		return this;
	}
	public String getNumberPlate() {
		return numberPlate;
	}
	public CarrierPacking setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
		return this;
	}
	public String getDriverName() {
		return driverName;
	}
	public CarrierPacking setDriverName(String driverName) {
		this.driverName = driverName;
		return this;
	}
	public String getDriverDocument() {
		return driverDocument;
	}
	public CarrierPacking setDriverDocument(String driverDocument) {
		this.driverDocument = driverDocument;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public CarrierPacking setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public CarrierPacking setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public CarrierPacking setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public CarrierPacking setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public CarrierPacking setComments(String comments) {
		this.comments = comments;
		return this;
	}
}
