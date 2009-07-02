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

import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.SalaryEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.config.Bank;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class SalaryEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(SalaryEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private SalaryEntryHeader header;

	private Company company;

	private AccountingUtil accountingUtil;

	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
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

	public SalaryEntryHeader getHeader() {
		return header;
	}

	public void setHeader(SalaryEntryHeader header) {
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
	
	private SalaryEntryHeader initializeHeader() {
		SalaryEntryHeader header = new SalaryEntryHeader();
		header.setDate(new Date());
		header.setRegistryBank(new RegistryBank());
		header.getRegistryBank().setBank(new Bank());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
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
					AccountingPeriodUtil.validateAccountPeriod(getHeader().getDate());
					AccountEntry entry = new AccountEntry();
					if (!this.isNew) {
						deleteAccountEntryDetails(getAccountEntry());
						entry = this.getAccountEntry();
					}
					entry.setEntryDate(getHeader().getDate());
					entry.setAccountPeriod(AccountUtil.obtainPeriod(getHeader().getDate()).getId());
					entry.setJournal(null);
					entry.setType(AccountEntryType.SALARY);
					entry.setSecurityLevel(getHeader().getSecurityLevel());
					entry = insertorUpdateAccountEntry(entry);
					insertEntryDetails(entry);
					setAccountEntry(entry);
					
					this.isNew = false;
					loadAccountEntryController(entry);
				} catch (ManagerBeanException e) {
					LOGGER.log(Level.SEVERE, "Error accepting AccountEntry", e);
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
				deleteAccountEntryDetails(getAccountEntry());
				deleteAccountEntry(getAccountEntry());
				
				AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
				entryController.onEditSearch(null);
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
			LOGGER.log(Level.SEVERE, "Error inserting AccountEntry", e);
		}
		return entry;
	}
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.SALARY_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getGrossSalary());
			accountEntryDetailBean.insert(detail);

			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getCompanySocialInsurance());
			accountEntryDetailBean.insert(detail);

			// Tercer Apunte
			if (getHeader().getRetention() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getHeader().getConcept());
				detail.setCredit(getHeader().getRetention());
				accountEntryDetailBean.insert(detail);
			}

			// Cuarto Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getTotalSocialInsurance());
			accountEntryDetailBean.insert(detail);

			// Quinto Apunte
			detail = new AccountEntryDetail();
			if (getHeader().getRegistryBank() != null && getHeader().getRegistryBank().getId() != null) {
				detail.setAccount(AccountUtil.obtainRBankAccount(getHeader().getRegistryBank()));
			} else {
				detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.PENDING_SALARY_ACCOUNT));
			}
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getNetSalary());
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

		SalaryEntryHeader header = new SalaryEntryHeader();
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "465*");
		if (accountEntryDetail != null) {
			header.setConcept(accountEntryDetail.getConcept());
		} else {
			accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			if (accountEntryDetail != null) {
				header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getAccount().getId()));
				header.setConcept(accountEntryDetail.getConcept());
			}
		}
		header.setSecurityLevel(entry.getSecurityLevel());
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "640*");
		header.setGrossSalary((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "475*");
		header.setRetention((accountEntryDetail != null)?accountEntryDetail.getCredit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "642*");
		header.setCompanySocialInsurance((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "476*");
		header.setEmployeeSocialInsurance((accountEntryDetail != null)?accountEntryDetail.getCredit() - header.getCompanySocialInsurance():0);
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_salary_entry";
	}
	
	
	public String getPeriodMessage() {
		try {
			return AccountingPeriodUtil.getValidAccountPeriod(getHeader().getDate());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return " - ";
		}
	}

}