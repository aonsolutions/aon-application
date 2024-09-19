package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class InvoiceMin implements Serializable {
	
	private static final long serialVersionUID = 8998986993895056017L;
	
	private Integer id;
	private Integer domain;
	private Integer  activity;
	private String activityName;
	private String activityEpigraph;
	private InvoiceType type;
	private String series;
	private int number;
	private String referenceCode;
	private InvoiceTransactionType transaction;
	private Date issueDate;
	private Date taxDate;
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Integer scope;
	private SecurityLevel securityLevel;
	private boolean recorded;
	private RectificationType rectificationType;
	private Integer rectificationInvoiceId;
	private double total;

	public Integer getId() {
		return id;
	}
	public InvoiceMin setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceMin setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getActivity() {
		return activity;
	}
	public InvoiceMin setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public String getActivityEpigraph() {
		return activityEpigraph;
	}
	public InvoiceMin setActivityEpigraph(String activityEpigraph) {
		this.activityEpigraph = activityEpigraph;
		return this;
	}

	public String getActivityName() {
		return activityName;
	}
	public InvoiceMin setActivityName(String activityName) {
		this.activityName= activityName;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public InvoiceMin setType(InvoiceType type) {
		this.type = type;
		return this;
	}

	public String getSeries() {
		return series;
	}
	public InvoiceMin setSeries(String series) {
		this.series = series;
		return this;
	}
	
	public int getNumber() {
		return number;
	}
	public InvoiceMin setNumber(int number) {
		this.number = number;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public InvoiceMin setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public InvoiceMin setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}

	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceMin setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public InvoiceMin setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public InvoiceMin setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public InvoiceMin setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public InvoiceMin setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public InvoiceMin setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	
	public String getRegistryName() {
		return registryName;
	}
	public InvoiceMin setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public InvoiceMin setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public InvoiceMin setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}

	public Integer getScope() {
		return scope;
	}
	public InvoiceMin setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public boolean isRecorded() {
		return recorded;
	}
	public InvoiceMin setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public InvoiceMin setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
		return this;
	}
	public Integer getRectificationInvoiceId() {
		return rectificationInvoiceId;
	}
	public InvoiceMin setRectificationInvoiceId(Integer rectificationInvoiceId) {
		this.rectificationInvoiceId = rectificationInvoiceId;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public InvoiceMin setTotal(double total) {
		this.total = total;
		return this;
	}

	public String getDocumentNumber() {
		return FinanceUtil.getDocumentNumber(type, series, number);
	}
	
	public String getSeriesNumber() {
		return FinanceUtil.getSeriesNumber(series, number);
	}
	
	public static InvoiceMin to(Invoice inv) {
		if (inv == null) return null;
		return new InvoiceMin()
			.setId(inv.getId())
			.setDomain(inv.getDomain())
			.setActivity( inv.getActivity().map( a -> a.getId()).orElse(null))
			.setActivityName(inv.getActivity().map( a -> a.getDescription()).orElse(null))
			.setActivityEpigraph(inv.getActivity().map( a -> a.getEpigraph()).orElse(null))
			.setType(inv.getType())
			.setSeries(inv.getSeries())
			.setNumber(inv.getNumber())
			.setReferenceCode(inv.getReferenceCode())
			.setTransaction(inv.getTransaction())
			.setIssueDate(inv.getIssueDate())
			.setTaxDate(inv.getTaxDate())
			.setRegistry(inv.getRegistry())
			.setRegistryDocument(inv.getRegistryDocument())
			.setRegistryDocumentType(inv.getRegistryDocumentType())
			.setRegistryDocumentCountry(inv.getRegistryDocumentCountry())
			.setRegistryName(inv.getRegistryName())
			.setScope(Optional.ofNullable(inv.getScope()).map( s -> s.getId()).orElse(null))
			.setConfidential(inv.isConfidential())
			.setRecorded(inv.isRecorded())
			.setRectificationType(inv.getRectificationType())
			.setRectificationInvoiceId(inv.getRectificationInvoiceId())
			.setTotal(inv.getTotal())
		;		
		
	}
	
}

