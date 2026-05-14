package com.esferalia.aon.occam.api.model.invoice;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.console.ConsoleLogger;
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
	private Person person;
	private DataResponse dataResponse;
	private boolean preserveRawdocOnDeletion;
	private boolean failOnWrongValidation = true;
	private InvoiceCommunicationQuery communicationQuery;
	private ConsoleLogger logger;
	
	private static final ConsoleLogger VOID_CONSOLE_LOOGER = new ConsoleLogger() {

		@Override public void title(String id, String msg) {/* Nothing */}
		@Override public void subtitle(String id, String msg) {/* Nothing */}
		@Override public void ok(String id, String msg) {/* Nothing */}
		@Override public void error(String id, String msg) {/* Nothing */}
		@Override public void warning(String id, String msg) {/* Nothing */}
		@Override public void message(String id, String msg) {/* Nothing */}
		@Override public void progress(String id, int count, int progress, String msg) {/* Nothing */}
		@Override public void progress(String id, int count, int progress) {/* Nothing */}
		@Override public void mainProgress(String id, int count, int progress) {/* Nothing */}
	};

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

	public boolean isFailOnWrongValidation() {
		return failOnWrongValidation;
	}
	public InvoiceCommunicatorContext setFailOnWrongValidation(boolean failOnWrongValidation) {
		this.failOnWrongValidation = failOnWrongValidation;
		return this;
	}

	public ConsoleLogger getLogger() {
		return logger==null?VOID_CONSOLE_LOOGER:logger;
	}
	public InvoiceCommunicatorContext setLogger(ConsoleLogger logger) {
		this.logger = logger;
		return this;
	}
	
}
