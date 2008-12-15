package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRemoverController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(InvoiceRemoverController.class.getName());
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	private ArrayList<Invoice> checks = new ArrayList<Invoice>();

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
			if(isRemovable(invoice)){
				if (!checks.contains( invoice )) {
					checks.add( invoice );
				}
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Invoice to = (Invoice) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Invoice to = (Invoice) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<Invoice> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new ArrayList<Invoice>();
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		clearCheckedInvoices();
	}
	
	public boolean isModelToRemovable() throws ManagerBeanException{
		if ( getModel().isRowAvailable() ) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return isRemovable(invoice);
		}
		return false;
	}
	
	private boolean isRemovable(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING));
		if(financeBean.getCount(criteria) == 0){
			return true;
		}
		return false;
	}

	public void onRemoveSelected(ActionEvent event){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Iterator<Invoice> iter = getCheckedInvoices().iterator();
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );
			while(iter.hasNext()){
				Invoice invoice = iter.next();
				try {
					HibernateUtil.beginTransaction(sessionName);
					if(invoice.getStatus().equals(InvoiceStatus.SCORED)){
						getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
					}
					removeFinanceTrackings(invoice);
					removeFinances(invoice);
					removeInvoiceDetailAccounts(invoice);
					removeInvoiceDetails(invoice);
					removeInvoiceAddress(invoice);
					getManagerBean().remove(invoice);
					HibernateUtil.getSession(sessionName).flush();					
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.log(Level.SEVERE, msg, e);
					}
					String msg =  "Error deleting invoice:  " + invoice.getSeries()+"/"+ invoice.getNumber();
					LOGGER.log(Level.SEVERE, msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFinanceTrackings(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_INVOICE_ID), invoice.getId());
		Iterator iter = financeTrackingBean.getList(criteria).iterator();
		while(iter.hasNext()){
			financeTrackingBean.remove((FinanceTracking)iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator iter = financeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			financeBean.remove((Finance)iter.next());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceDetailAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceAccountBean.getList(criteria).iterator();
		while(iter.hasNext()){
			invoiceAccountBean.remove((InvoiceDetailAccount)iter.next());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeInvoiceDetails(Invoice invoice) throws InvoicingException, ManagerBeanException {
		Iterator iter = obtainInvoiceDetails(invoice).iterator();
		while(iter.hasNext()){
			InvoiceDetail detail = (InvoiceDetail)iter.next();
			IInvoiceDetailRemover remover = InvoiceRemoverFactory.getInvoiceDetailRemover(detail.getSource()); 
			remover.removeDetail(detail);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceAddress(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IFinanceAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceAddressBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			invoiceAddressBean.remove((InvoiceAddress)iter.next());
		}
	}
	
	@SuppressWarnings("unchecked")
	private List obtainInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		return invoiceDetailBean.getList(criteria);
	}
}