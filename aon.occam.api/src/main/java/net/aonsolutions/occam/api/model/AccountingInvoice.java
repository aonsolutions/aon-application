package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.occam.api.model.type.InvoiceErrorLevel;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class AccountingInvoice implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -4435280253306756102L;

	private LinkedList<AccountEntry> accountEntries = new LinkedList<>();
	private Invoice invoice;
	private InvoiceRegistry registry;
	private String manualConcept;
	
	private boolean duaLinked;
	private AccountingDUAInvoice duaInvoice;	// Si la factura es nacional y está vinculada a un DUA, informacion del DUA y de la factura extraconumitaria
	private Integer duaNationalInvoice;			// Si la factura es extracomunitaria y está vinculada a un DUA, id de la factura nacional.

	private Account payAccount;
	private boolean authFinanceCalculation;

	@Override
	public LinkedList<AccountEntry> getAccountEntries() {
		return accountEntries;
	}

	@Override
	public AccountEntry getAccountEntry() {
		return accountEntries.isEmpty() ? null : accountEntries.get(0) ;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		if (accountEntries.isEmpty()) {
			accountEntries.add(accountEntry);
		} else {
			accountEntries.set(0, accountEntry);
		}
	}
	
	public Optional<Invoice> getInvoice() {
		return Optional.ofNullable(invoice);
	}
	public AccountingInvoice setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public Optional<InvoiceRegistry> getRegistry() {
		return Optional.ofNullable(registry);
	}
	public AccountingInvoice setRegistry(InvoiceRegistry registry) {
		this.registry = registry;
		return this;
	}
	
	public String getManualConcept() {
		return manualConcept;
	}
	public AccountingInvoice setManualConcept(String manualConcept) {
		this.manualConcept = manualConcept;
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
	
	public Optional<Account> getPayAccount() {
		return Optional.ofNullable(payAccount);
	}
	public AccountingInvoice setPayAccountId(Account payAccount) {
		this.payAccount = payAccount;
		return this;
	}
	
	public boolean isAuthFinanceCalculation() {
		return authFinanceCalculation;
	}
	public AccountingInvoice setAuthFinanceCalculation(boolean authFinanceCaleulation) {
		this.authFinanceCalculation = authFinanceCaleulation;
		return this;
	}
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	// *********************************************
	private boolean tediParsed;
	private Integer workplace;
	private boolean accountSource;
	private InvoiceWithholding privWithholdingData;
	private LinkedList<Account> suggestedAccounts;
	private boolean prepayments;
	private LinkedList<InvoiceRegistry> posibleRegistries;

	private boolean isTediParsed() {
		return tediParsed;
	}
	private AccountingInvoice setTediParsed(boolean tediParsed) {
		this.tediParsed = tediParsed;
		return this;
	}
	private boolean isFromRawdoc() {
		return getInvoice().map(i -> i.getRawdocId() == null).orElse(false);
	}
	private boolean hasTotal() {
		return getInvoice() != null && AonMathUtils.isNotZero( getTotalInvoice() ); 
	}
	
	private AccountingInvoice fillAccountEntry(AccountEntry accountEntry) {
		setAccountEntry(accountEntry);
		return this;
	}

	
	private Integer getWorkplace() {
		return workplace;
	}
	private AccountingInvoice setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	private boolean isAccountSource() {
		return accountSource;
	}
	private AccountingInvoice setAccountSource(boolean accountSource) {
		this.accountSource = accountSource;
		return this;
	}
	
	private boolean isDocumentAttached() {
		return this.invoice != null && this.invoice.getAttach().isPresent();
	}
	
	private Attach getAttach() {
		return this.invoice != null
			?this.invoice.getAttach().orElse(null)
			:null;
	}
	private AccountingInvoice setAttach(Attach attach) {
		this.invoice.setAttach(attach);
		return this;
	}
	
	private InvoiceTransactionType getTransaction() {
		return invoice != null?invoice.getTransaction():null;
	}

	private boolean hasWithholdingData() {
		return (privWithholdingData != null); 
	}
	private InvoiceWithholding getWithholdingData() {
		if (this.privWithholdingData == null) {
			setWithholdingData( new InvoiceWithholding() );
		}
		return privWithholdingData;
	}
	private AccountingInvoice setWithholdingData(InvoiceWithholding withholdingData) {
		this.privWithholdingData = withholdingData;
		return this;
	}
	
	private LinkedList<Account> getSuggestedAccounts() {
		return suggestedAccounts;
	}
	private AccountingInvoice setSuggestedAccounts(LinkedList<Account> suggestedAccounts) {
		this.suggestedAccounts = suggestedAccounts;
		return this;
	}
	private double getTotalInvoice() {
		return getInvoice().map(i -> i.getTotal()).orElse(0.0);
	}
	private boolean isNational() {
		return invoice != null && invoice.isNational();
	}
	private boolean isIntracommunity() {
		return invoice != null && invoice.isIntracommunity();
	}
	private boolean isExtracommunity() {
		return invoice != null && invoice.isExtracommunity();
	}
	private boolean isCanCeuMel() {
		return invoice != null && invoice.isCanCeuMel();
	}
	private boolean isIsp() {
		return invoice != null && invoice.isIsp();
	}

	private boolean isSales() {
		return invoice != null && invoice.isSales(); 
	}
	private boolean isPurchase() {
		return invoice != null && invoice.isPurchase(); 
	}
	private boolean isExpenses() {
		return invoice != null && invoice.isExpenses(); 
	}
	private boolean isUndeductible() {
		return invoice != null && invoice.isUndeductible(); 
	}
	
	private InvoiceType getInvoiceType() {
		return (invoice != null)?invoice.getType():null;
	}

	private boolean isSurcharge() {
		return invoice != null && invoice.isSurcharge();
	}

	private boolean isWithholding() {
		return invoice != null && invoice.isWithholding();
	}

	private boolean isWithholdingFarmer() {
		return invoice != null && invoice.isWithholdingFarmer();
	}

	private boolean isVatAccrualPayment() {
		return invoice != null && invoice.isVatAccrualPayment();
	}
	private boolean isService() {
		return invoice != null && invoice.isService();
	}
	private boolean isInvestment() {
		return invoice != null && invoice.isInvestment();
	}

	private boolean isRectifier() {
		return invoice != null && invoice.isRectifier();
	}

	private boolean isInputVatEnabled() {
		return invoice != null && invoice.isInputVatEnabled();
	}
	private boolean isOutputVatEnabled() {
		return invoice != null && invoice.isOutputVatEnabled();
	}
	
	public void setWithholdingAccount(Account acc) {
		getWithholdingData().setAccount(acc);
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

	public boolean hasPrepayments() {
		return prepayments;
	}
	public AccountingInvoice setPrepayments(boolean prepayments) {
		this.prepayments = prepayments;
		return this;
	}

	public LinkedList<InvoiceRegistry> getPosibleRegistries() {
		return posibleRegistries;
	}
	public AccountingInvoice setPosibleRegistries(LinkedList<InvoiceRegistry> posibleRegistries) {
		this.posibleRegistries = posibleRegistries;
		return this;
	}


	public Optional<InvoiceErrorLevel> getMoreSeriousLevel() {
		return InvoiceErrorLevel.value( 
			getInvoice()
				.map(Invoice::messageStream)
				.orElse( Stream.empty() )
				.mapToInt(e -> e.getLevel().ordinal())			
				.max()
				.orElse(-1));
	}
	
	public boolean isImportable() {
		return getMoreSeriousLevel()
			.map( l -> l.ordinal() < InvoiceErrorLevel.ERR.ordinal() )
			.orElse(true)
		;
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
