package com.code.aon.ui.accounting.controller;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class EndPeriodEntriesController {

	private Date date;
	private String period;
	private String previousPeriod;
	private String concept;
	private SecurityLevel securityLevel;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPreviousPeriod() {
		return previousPeriod;
	}

	public void setPreviousPeriod(String previousPeriod) {
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

	private void validateParameters(AccountEntryType accountEntryType) throws ManagerBeanException {
		if (StringUtils.isEmpty(period)) {
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

		if (accountEntryType == AccountEntryType.OPENING) {
			if (StringUtils.isEmpty(previousPeriod)) {
				String msg = "El periodo anterior es un campo requerido.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (period.equals(previousPeriod)) {
				String msg = "Los periodos no pueden ser iguales.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}

		if (existsEntry(getPeriod(), accountEntryType, getSecurityLevel())) {
			String msg = "Ya existe el asiento en el ejercicio " + getPeriod() + ".";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (accountEntryType == AccountEntryType.CLOSING
				|| accountEntryType == AccountEntryType.OPERATING) {
			if (existsEntry(getPeriod(), accountEntryType, getSecurityLevel())) {
				String msg = "Ya existe el asiento en el ejercicio " + getPeriod() + ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (!existsEntry(getPeriod(), AccountEntryType.OPENING, getSecurityLevel())) {
				String msg = "No existe el asiento de apertura en el ejercicio " + getPeriod()
						+ ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		if (accountEntryType == AccountEntryType.CLOSING) {
			if (!existsEntry(getPeriod(), AccountEntryType.OPERATING, getSecurityLevel())) {
				String msg = "No existe el asiento de explotación en el ejercicio " + getPeriod()
						+ ".";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}

	}

	private boolean existsEntry(String period, AccountEntryType accountEntryType,
			SecurityLevel securityLevel) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period);
		criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE),
				accountEntryType);
		criteria.addEqualExpression(entryBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);
		List<ITransferObject> list = entryBean.getList(criteria);
		return (list.size() > 0);
	}

	public void onOpeningEntry(ActionEvent event) {
		try {
			validateParameters(AccountEntryType.OPENING);
			AccountEntry previous = null;
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), getPreviousPeriod());
			criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE),
					AccountEntryType.CLOSING);
			criteria.addEqualExpression(entryBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), getSecurityLevel());
			List<ITransferObject> list = entryBean.getList(criteria);
			if (list.size() <= 0) {
				String msg = "No existe asiento de cierre en el ejercicio " + getPreviousPeriod()
						+ " (" + getSecurityLevel() + ").";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			previous = (AccountEntry) list.get(0);
			IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria detailCriteria = new Criteria();
			detailCriteria.addEqualExpression(entryDetailBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), previous
					.getId());
			detailCriteria.addOrder(entryDetailBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_LINE));
			List<ITransferObject> details = entryDetailBean.getList(detailCriteria);
			if (details.size() <= 0) {
				String msg = "El asiento de cierre en el ejercicio " + getPreviousPeriod()
						+ " no tiene apuntes.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			AccountEntry entry = new AccountEntry();
			entry.setAccountPeriod(getPeriod());
			entry.setEntryDate(getDate());
			entry.setType(AccountEntryType.OPENING);
			entry.setSecurityLevel(getSecurityLevel());
			entry = (AccountEntry) entryBean.insert(entry);
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
			String msg = "El asiento de apertura se ha generado correctamente.";
			AonUtil.addInfoMessage(msg);
		} catch (ManagerBeanException e) {
			String msg = "Error al generar el asiento de apertura del ejercicio " + getPeriod()
					+ ". (" + e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	@SuppressWarnings("unchecked")
	public void onClosingEntry(ActionEvent event) {
		try {
			validateParameters(AccountEntryType.CLOSING);
			List list = getUnbalancedAccounts(getPeriod(), AccountEntryType.CLOSING);
			saveClosingEntry(list);
			String msg = "El asiento de cierre se ha generado correctamente.";
			AonUtil.addInfoMessage(msg);
		} catch (ManagerBeanException e) {
			String msg = "Error al generar el asiento de cierre del ejercicio " + getPeriod()
					+ ". (" + e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	@SuppressWarnings("unchecked")
	private void saveClosingEntry(List list) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getPeriod());
		entry.setEntryDate(getDate());
		entry.setType(AccountEntryType.CLOSING);
		entry.setSecurityLevel(getSecurityLevel());
		entry = (AccountEntry) entryBean.insert(entry);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			AccountEntryDetail detail = new AccountEntryDetail();
			Object[] data = (Object[]) list.get(i);
			Account account = new Account();
			account.setId((String) data[0]);
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
	}

	@SuppressWarnings("unchecked")
	public void onOperatingEntry(ActionEvent event) {
		try {
			validateParameters(AccountEntryType.OPERATING);
			List list = getUnbalancedAccounts(getPeriod(), AccountEntryType.OPERATING);
			saveOperatingEntry(list);
			String msg = "El asiento de explotación se ha generado correctamente.";
			AonUtil.addInfoMessage(msg);
		} catch (ManagerBeanException e) {
			String msg = "Error al generar el asiento de cierre del ejercicio " + getPeriod()
					+ ". (" + e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	@SuppressWarnings("unchecked")
	private void saveOperatingEntry(List list) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getPeriod());
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
			Account account = new Account();
			account.setId((String) data[0]);
			detail.setAccount(account);
			detail.setAccountEntry(entry);
			detail.setConcept(getConcept());
			Double debit = (Double) data[1];
			Double credit = (Double) data[2];
			double balance = round(credit - debit);
			sum = round(sum + balance);
			if (balance > 0) {
				detail.setDebit(balance);
				detail.setCredit(0);
			} else {
				balance = round(balance * (-1));
				detail.setCredit(balance);
				detail.setDebit(0);
			}
			detail.setLine(i);
			entryDetailBean.insert(detail);
		}

		AccountEntryDetail detail = new AccountEntryDetail();
		Account account = new Account();
		account.setId("129");
		detail.setAccount(account);
		detail.setAccountEntry(entry);
		detail.setConcept(getConcept());
		if (sum > 0) {
			detail.setCredit(sum);
			detail.setDebit(0);
		} else {
			sum = round(sum * (-1));
			detail.setDebit(sum);
			detail.setCredit(0);
		}
		detail.setLine(i);
		entryDetailBean.insert(detail);
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

	@SuppressWarnings("unchecked")
	private List getUnbalancedAccounts(String period, AccountEntryType accountEntryType) {
		Session session = HibernateUtil.getSession();
		StringWriter sw = new StringWriter();
		sw
				.append("SELECT account.id,SUM(debit),SUM(credit) FROM AccountSummary WHERE accountPeriod = '");
		sw.append(period);
		sw.append("'");
		if (accountEntryType == AccountEntryType.OPERATING) {
			sw.append(" AND (account.id LIKE '6%' OR account.id LIKE '7%')");
		}
		sw.append(" GROUP BY account.id HAVING SUM(debit) != SUM(credit)");
		Query query = session.createQuery(sw.toString());
		return query.list();
	}
}
