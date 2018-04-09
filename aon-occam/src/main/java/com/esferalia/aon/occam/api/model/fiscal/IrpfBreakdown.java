package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IrpfBreakdown implements Serializable{
	
	private static final long serialVersionUID = 8600299724576523607L;
	
	private Integer activity;
	private String activityDescription;
	private String epigraph;
	
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String name;
	private Date issueDate;
	private boolean fromSalary;
	private boolean insidePeriod;
	
	// ------------------- facturas
	private InvoiceType invoiceType;
	private Integer invoice;
	private String series;
	private Integer number;
	private String referenceCode;
	private Date taxDate;
	private WithholdingType withholdingType;
	private IRPFRegime regime;
	private boolean inKind;
	private double base;
	private double percent;
	private double quota;
	private double deductiblePercent;
	private double deductibleQuota;
	private Integer groupByNif;
	private String zip;
	private String city;
	
	public Integer getActivity() {
		return activity;
	}
	public IrpfBreakdown setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public IrpfBreakdown setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public IrpfBreakdown setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getRegistryDocument() {
		return registryDocument;
	}
	public IrpfBreakdown setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public IrpfBreakdown setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public IrpfBreakdown setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getName() {
		return name;
	}
	public IrpfBreakdown setName(String name) {
		this.name = name;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public IrpfBreakdown setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public boolean isFromSalary() {
		return fromSalary;
	}
	public boolean isFromInvoice() {
		return !fromSalary;
	}
	public IrpfBreakdown setFromSalary(boolean fromSalary) {
		this.fromSalary = fromSalary;
		return this;
	}
	
	public boolean isInsidePeriod() {
		return insidePeriod;
	}
	public IrpfBreakdown setInsidePeriod(boolean insidePeriod) {
		this.insidePeriod = insidePeriod;
		return this;
	}
	// ----------------------------------------------------------------
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public IrpfBreakdown setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}
	public Integer getInvoice() {
		return invoice;
	}
	public IrpfBreakdown setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public IrpfBreakdown setSeries(String series) {
		this.series = series;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public IrpfBreakdown setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public IrpfBreakdown setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public IrpfBreakdown setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public IrpfBreakdown setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	public IRPFRegime getIRPFRegime() {
		return regime;
	}
	public IrpfBreakdown setIRPFRegime(IRPFRegime regime) {
		this.regime = regime;
		return this;
	}
	public Double getBase() {
		return base;
	}
	public IrpfBreakdown setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public IrpfBreakdown setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public IrpfBreakdown setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public double getDeductiblePercent() {
		return deductiblePercent;
	}
	public IrpfBreakdown setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public IrpfBreakdown setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	public Integer getGroupByNif() {
		return groupByNif;
	}
	public IrpfBreakdown setGroupByNif(Integer groupByNif) {
		this.groupByNif = groupByNif;
		return this;
	}
	public String getZip() {
		return zip;
	}
	public IrpfBreakdown setZip(String zip) {
		this.zip = zip;
		return this;
	}
	public String getCity() {
		return city;
	}
	public IrpfBreakdown setCity(String city) {
		this.city = city;
		return this;
	}
	public boolean isInKind() {
		return inKind;
	}
	public IrpfBreakdown setInKind(boolean inKind) {
		this.inKind = inKind;
		return this;
	}
	
	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == getInvoiceType()) ? 
				"E" 
				: (InvoiceType.UNDEDUCTIBLE == getInvoiceType()) 
				? "G" 
				: "R") + "-";
		if (!AonStringUtils.isEmpty(getSeries())) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}
	
	//
	public boolean isSalaryRetention() {
		return isFromSalary() && !isInKind();
	}
	public boolean isSalaryInKindRetention() {
		return isFromSalary() && isInKind();
	}
	public boolean isProfessional() {
		return isFromInvoice() && withholdingType == WithholdingType.PROFESSIONAL;
	}
	public boolean isRenting() {
		return isFromInvoice() && withholdingType == WithholdingType.RENTING;
	}
	public boolean isMovableCapital() {
		return isFromInvoice() && withholdingType == WithholdingType.MOVABLE_CAPITAL;
	}
	public boolean isFarmer() {
		return isFromInvoice() && withholdingType == WithholdingType.FARMER;
	}
	public boolean isTransportOperator() {
		return isFromInvoice() &&  withholdingType == WithholdingType.TRANSPORT_OPERATOR;
	}
	public boolean isObjectiveRegime() {
		return isFromInvoice() &&  (regime == IRPFRegime.OBJECTIVE);
	}
	public boolean isExempt() {
		return isFromInvoice() &&  (regime != null) && (regime == IRPFRegime.EXEMPT);
	}
	public boolean isNotObjectiveRegime() {
		return !isObjectiveRegime();
	}
	public boolean isSales() {
		return getInvoiceType() == InvoiceType.SALES;
	}
	
}

