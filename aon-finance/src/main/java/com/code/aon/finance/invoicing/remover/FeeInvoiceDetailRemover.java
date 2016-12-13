package com.code.aon.finance.invoicing.remover;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Prepayment;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;

public class FeeInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.FEE);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException {
		try {
			Customer customer = obtainCustomer(invoiceDetail.getInvoice().getRegistry());
			Date feeDate = obtainFeeDate(invoiceDetail);
			int line = obtainMaxLine(customer);

			CustomerFee customerFee = new CustomerFee();
			customerFee.setCustomer(customer);
			customerFee.setLine(++line);
			customerFee.setItem(invoiceDetail.getItem());
			customerFee.setDescription(invoiceDetail.getDescription());
			customerFee.setQuantity(invoiceDetail.getQuantity());
			customerFee.setPrice(invoiceDetail.getPrice());
			customerFee.setDiscountExpression(invoiceDetail.getDiscountExpression());
			customerFee.setInitialDate(feeDate);
			customerFee.setFinalDate(feeDate);
			customerFee.setBillingDate(feeDate);
			customerFee.setPeriod(BillingPeriod.NO_PERIOD);
			customerFee.setSecurityLevel(invoiceDetail.getInvoice().getSecurityLevel());
			customerFee.setSeller(invoiceDetail.getSeller());
			customerFee.setWorkPlace(invoiceDetail.getWorkPlace());

			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			customerFee = (CustomerFee)customerFeeBean.insert(customerFee);

			if (invoiceDetail.isPrepayment()) {
				IManagerBean prepaymentBean = BeanManager.getManagerBean(Prepayment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT), PrepaymentCollect.INVOICE_DETAIL);
				criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT_ID), invoiceDetail.getId());
				for (ITransferObject ito : prepaymentBean.getList(criteria)) {
					Prepayment prepayment = (Prepayment)ito;
					if (prepayment.getCustomer().getId() != customer.getId()) {
						prepayment.setCustomer(customer);
					}
					prepayment.setCollect(PrepaymentCollect.FEE);
					prepayment.setCollectId(customerFee.getId());
					prepaymentBean.update(prepayment);
					break;
				}
			}
		} catch (ManagerBeanException e) {
			throw new InvoicingException(e.getMessage(),e);
		}
	}

	private Customer obtainCustomer(Registry registry) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Customer customer = (Customer) customerBean.get(registry.getId());
		return customer;
	}

	private Date obtainFeeDate(InvoiceDetail invoiceDetail) {
		if (invoiceDetail.getSourceId() != null) {
			int year = invoiceDetail.getSourceId() / 100;
			int month = invoiceDetail.getSourceId() - (year * 100);
			return CommonUtil.getDate(year, (month-1), 1);
		}
		return invoiceDetail.getInvoice().getIssueDate();
	}

	private	int obtainMaxLine(Customer customer) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
		Projection projection = Projection.max(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_LINE));
		Object value = customerFeeBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value).intValue() : 0;
	}

}