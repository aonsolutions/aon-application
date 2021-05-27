package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod140 implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;

	public static class Mod140Withholding implements Serializable {

		private static final long serialVersionUID = 8897444490096530091L;

		private WithholdingType withholdingType;
		
		private double base;
		private double percentage;
		private double quota;

		public double getBase() {
			return base;
		}

		public void setBase(double base) {
			this.base = base;
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

		public WithholdingType getWithholdingType() {
			return withholdingType;
		}

		public void setWithholdingType(WithholdingType withholdingType) {
			this.withholdingType = withholdingType;
		}

	}

	public class Mod140VAT implements Serializable {

		private static final long serialVersionUID = 8897444490096530091L;

		private VatDeductionType vatDeductionType;
		private double base;
		private double percentage;
		private double quota;
		private double surcharge;
		private double surchargeQuota;
		private double deductibleQuota;

		public VatDeductionType getVatDeductionType() {
			return vatDeductionType;
		}

		public void setVatDeductionType(VatDeductionType vatDeductionType) {
			this.vatDeductionType = vatDeductionType;
		}

		public double getBase() {
			return base;
		}

		public void setBase(double base) {
			this.base = base;
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

		public double getSurcharge() {
			return surcharge;
		}

		public void setSurcharge(double surcharge) {
			this.surcharge = surcharge;
		}

		public double getSurchargeQuota() {
			return surchargeQuota;
		}

		public void setSurchargeQuota(double surchargeQuota) {
			this.surchargeQuota = surchargeQuota;
		}

		public double getDeductibleQuota() {
			return deductibleQuota;
		}

		public void setDeductibleQuota(double deductibleQuota) {
			this.deductibleQuota = deductibleQuota;
		}

	}
	
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
	private String scope;
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
	
	private HashMap<String, List<Mod140VAT>> invoiceVATs;
	private Mod140Withholding withholdingData;
	
	
	public Integer getId() {
		return id;
	}
	public Mod140 setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Mod140 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Mod140 setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Mod140 setNumber(int number) {
		this.number = number;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public Mod140 setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public Mod140 setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Mod140 setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public Mod140 setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public Mod140 setRectificationType(RectificationType rectificationType) {
		this.rectificationType = rectificationType;
		return this;
	}
	public Integer getRectificationInvoice() {
		return rectificationInvoice;
	}
	public Mod140 setRectificationInvoice(Integer rectificationInvoice) {
		this.rectificationInvoice = rectificationInvoice;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public Mod140 setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Mod140 setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Mod140 setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Mod140 setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Mod140 setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public String getRegistryTown() {
		return registryTown;
	}
	public Mod140 setRegistryTown(String registryTown) {
		this.registryTown = registryTown;
		return this;
	}
	public String getRegistryZIP() {
		return registryZIP;
	}
	public Mod140 setRegistryZIP(String registryZIP) {
		this.registryZIP = registryZIP;
		return this;
	}
	public String getRegistryProvinceCode() {
		return registryProvinceCode;
	}
	public Mod140 setRegistryProvinceCode(String registryProvinceCode) {
		this.registryProvinceCode = registryProvinceCode;
		return this;
	}
	public String getRegistryProvince() {
		return registryProvince;
	}
	public Mod140 setRegistryProvince(String registryProvince) {
		this.registryProvince = registryProvince;
		return this;
	}
	public String getScope() {
		return scope;
	}
	public Mod140 setScope(String scope) {
		this.scope = scope;
		return this;
	}
	public InvoiceType getType() {
		return type;
	}
	public Mod140 setType(InvoiceType type) {
		this.type = type;
		return this;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public Mod140 setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
		return this;
	}
	public boolean isRecorded() {
		return recorded;
	}
	public Mod140 setRecorded(boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public Mod140 setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public boolean isWithholding() {
		return withholding;
	}
	public Mod140 setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Mod140 setWithholdingFarmer(boolean withholdingFarmer) {
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Mod140 setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public boolean isInvestment() {
		return investment;
	}
	public Mod140 setInvestment(boolean investment) {
		this.investment = investment;
		return this;
	}
	public boolean isService() {
		return service;
	}
	public Mod140 setService(boolean service) {
		this.service = service;
		return this;
	}
	public boolean isAdvance() {
		return advance;
	}
	public Mod140 setAdvance(boolean advance) {
		this.advance = advance;
		return this;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public Mod140 setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public double getVatQuota() {
		return vatQuota;
	}
	public Mod140 setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
		return this;
	}
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public Mod140 setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public Mod140 setTotal(double total) {
		this.total = total;
		return this;
	}
	public Mod140Withholding getWithholdingData() {
		return withholdingData;
	}
	public Mod140 setWithholdingData(Mod140Withholding withholdingData) {
		this.withholdingData = withholdingData;
		return this;
	}
	
	public HashMap<String,List<Mod140VAT>> getInvoiceVATs() {
		return invoiceVATs;
	}
	
	public Mod140 setInvoiceVATs(HashMap<String,List<Mod140VAT>> invoiceVATs) {
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
	
	public Mod140 initialize() {
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
	
	public Mod140VAT ensureInvoiceVAT(String account,double percentage, double surcharge) {
		if (invoiceVATs == null) {
			invoiceVATs = new HashMap<String, List<Mod140VAT>>();
		}
		if (invoiceVATs.get(account) == null) {
			invoiceVATs.put(account, new ArrayList<Mod140VAT>());
		}
		for (Mod140VAT invoiceVAT : invoiceVATs.get(account)) {
			if (invoiceVAT.getPercentage() == percentage && invoiceVAT.getSurcharge() == surcharge) {
				return invoiceVAT;
			}
		}
		Mod140VAT invoiceVAT = new Mod140VAT();
		invoiceVAT.setPercentage(percentage);
		invoiceVAT.setSurcharge(surcharge);
		invoiceVATs.get(account).add(invoiceVAT);
		
		return invoiceVAT;
	}

}
