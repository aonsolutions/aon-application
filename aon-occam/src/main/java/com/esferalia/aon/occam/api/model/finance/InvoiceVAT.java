package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceVAT implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private InvoiceDetail invoiceDetail;
	
	// ----------------------------------------------------
	// ---------------------------------- [InvoiceDetail]--
	// ----------------------------------------------------
	public InvoiceDetail getInvoiceDetail() {
		if ( invoiceDetail == null) {
			invoiceDetail = new InvoiceDetail();
		}
		return invoiceDetail;
	}
	public InvoiceVAT setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
		return this;
	}
	public boolean isWithholding() {
		return getInvoiceDetail().getWithholdingTax().isPresent();
	}
	
	// ----------------------------------------------------
	// -------------------------------------- [InvoiceTax]--
	// ----------------------------------------------------
	public InvoiceTax getInvoiceTax() {
		return getInvoiceDetail().getVatTax().orElse(null);
	}
	
	public Integer getId() {
		return getInvoiceTax().getId();
	}
	public InvoiceVAT setId(Integer id) {
		getInvoiceTax().setId( id );
		return this;
	}
	
	public double getBase() {
		return getInvoiceDetail().getTaxableBase();
	}
	public InvoiceVAT setBase(double base) {
		base = AonMathUtils.round(base, 4);
		getInvoiceDetail().setTaxableBase(base);
		getInvoiceDetail().setPrice(base);
		getInvoiceDetail().setDiscount(0);
		getInvoiceDetail().setQuantity(1);
		getInvoiceTax().setBase(base);
		return this;
	}

	public double getPercentage() {
		return getInvoiceTax().getPercentage();
	}
	public InvoiceVAT setPercentage(double percentage) {
		percentage = AonMathUtils.round(percentage, 2);
		getInvoiceTax().setPercentage( percentage );
		return this;
	}
	
	public double getQuota() {
		return getInvoiceTax().getQuota();
	}
	public InvoiceVAT setQuota(double quota) {
		quota = AonMathUtils.round(quota, 2);
		getInvoiceTax().setQuota( quota );
		return this;
	}

	public double getSurcharge() {
		return getInvoiceTax().getSurcharge();
	}
	public InvoiceVAT setSurcharge(double surcharge) {
		surcharge = AonMathUtils.round(surcharge, 2);
		getInvoiceTax().setSurcharge( surcharge );
		return this;
	}

	public double getSurchargeQuota() {
		return getInvoiceTax().getSurchargeQuota();
	}
	public InvoiceVAT setSurchargeQuota(double surchargeQuota) {
		surchargeQuota = AonMathUtils.round(surchargeQuota, 2);
		getInvoiceTax().setSurchargeQuota( surchargeQuota );
		return this;
	}

	public VatDeductionType getVatDeductionType() {
		return getInvoiceTax().getVatDeductionType();
	}
	public InvoiceVAT setVatDeductionType(VatDeductionType vatDeductionType) {
		getInvoiceTax().setVatDeductionType( vatDeductionType );
		return this;
	}

	public double getDeductiblePercent() {
		return getInvoiceTax().getDeductiblePercent();
	}
	public InvoiceVAT setDeductiblePercent(double deductiblePercent) {
		deductiblePercent = AonMathUtils.round(deductiblePercent, 2);
		getInvoiceTax().setDeductiblePercent( deductiblePercent );
		return this;
	}

	public double getDeductibleQuota() {
		return getInvoiceTax().getDeductibleQuota();
	}
	public double getNoDeductibleQuota() {
		return AonMathUtils.round( getQuota() - getDeductibleQuota() );
	}
	public InvoiceVAT setDeductibleQuota(double deductibleQuota) {
		deductibleQuota = AonMathUtils.round(deductibleQuota, 2);
		getInvoiceTax().setDeductibleQuota( deductibleQuota );
		return this;
	}
	
	public double getDirectTaxPercent() {
		return getInvoiceTax().getDirectTaxPercent();
	}
	public InvoiceVAT setDirectTaxPercent(double directTaxPercent) {
		directTaxPercent = AonMathUtils.round(directTaxPercent, 2);
		getInvoiceTax().setDirectTaxPercent( directTaxPercent );
		return this;
	}
	
	public double getDirectTaxNoDedExpenses() {
		return getInvoiceTax().getDirectTaxNoDedExpenses();
	}
	public double getDirectTaxDedExpenses() {
		return getInvoiceTax().getDirectTaxDedExpenses();
	}

	public boolean isAnyQuotaEdited() {
		return getInvoiceTax().isAnyQuotaEdited();
	}
	
	public boolean isQuotaEdited() {
		return getInvoiceTax().isQuotaEdited();
	}
	public InvoiceVAT setQuotaEdited(boolean quotaEdited) {
		getInvoiceTax().setQuotaEdited( quotaEdited );
		return this;
	}
	
	public boolean isSurchargeQuotaEdited() {
		return getInvoiceTax().isSurchargeQuotaEdited();
	}
	public InvoiceVAT setSurchargeQuotaEdited(boolean surchargeQuotaEdited) {
		getInvoiceTax().setSurchargeQuotaEdited( surchargeQuotaEdited );
		return this;
	}
	
	public boolean isDeductibleQuotaEdited() {
		return getInvoiceTax().isDeductibleQuotaEdited();
	}
	public InvoiceVAT setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		getInvoiceTax().setDeductibleQuotaEdited( deductibleQuotaEdited );
		return this;
	}
	
	// ----------------------------------------------------
	public Account getOutputAccount() {
		return getInvoiceTax().getOutputAccount();
	}
	public InvoiceVAT setOutputAccount(Account outputAccount) {
		getInvoiceTax().setOutputAccount(outputAccount);
		return this;
	}


	public Account getInputAccount() {
		return getInvoiceTax().getInputAccount();
	}
	public InvoiceVAT setInputAccount(Account inputAccount) {
		getInvoiceTax().setInputAccount(inputAccount);
		return this;
	}

	public Account getAdjAccount() {
		return getInvoiceTax().getAdjAccount();
	}
	public InvoiceVAT setAdjAccount(Account adjAccount) {
		getInvoiceTax().setAdjAccount(adjAccount);
		return this;
	}

	public Account getAdjDirectTaxAccount() {
		return getInvoiceTax().getAdjDirectTaxAccount();
	}
	public InvoiceVAT setAdjDirectTaxAccount(Account adjDirectTaxAccount) {
		getInvoiceTax().setAdjDirectTaxAccount(adjDirectTaxAccount);
		return this;
	}
	
	public InvoiceVAT copy() {
		return new InvoiceVAT()
			.setInvoiceDetail(this.invoiceDetail.copy())
			;
	}
	
}
