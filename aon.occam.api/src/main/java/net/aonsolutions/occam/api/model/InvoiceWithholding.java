package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.type.WithholdingType;

public class InvoiceWithholding implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private WithholdingType withholdingType;
	private double base;
	private double percentage;
	private double quota;
	private double directTaxPercent;
	private double deductibleQuota;
	private Account account;
	private Account adjDirectTaxAccount;
	
	private boolean quotaEdited;
	private boolean deductibleQuotaEdited;
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceWithholding setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}

	public double getBase() {
		return base;
	}
	public InvoiceWithholding setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}
	public InvoiceWithholding setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}
	public InvoiceWithholding setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceWithholding setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public double getDirectTaxPercent() {
		return directTaxPercent;
	}
	public InvoiceWithholding setDirectTaxPercent(double directTaxPercent) {
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


	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public InvoiceWithholding setAccount(Account account) {
		this.account = account;
		return this;
	}
	
	public Optional<Account> getAdjDirectTaxAccount() {
		return Optional.ofNullable(adjDirectTaxAccount);
	}
	public InvoiceWithholding setAdjDirectTaxAccount(Account adjDirectTaxAccount) {
		this.adjDirectTaxAccount = adjDirectTaxAccount;
		return this;
	}
	
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceWithholding setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	
	public boolean isDeductibleQuotaEdited() {
		return deductibleQuotaEdited;
	}
	public InvoiceWithholding setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		this.deductibleQuotaEdited = deductibleQuotaEdited;
		return this;
	}
	
	public InvoiceWithholding initialize() {
		return setBase(0.0)
			.setQuota(0)
			.setDeductibleQuota(0.0)
			.setQuotaEdited(false)
			.setDeductibleQuotaEdited(false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceWithholding) {
			InvoiceWithholding other = (InvoiceWithholding) obj;
			return AonObjectUtils.equals( this.withholdingType,other.withholdingType )
				&& AonObjectUtils.equals( this.base,other.base )
				&& AonObjectUtils.equals( this.percentage,other.percentage )
				&& AonObjectUtils.equals( this.quota,other.quota )
				&& AonObjectUtils.equals( this.directTaxPercent,other.directTaxPercent )
				&& AonObjectUtils.equals( this.deductibleQuota,other.deductibleQuota )
				&& AonObjectUtils.equals( this.account,other.account )
				&& AonObjectUtils.equals( this.adjDirectTaxAccount,other.adjDirectTaxAccount )
			;
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 
    		+ AonObjectUtils.requireNonNullElse(withholdingType, 0).hashCode()
    		+ AonObjectUtils.requireNonNullElse(base, 0).hashCode()
    		+ AonObjectUtils.requireNonNullElse(percentage, 0).hashCode()
    		+ AonObjectUtils.requireNonNullElse(quota, 0).hashCode()
    		+ AonObjectUtils.requireNonNullElse(directTaxPercent, 0).hashCode()
    		+ AonObjectUtils.requireNonNullElse(deductibleQuota, 0).hashCode()
			+ AonObjectUtils.requireNonNullElse(account, 0).hashCode()
			+ AonObjectUtils.requireNonNullElse(adjDirectTaxAccount, 0).hashCode()
		;
	}
	
}
