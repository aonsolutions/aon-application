package com.code.aon.accounting.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class PeriodEntriesManager {
	private PeriodEntriesParams params;
	
	public PeriodEntriesManager( PeriodEntriesParams params ) {
		if (params == null) {
			throw new IllegalArgumentException("Los parámetros no pueden ser nulos.");
		}
		this.params = params;
	}
	

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodEntriesManager.class.getName());
	
	public AccountEntry[] createOperatingEntry() throws ManagerBeanException {
		validateOperatingEntryParameters();
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
				
				AccountEntry officialEntry = saveOperatingEntry(SecurityLevel.OFFICIAL);
				AccountEntry confidentialEntry = null;
				if (params.isConfidentialEntryPresent()) {
					confidentialEntry = saveOperatingEntry(SecurityLevel.CONFIDENTIAL);
				}

				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				params.getPeriod().setStatus(AccountPeriodStatus.OPERATING);
				params.setPeriod((Period) HibernateUtil.getSession(sessionName).merge(params.getPeriod()));
				params.setPeriod((Period) periodBean.update(params.getPeriod()));

				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				
				return (params.isConfidentialEntryPresent())?new AccountEntry[] {officialEntry,confidentialEntry}:new AccountEntry[] {officialEntry}; 
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error al generar el asiento de explotación del ejercicio " + params.getPeriod().getName() + ". (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				throw new ManagerBeanException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private AccountEntry saveOperatingEntry(SecurityLevel securityLevel) throws ManagerBeanException {
		if (params.isClosingEntry()) {
			deleteIfExists(params.getPeriod(),AccountEntryType.CLOSING,SecurityLevel.OFFICIAL);	
			deleteIfExists(params.getPeriod(),AccountEntryType.CLOSING,SecurityLevel.CONFIDENTIAL);
		}
		deleteIfExists(params.getPeriod(),AccountEntryType.OPERATING,securityLevel);
		List<?> list = getUnbalancedAccounts(params.getPeriod(), AccountEntryType.OPERATING,securityLevel);
		if (list == null || list.isEmpty()) {
			return null;
		}
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(params.getPeriod());
		entry.setEntryDate(params.getOperatingDate());
		entry.setType(AccountEntryType.OPERATING);
		entry.setSecurityLevel(securityLevel);
		entry = (AccountEntry) entryBean.insert(entry);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		double sum = 0;
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			AccountEntryDetail detail = new AccountEntryDetail();
			Object[] data = (Object[]) list.get(i);
			Account account = (Account) accountBean.get((Integer) data[0]);
			detail.setAccount(account);
			detail.setAccountEntry(entry);
			detail.setConcept(params.getOperatingConcept());
			Double debit = (Double) data[1];
			Double credit = (Double) data[2];
			double balance = CommonUtil.round(credit - debit);
			sum = CommonUtil.round(sum + balance);
			if (balance !=  0) {
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
		}

		AccountEntryDetail detail = new AccountEntryDetail();
		Account account = getResultAcount();
		detail.setAccount(account);
		detail.setAccountEntry(entry);
		detail.setConcept(params.getOperatingConcept());
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

	public void deleteIfExists(Period period, AccountEntryType type, SecurityLevel securityLevel) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), period.getId());
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_TYPE), type);
		if (securityLevel != null) {
			criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);
		}
		List<ITransferObject> list = entryBean.getList(criteria);
		for (ITransferObject to : list) {
			AccountEntry entry = (AccountEntry) to;
			Criteria c = new Criteria();
			c.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
			List<ITransferObject> details = entryDetailBean.getList(c);
			for (ITransferObject detail : details) {
				entryDetailBean.remove(detail);
			}
			entryBean.remove(entry);
		}
	}

	private void validateOperatingEntryParameters() throws ManagerBeanException {
		validateParameters(AccountEntryType.OPERATING);	
		// Si no existe asiento de apertura y el ejercicio no es el primero cerrado, se lanza el error.
		AccountingUtil util = new AccountingUtil();
		if (!util.existsEntry(params.getPeriod(), AccountEntryType.OPENING, SecurityLevel.OFFICIAL)) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Criteria c = new Criteria();
			c.addLessThanExpression(periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE), params.getPeriod().getInitiationDate());
			c.addEqualExpression(periodBean.getFieldName(IEntityAlias.PERIOD_STATUS), AccountPeriodStatus.CLOSED);
			if (periodBean.getList(c).size() > 0) {
				String msg = "No existe el asiento de apertura en el ejercicio " + params.getPeriod().getName() + ".";
				throw new ManagerBeanException(msg);
			}
		}
		if (params.getOperatingDate() == null) {
			String msg = "La fecha del asiento de explotación es un campo requerido.";
			throw new ManagerBeanException(msg);
		}
		if (StringUtils.isEmpty(params.getOperatingConcept())) {
			String msg = "El concepto del asiento de explotación no puede estar vacio.";
			throw new ManagerBeanException(msg);
		}
	}

	private void validateParameters(AccountEntryType accountEntryType) throws ManagerBeanException {
		if (params.getPeriod() == null) {
			String msg = "El periodo es un campo requerido.";
			throw new ManagerBeanException(msg);
		}
	}
	
	private Account getResultAcount() throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "129");
		Iterator<ITransferObject> iter = accountBean.getList(c).iterator();
		if (!iter.hasNext()) {
			Account account = new Account();
			account.setCode("129");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		}
		c = new Criteria();
		c.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "1290");
		iter = accountBean.getList(c).iterator();
		if (!iter.hasNext()) {
			Account account = new Account();
			account.setCode("1290");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		}
		c = new Criteria();
		c.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), "129000000");
		iter = accountBean.getList(c).iterator();
		Account account = null;
		if (!iter.hasNext()) {
			account = new Account();
			account.setCode("129000000");
			account.setDescription("Resultados del ejercicio.");
			accountBean.insert(account);
		} else {
			account = (Account) iter.next();
		}
		return account;
	}

	private List<?> getUnbalancedAccounts(Period period, AccountEntryType accountEntryType, SecurityLevel securityLevel) {
		String sessionName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionName);
		StringBuilder sw = new StringBuilder();
		sw.append("SELECT account.id,SUM(debit),SUM(credit)");
		sw.append(" FROM AccountEntryDetail ");
		sw.append(" WHERE ");
		sw.append( DomainManager.getSQLWhereClause("accountEntry.domain") );
		sw.append(" AND accountEntry.accountPeriod = '");
		sw.append(period.getId());
		sw.append("'");
		if (accountEntryType == AccountEntryType.OPERATING) {
			sw.append(" AND (account.code LIKE '6%' OR account.code LIKE '7%')");
		}
		if (securityLevel == SecurityLevel.CONFIDENTIAL) {
			sw.append(" AND accountEntry.securityLevel = ");;
			sw.append(Integer.toString(securityLevel.ordinal()));
		} else {
			sw.append(" AND (accountEntry.securityLevel = ");;
			sw.append(Integer.toString(securityLevel.ordinal()));
			sw.append(" OR accountEntry.securityLevel IS NULL) ");;
		}
		sw.append(" GROUP BY account.id HAVING SUM(debit) != SUM(credit)");
		sw.append(" ORDER BY account.code ");
		Query query = session.createQuery(sw.toString());
		return query.list();
	}
	
	public AccountEntry[] createClosingEntry() throws ManagerBeanException {
		validateClosingEntryParameters();
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
				AccountEntry officialEntry = saveClosingEntry(SecurityLevel.OFFICIAL);
				AccountEntry confidentialEntry = null; 
				if (params.isConfidentialEntryPresent()) {
					confidentialEntry = saveClosingEntry(SecurityLevel.CONFIDENTIAL);
				}
				
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				params.getPeriod().setStatus(AccountPeriodStatus.CLOSED);
				params.setPeriod((Period) HibernateUtil.getSession(sessionName).merge(params.getPeriod()));
				params.setPeriod((Period) periodBean.update(params.getPeriod()));

				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				return (params.isConfidentialEntryPresent())?new AccountEntry[] {officialEntry,confidentialEntry}:new AccountEntry[] {officialEntry};
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error al generar el asiento de cierre del ejercicio " + params.getPeriod() + ". (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				throw new ManagerBeanException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private void validateClosingEntryParameters() throws ManagerBeanException {
		validateParameters(AccountEntryType.CLOSING);
		AccountingUtil util = new AccountingUtil();
		if (!util.existsEntry(params.getPeriod(), AccountEntryType.OPERATING, SecurityLevel.OFFICIAL)) {
			List<?> list = getUnbalancedAccounts(params.getPeriod(), AccountEntryType.OPERATING,SecurityLevel.OFFICIAL);
			if (list != null && !list.isEmpty()) {
				String msg = "No existe el asiento de explotación en el ejercicio " + params.getPeriod().getName() + ".";
				throw new ManagerBeanException(msg);
			}
		}
	}

	private AccountEntry saveClosingEntry(SecurityLevel securityLevel) throws ManagerBeanException {
		deleteIfExists(params.getPeriod(),AccountEntryType.CLOSING,securityLevel);
		List<?> list = getUnbalancedAccounts(params.getPeriod(), AccountEntryType.CLOSING, securityLevel);
		if (list == null || list.isEmpty()) {
			return null;
		}
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(params.getPeriod());
		entry.setEntryDate(params.getClosingDate());
		entry.setType(AccountEntryType.CLOSING);
		entry.setSecurityLevel(securityLevel);
		entry = (AccountEntry) entryBean.insert(entry);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			AccountEntryDetail detail = new AccountEntryDetail();
			Object[] data = (Object[]) list.get(i);
			Account account = (Account) accountBean.get((Integer) data[0]);
			detail.setAccount(account);
			detail.setAccountEntry(entry);
			detail.setConcept(params.getClosingConcept());
			Double debit = (Double) data[1];
			Double credit = (Double) data[2];
			double balance = CommonUtil.round(credit - debit);
			if (balance !=  0) {
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
		}
		return entry;
	}

	public AccountEntry[] createOpeningEntry() throws ManagerBeanException {
		validateOpeningEntryParameters();
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
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				if (params.isOpeningPeriodCreationEnabled()) {
					periodBean.insert(params.getOpeningPeriod());	
				}
				AccountEntry officialEntry = saveOpeningEntry( SecurityLevel.OFFICIAL);
				AccountEntry confidentialEntry = null;
				if (params.isConfidentialEntryPresent()) {
					confidentialEntry = saveOpeningEntry( SecurityLevel.CONFIDENTIAL);	
				}
				
				params.getOpeningPeriod().setStatus(AccountPeriodStatus.OPENING);
				params.setOpeningPeriod((Period) HibernateUtil.getSession(sessionName).merge(params.getOpeningPeriod()));
				params.setOpeningPeriod((Period) periodBean.update(params.getOpeningPeriod()));
			
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				
				return (params.isConfidentialEntryPresent())?new AccountEntry[] {officialEntry,confidentialEntry}:new AccountEntry[] {officialEntry};
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error al generar el asiento de apertura del ejercicio " + params.getOpeningPeriod().getName() + ". (" + e.getMessage() + ")";
				LOGGER.error(msg, e);
				throw new ManagerBeanException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

	}
	
	
	private AccountEntry saveOpeningEntry(SecurityLevel securityLevel) throws ManagerBeanException {
		deleteIfExists(params.getOpeningPeriod(),AccountEntryType.OPENING,securityLevel);
		AccountEntry previous = null;
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(params.getOpeningPeriod());
		entry.setEntryDate(params.getOpeningDate());
		entry.setType(AccountEntryType.OPENING);
		entry.setSecurityLevel(securityLevel);
		entry = (AccountEntry) entryBean.insert(entry);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), params.getPeriod().getId());
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_TYPE), AccountEntryType.CLOSING);
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);
		List<ITransferObject> list = entryBean.getList(criteria);
		if (list.size() > 0) {
			previous = (AccountEntry) list.get(0);
			IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria detailCriteria = new Criteria();
			detailCriteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), previous
					.getId());
			detailCriteria.addOrder(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_LINE));
			List<ITransferObject> details = entryDetailBean.getList(detailCriteria);
			if (details.size() <= 0) {
				String msg = "El asiento de cierre en el ejercicio " + params.getPeriod().getId() + " no tiene apuntes.";
				throw new ManagerBeanException(msg);
			}
			for (ITransferObject to : details) {
				AccountEntryDetail det = (AccountEntryDetail) to;
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccount(det.getAccount());
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(det.getBalancingAccount());
				detail.setConcept(params.getOpeningConcept());
				detail.setCredit(det.getDebit());
				detail.setDebit(det.getCredit());
				detail.setLine(det.getLine());
				if (CommonUtil.round(det.getDebit())==0 && CommonUtil.round(det.getCredit())==0) {
					// Nada
				} else {
					entryDetailBean.insert(detail);
				}
			}
			return entry;
		}
		return null;
	}

	private void validateOpeningEntryParameters() throws ManagerBeanException {
		validateParameters(AccountEntryType.OPENING);	
		if (params.getPeriod().equals(params.getOpeningPeriod())) {
			String msg = "Los periodos no pueden ser iguales.";
			throw new ManagerBeanException(msg);
		}
		AccountingUtil util = new AccountingUtil();
		if (!util.existsEntry(params.getPeriod(), AccountEntryType.CLOSING, SecurityLevel.OFFICIAL)) {
			String msg = "No existe asiento de cierre el ejercicio " + params.getPeriod().getId() + ".";
			throw new ManagerBeanException(msg);
		}
	}
	
}
