package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.LoanFeeEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.utils.AccountPeriodValidator;
import com.code.aon.ui.form.FormUtil;

public class LoanFeeEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(LoanFeeEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private LoanFeeEntryHeader header;

	private AccountUtils accountUtils;

	public AccountUtils getAccountUtils() {
		if (accountUtils == null) {
			accountUtils = new AccountUtils();
		}
		return accountUtils;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	public LoanFeeEntryHeader getHeader() {
		return header;
	}

	public void setHeader(LoanFeeEntryHeader header) {
		this.header = header;
	}

	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = initializeHeader();
	}
	
	private LoanFeeEntryHeader initializeHeader() {
		LoanFeeEntryHeader header = new LoanFeeEntryHeader();
		header.setFeeDate(new Date());
		return header;
	}
	
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountPeriodValidator.validateAccountPeriod(getHeader().getFeeDate());
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		entry.setEntryDate(getHeader().getFeeDate());
		entry.setAccountPeriod(AccountUtil.obtainPeriod(getHeader().getFeeDate()).getId());
		entry.setJournal(null);
		entry.setType(AccountEntryType.LOAN_FEE);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry);
		setAccountEntry(entry);
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	public void onRemove(ActionEvent event){
		deleteAccountEntryDetails(getAccountEntry());
		deleteAccountEntry(getAccountEntry());
	}
	
	private AccountEntry insertorUpdateAccountEntry(AccountEntry entry) {
		try {
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			if(this.isNew){
				entry = (AccountEntry)entryBean.insert(entry);
			}else{
				entry = (AccountEntry)entryBean.update(entry);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting AccountEntry", e);
		}
		return entry;
	}
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			Account rBankAccount = AccountUtil.obtainRBankAccount(getHeader().getRegistryBank());
			Account loanAccount = obtainLoanAccount(getHeader().getLoan());
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(getHeader().getAmortization() + getHeader().getInterest());
			detail.setBalancingAccount(loanAccount);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(loanAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(getHeader().getDescription());
			detail.setDebit(getHeader().getAmortization());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setConcept(getHeader().getDescription());
			detail.setDebit(getHeader().getInterest());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting details related with AccountEntry with id=" + accountEntry.getId(), e);
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			accountEntryBean.remove(accountEntry);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting AccountEntry with id= " + accountEntry.getId(), e);
		}
	}
	
	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */	
	@SuppressWarnings("unchecked")
	private Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount = (LoanAccount)iter.next();
			return loanAccount.getAccount();
		}
		return null;
	}
	
	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountEntryController", e);
		}
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		setAccountEntry(entry);
		LoanFeeEntryHeader header = new LoanFeeEntryHeader();
		Loan loan = obtainLoan(entry);
		AccountEntryDetail accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.LOAN_ACCOUNT_PREFIX + "*");
		header.setAmortization(accountEntryDetail.getDebit());
		header.setDescription(accountEntryDetail.getConcept());
		header.setFeeDate(entry.getEntryDate());
		header.setLoan(loan);
		header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getBalancingAccount().getId()));
		accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "*");
		header.setInterest(accountEntryDetail.getDebit());
		setHeader(header);
	}
	
	@SuppressWarnings("unchecked")
	private Loan obtainLoan(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.LOAN_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LoanAccount)iter.next()).getLoan();
		}
		return null;
	}

	@Override
	public String getNavigationKey() {
		return "account_loan_fee_entry";
	}
}