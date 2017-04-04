package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;

public class VatContext implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private Integer invoice;
	private Integer activity;
	private String activityDescription;
	private boolean vatGeneralRegime;
	private String epigraph;
	private String documentNumber;
	private String referenceCode;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Date issueDate;
	private Date taxDate;
	
	private boolean insidePeriod;
	
	private InvoiceType invoiceType;
	private RectificationType rectificationType;
	private boolean service;
	private InvoiceTransactionType transaction;
	private boolean investment;
	private boolean vatAccrualRegime;
	private VatDeductionType vatDeductionType;
	private boolean farmerRegime;
	
	private double base;
	private double percentage;
	private double quota;
	
	private Integer investAsset;
	private double deductiblePercent;
	private double deductibleQuota;
	
	private boolean surcharge;
	private double surchargePercent;
	private double surchargeQuota;
	
	public Integer getInvoice() {
		return invoice;
	}
	public VatContext setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public VatContext setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public VatContext setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public boolean isVatGeneralRegime() {
		return vatGeneralRegime;
	}
	public VatContext setVatGeneralRegime(boolean vatGeneralRegime) {
		this.vatGeneralRegime = vatGeneralRegime;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public VatContext setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public VatContext setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public VatContext setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public VatContext setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public VatContext setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public VatContext setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public VatContext setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public VatContext setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public VatContext setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public boolean isInsidePeriod() {
		return insidePeriod;
	}
	public VatContext setInsidePeriod(boolean insidePeriod) {
		this.insidePeriod = insidePeriod;
		return this;
	}
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public VatContext setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public VatContext setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
		return this;
	}
	public boolean isService() {
		return service;
	}
	public VatContext setService(boolean service) {
		this.service = service;
		return this;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public VatContext setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	public boolean isInvestment() {
		return investment;
	}
	public VatContext setInvestment(boolean investment) {
		this.investment = investment;
		return this;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public VatContext setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public boolean isVatAccrualRegime() {
		return vatAccrualRegime;
	}
	public VatContext setVatAccrualRegime(boolean vatAccrualRegime) {
		this.vatAccrualRegime = vatAccrualRegime;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public VatContext setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}
	public boolean isFarmerRegime() {
		return farmerRegime;
	}
	public VatContext setFarmerRegime(boolean farmerRegime) {
		this.farmerRegime = farmerRegime;
		return this;
	}
	
	public double getBase() {
		return base;
	}
	public VatContext setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercentage() {
		return percentage;
	}
	public VatContext setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public VatContext setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public Integer getInvestAsset() {
		return investAsset;
	}
	public VatContext setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	public double getDeductiblePercent() {
		return deductiblePercent;
	}
	public VatContext setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public VatContext setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public VatContext setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
		return this;
	}
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public VatContext setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}
	public boolean isRectification() {
		return (rectificationType == RectificationType.NORMAL_RECTIFIER);
	}
	public boolean isSales() {
		return (invoiceType == InvoiceType.SALES);
	}
	public boolean isPurchase() {
		return (invoiceType == InvoiceType.PURCHASE);
	}
	public boolean isExpenses() {
		return (invoiceType == InvoiceType.EXPENSES || invoiceType == InvoiceType.UNDEDUCTIBLE);
	}
	public boolean isNational() {
		return (transaction == InvoiceTransactionType.NATIONAL);
	}
	public boolean isIntracommunity() {
		return (transaction == InvoiceTransactionType.INTRACOMMUNITY);
	}
	public boolean isExtracommunity() {
		return (transaction == InvoiceTransactionType.EXTRACOMMUNITY);
	}
	public boolean isCanCeuMel() {
		return (transaction == InvoiceTransactionType.CAN_CEU_MEL);
	}
	public boolean isOtherISP() {
		return (transaction == InvoiceTransactionType.OTHER_ISP);
	}
	public boolean isNationalSales() {
		return isNational() && isSales();
	}
	public boolean isIntracommunitySales() {
	return isIntracommunity() && isSales();
}
	public boolean isNationalPurchase() {
		return isNational() && isPurchase();
	}
	public boolean isOtherISPPurchase() {
		return isOtherISP() && isPurchase();
	}
	public boolean isIntracommunityPurchase(){
		return isIntracommunity() && isPurchase();
	} 
	public boolean isExtracommunityPurchase(){
		return isExtracommunity() && isPurchase();
	} 
	public boolean isCanCeuMelPurchase(){
		return isCanCeuMel() && isPurchase();
	}
	public boolean isNationalExpenses() {
		return isNational() && isExpenses();
	}
	public boolean isOtherISPExpenses() {
		return isOtherISP() && isExpenses();
	}
	public boolean isExtracommunityExpenses(){
		return isExtracommunity() && isExpenses();
	} 
	public boolean isCanCeuMelExpenses(){
		return isCanCeuMel() && isExpenses();
	}
	public boolean isIntracommunityExpenses(){
		return isIntracommunity() && isExpenses();
	} 
	public boolean isWithoutRightDeductionType() {
		return  vatDeductionType == VatDeductionType.WITHOUT_RIGHT;
	}
	
}
