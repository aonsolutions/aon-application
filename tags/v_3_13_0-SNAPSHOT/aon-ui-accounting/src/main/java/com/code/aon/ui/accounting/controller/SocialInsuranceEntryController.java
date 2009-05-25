package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.SocialInsuranceEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Bank;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.FormUtil;

public class SocialInsuranceEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(SocialInsuranceEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private SocialInsuranceEntryHeader header;

	private Company company;

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

	public SocialInsuranceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(SocialInsuranceEntryHeader header) {
		this.header = header;
	}
	
	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					setCompany((Company) iter.next());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error obtaining Company!");
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = initializeHeader();
	}
	
	private SocialInsuranceEntryHeader initializeHeader() {
		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setDate(new Date());
		header.setRegistryBank(new RegistryBank());
		header.getRegistryBank().setBank(new Bank());
		return header;
	}
	
	public List<SelectItem> getCompanyRegistryBanks() {
		return getRegistryBanks(getCompany());
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getRegistryBanks(Registry registry) {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, StringUtils.abbreviate(rBank.getBank().getName(), 30)
						+ " [" + rBank.getBankAccount().toString() + "]");
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks", e);
		}
		return rBanks;
	}

	public void accept(ActionEvent event) {
		try {
			AccountEntry entry = new AccountEntry();
			if (!this.isNew) {
				deleteAccountEntryDetails(getAccountEntry());
				entry = this.getAccountEntry();
			}
			entry.setEntryDate(getHeader().getDate());
			entry.setAccountPeriod(AccountUtil.obtainPeriod(getHeader().getDate()).getId());
			entry.setJournal(null);
			entry.setType(AccountEntryType.SOCIAL_INSURANCE);
			entry.setSecurityLevel(getHeader().getSecurityLevel());
			entry = insertorUpdateAccountEntry(entry);
			insertEntryDetails(entry);
			updateSalaryAccount(entry);
			setAccountEntry(entry);

			this.isNew = false;
			loadAccountEntryController(entry);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error accepting AccountEntry", e);
		}
	}
	
	public void onRemove(ActionEvent event){
		deleteAccountEntryDetails(getAccountEntry());
		deleteAccountEntry(getAccountEntry());

		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		entryController.onEditSearch(null);
	}
	
	private AccountEntry insertorUpdateAccountEntry(AccountEntry entry) {
		try {
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			if (this.isNew) {
				entry = (AccountEntry)entryBean.insert(entry);
			} else {
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

			Account socialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
			Account bankAccount = null;
			if (header.getRegistryBank() != null && header.getRegistryBank().getId() != null) {
				bankAccount = AccountUtil.obtainRBankAccount(header.getRegistryBank());
			} else {
				bankAccount = AccountUtil.obtainCashAccount();
			}
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(bankAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(bankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateSalaryAccount(AccountEntry entry) throws Exception {
		Calendar currentFrom = new GregorianCalendar();
		currentFrom.setTime(entry.getEntryDate());
		currentFrom.set(Calendar.DATE, 1);
		Calendar currentTo = new GregorianCalendar();
		currentTo.setTime(entry.getEntryDate());
		currentTo.set(Calendar.DATE, 1);
		currentTo.add(Calendar.MONTH, 1);
		currentTo.add(Calendar.DATE, -1);

		Calendar previousFrom = new GregorianCalendar();
		previousFrom.setTime(entry.getEntryDate());
		previousFrom.set(Calendar.DATE, 1);
		previousFrom.add(Calendar.MONTH, -1);
		Calendar previousTo = new GregorianCalendar();
		previousTo.setTime(entry.getEntryDate());
		previousTo.set(Calendar.DATE, 1);
		previousTo.add(Calendar.DATE, -1);

		AccountEntry acumEntry = new AccountEntry();
		int acumEntryId = 0; 
		double acumAmount = 0;
		boolean found = false; 
		Account socialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
		Account companySocialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT);

		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), currentFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), currentTo.getTime());
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.SOCIAL_INSURANCE);
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		Iterator iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			acumAmount += CommonUtil.round(entryDetail.getDebit() - entryDetail.getCredit());
		}

		criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), previousFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), previousTo.getTime());
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.SALARY);
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			acumAmount += CommonUtil.round(entryDetail.getDebit() - entryDetail.getCredit());
		}

		criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE), previousFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE), previousTo.getTime());
		criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE), AccountEntryType.SALARY);
		criteria.addOrder(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE));
		iterator = entryBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			acumEntry = (AccountEntry)iterator.next();
			acumEntryId = acumEntry.getId();
		} else {
			acumEntry.setEntryDate(previousTo.getTime());
			acumEntry.setAccountPeriod(AccountUtil.obtainPeriod(previousTo.getTime()).getId());
			acumEntry.setJournal(null);
			acumEntry.setType(AccountEntryType.SALARY);
			acumEntry.setSecurityLevel(getHeader().getSecurityLevel());
			acumEntry = (AccountEntry)entryBean.insert(acumEntry);
			acumEntryId = acumEntry.getId();
		}

		criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), acumEntryId);
		Expression expr1 = ExpressionUtilities.getEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), companySocialInsuranceAccount.getId());
		Expression expr2 = ExpressionUtilities.getEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			if (entryDetail.getAccount().equals(companySocialInsuranceAccount)) {
				if (entryDetail.getCredit() > 0) {
					entryDetail.setCredit(CommonUtil.round(entryDetail.getCredit() + acumAmount));
				} else {
					entryDetail.setDebit(CommonUtil.round(entryDetail.getDebit() + acumAmount));
				}
			} else {
				if (entryDetail.getDebit() > 0) {
					entryDetail.setDebit(CommonUtil.round(entryDetail.getDebit() + acumAmount));
				} else {
					entryDetail.setCredit(CommonUtil.round(entryDetail.getCredit() + acumAmount));
				}
			}
			entryDetailBean.update(entryDetail);
			found = true;
		} 
		if (!found) {
			AccountEntryDetail entryDetail = new AccountEntryDetail();
			entryDetail.setAccount(companySocialInsuranceAccount);
			entryDetail.setAccountEntry(acumEntry);
			entryDetail.setBalancingAccount(socialInsuranceAccount);
			entryDetail.setConcept(getHeader().getConcept());
			entryDetail.setDebit(acumAmount);
			entryDetailBean.insert(entryDetail);

			entryDetail = new AccountEntryDetail();
			entryDetail.setAccount(socialInsuranceAccount);
			entryDetail.setAccountEntry(acumEntry);
			entryDetail.setBalancingAccount(companySocialInsuranceAccount);
			entryDetail.setConcept(getHeader().getConcept());
			entryDetail.setCredit(acumAmount);
			entryDetailBean.insert(entryDetail);
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

		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, "570*");
		if (accountEntryDetail == null) {
			accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getAccount().getId()));
		}
		header.setConcept(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		header.setAmount(accountEntryDetail.getCredit());
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_social_insurance_entry";
	}

}
