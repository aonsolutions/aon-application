package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CarrierPacking implements Serializable{

	private Integer id;
	private Integer domain;
	private String series;
	private Integer number;
	private CarrierPackingType type;
	private CarrierPackingStatus status;
	private Date issueDate;
	private Integer carrier;
	private String carrierName;
	private String carrierDocument;
	private Date deliveryDate;
	private String carrierReference;
	private String numberPlate;
	private String driverName;
	private String driverDocument;
	
	private String comments;
	private String params;
	private String observation;
	
	private Double gross; // PESO BRUTO
	private Double tare; // TARA
	private Double additionalTare; // TARA
	private Double net; // PESO NETO
	private Date receptionStartDate; // FECHA ENTRADA TRANSPORTE (RECEPCIÓN)
	private Date receptionEndDate; // FECHA SALIDA TRANSPORTE (RECEPCIÓN)
	
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
	
	public String getCarrierDocument() {
		return carrierDocument;
	}
	public CarrierPacking setCarrierDocument(String carrierDocument) {
		this.carrierDocument = carrierDocument;
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
		separateComments(comments);
		return this;
	}
	public String getParams() {
		return params;
	}
	public CarrierPacking setParams(String params) {
		this.params = params;
		return this;
	}
	public String getObservation() {
		return observation;
	}
	public CarrierPacking setObservation(String observation) {
		this.observation = observation;
		return this;
	}
	

	public Double getGross() {
		return gross;
	}
	public CarrierPacking setGross(Double gross) {
		this.gross = gross;
		return this;
	}
	public Double getTare() {
		return tare;
	}
	public CarrierPacking setTare(Double tare) {
		this.tare = tare;
		return this;
	}
	public Double getAdditionalTare() {
		return additionalTare;
	}
	public CarrierPacking setAdditionalTare(Double additionalTare) {
		this.additionalTare = additionalTare;
		return this;
	}
	
	public Double getNet() {
		return net;
	}
	public CarrierPacking setNet(Double net) {
		this.net = net;
		return this;
	}
	public Date getReceptionStartDate() {
		return receptionStartDate;
	}
	public CarrierPacking setReceptionStartDate(Date receptionStartDate) {
		this.receptionStartDate = receptionStartDate;
		return this;
	}
	public Date getReceptionEndDate() {
		return receptionEndDate;
	}
	public CarrierPacking setReceptionEndDate(Date receptionEndDate) {
		this.receptionEndDate = receptionEndDate;
		return this;
	}
	public void separateComments(String comments) {
		if(comments == null){
			comments = "";
		}
		Integer a = comments.indexOf("<params>");
		Integer z = comments.indexOf("</params>") + 9;
		if(a.equals(-1) || z < 9){
			setObservation(comments);
			setParams("");
		}else {
			setObservation(comments.substring(0,a) + comments.substring(z));
			setParams(comments.substring(a,z));
		}
	}
}
