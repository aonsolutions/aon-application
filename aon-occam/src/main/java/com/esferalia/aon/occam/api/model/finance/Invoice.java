package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;

public class Invoice implements Serializable, HasAudit {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private Integer id;
	private int domain;
	private Integer activity; 				//**
	private String epigraph;
	private Integer investAsset;			//**
	private Integer project;				//**
	private String series;
	private int number;
	private String referenceCode;
	private Date issueDate;
	private Date taxDate;
	private RectificationType rectificationType;
	private SecurityLevel securityLevel;
	private Integer rectificationInvoice;
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Integer registryAddress;		//**
	private StreetType addressStreetType;	//**
	private String address;					//**
	private String addressNumber;			//**
	private String addressTown;
	private String addressZIP;
	private Integer addressGeozone;			//**
	private String addressProvinceCode;
	private String addressProvince;
	private Scope scope;
	private InvoiceType type;
	private InvoiceTransactionType transaction;
	private boolean recorded;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private boolean investment;
	private boolean service;
	private boolean advance;
	private boolean signed;
	private double taxableBase;
	private double vatQuota;
	private double retentionQuota;
	private double total;
	private Integer posShift;
	private Integer seller;
	
	private String comments;
	private String remarks;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	private LinkedList<InvoiceDetail> details;
	
	public Integer getId() {
		return id;
	}
	public Invoice setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Invoice setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public Invoice setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public Invoice setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	public Integer getInvestAsset() {
		return investAsset;
	}
	public Invoice setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public Invoice setProject(Integer project) {
		this.project = project;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Invoice setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Invoice setNumber(int number) {
		this.number = number;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public Invoice setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Invoice setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public Invoice setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public Invoice setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
		return this;
	}
	public boolean isRectified() {
		return (getRectificationType() == RectificationType.RECTIFIED);
	}
	public boolean isRectifier() {
		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER 
			|| getRectificationType() == RectificationType.SPECIAL_RECTIFIER);
	}
	public Integer getRectificationInvoice() {
		return rectificationInvoice;
	}
	public Invoice setRectificationInvoice(Integer rectificationInvoice) {
		this.rectificationInvoice = rectificationInvoice;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public Invoice setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Invoice setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Invoice setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Invoice setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Invoice setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public Integer getRegistryAddress() {
		return registryAddress;
	}
	public Invoice setRegistryAddress(Integer registryAddress) {
		this.registryAddress = registryAddress;
		return this;
	}
	public StreetType getAddressStreetType() {
		return addressStreetType;
	}
	public Invoice setAddressStreetType(StreetType addressStreetType) {
		this.addressStreetType = addressStreetType;
		return this;
	}
	public String getAddress() {
		return address;
	}
	public Invoice setAddress(String address) {
		this.address = address;
		return this;
	}
	public String getAddressNumber() {
		return addressNumber;
	}
	public Invoice setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
		return this;
	}
	public String getAddressTown() {
		return addressTown;
	}
	public Invoice setAddressTown(String addressTown) {
		this.addressTown = addressTown;
		return this;
	}
	public String getAddressZIP() {
		return addressZIP;
	}
	public Invoice setAddressZIP(String addressZIP) {
		this.addressZIP = addressZIP;
		return this;
	}
	public Integer getAddressGeozone() {
		return addressGeozone;
	}
	public Invoice setAddressGeozone(Integer addressGeozone) {
		this.addressGeozone = addressGeozone;
		return this;
	}
	public String getAddressProvinceCode() {
		return addressProvinceCode;
	}
	public Invoice setAddressProvinceCode(String addressProvinceCode) {
		this.addressProvinceCode = addressProvinceCode;
		return this;
	}
	public String getAddressProvince() {
		return addressProvince;
	}
	public Invoice setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
		return this;
	}
	public Scope getScope() {
		return scope;
	}
	public Invoice setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.type = type;
		return this;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Invoice setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	public boolean isRecorded() {
		return recorded;
	}
	public Invoice setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public Invoice setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public boolean isWithholding() {
		return withholding;
	}
	public Invoice setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Invoice setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Invoice setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public boolean isInvestment() {
		return investment;
	}
	public Invoice setInvestment(boolean investment) {
		this.investment = investment;
		return this;
	}
	public boolean isService() {
		return service;
	}
	public Invoice setService(boolean service) {
		this.service = service;
		return this;
	}
	public boolean isAdvance() {
		return advance;
	}
	public Invoice setAdvance(boolean advance) {
		this.advance = advance;
		return this;
	}
	public boolean isSigned() {
		return signed;
	}
	public Invoice setSigned(boolean signed) {
		this.signed = signed;
		return this;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public Invoice setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public double getVatQuota() {
		return vatQuota;
	}
	public Invoice setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
		return this;
	}
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public Invoice setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public Invoice setTotal(double total) {
		this.total = total;
		return this;
	}
	public Integer getSeller() {
		return seller;
	}
	public Invoice setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	public Integer getPosShift() {
		return posShift;
	}
	public Invoice setPosShift(Integer posShift) {
		this.posShift = posShift;
		return this;
	}
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Invoice setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Invoice setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	public String getDocumentNumber() {
		return FinanceUtil.getDocumentNumber(type, series, number);
	}
	
	public String getComments() {
		return comments;
	}
	public Invoice setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}
	public Invoice setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Invoice setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Invoice setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Invoice setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Invoice setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public LinkedList<InvoiceDetail> getDetails() {
		return details;
	}
	public Invoice setDetails(LinkedList<InvoiceDetail> details) {
		this.details = details;
		return this;
	}

	// ---------------------------------------------------------- UTIL
	public boolean isNational() {
		return getTransaction() == InvoiceTransactionType.NATIONAL;
	}
	public boolean isIntracommunity() {
		return getTransaction() == InvoiceTransactionType.INTRACOMMUNITY;
	}
	public boolean isIsp() {
		return getTransaction() == InvoiceTransactionType.OTHER_ISP;
	}
	public boolean isSales() {
		return getType() == InvoiceType.SALES;
	}
	public boolean isPurchase() {
		return getType() == InvoiceType.PURCHASE;
	}
	public boolean isExpenses() {
		return getType() == InvoiceType.EXPENSES;
	}
	public boolean isUndeductible() {
		return getType() == InvoiceType.UNDEDUCTIBLE;
	}
	
	public boolean isOutputVatEnabled() {
		return (isSales() && isNational())
			|| ((isPurchase() || isExpenses()) && (isIntracommunity() || isIsp()));
	}
	public boolean isInputVatEnabled() {
		return ((isPurchase() || isExpenses()) 
				&& (isNational() || isIntracommunity() || isIsp())) 
		;
	}
}

