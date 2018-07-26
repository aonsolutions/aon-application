package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationBreakdown implements Serializable{
	
	private static final long serialVersionUID = -7155156494184164721L;

	private Date entryDate;            // Fecha del apunte
	private Date taxDate;              // Fecha de IVA (facturas)	
	private String account;            // Cuenta contable
	private String accountDescription; // Descripción cuenta contable
	private String concept;  		   // Concepto del apunte
	private Integer invoice;           // ID de la factura (facturas)
	private String docNumber;          // Nº Documento (se coge de la linea del asiento)  (facturas)	
	private String registryDocument;   // NIF del Titular  (facturas)
	private String registryName;       // Nombre del Titular  (facturas)
	private double base;               // Base imponible en facturas, debe o haber en apuntes
	private double percent;            // Porcentaje de IVA  (facturas)
	private double quota;	           // Cuota de IVA  (facturas)
	private double deductibleQuota;	   // Cuota deducible de IVA  (facturas)
	private double surchargePercent;   // Porcentaje de Recargo de Equivalencia  (facturas)
	private double surchargeQuota;     // Cuota de Recargo de Equivalencia  (facturas)
	private double total;              // Base imponible + iva + req en facturas, y total apunte en el resto de apuntes	

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

