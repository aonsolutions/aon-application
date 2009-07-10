package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.InvoicingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Invoicing controller listener
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InvoicingListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private static final Logger LOGGER = Logger.getLogger(InvoicingListener.class.getName());

	/**
	 * Fills tax info in invoice before add
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		fillTaxInfo(invoice);
	}

	/**
	 * Fills tax info in invoice before update
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		fillTaxInfo(invoice);
	}
	
	/**
	 * Assigns in invoicing controller warehouseid property 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoicingController invoicingController = (InvoicingController)event.getController();
		invoicingController.setWarehouseId(obtainWarehouseId((Invoice)invoicingController.getTo()));
		super.afterBeanSelected(event);
	}
	
	/**
	 * Resets in invoicing controller warehouseid
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((InvoicingController)event.getController()).setWarehouseId(null);
		super.afterBeanCreated(event);
	}
	
	/**
	 * Fills the criteria for type purchase 
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		Criteria criteria;
		try {
			criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.PURCHASE);
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_SERIES));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Gets company info and fills invoice tax related info 
	 * 
	 * @param invoice related invoice
	 */
	@SuppressWarnings("unchecked")
	private void fillTaxInfo(Invoice invoice) {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator iter = companyBean.getList(null).iterator();
			boolean surcharge = false;
			boolean taxFree = false;
			if(iter.hasNext()){
				Company company = (Company)iter.next();
				surcharge = company.isSurcharge();
				taxFree = company.isTaxFree();
			}
			invoice.setTaxFree(taxFree);
			invoice.setSurcharge(surcharge);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error obtaining company", e);
		}
	}
	
	/**
	 * Recovers warehouse ident of this invoice
	 * 
	 * @param invoice related invoice
	 * @return warehouse ident
	 */
	private Integer obtainWarehouseId(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DIRECT_PURCHASE);
			Iterator iter = invoiceDetailBean.getList(criteria, 0, 1).iterator();
			IncomeDetail incomeDetail = null;
			if(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				incomeDetail = obtainIncomeDetail(invoiceDetail.getDeliveryDetail());
			}else{
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
				iter = invoiceDetailBean.getList(criteria, 0, 1).iterator();
				if(iter.hasNext()){
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					incomeDetail = obtainIncomeDetail(invoiceDetail.getDeliveryDetail());
				}
			}
			if(incomeDetail != null){
				return incomeDetail.getWarehouse().getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining warehouse for invoice with id= " + invoice.getId(), e);
		}
		return null;
	}

	/**
	 * Recover incomedetail with this deliverydetail
	 * 
	 * @param deliveryDetail related delivery detail
	 * @return income detail
	 * @throws ManagerBeanException
	 */
	private IncomeDetail obtainIncomeDetail(Integer deliveryDetail) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), deliveryDetail);
		Iterator iter = incomeDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (IncomeDetail)iter.next();
		}
		return null;
	}
}