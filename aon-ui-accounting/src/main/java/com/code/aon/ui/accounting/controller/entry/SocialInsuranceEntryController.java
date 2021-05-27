package com.code.aon.ui.accounting.controller.entry;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.SocialInsuranceEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class SocialInsuranceEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SocialInsuranceEntryController.class.getName());

	private SocialInsuranceEntry entry;
	private String navigationKey;

	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private Balance socialInsuranceBalance;
	private List<AccountEntryDetailExtended> socialInsuranceDetail;
	private DataModel socialInsuranceDetailModel;
	private Account socialInsuranceAccount;

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public Account getSocialInsuranceAccount() throws ManagerBeanException {
		if (socialInsuranceAccount == null) {
			socialInsuranceAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC);
		}
		return socialInsuranceAccount;
	}

	public SocialInsuranceEntry getEntry() {
		return entry;
	}

	public void setEntry(SocialInsuranceEntry entry) {
		this.entry = entry;
	}

	public void onReset(ActionEvent event) {
		try {
			socialInsuranceAccount = null;
			setEntry(new SocialInsuranceEntry());
			getEntry().setPeriod(AccountingPeriodUtil.getDefaultPeriod());
			getEntry().setDate(new Date());
			getEntry().setSecurityLevel(SecurityLevel.OFFICIAL);
			getEntry().setMonth(null);
			getEntry().setYear(AccountingPeriodUtil.getDefaultPeriod() == null ? null : AccountingPeriodUtil.getDefaultPeriod());	
			setSocialInsuranceBalance(null);
			setSocialInsuranceDetail(null);
			setSocialInsuranceDetailModel(null);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
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
				entry.setType(AccountEntryType.SOCIAL_INSURANCE);
				entry.setSecurityLevel(getEntry().getSecurityLevel());
				entry = (AccountEntry) entryBean.insert(entry);
				entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
				insertEntryDetails(entry);
				AccountEntry adjust = null;
				if (getEntry().isPaymentAdjustable()) {
					adjust = adjustSocialInsuranceEntry(entry);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				loadAccountEntryController(entry, adjust);
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

			Account socialInsuranceAccount = getSocialInsuranceAccount();
			Account bankAccount = null;
			if (getEntry().getRegistryBank() != null && getEntry().getRegistryBank().getId() != null) {
				bankAccount = getAccountBridgeUtil().obtainRBankAccount(getEntry().getRegistryBank());
			} else {
				bankAccount = getAccountingUtil().obtainCashAccount();
			}
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(bankAccount);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getAmount());
			accountEntryDetailBean.insert(detail);

			if (getEntry().getRecharge() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getEntry().getRechargeAccount());
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(bankAccount);
				detail.setConcept(getEntry().getConcept());
				detail.setDebit(getEntry().getRecharge());
				accountEntryDetailBean.insert(detail);
			}

			detail = new AccountEntryDetail();
			detail.setAccount(bankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getTotal());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}

	private AccountEntry adjustSocialInsuranceEntry(AccountEntry entry) throws ManagerBeanException {
		double dif = CommonUtil.round(getSocialInsuranceBalance().getCreditBalance() - getEntry().getAmount());
		AccountEntry adjustEntry = null;
		if (dif != 0) {
			adjustEntry = new AccountEntry();
			Account companySocialInsuranceAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC);
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			adjustEntry.setEntryDate(getSocialInsuranceBalance().getToDate());
			adjustEntry.setType(AccountEntryType.SOCIAL_INSURANCE_ADJUST);
			adjustEntry.setSecurityLevel(entry.getSecurityLevel());
			// adjustEntry.setAccountPeriod(entry.getAccountPeriod());
			adjustEntry.setAccountPeriod(getEntry().getYear());
			adjustEntry = (AccountEntry) entryBean.insert(adjustEntry);
			String concept = StringUtils.abbreviate("AJUSTE " + getEntry().getConcept(), 32);

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(adjustEntry);
			detail.setAccount(getSocialInsuranceAccount());
			detail.setLine(0);
			detail.setConcept(concept);
			detail.setDebit(dif);
			detail.setBalancingAccount(companySocialInsuranceAccount);
			entryDetailBean.insert(detail);

			detail = new AccountEntryDetail();
			detail.setAccountEntry(adjustEntry);
			detail.setAccount(companySocialInsuranceAccount);
			detail.setLine(1);
			detail.setConcept(concept);
			detail.setCredit(dif);
			detail.setBalancingAccount(getSocialInsuranceAccount());
			entryDetailBean.insert(detail);
		}
		return adjustEntry;
	}

	private void loadAccountEntryController(AccountEntry entry, AccountEntry adjust) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		Expression expr1 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID),
				entry.getId());
		if (adjust == null) {
			criteria.addExpression(expr1);
		} else {
			Expression expr2 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean()
					.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), adjust.getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	public boolean isPaymentAdjustDisabled() {
		boolean a = (!getEntry().isPaymentAdjustable() || getEntry().getMonth() == null);
		return a;
	}

	public void onChangeMonth(ActionEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
		setSocialInsuranceDetailModel(null);
		initializeDates();
	}

	public void onChangePeriod(ActionEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
		setSocialInsuranceDetailModel(null);
		initializeDates();
	}

	private void initializeDates() {
		Calendar c = Calendar.getInstance();
		c.setTime( getEntry().getYear().getInitiationDate() );
		int year = c.get(Calendar.YEAR );
		getEntry().setFromDate( CommonUtil.getDate(year, getEntry().getMonth().getValue(), 1));
		int days = CommonUtil.daysInMonth(getEntry().getFromDate());
		getEntry().setToDate( CommonUtil.getDate(year, getEntry().getMonth().getValue(), days));
	}

	public Balance getSocialInsuranceBalance() {
		try {
			if (socialInsuranceBalance == null && getEntry().getYear() != null && getEntry().getMonth() != null) {
				IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
				String dateAlias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
				String typeAlias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE);
				String accountAlias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
				String securityLevelAlias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL);
				Criteria c = new Criteria();
				c.addBetweenExpression(dateAlias, getEntry().getFromDate(), getEntry().getToDate());
				c.addEqualExpression(accountAlias, getSocialInsuranceAccount().getId());
				c.addExpression(ExpressionUtilities.getEqualExpression(typeAlias, AccountEntryType.SALARY));
				if (!AonUtil.getRoleManager().isConfidentiality()) {
					c.addEqualExpression(securityLevelAlias, SecurityLevel.OFFICIAL);
				} else {
					c.addEqualExpression(securityLevelAlias, getEntry().getSecurityLevel());	
				}
				Balance balance = new Balance();
				balance.setAccount(getSocialInsuranceAccount().getCode());
				balance.setDescription(getSocialInsuranceAccount().getDescription());
				balance.setFromDate(getEntry().getFromDate());
				balance.setToDate(getEntry().getToDate());
				List<ITransferObject> list = bean.getList(c);
				setSocialInsuranceDetail(new LinkedList<AccountEntryDetailExtended>());
				for (ITransferObject to : list) {
					AccountEntryDetail detail = (AccountEntryDetail) to;
					AccountEntryDetailExtended ex = new AccountEntryDetailExtended();
					ex.setDisabled(false);
					ex.setDetail(detail);
					balance.addBalance(detail);
					socialInsuranceDetail.add(ex);
				}
				setSocialInsuranceBalance(balance);
				setSocialInsuranceDetailModel(new SerializableListDataModel(getSocialInsuranceDetail()));
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener el saldo de la cuenta.";
			AonUtil.addErrorMessage(msg);
		} catch (NumberFormatException e) {
			// Nothing
		}
		return socialInsuranceBalance;
	}

	public void setSocialInsuranceBalance(Balance balance) {
		this.socialInsuranceBalance = balance;
	}

	public List<AccountEntryDetailExtended> getSocialInsuranceDetail() {
		return socialInsuranceDetail;
	}

	public void setSocialInsuranceDetail(List<AccountEntryDetailExtended> socialInsuranceDetail) {
		this.socialInsuranceDetail = socialInsuranceDetail;
	}
	public DataModel getSocialInsuranceDetailModel() {
		return this.socialInsuranceDetailModel; 
	}
	public void setSocialInsuranceDetailModel(DataModel socialInsuranceDetailModel) {
		this.socialInsuranceDetailModel = socialInsuranceDetailModel;
	}
	
	public void onResetBalance(ActionEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
		setSocialInsuranceDetailModel(null);
	}
	
	public void onDisable(ActionEvent event) {
		AccountEntryDetailExtended e = (AccountEntryDetailExtended) getSocialInsuranceDetailModel().getRowData();
		if (e.isDisabled()) {
			getSocialInsuranceBalance().substractBalance(e.getDetail());
		} else {
			getSocialInsuranceBalance().addBalance(e.getDetail());
		}
		
	}
	
	public static class AccountEntryDetailExtended implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private AccountEntryDetail detail;
		private boolean disabled;
		
		public AccountEntryDetail getDetail() {
			return detail;
		}
		public void setDetail(AccountEntryDetail detail) {
			this.detail = detail;
		}
		public boolean isDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
	}
}


