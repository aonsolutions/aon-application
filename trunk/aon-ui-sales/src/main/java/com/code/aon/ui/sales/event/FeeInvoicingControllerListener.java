package com.code.aon.ui.sales.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.CustomerFee;
import com.code.aon.sales.enumeration.BillingPeriod;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.FeeInvoicingDetailController;
import com.code.aon.ui.util.AonUtil;

public class FeeInvoicingControllerListener extends ControllerAdapter {
	
	private static final String FEE_INVOINCING_DETAIL_CONTROLLER_NAME = "feeInvoicingDetail";

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
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
		FeeInvoicingDetailController detailController = (FeeInvoicingDetailController)AonUtil.getController(FEE_INVOINCING_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		FeeInvoicingDetailController detailController = (FeeInvoicingDetailController)AonUtil.getController(FEE_INVOINCING_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		FeeInvoicingDetailController detailController = (FeeInvoicingDetailController)AonUtil.getController(FEE_INVOINCING_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		FeeInvoicingDetailController detailController = (FeeInvoicingDetailController)AonUtil.getController(FEE_INVOINCING_DETAIL_CONTROLLER_NAME);
		detailController.setWorkPlace(null);
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
}
