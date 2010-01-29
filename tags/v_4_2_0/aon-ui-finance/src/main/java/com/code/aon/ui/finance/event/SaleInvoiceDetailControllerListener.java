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
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.finance.controller.SaleInvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceDetailControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceDetailControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		controller.setLongDescription(false);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		if (invoiceDetail.getDescription().length() > 64) {
			controller.setLongDescription(true);
		} else {
			controller.setLongDescription(false);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		obtainTaxableBase(event, invoiceDetail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(event, invoiceDetail);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
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

	private void obtainTaxableBase(ControllerEvent event, InvoiceDetail invoiceDetail) {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
	}

}