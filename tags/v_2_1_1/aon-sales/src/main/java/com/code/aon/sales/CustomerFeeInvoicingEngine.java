package com.code.aon.sales;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoicingGroup;
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
import com.code.aon.ql.util.ExpressionException;
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
		try {
			Criteria criteria = createInvoicingCriteria(params);
			List invoicedList = invoiceGroupFees(criteria, params);
			criteria = completeCriteriaWithCustomerData(criteria, params);
			List feeList = obtainFeeList(updateCriteria(criteria,invoicedList));
			invoiceFees(feeList, params);
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Criteria completeCriteriaWithCustomerData(Criteria criteria,InvoicingParameters params) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		if(params.getCustomerId() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), params.getCustomerId());
		}
		return criteria;
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		Expression dateExpression = createFromToExpression(params.getMonth(), params.getYear());
		criteria.addExpression(dateExpression);
		if(params.getSecurityLevel() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_SECURITY_LEVEL), params.getSecurityLevel());
		}
		if(params.getWorkPlaceId() != null){
			criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_WORK_PLACE_ID), params.getWorkPlaceId());
		}
		criteria.addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addOrder(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_SURNAME));
		criteria.addOrder(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	private void invoiceFees(List feeList, InvoicingParameters params) throws ManagerBeanException {
		int counter = params.getNumber();
		Invoice invoice = null;
		Integer previousCustomerId = new Integer(Integer.MIN_VALUE);
		Iterator iter = feeList.iterator();
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
			InvoiceDetail invoiceDetail = createInvoiceDetail(customerFee, invoice, params);
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingDAO().updateSource(customerFee);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
		// se crea finance asociado al ultimo invoice, que ya no tiene más detalles
		if(invoice != null){
			getInvoicingDAO().createFinances(invoice);
		}
	}

	@SuppressWarnings("unchecked")
	private List invoiceGroupFees(Criteria criteria, InvoicingParameters params) throws ManagerBeanException, ExpressionException {
		List<Integer> invoicedList = new LinkedList<Integer>();
		IManagerBean invoicigGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria groupCriteria = new Criteria();
		if(params.getCustomerId() != null){
			groupCriteria.addEqualExpression(invoicigGroupBean.getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID), params.getCustomerId());
		}
		Iterator iter = invoicigGroupBean.getList(groupCriteria).iterator();
		while(iter.hasNext()){
			InvoicingGroup group = (InvoicingGroup)iter.next();
			List list = invoiceGroup(group, criteria, params);
			invoicedList.addAll(list);
		}
		return invoicedList;
	}

	@SuppressWarnings("unchecked")
	private List invoiceGroup(InvoicingGroup group, Criteria criteria, InvoicingParameters params) throws ManagerBeanException, ExpressionException {
		List<Integer> invoicedList = new LinkedList<Integer>();
		IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria groupCriteria = new Criteria();
		Expression exp = null;
		exp = ExpressionUtilities.getOrExpression(exp, ExpressionUtilities.getEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), group.getParent().getId()));
		Criteria parentCriteria = new Criteria();
		parentCriteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID), group.getId());
		Iterator iter = invoicingGroupDetailBean.getList(parentCriteria).iterator();
		while(iter.hasNext()){
			InvoicingGroupDetail detail = (InvoicingGroupDetail)iter.next();
			exp = ExpressionUtilities.getOrExpression(exp, ExpressionUtilities.getEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), detail.getChild().getId()));
		}
		groupCriteria.addExpression(ExpressionUtilities.getAndExpression(criteria.getExpression(), exp));
		// Cuotas tanto del padre como de los hijos
		Iterator feeIter = customerFeeBean.getList(groupCriteria).iterator();
		if(feeIter.hasNext()){
			invoicedList.add(group.getParent().getId());
			params.setNumber(calculateNextNumber(params.getNumber(), params.getSeries()));
			Invoice invoice = createInvoice(group, params);
			getInvoicingDAO().insertInvoice(invoice);
			getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getSeries() + "/" + invoice.getNumber());
			params.setNumber(params.getNumber()+1);
			while(feeIter.hasNext()){
				CustomerFee fee = (CustomerFee)feeIter.next();
				InvoiceDetail invoiceDetail = createInvoiceDetail(fee, invoice, params);
				getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
				getInvoicingDAO().updateSource(fee);
				getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());			
				invoicedList.add(fee.getCustomer().getId());
			}
			getInvoicingDAO().createFinances(invoice);
		}
		return invoicedList;
	}

	private Invoice createInvoice(InvoicingGroup group, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setNumber(params.getNumber());
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setRegistry(group.getParent());
		invoice.setRegistryDocument(group.getParent().getDocument());
		invoice.setRegistryName((group.getParent().getName() == null?"":group.getParent().getName()) + " " + (group.getParent().getSurname()==null?"":group.getParent().getSurname()));
		invoice.setSeries(params.getSeries());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(params.getSecurityLevel());
		invoice.setSurcharge(obtainRelatedCustomer(group.getParent()).isSurcharge());
		invoice.setTaxFree(obtainRelatedCustomer(group.getParent()).isTaxFree());
		invoice.setWithholding(obtainRelatedCustomer(group.getParent()).isWithholding());
		return invoice;
	}

	@SuppressWarnings("unchecked")
	private List obtainFeeList(Criteria updatedCriteria) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		return customerFeeBean.getList(updatedCriteria);
	}

	@SuppressWarnings("unchecked")
	private Criteria updateCriteria(Criteria criteria, List invoicedList) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Iterator iter = invoicedList.iterator();
		Expression exp =  null;
		while(iter.hasNext()){
			Integer id = (Integer)iter.next();
			exp = ExpressionUtilities.getAndExpression(exp, ExpressionUtilities.getNotEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), id));
		}
		if(exp != null){
			criteria.addExpression(exp);
		}
		return criteria;
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

	private InvoiceDetail createInvoiceDetail(CustomerFee customerFee, Invoice invoice, InvoicingParameters params) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setDeliveryDetail(null);
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setItem(customerFee.getItem());
        invoiceDetail.setDescription(customerFee.getDescription());
        if (!invoice.getRegistry().getId().equals(customerFee.getCustomer().getId())) {
            invoiceDetail.setDescription(invoiceDetail.getDescription() + " - " + customerFee.getCustomer().getRegistry().getName() + " " + customerFee.getCustomer().getRegistry().getSurname());
        }
        invoiceDetail.setDiscountExpression(customerFee.getDiscountExpression());
		invoiceDetail.setPrice(customerFee.getPrice() * calculateCorrectionFactor(customerFee, params));
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
		Registry registry = customerFee.getCustomer().getRegistry();
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryName((registry.getName() == null?"":registry.getName()) + " " + (registry.getSurname()==null?"":registry.getSurname()));
		invoice.setSeries(params.getSeries());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(params.getSecurityLevel());
		invoice.setSurcharge(customerFee.getCustomer().isSurcharge());
		invoice.setTaxFree(customerFee.getCustomer().isTaxFree());
		invoice.setWithholding(customerFee.getCustomer().isWithholding());
		return invoice;
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

	private double calculateCorrectionFactor(CustomerFee customerFee, InvoicingParameters params) {
		if (customerFee.getPeriod().getValue() == 0) {
			return 1;
		} else {
			Calendar calendar = new GregorianCalendar(params.getYear(), params.getMonth().getValue(), 1, 0, 0, 0);
			Date fromInv = calendar.getTime();
			calendar.add(Calendar.MONTH, customerFee.getPeriod().getValue());
			calendar.add(Calendar.DATE, -1);
			Date toInv = calendar.getTime();

			Date iniFee = (customerFee.getInitialDate().before(fromInv)) ? fromInv : customerFee.getInitialDate();
			Date endFee = (customerFee.getFinalDate() == null || customerFee.getFinalDate().after(toInv)) ? toInv : customerFee.getFinalDate();

			return (double)daysBetween(iniFee, endFee) / (double)daysBetween(fromInv, toInv);
		}
	}

	private long daysBetween(Date from, Date to) {
		return ((to.getTime() - from.getTime()) / ((60 * 60 * 1000) * 24)) + 1;
	}

	@SuppressWarnings("unchecked")
	private Customer obtainRelatedCustomer(Registry parent) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), parent.getId());
		Iterator iter = customerBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}

}