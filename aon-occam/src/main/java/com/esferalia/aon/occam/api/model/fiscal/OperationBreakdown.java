package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class OperationBreakdown implements Serializable {
	
	private static final long serialVersionUID = -640500611675649L;
	
	private String activityCode; 			// Actividad: Código
	private String activityType; 			// Actividad: Tipo
	private String activityIAE;  			// Actividad: Grupo o Epígrafe del IAE
	private String invoiceType; 			// Tipo de Factura	
	private String conceptCode; 			// Codigo Concepto de Ingreso o Gasto
	private double conceptAmount; 			// Ingreso computable o Gasto deducible 	
	private Date entryDate; 				// Fecha Expedición
	private Date taxDate;        			// Fecha Liquidación (Ejercicio y Periodo de Autoliquidación)	
	private String invoiceSeries; 			// Identificación de la Factura: Serie (Emitidas)
	private String invoiceNumber; 			// Identificación de la Factura: Número (Emitidas), Serie-Numero (Recibidas)
	private String receptionNumber; 		// Número recepción (Recibidas)
	private Date receptionDate; 			// Fecha Recepción (Recibidas) (Fecha Asiento)
	private String documentType; 			// NIF Destinatario/Expedidor: Tipo
	private String documentCountry;  		// NIF Destinatario/Expedidor: Código País
	private String document; 				// NIF Destinatario/Expedidor: Identificación
	private String name; 					// Nombre Destinatario/Expedidor	
	private String operationKey; 			// Clave de Operación 	
	private String operationQualification; 	// Calificación de la Operación (Emitidas)	
	private String exemptOperation;  		// Operación Exenta (Emitidas)
	private boolean investment; 			// Bien de Inversión (Recibidas)
	private boolean isp; 					// Inversión del Sujeto Pasivo (Recibidas)
	private double total; 					// Total Factura (Base + IVA + REQ)	
	private double base;               		// Base Imponible	
	private double percent;            		// Tipo de IVA	
	private double quota;	           		// Cuota IVA Repercutido/Soportado
	private double deductibleQuota;    		// Cuota Deducible (Recibidas)
	private double surchargePercent;	   	// Tipo de Recargo Eq.	
	private double surchargeQuota;     		// Cuota Recargo Eq.	
	private Date payDate; 					// Fecha Cobro/Pago (Operación Criterio de Caja de IVA y/o artículo 7.2.1º de Reglamento del IRPF)
	private double payAmount; 				// Importe Cobro/Pago
	private String payMethod; 				// Medio Utilizado Cobro/Pago
	private String payMethodName; 			// Identificación Medio Utilizado Cobro/Pago
	private double retentionPercent;   		// Tipo Retención del IRPF	
	private double retentionQuota;    		// Importe Retenido del IRPF	
	private String buildingLocation; 		// Situación del Inmueble;	
	private String cadasdralReference; 		// Referencia Catastral del Inmueble
	private int entryId;                    // ID del asiento
	private int entryJournal;               // Número de Diario del asiento
	private String conceptDescription; 		// Descripción Concepto de Ingreso o Gasto
	private String accountCode; 			// Código Cuenta Contable
	private Date accountEntryDate;        	// Fecha del Asiento
	private Date invoiceTaxDate;        	// Fecha IVA de la factura
	
	public String getActivityCode() {
		return activityCode;
	}
	public OperationBreakdown setActivityCode(String activityCode) {
		this.activityCode = activityCode;
		return this;
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
	public String getConceptCode() {
		return conceptCode;
	}
	public OperationBreakdown setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
		return this;
	}
	public double getConceptAmount() {
		return conceptAmount;
	}
	public OperationBreakdown setConceptAmount(double conceptAmount) {
		this.conceptAmount = conceptAmount;
		return this;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public OperationBreakdown setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public OperationBreakdown setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
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
	public String getReceptionNumber() {
		return receptionNumber;
	}
	public OperationBreakdown setReceptionNumber(String receptionNumber) {
		this.receptionNumber = receptionNumber;
		return this;
	}
	public Date getReceptionDate() {
		return receptionDate;
	}
	public OperationBreakdown setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
		return this;
	}
	public String getDocumentType() {
		return documentType;
	}
	public OperationBreakdown setDocumentType(String documentType) {
		this.documentType = documentType;
		return this;
	}
	public String getDocumentCountry() {
		return documentCountry;
	}
	public OperationBreakdown setDocumentCountry(String documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public OperationBreakdown setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public OperationBreakdown setName(String name) {
		this.name = name;
		return this;
	}
	public String getOperationKey() {
		return operationKey;
	}
	public OperationBreakdown setOperationKey(String operationKey) {
		this.operationKey = operationKey;
		return this;
	}
	public String getOperationQualification() {
		return operationQualification;
	}
	public OperationBreakdown setOperationQualification(String operationQualification) {
		this.operationQualification = operationQualification;
		return this;
	}
	public String getExemptOperation() {
		return exemptOperation;
	}
	public OperationBreakdown setExemptOperation(String exemptOperation) {
		this.exemptOperation = exemptOperation;
		return this;
	}
	public boolean isInvestment() {
		return investment;
	}
	public OperationBreakdown setInvestment(boolean investment) {
		this.investment = investment;
		return this;
	}
	public boolean isIsp() {
		return isp;
	}
	public OperationBreakdown setIsp(boolean isp) {
		this.isp = isp;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public OperationBreakdown setTotal(double total) {
		this.total = total;
		return this;
	}
	public double getBase() {
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
	public String getBuildingLocation() {
		return buildingLocation;
	}
	public OperationBreakdown setBuildingLocation(String buildingLocation) {
		this.buildingLocation = buildingLocation;
		return this;
	}
	public String getCadasdralReference() {
		return cadasdralReference;
	}
	public OperationBreakdown setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}
	public int getEntryId() {
		return entryId;
	}
	public OperationBreakdown setEntryId(int entryId) {
		this.entryId = entryId;
		return this;
	}
	public int getEntryJournal() {
		return entryJournal;
	}
	public OperationBreakdown setEntryJournal(int entryJournal) {
		this.entryJournal = entryJournal;
		return this;
	}
	public String getConceptDescription() {
		return conceptDescription;
	}
	public OperationBreakdown setConceptDescription(String conceptDescription) {
		this.conceptDescription = conceptDescription;
		return this;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public OperationBreakdown setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}
	public Date getAccountEntryDate() {
		return accountEntryDate;
	}
	public OperationBreakdown setAccountEntryDate(Date accountEntryDate) {
		this.accountEntryDate = accountEntryDate;
		return this;
	}
	public Date getInvoiceTaxDate() {
		return invoiceTaxDate;
	}
	public OperationBreakdown setInvoiceTaxDate(Date invoiceTaxDate) {
		this.invoiceTaxDate = invoiceTaxDate;
		return this;
	}	
	
}
