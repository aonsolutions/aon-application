package com.code.aon.finance.invoicing.remover;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.registry.Registry;

public class FeeInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(FeeInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.FEE);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
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
			LOGGER.fine("Attempt to remove Invoice Detail: " + invoiceDetail.getId());
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

	private Customer obtainCustomer(Registry registry) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Customer customer = (Customer) customerBean.get(registry.getId());
		return customer;
	}

}