package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceControllerListener.class.getName());

//	private static final String SALE_INVOICE_DETAIL_CONTROLLER_NAME = "saleInvoiceDetail";
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		Criteria criteria;
		try {
			criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_SERIES));
			criteria.addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		fillTaxInfo(invoice);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		fillTaxInfo(invoice);
	}

	@SuppressWarnings("unchecked")
	private void fillTaxInfo(Invoice invoice) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), invoice.getRegistry().getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			boolean surcharge = false;
			boolean taxFree = false;
			boolean withholding = false;
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				surcharge = customer.isSurcharge();
				taxFree = customer.isTaxFree();
				withholding = customer.isWithholding();
			}
			invoice.setSurcharge(surcharge);
			invoice.setTaxFree(taxFree);
			invoice.setWithholding(withholding);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error obtaining customer", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		Iterator iter = invoice.getDetailList().iterator();
		while(iter.hasNext()){
			InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
			if(invoiceDetail.getSource().equals(InvoiceSource.FEE)){
				try {
					createCustomerFee(invoiceDetail);
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e);
				}
			}
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			saleInvoiceController.loadAddresses(null);
//			saleInvoiceController.setSeriesDescripition("");
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}

//		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
//		detailController.setWorkPlace(null);
	}
/*	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}
*/	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			Invoice invoice = (Invoice) saleInvoiceController.getTo();
			saleInvoiceController.loadAddresses(invoice.getRegistry().getId());
//			saleInvoiceController.setSeriesDescripition(obtainSeriesDescription(invoice.getSeries()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private void createCustomerFee(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		CustomerFee customerFee = new CustomerFee();
		customerFee.setCustomer(obtainCustomer(invoiceDetail.getInvoice().getRegistry()));
		customerFee.setDescription(invoiceDetail.getDescription());
		customerFee.setDiscountExpression(invoiceDetail.getDiscountExpression());
		customerFee.setInitialDate(invoiceDetail.getInvoice().getIssueDate());
		customerFee.setFinalDate(invoiceDetail.getInvoice().getIssueDate());
		customerFee.setBillingDate(invoiceDetail.getInvoice().getIssueDate());
		customerFee.setItem(invoiceDetail.getItem());
		customerFee.setPeriod(BillingPeriod.NO_PERIOD);
		customerFee.setPrice(invoiceDetail.getPrice());
		customerFee.setQuantity(invoiceDetail.getQuantity());
		customerFee.setSecurityLevel(invoiceDetail.getInvoice().getSecurityLevel());
		customerFee.setWorkPlace(invoiceDetail.getWorkPlace());
		customerFeeBean.insert(customerFee);
	}
	
	@SuppressWarnings("unchecked")
	private Customer obtainCustomer(Registry registry) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), registry.getId());
		Iterator iter = customerBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}
/*	
	@SuppressWarnings("unchecked")
	private String obtainSeriesDescription(String series) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), series);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((Series)iter.next()).getDescription();
		}
		return null;
	}
*/
}