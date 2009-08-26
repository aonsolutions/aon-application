package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AccountChangeController {

	private Account initAccount;
	private Account balancingAccount;
	private Account finalAccount;
	private Date fromDate;
	private Date toDate;
	private Period period;
	private SecurityLevel securityLevel;
	private static final Logger LOGGER = Logger.getLogger(AccountEntryDetail.class.getName());
	
	
	public void onReset(ActionEvent e){
		setInitAccount(null);
		setBalancingAccount(null);
		setFinalAccount(null);
		setFromDate(null);
		setToDate(null);
		setPeriod(null);
		setSecurityLevel(null);
	}
	

	public void onChangeAccounts(ActionEvent e)  {
		if(!finalAccount.isEntryEnabled()){
			String msg ="La Cuenta Destino no permite apuntes";
				LOGGER.log(Level.SEVERE, msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
		}
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
                int count=0;
				IManagerBean bean;
				bean = BeanManager.getManagerBean(AccountEntryDetail.class);

				String accountInit = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
				String accountBalancing = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT_ID);
				String date = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
				String accperiod = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD);
				String security = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountInit, initAccount.getId());
				criteria.addEqualExpression(accperiod, period.getId());

				if (fromDate != null) {
					criteria.addGreaterThanOrEqualExpression(date, fromDate);
				}
				if (toDate != null) {
					criteria.addLessThanOrEqualExpression(date, toDate);
				}
				if (securityLevel != null) {
					criteria.addEqualExpression(security, securityLevel);
				}
				if (balancingAccount != null) {
					criteria.addEqualExpression(accountBalancing, balancingAccount.getId());
				}

				List<ITransferObject> accountDetailList = bean.getList(criteria);

				for (ITransferObject to : accountDetailList) {
					AccountEntryDetail acc = (AccountEntryDetail) to;
					acc.setAccount(finalAccount);
					bean.update(acc);
					count++;

					removeRelatedInvoiceAccounts(acc.getAccountEntry());
				}

				criteria = new Criteria();
				criteria.addEqualExpression(accountBalancing, initAccount.getId());
				criteria.addEqualExpression(accperiod, period.getId());
				if (fromDate != null) {
					criteria.addGreaterThanOrEqualExpression(date, fromDate);
				}
				if (toDate != null) {
					criteria.addLessThanOrEqualExpression(date, toDate);
				}
				if (securityLevel != null) {
					criteria.addEqualExpression(security, securityLevel);
				}
				if (balancingAccount != null) {
					criteria.addEqualExpression(accountInit, balancingAccount.getId());
				}

				accountDetailList = bean.getList(criteria);

				for (ITransferObject to : accountDetailList) {
					AccountEntryDetail acc = (AccountEntryDetail) to;
					acc.setBalancingAccount(finalAccount);
					bean.update(acc);
					count++;
				}

				AonUtil.addInfoMessage("Se han cambiado "+count+" líneas de apuntes. " +
						"Debe regenerar la contabilidad, (acumulados de " +
						"cuentas y ayudas de contrapartidas), para que los cambios " +
						"sean realmente efectivos.");
				
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception ex) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, ex);
				}
				String msg = "Error on aon-account:  " + ex.getMessage();
				LOGGER.log(Level.SEVERE, msg, ex);
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

	private void removeRelatedInvoiceAccounts(AccountEntry accEntry) throws ManagerBeanException {
		IManagerBean accEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), accEntry.getId());
		Iterator<?> iterator = accEntryInvoiceBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			AccountEntryInvoice accEntryInvoice = (AccountEntryInvoice)iterator.next();

			IManagerBean invoiceDetailAccBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailAccBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), accEntryInvoice.getInvoice().getId());
			criteria.addEqualExpression(invoiceDetailAccBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_ACCOUNT_ID), initAccount.getId());
			Iterator<?> iter = invoiceDetailAccBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				InvoiceDetailAccount invoiceDetailAcc = (InvoiceDetailAccount)iter.next();
				invoiceDetailAcc.setAccount(finalAccount);
				invoiceDetailAccBean.update(invoiceDetailAcc);
			}
			
			IManagerBean invoiceTaxAccBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceTaxAccBean.getFieldName(IAccountBridgeAlias.INVOICE_TAX_ACCOUNT_INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), accEntryInvoice.getInvoice().getId());
			criteria.addEqualExpression(invoiceTaxAccBean.getFieldName(IAccountBridgeAlias.INVOICE_TAX_ACCOUNT_ACCOUNT_ID), initAccount.getId());
			iter = invoiceTaxAccBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				InvoiceTaxAccount invoiceTaxAcc = (InvoiceTaxAccount)iter.next();
				invoiceTaxAcc.setAccount(finalAccount);
				invoiceTaxAccBean.update(invoiceTaxAcc);
			}
		}
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Account getInitAccount() {
		return initAccount;
	}

	public void setInitAccount(Account initAccount) {
		this.initAccount = initAccount;
	}

	public Account getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	public Account getFinalAccount() {
		return finalAccount;
	}

	public void setFinalAccount(Account finalAccount) {
		this.finalAccount = finalAccount;
	}

}