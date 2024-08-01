package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingInvoice implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private boolean tediParsed;
	private boolean fromRawdoc;
	
	private AccountEntry accountEntry;
	private Invoice invoice;
	private AccountingRegistry registry;
	private Integer workplace;
	private boolean accountSource;
	
	private Attach attach;
	private String manualConcept;

	private InvoiceWithholding privWithholdingData;
	private LinkedList<Account> suggestedAccounts;
	private LinkedList<InvoiceVAT> vats;
	private LinkedList<AccountEntry> accountEntries;
	private boolean prepayments;
	
	private boolean duaLinked;
	// Si la factura es nacional y está vinculada a un DUA, 
	// informacion del DUA y de la factura extraconumitaria
	private AccountingDUAInvoice duaInvoice;
	// Si la factura es extracomunitaria y está vinculada a un 
	// DUA, id de la factura nacional.
	private Integer duaNationalInvoice;
	
	private boolean authFinanceCalculation;
	private Integer payAccountId;
	private String payAccountCode;
	private String payAccountDescription;
	
	private LinkedList<AccountingRegistry> posibleRegistries;

	public boolean isTediParsed() {
		return tediParsed;
	}
	public AccountingInvoice setTediParsed(boolean tediParsed) {
		this.tediParsed = tediParsed;
		return this;
	}
	public boolean isFromRawdoc() {
		return fromRawdoc;
	}
	public AccountingInvoice setFromRawdoc(boolean fromRawdoc) {
		this.fromRawdoc = fromRawdoc;
		return this;
	}
	public boolean hasTotal() {
		return getInvoice() != null && AonMathUtils.isNotZero( getTotalInvoice() ); 
	}
	
	@Override
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	
	public AccountingInvoice fillAccountEntry(AccountEntry accountEntry) {
		setAccountEntry(accountEntry);
		return this;
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
		return (privWithholdingData != null); 
	}
	public InvoiceWithholding getWithholdingData() {
		if (this.privWithholdingData == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		return privWithholdingData;
	}
	public AccountingInvoice setWithholdingData(InvoiceWithholding withholdingData) {
		this.privWithholdingData = withholdingData;
		return this;
	}
	
	@Override
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
			setVats(new LinkedList<>());
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
	public boolean isExtracommunity() {
		return invoice != null && invoice.isExtracommunity();
	}
	public boolean isCanCeuMel() {
		return invoice != null && invoice.isCanCeuMel();
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
	
	public InvoiceType getInvoiceType() {
		return (invoice != null)?invoice.getType():null;
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

	public boolean isRectifier() {
		return invoice != null && invoice.isRectifier();
	}

	public boolean isInputVatEnabled() {
		return invoice != null && invoice.isInputVatEnabled();
	}
	public boolean isOutputVatEnabled() {
		return invoice != null && invoice.isOutputVatEnabled();
	}
	
	public void setWithholdingAccount(Account acc) {
		getWithholdingData().setAccountId(acc.getId())
			.setAccountCode(acc.getCode())
			.setAccountDescription(acc.getDescription());
	}	
	public void setWithholdingBase(Double base) {
		getWithholdingData().setBase(base);
	}
	public void setWithholdingPercent(Double percent) {
		getWithholdingData().setPercentage(percent);
	}
	public void setWithholdingQuota(Double quota) {
		getWithholdingData().setQuota(quota);
	}
	public void setWithholdingType(WithholdingType type) {
		getWithholdingData().setWithholdingType(type);
	}
	public boolean isWithholdingQuotaEdited() {
		return getWithholdingData().isQuotaEdited();
	}
	public void setWithholdingQuotaEdited(boolean edited) {
		getWithholdingData().setQuotaEdited(edited);
	}

	public InvoiceVAT getFirstVat() {
		return getVats().get(0);
	}
	public boolean hasPrepayments() {
		return prepayments;
	}
	public AccountingInvoice setPrepayments(boolean prepayments) {
		this.prepayments = prepayments;
		return this;
	}
	public boolean isDuaLinked() {
		return duaLinked;
	}
	public AccountingInvoice setDuaLinked(boolean duaLinked) {
		this.duaLinked = duaLinked;
		return this;
	}
	
	public AccountingDUAInvoice getDuaInvoice() {
		return duaInvoice;
	}
	public AccountingInvoice setDuaInvoice(AccountingDUAInvoice duaInvoice) {
		this.duaInvoice = duaInvoice;
		return this;
	}

	public Integer getDuaNationalInvoice() {
		return duaNationalInvoice;
	}
	public AccountingInvoice setDuaNationalInvoice(Integer duaNationalInvoice) {
		this.duaNationalInvoice = duaNationalInvoice;
		return this;
	}
	public boolean isAuthFinanceCalculation() {
		return authFinanceCalculation;
	}
	public AccountingInvoice setAuthFinanceCalculation(boolean authFinanceCaleulation) {
		this.authFinanceCalculation = authFinanceCaleulation;
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
	
	public LinkedList<AccountingRegistry> getPosibleRegistries() {
		return posibleRegistries;
	}
	public AccountingInvoice setPosibleRegistries(LinkedList<AccountingRegistry> posibleRegistries) {
		this.posibleRegistries = posibleRegistries;
		return this;
	}

	public List<InvoiceError> getMessages() {
		return invoice.getMessages();
	}

	public void add(InvoiceError error) {
		invoice.addMessage(error);
	}

	public InvoiceErrorLevel getMoreSeriousLevel() {
		InvoiceErrorLevel level  = null;
		if (getMessages() != null) {
			for (InvoiceError error : getMessages()) {
				if (level == null || error.getLevel().ordinal() > level.ordinal()) {
					level = error.getLevel();
				}
			}
		}
		return level;
	}
	
	public boolean isImportable() {
		InvoiceErrorLevel level = getMoreSeriousLevel();
		return ( level == null || level.ordinal() < InvoiceErrorLevel.ERR.ordinal() );
	}
	public void clearMessages() {
		invoice.clearMessages();		
	}
	
	public boolean isVatImportationAvailable() {
		return invoice != null && invoice.isVatImportationAvailable();
	}
	public boolean isVatImportation() {
		return invoice != null && invoice.isVatImportation();
	}

}
