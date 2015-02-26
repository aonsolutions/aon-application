package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
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
	
	private InvoiceWithholding withholdingData;
	private List<InvoiceVAT> invoiceVATs;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public void setEpigraph(String epigraph) {
		this.epigraph = epigraph;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public void setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public void setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
	}
	public Integer getRectificationInvoice() {
		return rectificationInvoice;
	}
	public void setRectificationInvoice(Integer rectificationInvoice) {
		this.rectificationInvoice = rectificationInvoice;
	}
	public Integer getRegistry() {
		return registry;
	}
	public void setRegistry(Integer registry) {
		this.registry = registry;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public void setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public void setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
	}
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	public String getRegistryTown() {
		return registryTown;
	}
	public void setRegistryTown(String registryTown) {
		this.registryTown = registryTown;
	}
	public String getRegistryZIP() {
		return registryZIP;
	}
	public void setRegistryZIP(String registryZIP) {
		this.registryZIP = registryZIP;
	}
	public String getRegistryProvinceCode() {
		return registryProvinceCode;
	}
	public void setRegistryProvinceCode(String registryProvinceCode) {
		this.registryProvinceCode = registryProvinceCode;
	}
	public String getRegistryProvince() {
		return registryProvince;
	}
	public void setRegistryProvince(String registryProvince) {
		this.registryProvince = registryProvince;
	}
	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}
	public boolean isRecorded() {
		return recorded;
	}
	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}
	public boolean isWithholding() {
		return withholding;
	}
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public void setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public void setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
	}
	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}
	public boolean isService() {
		return service;
	}
	public void setService(boolean service) {
		this.service = service;
	}
	public boolean isAdvance() {
		return advance;
	}
	public void setAdvance(boolean advance) {
		this.advance = advance;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	public double getVatQuota() {
		return vatQuota;
	}
	public void setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
	}
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public void setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}

	public InvoiceWithholding getWithholdingData() {
		return withholdingData;
	}
	public void setWithholdingData(InvoiceWithholding withholdingData) {
		this.withholdingData = withholdingData;
	}
	
	public List<InvoiceVAT> getInvoiceVATs() {
		return invoiceVATs;
	}
	
	public void setInvoiceVATs(List<InvoiceVAT> invoiceVATs) {
		this.invoiceVATs = invoiceVATs;
	}
	
	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!AonStringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(Integer.toString(number), 6, "0");
		return documentNumber;
	}
	
	public void initialize() {
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
