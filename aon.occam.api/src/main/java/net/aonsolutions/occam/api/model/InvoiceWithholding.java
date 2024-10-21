package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.model.type.WithholdingType;

public class InvoiceWithholding implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private WithholdingType withholdingType;
	private double base;
	private double percentage;
	private double quota;
	private double deductibleQuota;
	private Account account;
	
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
	
	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public InvoiceWithholding setAccount(Account account) {
		this.account = account;
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
}
