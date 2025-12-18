package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Month;

public class InvoiceCommunicationQuery implements Serializable {
	
	private static final long serialVersionUID = -8245633256885112468L;
	
	private Integer year;
	private Month month;
	private Integer id;
	private String referenceCode;
	private String registryDocument;
	private String registryName;
	private Date date;
	private Date fromDate;
	private Date toDate;
	
	public Optional<Integer> getYear() {
		return Optional.ofNullable(year);
	}
	public InvoiceCommunicationQuery setYear(Integer year) {
		this.year = year;
		return this;
	}
	
	public Optional<Month> getMonth() {
		return Optional.ofNullable(month);
	}
	public InvoiceCommunicationQuery setMonth(Month month) {
		this.month = month;
		return this;
	}
	
	public Optional<Integer> getId() {
		return Optional.ofNullable(id);
	}
	public InvoiceCommunicationQuery setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Optional<String> getReferenceCode() {
		return Optional.ofNullable(referenceCode);
	}
	public InvoiceCommunicationQuery setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	
	public Optional<String> getRegistryDocument() {
		return Optional.ofNullable(registryDocument);
	}
	public InvoiceCommunicationQuery setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	
	public Optional<String> getRegistryName() {
		return Optional.ofNullable(registryName);
	}
	public InvoiceCommunicationQuery setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	
	public boolean hasRegistryData() {
		return getRegistryDocument().isPresent() || getRegistryName().isPresent();
	}
	
	public Optional<Date> getDate() {
		return Optional.ofNullable(date);
	}
	public InvoiceCommunicationQuery setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Optional<Date> getFromDate() {
		return Optional.ofNullable(fromDate);
	}
	public InvoiceCommunicationQuery setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	
	public Optional<Date> getToDate() {
		return Optional.ofNullable(toDate);
	}
	public InvoiceCommunicationQuery setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	
}
