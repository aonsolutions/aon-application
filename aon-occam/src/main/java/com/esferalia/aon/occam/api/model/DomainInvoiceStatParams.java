package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class DomainInvoiceStatParams implements Serializable {

	private static final long serialVersionUID = 7660434240269902891L;

	private int domain;
	
	private Date fromDate;
	private Date toDate;
	private Integer active;
	private String query;
	private Integer impersonatedUser;
	private Integer scope;
	private Integer scoredFilter;
	private FiscalModelType fiscalModelType;
	
	private Boolean invoices;
	private Boolean alcatraz;
	private Boolean rawdoc;
	
	private int limit;
	private int offset;
	
	public int getDomain() {
		return domain;
	}
	public DomainInvoiceStatParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Optional<Date> getFromDate() {
		return Optional.ofNullable(fromDate);
	}
	public DomainInvoiceStatParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	
	public Optional<Date> getToDate() {
		return Optional.ofNullable(toDate);
	}
	public DomainInvoiceStatParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	
	public Optional<Integer> getActive() {
		return Optional.ofNullable(active);
	}
	public DomainInvoiceStatParams setActive(Integer active) {
		this.active = active;
		return this;
	}
	
	public Optional<String> getQuery() {
		return Optional.ofNullable(query);
	}
	public DomainInvoiceStatParams setQuery(String query) {
		this.query = query;
		return this;
	}
	
	public Optional<Integer> getImpersonatedUser() {
		return Optional.ofNullable(impersonatedUser);
	}
	public DomainInvoiceStatParams setImpersonatedUser(Integer impersonatedUser) {
		this.impersonatedUser = impersonatedUser;
		return this;
	}
	
	public Optional<Integer> getScope() {
		return Optional.ofNullable(scope);
	}
	public DomainInvoiceStatParams setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public Optional<Integer> getScoredFilter() {
		return Optional.ofNullable(scoredFilter);
	}
	public DomainInvoiceStatParams setScoredFilter(Integer scoredFilter) {
		this.scoredFilter = scoredFilter;
		return this;
	}
	
	public Optional<FiscalModelType> getFiscalModelType() {
		return Optional.ofNullable(fiscalModelType);
	}
	public DomainInvoiceStatParams setFiscalModelType(FiscalModelType fiscalModelType) {
		this.fiscalModelType = fiscalModelType;
		return this;
	}
	
	public Optional<Boolean> getInvoices() {
		return Optional.ofNullable(invoices);
	}
	public DomainInvoiceStatParams setInvoices(Boolean invoices) {
		this.invoices = invoices;
		return this;
	}
	
	public Optional<Boolean> getAlcatraz() {
		return Optional.ofNullable(alcatraz);
	}
	public DomainInvoiceStatParams setAlcatraz(Boolean alcatraz) {
		this.alcatraz = alcatraz;
		return this;
	}
	
	public Optional<Boolean> getRawdoc() {
		return Optional.ofNullable(rawdoc);
	}
	public DomainInvoiceStatParams setRawdoc(Boolean rawdoc) {
		this.rawdoc = rawdoc;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	public DomainInvoiceStatParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	public DomainInvoiceStatParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
}
