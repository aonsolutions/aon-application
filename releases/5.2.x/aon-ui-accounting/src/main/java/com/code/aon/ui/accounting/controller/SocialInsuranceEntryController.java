package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountEntryLink;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.SocialInsuranceEntry;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class SocialInsuranceEntryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SocialInsuranceEntryController.class.getName());
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	private SocialInsuranceEntry entry;
	private String navigationKey;

	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private Balance socialInsuranceBalance;
	private List<AccountEntryDetail> socialInsuranceDetail;
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
			socialInsuranceAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
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
			getEntry().setYear(AccountingPeriodUtil.getDefaultPeriod() == null ? null : AccountingPeriodUtil.getDefaultPeriod().getId());
			setSocialInsuranceBalance(null);
			setSocialInsuranceDetail(null);
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
				entry.setAccountPeriod(getEntry().getPeriod().getId());
				entry.setType(AccountEntryType.SOCIAL_INSURANCE);
				entry.setSecurityLevel(getEntry().getSecurityLevel());
				entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
				entry = (AccountEntry) entryBean.update(entry);
				insertEntryDetails(entry);
				AccountEntry adjust = null;
				if (getEntry().isPaymentAdjustable()) {
					adjust = adjustSocialInsuranceEntry(entry);
				}
				loadAccountEntryController(entry, adjust);
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
			Account companySocialInsuranceAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT);
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
		AccountEntryController entryController = (AccountEntryController) FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		Expression expr1 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID),
				entry.getId());
		if (adjust == null) {
			criteria.addExpression(expr1);
		} else {
			Expression expr2 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean()
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), adjust.getId());
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

	public void onChangeMonth(ValueChangeEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
	}

	public void onChangePeriod(ValueChangeEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
	}

	public Balance getSocialInsuranceBalance() {
		try {
			if (socialInsuranceBalance == null && getEntry().getYear() != null && getEntry().getMonth() != null) {
				int year = Integer.parseInt(getEntry().getYear());
				Date fromDate = CommonUtil.getDate(year, getEntry().getMonth().getValue(), 1);
				int days = CommonUtil.daysInMonth(fromDate);
				Date toDate = CommonUtil.getDate(year, getEntry().getMonth().getValue(), days);

				IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
				IManagerBean linkBean = BeanManager.getManagerBean(AccountEntryLink.class);
				String dateAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
				String typeAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE);
				String accountAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
				String accountEntryFromAlias = linkBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_LINK_ENTRY_FROM_ID);
				Criteria c = new Criteria();
				c.addBetweenExpression(dateAlias, fromDate, toDate);
				c.addEqualExpression(accountAlias, getSocialInsuranceAccount().getId());
				c.addExpression(ExpressionUtilities.getEqualExpression(typeAlias, AccountEntryType.SALARY));
				Balance balance = new Balance();
				balance.setAccount(getSocialInsuranceAccount().getId());
				balance.setDescription(getSocialInsuranceAccount().getDescription());
				balance.setFromDate(fromDate);
				balance.setToDate(toDate);
				List<ITransferObject> list = bean.getList(c);
				socialInsuranceDetail = new LinkedList<AccountEntryDetail>();
				for (ITransferObject to : list) {
					AccountEntryDetail detail = (AccountEntryDetail) to;
					boolean add = true;
					if (detail.getAccountEntry().getType() == AccountEntryType.SOCIAL_INSURANCE) {
						// Si existe un apunte de seguridad social en el
						// periodo, es ncesario saber si
						// el apunte de ajuste generado por él, es del mismo
						// periodo, para tenerlo en
						// cuenta.
						Criteria c1 = new Criteria();
						c1.addEqualExpression(accountEntryFromAlias, detail.getAccountEntry().getId());
						List<ITransferObject> links = linkBean.getList(c1);
						if (links != null && links.size() > 0) {
							AccountEntryLink link = (AccountEntryLink) links.get(0);
							AccountEntry linked = link.getEntryTo();
							Date date = linked.getEntryDate();
							add = ((DateUtils.isSameDay(fromDate, date) || fromDate.before(date)) 
								&& (DateUtils.isSameDay(toDate, date) || toDate.after(date)));
						}
					}
					if (add) {
						balance.addBalance(detail);
						socialInsuranceDetail.add(detail);
					}
				}
				setSocialInsuranceBalance(balance);
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

	public List<AccountEntryDetail> getSocialInsuranceDetail() {
		return socialInsuranceDetail;
	}

	public void setSocialInsuranceDetail(List<AccountEntryDetail> socialInsuranceDetail) {
		this.socialInsuranceDetail = socialInsuranceDetail;
	}
}
