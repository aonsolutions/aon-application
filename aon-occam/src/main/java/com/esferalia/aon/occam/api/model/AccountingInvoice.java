package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class AccountingInvoice implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private boolean tediParsed;
	
	private AccountEntry accountEntry;
	private Invoice invoice;
	private AccountingRegistry registry;
	private Integer workplace;
	private boolean accountSource;
	
	private Attach attach;
	private String manualConcept;

	private InvoiceWithholding withholdingData;
	private LinkedList<Account> suggestedAccounts;
	private LinkedList<InvoiceVAT> vats;
	private LinkedList<AccountEntry> accountEntries;
	private boolean prepayments;
	
	private Integer payAccountId;
	private String payAccountCode;
	private String payAccountDescription;
	
	public boolean isTediParsed() {
		return tediParsed;
	}
	public AccountingInvoice setTediParsed(boolean tediParsed) {
		this.tediParsed = tediParsed;
		return this;
	}
	
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
	public Integer getWorkplace() {
		return workplace;
	}
	public AccountingInvoice setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public boolean isAccountSource() {
		return accountSource;
	}
	public AccountingInvoice setAccountSource(boolean accountSource) {
		this.accountSource = accountSource;
		return this;
	}
	
	public boolean isDocumentAttached() {
		return this.attach != null;
	}
	
	public Attach getAttach() {
		return attach;
	}
	public AccountingInvoice setAttach(Attach attach) {
		this.attach = attach;
		return this;
	}
	
	public InvoiceTransactionType getTransaction() {
		return invoice != null?invoice.getTransaction():null;
	}

	public String getManualConcept() {
		return manualConcept;
	}
	public AccountingInvoice setManualConcept(String manualConcept) {
		this.manualConcept = manualConcept;
		return this;
	}
	public boolean hasWithholdingData() {
		return (withholdingData != null); 
	}
	public InvoiceWithholding getWithholdingData() {
		ensureWithholdingData();
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
	public boolean isUndeductible() {
		return invoice != null && invoice.isUndeductible(); 
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
	public boolean isService() {
		return invoice != null && invoice.isService();
	}
	public boolean isInvestment() {
		return invoice != null && invoice.isInvestment();
	}

	public boolean isInputVatEnabled() {
		return invoice != null && invoice.isInputVatEnabled();
	}
	public boolean isOutputVatEnabled() {
		return invoice != null && invoice.isOutputVatEnabled();
	}
	public boolean isVatEnabled() {
		return (isInputVatEnabled() != isOutputVatEnabled());
	}
	
	public void setWithholdingAccount(Account acc) {
		ensureWithholdingData();
		getWithholdingData().setAccountId(acc.getId())
			.setAccountCode(acc.getCode())
			.setAccountDescription(acc.getDescription());
	}	
	public void setWithholdingBase(Double base) {
		ensureWithholdingData();
		getWithholdingData().setBase(base);
		//getWithholdingData().setQuota(AonMathUtils.round(base * getWithholdingData().getPercentage() / 100));
	}
	public void setWithholdingPercent(Double percent) {
		ensureWithholdingData();
		getWithholdingData().setPercentage(percent);
		//getWithholdingData().setQuota(AonMathUtils.round(getWithholdingData().getBase() * percent / 100));
	}
	public void setWithholdingQuota(Double quota) {
		ensureWithholdingData();
		getWithholdingData().setQuota(quota);
	}
	public void setWithholdingType(WithholdingType type) {
		ensureWithholdingData();
		getWithholdingData().setWithholdingType(type);
	}
	
	public InvoiceVAT getFirstVat() {
		return getVats().get(0);
	}
	private void ensureWithholdingData() {
		if (this.withholdingData == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
	}
	public boolean hasPrepayments() {
		return prepayments;
	}
	public AccountingInvoice setPrepayments(boolean prepayments) {
		this.prepayments = prepayments;
		return this;
	}
	public Integer getPayAccountId() {
		return payAccountId;
	}
	public AccountingInvoice setPayAccountId(Integer payAccountId) {
		this.payAccountId = payAccountId;
		return this;
	}
	public String getPayAccountCode() {
		return payAccountCode;
	}
	public AccountingInvoice setPayAccountCode(String payAccountCode) {
		this.payAccountCode = payAccountCode;
		return this;
	}
	public String getPayAccountDescription() {
		return payAccountDescription;
	}
	public AccountingInvoice setPayAccountDescription(String payAccountDescription) {
		this.payAccountDescription = payAccountDescription;
		return this;
	}
	
	
}
