package com.code.aon.ui.accounting.controller;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class EndPeriodEntriesController {

	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private static final Logger LOGGER = LoggerFactory.getLogger(EndPeriodEntriesController.class
			.getName());

	private Date date;
	private Period period;
	private Period previousPeriod;
	private String concept;
	private SecurityLevel securityLevel;

	private String navigationKey;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Period getPreviousPeriod() {
		return previousPeriod;
	}

	public void setPreviousPeriod(Period previousPeriod) {
		this.previousPeriod = previousPeriod;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void onInit(ActionEvent event) {
		setDate(null);
		try {
			setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			setPeriod(null);
		}
		setPreviousPeriod(null);
		setConcept(null);
		setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	private void validateParameters(AccountEntryType accountEntryType)  {
		try {
			if (period == null) {
				String msg = "El periodo es un campo requerido.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (date == null) {
				String msg = "La fecha es un campo requerido.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (StringUtils.isEmpty(getConcept())) {
				String msg = "El concepto del apunte no puede estar vacio.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			AccountingUtil util = new AccountingUtil();
			if (util.existsEntry(getPeriod(), accountEntryType, getSecurityLevel())) {
				String msg = "Ya existe el asiento en el ejercicio " + getPeriod().getId() + ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
	
			if (accountEntryType == AccountEntryType.OPENING) {
				if (period.equals(previousPeriod)) {
					String msg = "Los periodos no pueden ser iguales.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
	
				if (!util.existsEntry(previousPeriod, AccountEntryType.CLOSING, getSecurityLevel())) {
					String msg = "No existe asiento de cierre el ejercicio " + previousPeriod.getId()
							+ ".";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			} else if (accountEntryType == AccountEntryType.OPERATING) {
				// Si no existe asiento de apertura y el ejercicio no es el primero cerrado,
				// se lanza el error.
				if (!util.existsEntry(getPeriod(), AccountEntryType.OPENING, getSecurityLevel())) {
					IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
					Criteria c = new Criteria();
					c.addLessThanExpression(periodBean
							.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), getPeriod()
							.getInitiationDate());
					c.addEqualExpression(periodBean
							.getFieldName(IAccountingAlias.PERIOD_STATUS), AccountPeriodStatus.CLOSED);
					if (periodBean.getList(c).size() > 0) {
						String msg = "No existe el asiento de apertura en el ejercicio "
								+ getPeriod().getId() + ".";
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
				}
			} else if (accountEntryType == AccountEntryType.CLOSING) {
				if (!util.existsEntry(getPeriod(), AccountEntryType.OPERATING, getSecurityLevel())) {
					String msg = "No existe el asiento de explotación en el ejercicio "
							+ getPeriod().getId() + ".";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}

	}

	public void onOpeningEntry(ActionEvent event) {
		validateParameters(AccountEntryType.OPENING);
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
				AccountEntry previous = null;
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);

				AccountEntry entry = new AccountEntry();
				entry.setAccountPeriod(getPeriod().getId());
				entry.setEntryDate(getDate());
				entry.setType(AccountEntryType.OPENING);
				entry.setSecurityLevel(getSecurityLevel());
				entry = (AccountEntry) entryBean.insert(entry);

				if (getPreviousPeriod() != null) {
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(entryBean
							.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD),
							getPreviousPeriod().getId());
					criteria.addEqualExpression(entryBean
							.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE),
							AccountEntryType.CLOSING);
					criteria.addEqualExpression(entryBean
							.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL),
							getSecurityLevel());
					List<ITransferObject> list = entryBean.getList(criteria);
					if (list.size() <= 0) {
						String msg = "No existe asiento de cierre en el ejercicio "
								+ getPreviousPeriod().getId() + " (" + getSecurityLevel() + ").";
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
					previous = (AccountEntry) list.get(0);
					IManagerBean entryDetailBean = BeanManager
							.getManagerBean(AccountEntryDetail.class);
					Criteria detailCriteria = new Criteria();
					detailCriteria.addEqualExpression(entryDetailBean
							.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID),
							previous.getId());
					detailCriteria.addOrder(entryDetailBean
							.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_LINE));
					List<ITransferObject> details = entryDetailBean.getList(detailCriteria);
					if (details.size() <= 0) {
						String msg = "El asiento de cierre en el ejercicio "
								+ getPreviousPeriod().getId() + " no tiene apuntes.";
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
					for (ITransferObject to : details) {
						AccountEntryDetail det = (AccountEntryDetail) to;
						AccountEntryDetail detail = new AccountEntryDetail();
						detail.setAccount(det.getAccount());
						detail.setAccountEntry(entry);
						detail.setBalancingAccount(det.getBalancingAccount());
						detail.setConcept(getConcept());
						detail.setCredit(det.getDebit());
						detail.setDebit(det.getCredit());
						detail.setLine(det.getLine());
						entryDetailBean.insert(detail);
					}
				}
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				getPeriod().setStatus(AccountPeriodStatus.OPENING);
				setPeriod( (Period) HibernateUtil.getSession(sessionName).merge(getPeriod()));
				setPeriod( (Period) periodBean.update( getPeriod() ) );
				
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
				String msg = "Error al generar el asiento de apertura del ejercicio " + getPeriod()
						+ ". (" + e.getMessage() + ")";
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

	public String navigate() {
		return navigationKey;
	}

	@SuppressWarnings("unchecked")
	public void onClosingEntry(ActionEvent event) {
		validateParameters(AccountEntryType.CLOSING);
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
				List list = getUnbalancedAccounts(getPeriod(), AccountEntryType.CLOSING);
				AccountEntry entry = saveClosingEntry(list);

				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				getPeriod().setStatus(AccountPeriodStatus.CLOSED);
				setPeriod( (Period) HibernateUtil.getSession(sessionName).merge(getPeriod()));
				setPeriod( (Period) periodBean.update( getPeriod() ) );

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
				String msg = "Error al generar el asiento de cierre del ejercicio " + getPeriod()
				+ ". (" + e.getMessage() + ")";
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

	@SuppressWarnings("unchecked")
	private AccountEntry saveClosingEntry(List list) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getPeriod().getId());
		entry.setEntryDate(getDate());
		entry.setType(AccountEntryType.CLOSING);
		entry.setSecurityLevel(getSecurityLevel());
		entry = (AccountEntry) entryBean.insert(entry);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			AccountEntryDetail detail = new AccountEntryDetail();
			Object[] data = (Object[]) list.get(i);
			Account account = (Account) accountBean.get((String) data[0]);
			detail.setAccount(account);
			detail.setAccountEntry(entry);
			detail.setConcept(getConcept());
			Double debit = (Double) data[1];
			Double credit = (Double) data[2];
			detail.setDebit(credit);
			detail.setCredit(debit);
			detail.setLine(i);
			entryDetailBean.insert(detail);
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	public void onOperatingEntry(ActionEvent event) {
		validateParameters(AccountEntryType.OPERATING);
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
				List list = getUnbalancedAccounts(getPeriod(), AccountEntryType.OPERATING);
				AccountEntry entry = saveOperatingEntry(list);
				
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				getPeriod().setStatus(AccountPeriodStatus.OPERATING);
				setPeriod( (Period) HibernateUtil.getSession(sessionName).merge(getPeriod()));
				setPeriod( (Period) periodBean.update( getPeriod() ) );

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
				String msg = "Error al generar el asiento de explotación del ejercicio " + getPeriod()
					+ ". (" + e.getMessage() + ")";
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

	@SuppressWarnings("unchecked")
	private AccountEntry saveOperatingEntry(List list) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getPeriod().getId());
		entry.setEntryDate(getDate());
		entry.setType(AccountEntryType.OPERATING);
		entry.setSecurityLevel(getSecurityLevel());
		entry = (AccountEntry) entryBean.insert(entry);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		double sum = 0;
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			AccountEntryDetail detail = new AccountEntryDetail();
			Object[] data = (Object[]) list.get(i);
			Account account = (Account) accountBean.get((String) data[0]);
			detail.setAccount(account);
			detail.setAccountEntry(entry);
			detail.setConcept(getConcept());
			Double debit = (Double) data[1];
			Double credit = (Double) data[2];
			double balance = CommonUtil.round(credit - debit);
			sum = CommonUtil.round(sum + balance);
			if (balance > 0) {
				detail.setDebit(balance);
				detail.setCredit(0);
			} else {
				balance = CommonUtil.round(balance * (-1));
				detail.setCredit(balance);
				detail.setDebit(0);
			}
			detail.setLine(i);
			entryDetailBean.insert(detail);
		}

		AccountEntryDetail detail = new AccountEntryDetail();
		Account account = getResultAcount();
		detail.setAccount(account);
		detail.setAccountEntry(entry);
		detail.setConcept(getConcept());
		if (sum > 0) {
			detail.setCredit(sum);
			detail.setDebit(0);
		} else {
			sum = CommonUtil.round(sum * (-1));
			detail.setDebit(sum);
			detail.setCredit(0);
		}
		detail.setLine(i);
		entryDetailBean.insert(detail);
		return entry;
	}

	private Account getResultAcount() throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Account account = (Account) accountBean.get("129");
		if (account == null) {
			account = new Account();
			account.setId("129");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		}
		account = (Account) accountBean.get("1290");
		if (account == null) {
			account = new Account();
			account.setId("1290");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		}
		account = (Account) accountBean.get("129000000");
		if (account == null) {
			account = new Account();
			account.setId("129000000");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		}
		return account;
	}

	@SuppressWarnings("unchecked")
	private List getUnbalancedAccounts(Period period, AccountEntryType accountEntryType) {
		String sessionName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionName);
		StringWriter sw = new StringWriter();
		sw
				.append("SELECT account.id,SUM(debit),SUM(credit) FROM AccountSummary WHERE accountPeriod = '");
		sw.append(period.getId());
		sw.append("'");
		if (accountEntryType == AccountEntryType.OPERATING) {
			sw.append(" AND (account.id LIKE '6%' OR account.id LIKE '7%')");
		}
		sw.append(" GROUP BY account.id HAVING SUM(debit) != SUM(credit)");
		Query query = session.createQuery(sw.toString());
		return query.list();
	}

	private void loadAccountEntryController(AccountEntry entry) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController) FormUtil
				.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
				IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}
}
