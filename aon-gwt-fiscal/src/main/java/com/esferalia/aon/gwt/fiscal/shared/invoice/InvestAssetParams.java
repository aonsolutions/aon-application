package com.esferalia.aon.gwt.fiscal.shared.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class InvestAssetParams implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String value;
	private Integer domain;
	private Date from;
	private Date to;
	private List<InvoiceType> type;
	private InvoiceCommunicationType communicationType;
	private InvoiceCommunicationStatus communicationStatus;
	private int page;
	private int perPage;

	public InvestAssetParams() {
		this.page = 1;
		this.perPage = 30;
	}

	public Integer getDomain() {
		return domain;
	}
	
	public InvestAssetParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getValue() {
		return value;
	}

	public InvestAssetParams setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getFrom() {
		return from;
	}

	public InvestAssetParams setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Date getTo() {
		return to;
	}

	public InvestAssetParams setTo(Date to) {
		this.to = to;
		return this;
	}

	public List<InvoiceType> getType() {
		return type;
	}

	public InvestAssetParams setType(List<InvoiceType> type) {
		this.type = type;
		return this;
	}
	
	public InvestAssetParams setType(InvoiceType type) {
		this.type = new LinkedList<>();
		this.type.add(type);
		return this;
	}
	
	public InvestAssetParams addType(InvoiceType type) {
		if(this.type == null) {
			this.type = new LinkedList<>();
		}
		this.type.add(type);
		return this;
	}
	
	public InvoiceCommunicationType getCommunicationType() {
		return communicationType;
	}

	public InvestAssetParams setCommunicationType(InvoiceCommunicationType communicationType) {
		this.communicationType = communicationType;
		return this;
	}
	
	public InvoiceCommunicationStatus getCommunicationStatus() {
		return communicationStatus;
	}
	
	public InvestAssetParams setCommunicationStatus(InvoiceCommunicationStatus communicationStatus) {
		this.communicationStatus = communicationStatus;
		return this;
	}
	
	public int getPage() {
		return page;
	}

	public InvestAssetParams setPage(int page) {
		this.page = page;
		return this;
	}

	public int getPerPage() {
		return perPage;
	}

	public InvestAssetParams setPerPage(int perPage) {
		this.perPage = perPage;
		return this;
	}
}
