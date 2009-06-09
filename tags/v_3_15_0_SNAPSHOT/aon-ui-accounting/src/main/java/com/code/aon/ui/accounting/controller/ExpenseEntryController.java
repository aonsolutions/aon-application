package com.code.aon.ui.accounting.controller;

import java.util.Date;
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
import com.code.aon.accounting.ExpenseEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.accounting.utils.AccountPeriodValidator;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ExpenseEntryController implements ISpecialAccountEntry{
	
	private static final Logger LOGGER = Logger.getLogger(ExpenseEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private ExpenseEntryHeader header;

	private Company company;
	
	private String navigationKey;

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

	public ExpenseEntryHeader getHeader() {
		return header;
	}

	public void setHeader(ExpenseEntryHeader header) {
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
		setHeader( initializeHeader() );
	}
	
	private ExpenseEntryHeader initializeHeader() {
		ExpenseEntryHeader header = new ExpenseEntryHeader();
		header.setDate(new Date());
		header.setAccount(null);
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
			String message  = "Error obtaining Banks";
			LOGGER.log(Level.SEVERE, message , e);
			AonUtil.addErrorMessage(message);
		}
		return rBanks;
	}
	
	public String accept(){
		return navigationKey; 	
	}

	public void onAccept(ActionEvent event){
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				try {
					this.navigationKey = "accountEntry_form"; 
					AccountPeriodValidator.validateAccountPeriod(getHeader().getDate());
					AccountEntry entry = new AccountEntry();
					if (!this.isNew) {
						deleteAccountEntryDetails(getAccountEntry());
						entry = this.getAccountEntry();
					}
					entry.setEntryDate(getHeader().getDate());
					entry.setAccountPeriod(AccountUtil.obtainPeriod(getHeader().getDate()).getId());
					entry.setJournal(null);
					entry.setType(AccountEntryType.EXPENSES);
					entry.setSecurityLevel(getHeader().getSecurityLevel());
					entry = insertorUpdateAccountEntry(entry);
					insertEntryDetails(entry);
					setAccountEntry(entry);
					
					this.isNew = false;
					loadAccountEntryController(entry);
				} catch (ManagerBeanException e) {
					navigationKey = null;
					String message = "Error accepting AccountEntry";
					LOGGER.log(Level.SEVERE, message , e);
					AonUtil.addErrorMessage(message);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public void onRemove(ActionEvent event){
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				try {
					deleteAccountEntryDetails(getAccountEntry());
					deleteAccountEntry(getAccountEntry());
					
					AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
					entryController.onEditSearch(null);
				} catch (ManagerBeanException e) {
					String message = "Error deleting AccountEntry";
					LOGGER.log(Level.SEVERE, message , e);
					AonUtil.addErrorMessage(message);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
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
			String message = "Error inserting AccountEntry";
			LOGGER.log(Level.SEVERE, message , e);
			AonUtil.addErrorMessage(message);
		}
		return entry;
	}

	private void insertEntryDetails(AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

		Account bankAccount = null;
		if (getHeader().getRegistryBank() != null && getHeader().getRegistryBank().getId() != null) {
			bankAccount = AccountUtil.obtainRBankAccount(getHeader().getRegistryBank());
		} else {
			bankAccount = AccountUtil.obtainCashAccount();
		}
		// Primer apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccount(getHeader().getAccount());
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(bankAccount);
		detail.setConcept(getHeader().getConcept());
		detail.setDebit(getHeader().getAmount());
		accountEntryDetailBean.insert(detail);
		// Segundo apunte
		detail = new AccountEntryDetail();
		detail.setAccount(bankAccount);
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(getHeader().getAccount());
		detail.setConcept(getHeader().getConcept());
		detail.setCredit(getHeader().getAmount());
		accountEntryDetailBean.insert(detail);
	}

	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
		}
	}
	
	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}
	
	private void loadAccountEntryController(AccountEntry entry) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		setAccountEntry(entry);

		ExpenseEntryHeader header = new ExpenseEntryHeader();
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, 
				 AccountConstants.BANK_ACCOUNT_PREFIX + "*");
		if (accountEntryDetail != null) {
			header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getAccount().getId()));
		}
		accountEntryDetail = getAccountUtils().getEntryDetailFromAccountPattern(entry, "6*");
		header.setAccount(accountEntryDetail.getAccount());
		header.setConcept(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		header.setAmount(accountEntryDetail.getDebit());
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_expense_entry";
	}
	
	public String getPeriodMessage() {
		try {
			return AccountPeriodValidator.getValidAccountPeriod(getHeader().getDate());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			return " - ";
		}
	}
}
