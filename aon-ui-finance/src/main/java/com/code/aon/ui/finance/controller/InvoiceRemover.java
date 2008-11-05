package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.InvoiceRemovingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.remover.IInvoiceDetailRemover;
import com.code.aon.ui.finance.remover.InvoiceRemoverFactory;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRemover extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(InvoiceRemover.class.getName());
	
	private InvoiceRemovingParameters removingParams;

	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	private ArrayList<Invoice> checks = new ArrayList<Invoice>();
	

	public InvoiceRemovingParameters getRemovingParams() {
		return removingParams;
	}

	public void setRemovingParams(InvoiceRemovingParameters removingParams) {
		this.removingParams = removingParams;
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@SuppressWarnings({"unused","unchecked"})
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

	@SuppressWarnings("unused")
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
	
	public void onEditSearch(MenuEvent event) throws ManagerBeanException {
		this.onEditSearch((ActionEvent)event);
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeSearch();
	}
	
	private void initializeSearch() {
		try {
			((PageDataModel)this.getModel()).resize(0);
			removingParams = new InvoiceRemovingParameters();
			clearCheckedInvoices();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error initializing search");
			LOGGER.log(Level.SEVERE, "Error initializing search", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	public boolean isModelToRemovable() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return isRemovable(invoice);
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
		Iterator<Invoice> iter = getCheckedInvoices().iterator();
		while(iter.hasNext()){
			Invoice invoice = iter.next();
			try {
				if(invoice.getStatus().equals(InvoiceStatus.SCORED)){
					getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
				}
				removeInvoiceAddress(invoice);
				removeFinanceTrackings(invoice);
				removeFinances(invoice);
				removeInvoiceDetails(invoice);
				getManagerBean().remove(invoice);
				clearCheckedInvoices();
				this.onSearch(null);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error deleting invoice with id=" + invoice.getId(), e);
				AonUtil.addErrorMessage("Error deleting invoice with id=" + invoice.getId());
				throw new AbortProcessingException("Error deleting invoice with id=" + invoice.getId());
			}
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
	private void removeInvoiceDetails(Invoice invoice) {
		try {
			Iterator iter = obtainInvoiceDetails(invoice).iterator();
			while(iter.hasNext()){
				InvoiceDetail detail = (InvoiceDetail)iter.next();
				IInvoiceDetailRemover remover = InvoiceRemoverFactory.getInvoiceDetailRemover(detail.getSource()); 
				if(remover != null){
					remover.removeDetail(detail);
				}else{
					AonUtil.addErrorMessage("No remover available for invoiceDetails with source=" + detail.getSource().toString());
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error removing details for invoice with id=" + invoice.getId());
			LOGGER.log(Level.SEVERE, "Error removing details for invoice with id=" + invoice.getId(), e);
			throw new AbortProcessingException(e.getMessage());
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