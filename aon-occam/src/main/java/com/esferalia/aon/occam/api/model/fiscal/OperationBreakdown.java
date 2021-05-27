package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationBreakdown implements Serializable {

	private static final long serialVersionUID = 7829297928785116306L;
	
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
	
	// Añadido para poder obtener Libros Registro de IVA e IRPF según formato de la Agencia Tributaria	
	private String activityType;          	// Tipo de Actividad (1..5)
	private String activityIAE; 	   		// Epigrafe IAE
	private String invoiceType; 	   		// Tipo de Factura (F1,F2,...)
	private String conceptType; 	   		// Clave concepto Ingreso/Gasto (I01,IX1,GX1,G04,...) (Solo Libro IRPF)
	private double amount; 			   		// Ingreso computable o Gasto deducible (Solo Libro IRPF)
	private String invoiceSeries;      		// Serie de la Factura (Facturas Emitidas)
	private String invoiceNumber;      		// Numero de la Factura (Facturas Emitidas/Recibidas)
    private String registryDocumentType;	// Tipo NIF
	private String registryDocumentCountry;	// Pais NIF
	private String operationType; 			// Clave Operación (07,02) (Solo Libro IVA)
	private Date payDate; 					// Fecha cobro/pago (RECC) 
    private double payAmount; 				// Importe cobro/pago (RECC)
	private String payMethod; 				// Medio de cobro/pago (01..05) (RECC)
	private String payMethodName; 			// Identificación medio de cobro/pago (RECC)
	private double retentionPercent;   		// Porcentaje Retención (Solo Libro IRPF)
	private double retentionQuota;    		// Importe Retención (Solo Libro IRPF)
	
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
	public String getActivityType() {
		return activityType;
	}
	public OperationBreakdown setActivityType(String activityType) {
		this.activityType = activityType;
		return this;
	}
	public String getActivityIAE() {
		return activityIAE;
	}
	public OperationBreakdown setActivityIAE(String activityIAE) {
		this.activityIAE = activityIAE;
		return this;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public OperationBreakdown setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}
	public String getConceptType() {
		return conceptType;
	}
	public OperationBreakdown setConceptType(String conceptType) {
		this.conceptType = conceptType;
		return this;
	}
	public double getAmount() {
		return amount;
	}
	public OperationBreakdown setAmount(double amount) {
		this.amount = amount;
		return this;
	}
	public String getInvoiceSeries() {
		return invoiceSeries;
	}
	public OperationBreakdown setInvoiceSeries(String invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
		return this;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public OperationBreakdown setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
		return this;
	}
	public String getRegistryDocumentType() {
		return registryDocumentType;
	}
	public OperationBreakdown setRegistryDocumentType(String registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public String getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public OperationBreakdown setRegistryDocumentCountry(String registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getOperationType() {
		return operationType;
	}
	public OperationBreakdown setOperationType(String operationType) {
		this.operationType = operationType;
		return this;
	}
	public Date getPayDate() {
		return payDate;
	}
	public OperationBreakdown setPayDate(Date payDate) {
		this.payDate = payDate;
		return this;
	}
	public double getPayAmount() {
		return payAmount;
	}
	public OperationBreakdown setPayAmount(double payAmount) {
		this.payAmount = payAmount;
		return this;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public OperationBreakdown setPayMethod(String payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public String getPayMethodName() {
		return payMethodName;
	}
	public OperationBreakdown setPayMethodName(String payMethodName) {
		this.payMethodName = payMethodName;
		return this;
	}
	public double getRetentionPercent() {
		return retentionPercent;
	}
	public OperationBreakdown setRetentionPercent(double retentionPercent) {
		this.retentionPercent = retentionPercent;
		return this;
	}
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public OperationBreakdown setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
		return this;
	}
	
}

