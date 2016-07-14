package com.esferalia.aon.occam.api.model.project;

import java.util.Date;

public class ProjectReservationServiceDetail {

	Integer domain;
	Date effectiveDate;
	Integer id;
	Double price;
	Integer projectReservationRoomDetail;
	Integer projectReservationService;
	Double quantity;
	Double taxableBase;
	
	public Integer getDomain() {
		return domain;
	}
	public ProjectReservationServiceDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public ProjectReservationServiceDetail setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public ProjectReservationServiceDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public ProjectReservationServiceDetail setPrice(Double price) {
		this.price = price;
		return this;
	}
	public Integer getProjectReservationRoomDetail() {
		return projectReservationRoomDetail;
	}
	public ProjectReservationServiceDetail setProjectReservationRoomDetail(Integer projectReservationRoomDetail) {
		this.projectReservationRoomDetail = projectReservationRoomDetail;
		return this;
	}
	public Integer getProjectReservationService() {
		return projectReservationService;
	}
	public ProjectReservationServiceDetail setProjectReservationService(Integer projectReservationService) {
		this.projectReservationService = projectReservationService;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public ProjectReservationServiceDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Double getTaxableBase() {
		return taxableBase;
	}
	public ProjectReservationServiceDetail setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
}
