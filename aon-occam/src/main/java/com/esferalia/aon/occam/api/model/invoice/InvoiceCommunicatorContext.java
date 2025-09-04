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
	private boolean preserveRawdocOnDeletion;
	
//	private boolean error;
//	private String errorMessage;

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
	public InvoiceCommunicatorContext setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}
	
	public boolean isPreserveRawdocOnDeletion() {
		return preserveRawdocOnDeletion;
	}
	public InvoiceCommunicatorContext setPreserveRawdocOnDeletion(boolean preserveRawdocOnDeletion) {
		this.preserveRawdocOnDeletion = preserveRawdocOnDeletion;
		return this;
	}

//	public boolean isError() {
//		return error;
//	}
//	public InvoiceCommunicatorContext setError(boolean error) {
//		this.error = error;
//		return this;
//	}
//
//	public String getErrorMessage() {
//		return errorMessage;
//	}
//	public InvoiceCommunicatorContext setErrorMessage(String errorMessage) {
//		this.errorMessage = errorMessage;
//		return this;
//	}
	
	
}
