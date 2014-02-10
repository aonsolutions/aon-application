package com.code.aon.fiscal.invoice;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;

public class InvoiceReport {
	
	private static final String RECT_NORMAL = "Rect. Normal";
	private static final String RECT_SPECIAL = "Rect. Especial";
	
	private static final String OUTPUT_INVOICE = "Emit.";
	private static final String INPUT_INVOICE = "Recb.";

	private static final String INVESTMENT = "Bien Inver.";
	private static final String EXPENSE    = "Gasto";
	private static final String STANDARD   = "Bien Corriente";
	private static final String SERVICE    = "Servicio";
	
	private static final String VAT = "I.V.A.";
	private static final String RETENTION = "I.R.P.F.";

	private static final String WITH_RIGHT="Con Derecho a Deducción";
	private static final String WITHOUT_RIGHT="Sin Derecho a Deducción";
	
	private static final String NATIONAL = "Nacional";
	private static final String INTRACOMMUNITY="Intracomunitaria";
	private static final String EXTRACOMMUNITY="Extracomunitaria";
	
	private static final String PURCHASE="Compras";
	private static final String SALES="Ventas";
	private static final String EXPENSES="Gastos";
	private static final String UNDEDUCTIBLE="No deducible";

	private static final String PROFESSIONAL="Profesionales";
	private static final String RENTING="Arrendamientos";
	private static final String MOVABLE_CAPITAL="Capital Mobiliario";
	private static final String FARMER="Agricultores";
	private static final String TRANSPORT_OPERATOR="Transportistas y asimilados";

	private Integer id;
	private InvoiceType invoiceType;
	private InvoiceTransactionType transaction;
	private boolean investment;
	private RectificationType rectificationType;
	private Integer rectifiedInvoice;
	private Date taxDate;
	private Date issueDate;
	private String referenceCode;
	private String series;
	private Integer number;
	private String registryDocument;
	private String registryName;
	private TaxType taxType;
	private boolean surcharge;
	private boolean service;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	private double taxableBase;
	private double percentage;
	private double quota;
	private double deductibleQuota;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceTypeDesc() {
		if (getInvoiceType() == InvoiceType.PURCHASE) {
			return PURCHASE;
		} else if (getInvoiceType() == InvoiceType.SALES) {
			return SALES;
		} else if (getInvoiceType() == InvoiceType.EXPENSES) {
			return EXPENSES;
		} else if (getInvoiceType() == InvoiceType.UNDEDUCTIBLE) {
			return UNDEDUCTIBLE;
		}
		return invoiceType.toString();
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}
	public String getTransactionDesc() {
		if (getTransaction() == InvoiceTransactionType.NATIONAL) {
			return NATIONAL;
		} else if (getTransaction() == InvoiceTransactionType.INTRACOMMUNITY) {
			return INTRACOMMUNITY;
		} else if (getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY) {
			return EXTRACOMMUNITY;
		}
		return getTransaction().toString();
	}
	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public void setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
	}
	
	public Integer getRectifiedInvoice() {
		return rectifiedInvoice;
	}
	public void setRectifiedInvoice(Integer rectifiedInvoice) {
		this.rectifiedInvoice = rectifiedInvoice;
	}
	
	public String getRectificationTypeDesc() {
		if (getRectificationType() == RectificationType.NORMAL_RECTIFIER) {
			return RECT_NORMAL;
		} else if (getRectificationType() == RectificationType.SPECIAL_RECTIFIER) {
			return RECT_SPECIAL;
		}
		return "";
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public void setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	public TaxType getTaxType() {
		return taxType;
	}
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}
	public boolean isService() {
		return service;
	}
	public void setService(boolean service) {
		this.service = service;
	}
	public String getTaxTypeDesc() {
		String vatType = "";
		if (getTaxType() == TaxType.VAT) {
			vatType = VAT;
		} else  if (getTaxType() == TaxType.RETENTION) {
			vatType = RETENTION;
		} else {
			vatType = getTaxType().toString();
		}
		return vatType + (isSurcharge()?"(RE)":"");
	}

	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}
	public String getVatDeductionTypeDesc() {
		if (isVat()) {
			return (getVatDeductionType() == VatDeductionType.WITH_RIGHT)?WITH_RIGHT:
					(getVatDeductionType() == VatDeductionType.WITHOUT_RIGHT)?WITHOUT_RIGHT:
					getVatDeductionType().toString();
		}
		return "";
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}
	public String getWithholdingTypeDesc() {
		if (isRetention()) {
			return (getWithholdingType() == WithholdingType.PROFESSIONAL)?PROFESSIONAL:
				(getWithholdingType() == WithholdingType.RENTING)?RENTING:
				(getWithholdingType() == WithholdingType.MOVABLE_CAPITAL)?MOVABLE_CAPITAL:
				(getWithholdingType() == WithholdingType.FARMER)?FARMER:
				(getWithholdingType() == WithholdingType.TRANSPORT_OPERATOR)?TRANSPORT_OPERATOR:
				getWithholdingType().toString();
		}
		return "";
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	public double getDeductibleQuota() {
		if (deductibleQuota == 0) {
			return getQuota();
		}
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}

	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == getInvoiceType()) ? "E" : (InvoiceType.UNDEDUCTIBLE == getInvoiceType()) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(getSeries())) {
			documentNumber += getSeries() + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}

	// Método creado para no tener que definir todos los campos que se utilizan en la plantilla de report. 
	public InvoiceReport getInvoice() {
		return this;
	}
	
	public boolean isRetention() {
		return getTaxType() == TaxType.RETENTION;
	}
	public boolean isVat() {
		return getTaxType() == TaxType.VAT;
	}
	
	public String getAbbreviatedInvoiceType() {
		return (getInvoiceType() == InvoiceType.SALES)?OUTPUT_INVOICE:INPUT_INVOICE; 
	}
	public String getConceptNature() {
		if (getInvoiceType() == InvoiceType.SALES && isService()) {
			return SERVICE;
		}
		if (isInvestment()) {
			return INVESTMENT;
		} 
		if (getInvoiceType() == InvoiceType.EXPENSES || getInvoiceType() == InvoiceType.UNDEDUCTIBLE) {
			return EXPENSE;
		}
		return STANDARD;
	}

	public InvoiceReport clone() {
		InvoiceReport cloned = new InvoiceReport();
		cloned.setInvoiceType(getInvoiceType());
		cloned.setTransaction(getTransaction());
		cloned.setInvestment(isInvestment());
		cloned.setTaxDate(getTaxDate());
		cloned.setIssueDate(getIssueDate());
		cloned.setReferenceCode(getReferenceCode());
		cloned.setSeries(getSeries());
		cloned.setNumber(getNumber());
		cloned.setRegistryDocument(getRegistryDocument());
		cloned.setRegistryName(getRegistryName());
		cloned.setTaxType(getTaxType());
		cloned.setSurcharge(isSurcharge());
		cloned.setVatDeductionType(getVatDeductionType());
		cloned.setWithholdingType(getWithholdingType());
		cloned.setTaxableBase(getTaxableBase());
		cloned.setPercentage(getPercentage());
		cloned.setQuota(getQuota());
		cloned.setDeductibleQuota(getDeductibleQuota());
		return cloned;
		
	}
}
