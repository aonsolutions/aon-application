package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Invoice implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private Integer id;
	private int domain;
	private String series;
	private int number;
	private String epigraph;
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
	private String registryTown;
	private String registryZIP;
	private String registryProvinceCode;
	private String registryProvince;
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
	private double taxableBase;
	private double vatQuota;
	private double retentionQuota;
	private double total;
	
	private String accountCode;
	
	private InvoiceWithholding withholdingData;
	private List<InvoiceVAT> invoiceVATs;
	
	
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
	public String getEpigraph() {
		return epigraph;
	}
	public Invoice setEpigraph(String epigraph) {
		this.epigraph = epigraph;
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
	public String getRegistryTown() {
		return registryTown;
	}
	public Invoice setRegistryTown(String registryTown) {
		this.registryTown = registryTown;
		return this;
	}
	public String getRegistryZIP() {
		return registryZIP;
	}
	public Invoice setRegistryZIP(String registryZIP) {
		this.registryZIP = registryZIP;
		return this;
	}
	public String getRegistryProvinceCode() {
		return registryProvinceCode;
	}
	public Invoice setRegistryProvinceCode(String registryProvinceCode) {
		this.registryProvinceCode = registryProvinceCode;
		return this;
	}
	public String getRegistryProvince() {
		return registryProvince;
	}
	public Invoice setRegistryProvince(String registryProvince) {
		this.registryProvince = registryProvince;
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
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public InvoiceWithholding getWithholdingData() {
		return withholdingData;
	}
	public Invoice setWithholdingData(InvoiceWithholding withholdingData) {
		this.withholdingData = withholdingData;
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
	
	public List<InvoiceVAT> getInvoiceVATs() {
		return invoiceVATs;
	}
	
	public Invoice setInvoiceVATs(List<InvoiceVAT> invoiceVATs) {
		this.invoiceVATs = invoiceVATs;
		return this;
	}
	
	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!AonStringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(Integer.toString(number), 6, "0");
		return documentNumber;
	}
	
	public Invoice initialize() {
		this.id = null;
		this.domain = 0;
		this.series = null;
		this.number = 0;
		this.referenceCode = null;
		this.issueDate = null;
		this.taxDate = null;
		this.rectificationType = null;
		this.rectificationInvoice = null;
		this.registryDocument = null;
		this.registryDocumentType = null;
		this.registryDocumentCountry = null;
		this.registryName = null;
		this.type = null;
		this.transaction = null;
		this.recorded = false;
		this.surcharge = false;
		this.withholding = false;
		this.withholdingFarmer = false;
		this.vatAccrualPayment = false;
		this.investment = false;
		this.service = false;
		this.advance = false;
		this.taxableBase = 0.0;
		this.vatQuota = 0.0;
		this.retentionQuota = 0.0;
		this.total = 0.0;
		this.withholdingData = null;
		this.invoiceVATs = null;
		return this;
	}
	
	public InvoiceVAT ensureInvoiceVAT(double percentage, double surcharge) {
		if (invoiceVATs == null) {
			invoiceVATs = new ArrayList<InvoiceVAT>();
		}
		for (InvoiceVAT invoiceVAT : invoiceVATs) {
			if (invoiceVAT.getPercentage() == percentage && invoiceVAT.getSurcharge() == surcharge) {
				return invoiceVAT;
			}
		}
		InvoiceVAT invoiceVAT = new InvoiceVAT();
		invoiceVAT.setPercentage(percentage);
		invoiceVAT.setSurcharge(surcharge);
		
		invoiceVATs.add(invoiceVAT);
		
		return invoiceVAT;
	}

}
