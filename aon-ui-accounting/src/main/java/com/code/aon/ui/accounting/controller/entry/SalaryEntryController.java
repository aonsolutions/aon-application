package com.code.aon.ui.accounting.controller.entry;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.SalaryEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class SalaryEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryEntryController.class.getName());

	private SalaryEntry entry;
	private String navigationKey;
	private AccountBridgeUtil accountBridgeUtil;

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public SalaryEntry getEntry() {
		return entry;
	}

	public void setEntry(SalaryEntry entry) {
		this.entry = entry;
	}

	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void reset() throws ManagerBeanException {
		setEntry(new SalaryEntry());
		getEntry().setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		getEntry().setDate(new Date());
		getEntry().setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	public String accept() {
		return navigationKey;
	}

	public void onAccept(ActionEvent event) {
		// inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				this.navigationKey = "accountEntry_form";
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
				AccountEntry entry = new AccountEntry();
				entry.setEntryDate(getEntry().getDate());
				entry.setAccountPeriod(getEntry().getPeriod());
				entry.setType(AccountEntryType.SALARY);
				entry.setSecurityLevel(getEntry().getSecurityLevel());
				entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
				entry = (AccountEntry) entryBean.update(entry);
				insertEntryDetails(entry);
				loadAccountEntryController(entry);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				navigationKey = null;
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage();
				LOGGER.error(msg, e);
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

	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SALARY_ACC));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getGrossSalary());
			accountEntryDetailBean.insert(detail);

			if (getEntry().getAllowance() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_ALLOWANCE_ACC));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getEntry().getConcept());
				detail.setDebit(getEntry().getAllowance());
				accountEntryDetailBean.insert(detail);
			}

			if (getEntry().getCompensation() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_COMPENSATION_ACC));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getEntry().getConcept());
				detail.setDebit(getEntry().getCompensation());
				accountEntryDetailBean.insert(detail);
			}

			if (getEntry().getCompanySocialInsurance() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getEntry().getConcept());
				detail.setDebit(getEntry().getCompanySocialInsurance());
				accountEntryDetailBean.insert(detail);
			}

			if (getEntry().getRetention() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_SALARY_CHARGED_RET_ACC));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getEntry().getConcept());
				detail.setCredit(getEntry().getRetention());
				accountEntryDetailBean.insert(detail);
			}

			detail = new AccountEntryDetail();
			detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getTotalSocialInsurance());
			accountEntryDetailBean.insert(detail);

			// Quinto Apunte
			detail = new AccountEntryDetail();
			if (getEntry().getRegistryBank() != null && getEntry().getRegistryBank().getId() != null) {
				detail.setAccount(getAccountBridgeUtil().obtainRBankAccount(getEntry().getRegistryBank()));
			} else {
				detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC));
			}
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getNetSalary());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error al grabar el asiento de nómina.", e);
		}

	}

	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error cargando el asiento.", e);
		}
	}

}
