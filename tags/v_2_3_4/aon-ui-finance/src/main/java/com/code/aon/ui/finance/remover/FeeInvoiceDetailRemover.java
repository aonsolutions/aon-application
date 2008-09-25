package com.code.aon.ui.finance.remover;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.CustomerFee;
import com.code.aon.sales.enumeration.BillingPeriod;
import com.code.aon.ui.util.AonUtil;

public class FeeInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(FeeInvoiceDetailRemover.class.getName());

	@Override
	@SuppressWarnings("unchecked")
	public void removeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			/* CREAR LA CUOTA RELACIONADA */
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
			/* BORRAR LA LINEA DE FACTURA */
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error removing Details");
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Customer obtainCustomer(Registry registry) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), registry.getId());
		Iterator iter = customerBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}
}