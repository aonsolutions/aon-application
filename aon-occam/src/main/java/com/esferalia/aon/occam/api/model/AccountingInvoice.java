package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingInvoice implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private AccountEntry accountEntry;
	private Invoice invoice;
	private AccountingRegistry registry;

	private InvoiceWithholding withholdingData;
	private LinkedList<Account> suggestedAccounts;
	private LinkedList<InvoiceVAT> vats;
	
	private LinkedList<AccountEntry> accountEntries;
	
	@Override
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public AccountingInvoice setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public AccountingRegistry getRegistry() {
		return registry;
	}

	public AccountingInvoice setRegistry(AccountingRegistry registry) {
		this.registry = registry;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return invoice != null?invoice.getTransaction():null;
	}

	public InvoiceWithholding getWithholdingData() {
		return withholdingData;
	}
	public AccountingInvoice setWithholdingData(InvoiceWithholding withholdingData) {
		this.withholdingData = withholdingData;
		return this;
	}
	
	public LinkedList<AccountEntry> getAccountEntries() {
		return accountEntries;
	}
	public AccountingInvoice setAccountEntries(LinkedList<AccountEntry> accountEntries) {
		this.accountEntries = accountEntries;
		return this;
	}
	
	public LinkedList<Account> getSuggestedAccounts() {
		return suggestedAccounts;
	}
	public AccountingInvoice setSuggestedAccounts(LinkedList<Account> suggestedAccounts) {
		this.suggestedAccounts = suggestedAccounts;
		return this;
	}
	public LinkedList<InvoiceVAT> getVats() {
		return vats;
	}
	public AccountingInvoice addVat(InvoiceVAT vat) {
		if (getVats() == null) {
			setVats(new LinkedList<InvoiceVAT>());
		}
		getVats().add(vat);
		return this;
	}
	
	public AccountingInvoice setVats(LinkedList<InvoiceVAT> vats) {
		this.vats = vats;
		return this;
	}
	
	
	public void refreshWithholdingData() {
		double wb = 0.0; 
		for (InvoiceVAT vat : getVats()) {
			wb = wb + (vat.isWithholding()?vat.getBase():0.0);
		}
		setWithholdingBase(AonMathUtils.round(wb));
	}

	public double getTotalTaxableBase() {
		if (getVats() == null) return 0.0;
		double tb = 0.0; 
		for (InvoiceVAT vat : getVats()) {
			tb = tb + vat.getBase();
		}
		return AonMathUtils.round(tb);
	}
	
	public void calculateInvoiceTotals() {
		double t = 0.0;
		double vt = 0.0;
		double rt = 0.0;
		double tb = 0.0;
		if (getVats() != null) {
			for (InvoiceVAT vat : getVats()) {
				t = t + vat.getBase() + vat.getQuota() + vat.getSurchargeQuota();
				tb = tb + vat.getBase();
				if (isInputVatEnabled() != isOutputVatEnabled()) {
					vt = vt + (vat.getQuota() + vat.getSurchargeQuota());
				}
			}
			rt = (getWithholdingData()==null?0.0:getWithholdingData().getQuota());
			t = t - rt;
		}
		getInvoice().setTotal(t);
		getInvoice().setVatQuota(vt);
		getInvoice().setRetentionQuota(rt);
		getInvoice().setTaxableBase(tb);
	}
	
	public double getTotalInvoice() {
		return getInvoice().getTotal();
	}
	
	public boolean isNational() {
		return invoice != null && invoice.isNational();
	}
	public boolean isIntracommunity() {
		return invoice != null && invoice.isIntracommunity();
	}
	public boolean isIsp() {
		return invoice != null && invoice.isIsp();
	}

	public boolean isSales() {
		return invoice != null && invoice.isSales(); 
	}
	public boolean isPurchase() {
		return invoice != null && invoice.isPurchase(); 
	}
	public boolean isExpenses() {
		return invoice != null && invoice.isExpenses(); 
	}

	public boolean isSurcharge() {
		return invoice != null && invoice.isSurcharge();
	}

	public boolean isWithholding() {
		return invoice != null && invoice.isWithholding();
	}

	public boolean isWithholdingFarmer() {
		return invoice != null && invoice.isWithholdingFarmer();
	}

	public boolean isVatAccrualPayment() {
		return invoice != null && invoice.isVatAccrualPayment();
	}

	public boolean isInputVatEnabled() {
		return invoice != null && invoice.isInputVatEnabled();
	}
	public boolean isOutputVatEnabled() {
		return invoice != null && invoice.isOutputVatEnabled();
	}
	
	public void setWithholdingAccount(Account acc) {
		if (getWithholdingData() == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		getWithholdingData().setAccountId(acc.getId())
			.setAccountCode(acc.getCode())
			.setAccountDescription(acc.getDescription());
	}	
	public void setWithholdingBase(Double base) {
		if (getWithholdingData() == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		getWithholdingData().setBase(base);
		getWithholdingData().setQuota(AonMathUtils.round(base * getWithholdingData().getPercentage() / 100));
	}
	public void setWithholdingPercent(Double percent) {
		if (getWithholdingData() == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		getWithholdingData().setPercentage(percent);
		getWithholdingData().setQuota(AonMathUtils.round(getWithholdingData().getBase() * percent / 100));
	}
	public void setWithholdingQuota(Double quota) {
		if (getWithholdingData() == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		getWithholdingData().setQuota(quota);
	}
	public void setWithholdingType(WithholdingType type) {
		if (getWithholdingData() == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		getWithholdingData().setWithholdingType(type);
	}
	
	public InvoiceVAT getFirstVat() {
		return getVats().get(0);
	}
	
	
}
