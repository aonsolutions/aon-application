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
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceDetailControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceDetailControllerListener.class.getName());
	
	private IPriceStrategy priceStrategy;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail) event.getController().getTo();
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		obtainTaxableBase(invoiceDetail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail) event.getController().getTo();
		obtainTaxableBase(invoiceDetail);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail) event.getController().getTo();
		if (invoiceDetail.getSource().equals(InvoiceSource.FEE)) {
			try {
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
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException("Error removing InvoiceDetail with id="+ invoiceDetail.getId(), e);
			}
		}
	}

//	@Override
//	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
//		try {
//			SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)event.getController();
//			InvoiceDetail detail = (InvoiceDetail) detailController.getTo();
//			if (detailController.getWorkPlace() != null) {
//				detail.setWorkPlace(detailController.getWorkPlace());
//			} else {
//				SaleInvoiceController saleInvoiceController = (SaleInvoiceController)FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
//				detail.setWorkPlace(obtainSeriesWorkplace(((Invoice)saleInvoiceController.getTo()).getSeries()));
//			}
//		} catch (ManagerBeanException e) {
//			throw new ControllerListenerException("Error obtaining workplace related with the selected serie");
//		}
//	}

//	@Override
//	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
//		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)event.getController();
//		InvoiceDetail invoiceDetail = (InvoiceDetail) detailController.getTo();
//		detailController.setWorkPlace(invoiceDetail.getWorkPlace());
//	}

//	@Override
//	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
//		SaleInvoiceDetailController detailController = (SaleInvoiceDetailController)event.getController();
//		InvoiceDetail invoiceDetail = (InvoiceDetail) detailController.getTo();
//		detailController.setWorkPlace(invoiceDetail.getWorkPlace());
//	}

	@SuppressWarnings("unchecked")
	private Customer obtainCustomer(Registry registry) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), registry.getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Customer) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining customer with id="+ registry.getId(), e);
		}
		return null;
	}

	/*
	@SuppressWarnings("unchecked")
	private WorkPlace obtainSeriesWorkplace(String series) throws ManagerBeanException {
		WorkPlace workPlace = null;
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), series);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series dbSeries = (Series)iter.next();
			workPlace = (dbSeries.getWorkPlace() != null?dbSeries.getWorkPlace():workPlace);
		}
		return workPlace;
	}
	*/

	private void obtainTaxableBase(InvoiceDetail invoiceDetail) {
		invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
	}

	private IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
}