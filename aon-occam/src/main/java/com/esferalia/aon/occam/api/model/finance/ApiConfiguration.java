package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.Administration;

public class ApiConfiguration implements Serializable {
	
	private static final long serialVersionUID = 8891213108571781948L;
	
	private CompanyFull company;
	private Administration administration;
	private Integer defaultVat;
	private Integer defaultRetention;
	private String defaultSeries;
	private List<Tax> taxes;
	private List<Series> series;
	private List<Workplace> workplaces;
	private PrintInvoiceConfiguration printConfiguration;
	private InvoiceCommunicationConfiguration communicationConfiguration;
	private InvofoxConfiguration invofoxConfiguration;
	
	public CompanyFull getCompany() {
		return company;
	}
	public ApiConfiguration setCompany(CompanyFull company) {
		this.company = company;
		return this;
	}
	
	public Administration getAdministration() {
		return administration;
	}
	public ApiConfiguration setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public Integer getDefaultVat() {
		return defaultVat;
	}
	public ApiConfiguration setDefaultVat(Integer defaultVat) {
		this.defaultVat = defaultVat;
		return this;
	}
	
	public Integer getDefaultRetention() {
		return defaultRetention;
	}
	public ApiConfiguration setDefaultRetention(Integer defaultRetention) {
		this.defaultRetention = defaultRetention;
		return this;
	}
	
	public String getDefaultSeries() {
		return defaultSeries;
	}
	
	public ApiConfiguration setDefaultSeries(String defaultSeries) {
		this.defaultSeries = defaultSeries;
		return this;
	}
	
	public List<Tax> getTaxes() {
		return taxes;
	}
	public ApiConfiguration setTaxes(List<Tax> taxes) {
		this.taxes = taxes;
		return this;
	}
	
	public List<Series> getSeries() {
		return series;
	}
	public ApiConfiguration setSeries(List<Series> series) {
		this.series = series;
		return this;
	}
	
	public List<Workplace> getWorkplaces() {
		return workplaces;
	}
	public ApiConfiguration setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
		return this;
	}
	
	public PrintInvoiceConfiguration getPrintConfiguration() {
		return printConfiguration;
	}
	public ApiConfiguration setPrintConfiguration(PrintInvoiceConfiguration printConfiguration) {
		this.printConfiguration = printConfiguration;
		return this;
	}
	
	public InvoiceCommunicationConfiguration getCommunicationConfiguration() {
		return communicationConfiguration;
	}
	public ApiConfiguration setCommunicationConfiguration(InvoiceCommunicationConfiguration communicationConfiguration) {
		this.communicationConfiguration = communicationConfiguration;
		return this;
	}

	public InvofoxConfiguration getInvofoxConfiguration() {
		return invofoxConfiguration;
	}
	public ApiConfiguration setInvofoxConfiguration(InvofoxConfiguration invofoxConfiguration) {
		this.invofoxConfiguration = invofoxConfiguration;
		return this;
	}
}
