package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationBreakdown implements Serializable{
	
	private static final long serialVersionUID = -7155156494184164721L;

	private Integer activity;  
	private String activityDescription;
	private Boolean expenses;
	private Boolean irpf;
	
	private Date entryDate;  // Fecha del apunte 
	
	private String account; 
	private String accountDescription;
	private String concept;
	
	private double total; // Base imponible + iva + req en facturas, y total apunte en el resto de apuntes
	
	// ------------------- facturas
	private Integer invoice;
	private String docNumber;         // Nº Documento (se coge de la linea del asiento)	
	private String registryDocument;  // NIF del Titular
	private String registryName;      // Nombre del Titular
	private Date taxDate;             // Fecha de IVA	
	private double base;
	private double percent;
	private double quota;	
	private double deductibleQuota;	
	private double surchargePercent;
	private double surchargeQuota;

	public Boolean getExpenses() {
		return expenses;
	}
	public OperationBreakdown setExpenses(Boolean expenses) {
		this.expenses = expenses;
		return this;
	}
	public Boolean getIrpf() {
		return irpf;
	}
	public OperationBreakdown setIrpf(Boolean irpf) {
		this.irpf = irpf;
		return this;
	}
 	public Integer getActivity() {
		return activity;
	}
	public OperationBreakdown setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public OperationBreakdown setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public OperationBreakdown setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}	
	public String getRegistryName() {
		return registryName;
	}
	public OperationBreakdown setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}		
	public Integer getInvoice() {
		return invoice;
	}
	public OperationBreakdown setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}		
	public Double getBase() {
		return base;
	}
	public OperationBreakdown setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public OperationBreakdown setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public OperationBreakdown setQuota(double quota) {
		this.quota = quota;
		return this;
	}	
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public OperationBreakdown setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}	
	public Date getTaxDate() {
		return taxDate;
	}
	public OperationBreakdown setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public OperationBreakdown setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}
	public String getAccount() {
		return account;
	}
	public OperationBreakdown setAccount(String account) {
		this.account = account;
		return this;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public OperationBreakdown setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public OperationBreakdown setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public OperationBreakdown setTotal(double total) {
		this.total = total;
		return this;
	}
	public String getDocNumber() {
		return docNumber;
	}
	public OperationBreakdown setDocNumber(String docNumber) {
		this.docNumber = docNumber;
		return this;
	}
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public OperationBreakdown setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
		return this;
	}
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public OperationBreakdown setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}
	public String getFullConcept() {
		String c = "";
		if (accountDescription != null)
			c = c + accountDescription;
		if (concept != null)
			c = (c + " ["+concept+"]").trim();
		return c;
	}
	public String getFullDocumentName() {
		String c = "";
		if (registryDocument != null)
			c = c + registryDocument;
		if (registryName != null)
			c = (c + (registryDocument == null?"":" - ") + registryName).trim();
		return c;
		
	}
	
}

