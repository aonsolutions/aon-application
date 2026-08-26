package com.esferalia.aon.occam.api.model.invoice;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicatorContext {
	
	private final Domain domain; 
	private final User user;
	private final Integer certificateId;
	private final List<Invoice> invoices;
	private InvoiceCommunicationConfiguration config;
	private Company company;
	private Person person;
	private DataResponse dataResponse;
	private boolean preserveRawdocOnDeletion;
	private InvoiceCommunicationQuery communicationQuery;
	
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
	
	public Person getPerson() {
		return person;
	}
	
	public InvoiceCommunicatorContext setPerson(Person person) {
		this.person = person;
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
	
	public InvoiceCommunicationQuery getCommunicationQuery() {
		return communicationQuery;
	}
	public InvoiceCommunicatorContext setCommunicationQuery(InvoiceCommunicationQuery communicationQuery) {
		this.communicationQuery = communicationQuery;
		return this;
	}

	public Integer getExercise() throws InvoiceCommunicationException {
		List<Integer> exercises = invoiceStream()
			.map(InvoiceCommunicatorContext::exerciseDate)
			.filter(Objects::nonNull)
			.map(AonDateUtils::getYear)
			.distinct()
			.collect(Collectors.toList());
		
		if (AonCollectionUtils.isEmpty(exercises)) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0036);
		}
		if (exercises.size() > 1) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0035);
		}
		return exercises.get(0);
	}
	
	/**
	 * Fecha que determina el ejercicio de la factura: fecha de expedici\u00F3n en las
	 * facturas emitidas y fecha de recepci\u00F3n en las recibidas.
	 */
	private static Date exerciseDate(Invoice invoice) {
		if (invoice == null) return null;
		return invoice.isSales()
			? invoice.getExpDate()
			: invoice.getIssueDate();
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
