package com.esferalia.aon.occam.api.model.invoice;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicatorContext {
	
	private final Domain domain; 
	private final User user;
	private final Integer certificateId;
	private final List<Invoice> invoices;
	private InvoiceCommunicationConfiguration config;
	private Company company;
	private DataResponse dataResponse;

	public InvoiceCommunicatorContext(Domain domain,User user, Integer certificateId, List<Invoice> invoices) {
		this.domain = domain;
		this.user = user;
		this.certificateId = certificateId;
		this.invoices = invoices;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public User getUser() {
		return user;
	}
	public Integer getCertificateId() {
		return certificateId;
	}
	public Stream<Invoice> invoiceStream() {
		return AonCollectionUtils.stream(invoices);
	}
	public int invoiceCount() {
		return AonCollectionUtils.size(invoices);
	}
	public InvoiceCommunicationConfiguration getConfig() {
		return config;
	}
	public InvoiceCommunicatorContext setConfig(InvoiceCommunicationConfiguration config) {
		this.config = config;
		return this;
	}
	public Company getCompany() {
		return company;
	}
	public InvoiceCommunicatorContext setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public DataResponse getDataResponse() {
		return dataResponse;
	}
	public void setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
	}
	
	
}
