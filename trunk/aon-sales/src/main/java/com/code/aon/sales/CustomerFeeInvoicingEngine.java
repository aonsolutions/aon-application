package com.code.aon.sales;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.IInvoicingDAO;
import com.code.aon.finance.invoicing.IInvoicingEngine;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.sales.dao.ISalesAlias;

public class CustomerFeeInvoicingEngine implements IInvoicingEngine {
	
	private IInvoicingDAO invoicingDAO;
	
	private IInvoicingFeedBack invoicingFeedBack;
	
	public IInvoicingDAO getInvoicingDAO() {
		return invoicingDAO;
	}

	public IInvoicingFeedBack getInvoicingFeedBack() {
		return invoicingFeedBack;
	}

	public void setInvoicingDAO(IInvoicingDAO invoicingDAO) {
		this.invoicingDAO = invoicingDAO;
	}

	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack) {
		this.invoicingFeedBack = invoicingFeedBack; 
	}

	@SuppressWarnings("unchecked")
	public void invoice(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		Expression dateExpression = createFromToExpression(params.getMonth(), params.getYear());
		criteria.addExpression(dateExpression);
		if(params.getCustomerId() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), params.getCustomerId());
		}
		if(params.getSecurityLevel() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_SECURITY_LEVEL), params.getSecurityLevel());
		}
		if(params.getWorkPlaceId() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_WORK_PLACE_ID), params.getWorkPlaceId());
		}
		criteria.addOrder(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_SURNAME));
		criteria.addOrder(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		Iterator iter = customerFeeBean.getList(criteria).iterator();
		int counter = params.getNumber();
		Invoice invoice = null;
		Integer previousCustomerId = new Integer(Integer.MIN_VALUE);
		while(iter.hasNext()){
			CustomerFee customerFee = (CustomerFee)iter.next();
			if(!previousCustomerId.equals(customerFee.getCustomer().getId())){
				// se crea finance asociado al invoice anterior, que ya no tiene más detalles
				if(invoice != null){
					getInvoicingDAO().createFinances(invoice);
				}
				previousCustomerId = customerFee.getCustomer().getId();
				counter = calculateNextNumber(counter, params.getSeries());
				invoice = createInvoice(customerFee, counter, params);
				getInvoicingDAO().insertInvoice(invoice);
				getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getSeries() + "/" + invoice.getNumber());
				counter++;
			}
			InvoiceDetail invoiceDetail = createInvoiceDetail(customerFee, invoice);
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingDAO().updateSource(customerFee);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
		// se crea finance asociado al ultimo invoice, que ya no tiene más detalles
		if(invoice != null){
			getInvoicingDAO().createFinances(invoice);
		}
	}

	private Expression createFromToExpression(Month month, int year) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month.getValue(), 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date from = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date to = calendar.getTime();
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Expression billingExpr = ExpressionUtilities.getBetweenExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_BILLING_DATE), from, to);
		Expression initialExpr = ExpressionUtilities.getLessThanOrEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_INITIAL_DATE), to);
        Expression finalExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_FINAL_DATE), from);
        Expression finalNullExpr = ExpressionUtilities.getNullExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_FINAL_DATE));
        return ExpressionUtilities.getAndExpression(ExpressionUtilities.getAndExpression(billingExpr, initialExpr), ExpressionUtilities.getOrExpression(finalExpr, finalNullExpr));
	}

	private InvoiceDetail createInvoiceDetail(CustomerFee customerFee, Invoice invoice) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setDeliveryDetail(null);
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setItem(customerFee.getItem());
        invoiceDetail.setDescription(customerFee.getDescription());
        if (!invoice.getRegistry().getId().equals(customerFee.getCustomer().getId())) {
            invoiceDetail.setDescription(invoiceDetail.getDescription() + " - " + customerFee.getCustomer().getRegistry().getName() + " " + customerFee.getCustomer().getRegistry().getSurname());
        }
        invoiceDetail.setDiscountExpression(customerFee.getDiscountExpression());
		invoiceDetail.setPrice(customerFee.getPrice());
		invoiceDetail.setQuantity(customerFee.getQuantity());
		invoiceDetail.setSource(InvoiceSource.FEE);
		invoiceDetail.setTaxes(0.0);
		invoiceDetail.setWorkPlace(customerFee.getWorkPlace());
		return invoiceDetail;
	}
	
	private Invoice createInvoice(CustomerFee customerFee, int counter, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		Registry registry = obtainInvoicingGroupRegistry(customerFee.getCustomer().getRegistry());
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryName((registry.getName() == null?"":registry.getName()) + " " + (registry.getSurname()==null?"":registry.getSurname()));
		invoice.setSeries(params.getSeries());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(params.getSecurityLevel());
		invoice.setWithholding(customerFee.getCustomer().isWithholding());
		return invoice;
	}
	
	@SuppressWarnings("unchecked")
	private Registry obtainInvoicingGroupRegistry(Registry registry) throws ManagerBeanException {
		IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_CHILD_ID), registry.getId());
		Iterator iter = invoicingGroupDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			InvoicingGroupDetail groupDetail = (InvoicingGroupDetail)iter.next();
			return groupDetail.getInvoicingGroup().getParent();
		}
		return registry;
	}

	@SuppressWarnings("unchecked")
	private int calculateNextNumber(int counter, String series) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		counter = (counter == 0?1:counter);
		while(true){
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER), new Integer(counter));
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series);
			Iterator iter = invoiceBean.getList(criteria).iterator();
			if(!iter.hasNext()){
				break;
			}
			counter++;
		}
		return counter;
	}
}