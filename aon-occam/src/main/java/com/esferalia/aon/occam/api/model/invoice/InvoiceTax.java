package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceTax implements Serializable {

	private static final long serialVersionUID = 7037774854336091259L;

	private boolean deleted;

	private Integer id;
	private Integer domain;
	private Integer invoiceDetail;
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private VatDeductionType vatDeductionType;
	private double deductiblePercent;
	private double deductibleQuota;
	private double directTaxPercent;
	private WithholdingType withholdingType;
	
	private Account withholdingAccount;
	private Account outputAccount;
	private Account inputAccount;
	private Account adjAccount;
	private Account adjDirectTaxAccount;
	
	private boolean quotaEdited;
	private boolean surchargeQuotaEdited;
	private boolean deductibleQuotaEdited;
	
	public Integer getId() {
		return id;
	}
	public InvoiceTax setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceTax setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoiceDetail() {
		return invoiceDetail;
	}
	public InvoiceTax setInvoiceDetail(Integer invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
		return this;
	}
	
	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceTax setTaxType(TaxType taxType) {
		this.taxType = taxType;
		return this;
	}
	
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceTax setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public double getBase() {
		return base;
	}
	public InvoiceTax setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}
	public InvoiceTax setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}
	public InvoiceTax setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}
	public InvoiceTax setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public InvoiceTax setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceTax setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	public double getNoDeductibleQuota() {
		return AonMathUtils.round( getQuota() - getDeductibleQuota() );
	}
	
	public double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceTax setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public double getDirectTaxPercent() {
		return directTaxPercent;
	}
	public InvoiceTax setDirectTaxPercent(double directTaxPercent) {
		this.directTaxPercent = directTaxPercent;
		return this;
	}
	
	public double getDirectTaxNoDedExpenses() {
		double percent = AonMathUtils.round(100 - this.directTaxPercent);
		return AonMathUtils.round( this.base *  percent / 100);		
	}
	public double getDirectTaxDedExpenses() {
		return AonMathUtils.round( getBase() - getDirectTaxNoDedExpenses() );
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceTax setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	
	public Optional<Account> getWithholdingAccount() {
		return Optional.ofNullable(withholdingAccount);
	}
	public InvoiceTax setWithholdingAccount(Account withholdingAccount) {
		this.withholdingAccount = withholdingAccount;
		return this;
	}
	
	public Optional<Account> getOutputAccount() {
		return Optional.ofNullable(outputAccount);
	}
	public InvoiceTax setOutputAccount(Account outputAccount) {
		this.outputAccount = outputAccount;
		return this;
	}
	
	public Optional<Account> getInputAccount() {
		return Optional.ofNullable(inputAccount);
	}
	public InvoiceTax setInputAccount(Account inputAccount) {
		this.inputAccount = inputAccount;
		return this;
	}
	
	public Optional<Account> getAdjAccount() {
		return Optional.ofNullable(adjAccount);
	}
	public InvoiceTax setAdjAccount(Account adjAccount) {
		this.adjAccount = adjAccount;
		return this;
	}
	
	public Optional<Account> getAdjDirectTaxAccount() {
		return Optional.ofNullable(adjDirectTaxAccount);
	}
	public InvoiceTax setAdjDirectTaxAccount(Account adjDirectTaxAccount) {
		this.adjDirectTaxAccount = adjDirectTaxAccount;
		return this;
	}
	
	public boolean isAnyQuotaEdited() {
		return isQuotaEdited() 
			|| isSurchargeQuotaEdited() 
			|| isDeductibleQuotaEdited();
	}
	
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceTax setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	
	public boolean isSurchargeQuotaEdited() {
		return surchargeQuotaEdited;
	}
	public InvoiceTax setSurchargeQuotaEdited(boolean surchargeQuotaEdited) {
		this.surchargeQuotaEdited = surchargeQuotaEdited;
		return this;
	}
	
	public boolean isDeductibleQuotaEdited() {
		return deductibleQuotaEdited;
	}
	public InvoiceTax setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		this.deductibleQuotaEdited = deductibleQuotaEdited;
		return this;
	}

	public boolean isVatType() {
		return this.getTaxType() == TaxType.VAT;
	}
	public boolean isWithholdingType() {
		return this.getTaxType() == TaxType.RETENTION;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public boolean isNotDeleted() {
		return !deleted;
	}
	public InvoiceTax setDeleted( boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	
	public InvoiceTax copy() {
		return new InvoiceTax()
			.setId ( getId() )
			.setDomain ( getDomain() )
			.setDeleted(isDeleted()) 
			.setTaxType(taxType)
			.setBase(base)
			.setPercentage(percentage)
			.setQuota(deductibleQuota)
			.setSurcharge(surcharge)
			.setSurchargeQuota(surchargeQuota)
			.setVatDeductionType(vatDeductionType)
			.setDeductiblePercent(deductiblePercent)
			.setDeductibleQuota(deductibleQuota)
			.setWithholdingType(withholdingType)
			.setWithholdingAccount(withholdingAccount)
			.setOutputAccount(this.outputAccount)
			.setInputAccount(this.inputAccount)
			.setAdjAccount(this.adjAccount)
			.setAdjDirectTaxAccount(this.adjDirectTaxAccount)
			.setQuotaEdited(deductibleQuotaEdited)
			.setSurchargeQuotaEdited(surchargeQuotaEdited)
			.setDeductibleQuotaEdited(deductibleQuotaEdited)
			;
	}
}
