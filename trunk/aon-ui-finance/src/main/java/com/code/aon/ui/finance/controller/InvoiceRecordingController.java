package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRecordingController extends BasicController{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceRecordingController.class.getName());
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	private ArrayList<Invoice> checks = new ArrayList<Invoice>();

	private IPriceStrategy priceStrategy;

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
			if(isRecordable(invoice)){
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
	
	public boolean isModelToRecordable() throws ManagerBeanException{
		if ( getModel().isRowAvailable() ) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return isRecordable(invoice);
		}
		return false;
	}
	
	private boolean isRecordable(Invoice invoice) throws ManagerBeanException {
		return InvoiceStatus.PENDING.equals(invoice.getStatus()) && (getInvoiceTotal(invoice) == getFinanceTotal(invoice));
	}

	private double getInvoiceTotal(Invoice invoice) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}

	private double getFinanceTotal(Invoice invoice) throws ManagerBeanException {
		double financeTotal = 0;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = financeBean.getList(criteria).iterator();
		while(iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			financeTotal += finance.getAmount();
		}
		return financeTotal;
	}

	public List<SelectItem> getInvoiceTypes() {
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(AonUtil.getCurrentLocale()));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(AonUtil.getCurrentLocale()));
		types.add(item);
		return types;
	}

	public void onRecordSelected(ActionEvent event){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			Iterator<Invoice> iter = getCheckedInvoices().iterator();
			while(iter.hasNext()){
				Invoice invoice = iter.next();
				if (invoice.getStatus() == InvoiceStatus.PENDING) {
					try {
						HibernateUtil.beginTransaction(sessionName);
						getAccountEntryInvoiceWriter().recordInvoice(invoice);
						invoice.setStatus(InvoiceStatus.SCORED);
						HibernateUtil.getSession(sessionName).merge(invoice);
						HibernateUtil.getSession(sessionName).flush();
						HibernateUtil.commitTransaction(sessionName);
					} catch (Exception e) {
						try {
							HibernateUtil.rollbackTransaction(sessionName);
						} catch (DAOException daoe) {
							String msg =  "Unable to rollback transaction!";
							LOGGER.log(Level.SEVERE, msg, e);
						}
						String msg =  "Error recording invoice:  " + invoice.getReferenceCode();
						LOGGER.log(Level.SEVERE, msg, e);
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					} finally {
						HibernateUtil.closeSession(sessionName);
					}
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

}