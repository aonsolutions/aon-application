package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicationParams implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String query;
	private Integer domain;
	private Date from;
	private Date to;
	private List<InvoiceType> type;
	private InvoiceCommunicationType communicationType;
	private InvoiceCommunicationStatus communicationStatus;
	private Integer page;
	private Integer perPage;
	
	public InvoiceCommunicationParams() {
		page = 1; 
		perPage = 50;
	}
	
	public String getQuery() {
		return query;
	}
	public InvoiceCommunicationParams setQuery(String query) {
		this.query = query;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public InvoiceCommunicationParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Date getFrom() {
		return from;
	}
	public InvoiceCommunicationParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public InvoiceCommunicationParams setTo(Date to) {
		this.to = to;
		return this;
	}
	public List<InvoiceType> getType() {
		return type;
	}
	public InvoiceCommunicationParams setType(List<InvoiceType> types) {
		if (types == null) this.type = null; 
		this.type = AonCollectionUtils.stream(types)
			.filter(t -> t != null)
			.collect(Collectors.toCollection(LinkedList::new));
		return this;
	}
	public InvoiceCommunicationParams setType(InvoiceType ... types ) {
		if (types == null || types.length == 0) this.type = null; 
		this.type = AonCollectionUtils.stream(types)
			.filter(t -> t != null)
			.collect(Collectors.toCollection(LinkedList::new));
		return this;
	}
	public InvoiceCommunicationParams setType(InvoiceType invoiceType) {
		this.type = AonCollectionUtils.toList(invoiceType);
		return this;
	}
	
	public InvoiceCommunicationType getCommunicationType() {
		return communicationType;
	}
	public InvoiceCommunicationParams setCommunicationType(InvoiceCommunicationType communicationType) {
		this.communicationType = communicationType;
		return this;
	}
	public InvoiceCommunicationStatus getCommunicationStatus() {
		return communicationStatus;
	}
	public InvoiceCommunicationParams setCommunicationStatus(InvoiceCommunicationStatus communicationStatus) {
		this.communicationStatus = communicationStatus;
		return this;
	}
	public int getPage() {
		return page;
	}
	public InvoiceCommunicationParams setPage(int page) {
		this.page = page;
		return this;
	}
	public int getSafePage() {
		return (page == null) ? 1 : page;
	}
	public int getPerPage() {
		return perPage;
	}
	public InvoiceCommunicationParams setPerPage(int perPage) {
		this.perPage = perPage;
		return this;
	}
	public int getSafePerPage() {
		return (perPage == null) ? Integer.MAX_VALUE : perPage;
	}
	public int getOffset() {
		return getSafePerPage() * (getSafePage() - 1);	
	}
}
