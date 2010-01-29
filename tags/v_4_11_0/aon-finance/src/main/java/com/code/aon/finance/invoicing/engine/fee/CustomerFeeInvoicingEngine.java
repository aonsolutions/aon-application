package com.code.aon.finance.invoicing.engine.fee;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;

public class CustomerFeeInvoicingEngine implements IInvoicingEngine {
	
	private IInvoicingDAO invoicingDAO;
	private IInvoicingFeedBack invoicingFeedBack;
	private int detailLine = 0;
	
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
		List feeList = obtainFeeList(createInvoicingCriteria(params), params);
		invoiceFees(feeList, createInvoicingCriteria(params), params);
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		Expression dateExpression = createFromToExpression(params.getMonth(), params.getYear());
		criteria.addExpression(dateExpression);
		criteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		if (params.getItem() != null && params.getItem().getId() != null) {
			criteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ITEM_ID), params.getItem().getId());
		}
		if (params.getSecurityLevel() != null) {
			criteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_SECURITY_LEVEL), params.getSecurityLevel());
		}
		if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null) {
			criteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_WORK_PLACE_ID), params.getWorkPlace().getId());
		}
		criteria.addOrder(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_SURNAME));
		criteria.addOrder(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		criteria.addOrder(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_ID));
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
		Expression billingExpr = ExpressionUtilities.getBetweenExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_BILLING_DATE), from, to);
		Expression initialExpr = ExpressionUtilities.getLessThanOrEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_INITIAL_DATE), to);
        Expression finalExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_FINAL_DATE), from);
        Expression finalNullExpr = ExpressionUtilities.getNullExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_FINAL_DATE));
        return ExpressionUtilities.getAndExpression(ExpressionUtilities.getAndExpression(billingExpr, initialExpr), ExpressionUtilities.getOrExpression(finalExpr, finalNullExpr));
	}

	@SuppressWarnings("unchecked")
	private Criteria completeCriteriaWithCustomerData(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		if (params.getCustomer() != null && params.getCustomer().getId() != null) {
			criteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), params.getCustomer().getId());
			InvoicingGroup group = getInvoicingGroup(params.getCustomer().getRegistry());
			if (group != null) {
				IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
				Expression expr = null;
				Criteria parentCriteria = new Criteria();
				parentCriteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID), group.getId());
				Iterator iter = invoicingGroupDetailBean.getList(parentCriteria).iterator();
				while (iter.hasNext()) {
					InvoicingGroupDetail detail = (InvoicingGroupDetail)iter.next();
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), detail.getChild().getId()));
				}

				if (expr != null) {
					criteria.addOrExpression(expr);
				}
			}
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	private InvoicingGroup getInvoicingGroup(Registry registry) throws ManagerBeanException {
		IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupBean.getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID), registry.getId());
		Iterator iterator = invoicingGroupBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (InvoicingGroup)iterator.next();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private InvoicingGroupDetail getInvoicingGroupDetail(Registry registry) throws ManagerBeanException {
		IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_CHILD_ID), registry.getId());
		Iterator iterator = invoicingGroupDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (InvoicingGroupDetail)iterator.next();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List obtainFeeList(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		Map parentMap = new HashMap();
		List parentFeeList = new LinkedList();
		List childFeeList = new LinkedList();

		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria feeCriteria = new Criteria();
		feeCriteria.addExpression(criteria.getExpression());
		feeCriteria = completeCriteriaWithCustomerData(feeCriteria, params);
		List feeList = customerFeeBean.getList(feeCriteria);
		Iterator iterator = feeList.iterator();
		while (iterator.hasNext()) {
			CustomerFee customerFee = (CustomerFee)iterator.next();
			InvoicingGroupDetail invoicingGroupDetail = getInvoicingGroupDetail(customerFee.getCustomer().getRegistry());
			if (invoicingGroupDetail != null) {
				childFeeList.add(customerFee);
				if (!parentMap.containsKey(invoicingGroupDetail.getInvoicingGroup().getParent().getId())) {
					Criteria parentCriteria = new Criteria();
					parentCriteria.addExpression(feeCriteria.getExpression());
					parentCriteria.addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), invoicingGroupDetail.getInvoicingGroup().getParent().getId());
					if (customerFeeBean.getCount(parentCriteria) == 0) {
						Customer groupCustomer = new Customer();
						groupCustomer.setId(invoicingGroupDetail.getInvoicingGroup().getParent().getId());
						groupCustomer.setRegistry(invoicingGroupDetail.getInvoicingGroup().getParent());
						CustomerFee groupFee = new CustomerFee();
						groupFee.setCustomer(groupCustomer);
						parentFeeList.add(groupFee);
						parentMap.put(invoicingGroupDetail.getInvoicingGroup().getParent().getId(), groupFee);
					}
				}
			}
		}

		iterator = childFeeList.iterator();
		while (iterator.hasNext()) {
			CustomerFee customerFee = (CustomerFee)iterator.next();
			feeList.remove(customerFee);
		}
		feeList.addAll(parentFeeList);

		return orderFeeList(feeList);
	}

	@SuppressWarnings("unchecked")
	private List orderFeeList(List feeList) {
		class FeeComparator implements Comparator {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof CustomerFee && o2 instanceof CustomerFee) {
					CustomerFee fee1 = (CustomerFee)o1;
					String surname1 = (fee1.getCustomer().getRegistry().getSurname() != null) ? fee1.getCustomer().getRegistry().getSurname() : "";
					String name1 = (fee1.getCustomer().getRegistry().getName() != null) ? fee1.getCustomer().getRegistry().getName() : "";
					CustomerFee fee2 = (CustomerFee)o2;
					String surname2 = (fee2.getCustomer().getRegistry().getSurname() != null) ? fee2.getCustomer().getRegistry().getSurname() : "";
					String name2 = (fee2.getCustomer().getRegistry().getName() != null) ? fee2.getCustomer().getRegistry().getName() : "";
					return (surname1.compareTo(surname2) == 0) ? name1.compareTo(name2) : surname1.compareTo(surname2);
				}
				return 0;
			}
		}

		Collections.sort(feeList, new FeeComparator());
		return feeList;
	}

	@SuppressWarnings("unchecked")
	private void invoiceFees(List feeList, Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		int size = feeList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int counter = params.getInvoiceNumber();
		Invoice invoice = null;
		Integer previousCustomerId = new Integer(Integer.MIN_VALUE);
		Iterator iter = feeList.iterator();
		int i = 0;
		while(iter.hasNext()){
			CustomerFee customerFee = (CustomerFee)iter.next();
			counter = calculateNextNumber(params.getInvoiceSeries(), counter);

			InvoicingGroup group = getInvoicingGroup(customerFee.getCustomer().getRegistry());
			if (group != null) {
				if (!previousCustomerId.equals(customerFee.getCustomer().getId())) {
					invoiceGroup(group, criteria, counter, params);
				}
			} else {
				if (!previousCustomerId.equals(customerFee.getCustomer().getId())) {
					// Se crea finance asociado al invoice anterior, que ya no tiene más detalles.
					if (invoice != null) {
						getInvoicingDAO().createFinances(invoice, null);
					}
					invoice = createInvoice(customerFee, counter, params);
					counter++;
					detailLine = 0;
					getInvoicingDAO().insertInvoice(invoice);
					getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());
				}
				InvoiceDetail invoiceDetail = createInvoiceDetail(customerFee, invoice, params);
				getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
				getInvoicingDAO().updateSource(customerFee);
				getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
			}
			previousCustomerId = customerFee.getCustomer().getId();

			i++;
			if (i < size) {
				// La última vuelta se ignora para que el progreso se quede incompleto porque falta el calculo de vencimientos. 
				getInvoicingFeedBack().setCurrentRow(i);	
			} 
		}
		// Se crea finance asociado al ultimo invoice, que ya no tiene más detalles
		if (invoice != null) {
			getInvoicingDAO().createFinances(invoice, null);
		}
		// Finalizado el calculo de vtos. se fija el progreso.
		getInvoicingFeedBack().setCurrentRow(size);
	}

	@SuppressWarnings("unchecked")
	private void invoiceGroup(InvoicingGroup group, Criteria criteria, int counter, InvoicingParameters params) throws ManagerBeanException {
		InvoicingParameters tmpParams = new InvoicingParameters();
		if (params.getCustomer() != null && params.getCustomer().getId() != null) {
			tmpParams.setCustomer(params.getCustomer());
		} else {
			Customer customer = new Customer();
			customer.setId(group.getParent().getId());
			customer.setRegistry(group.getParent());
			tmpParams.setCustomer(customer);
		}

		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria feeCriteria = new Criteria();
		feeCriteria.addExpression(criteria.getExpression());
		feeCriteria = completeCriteriaWithCustomerData(feeCriteria, tmpParams);
		Iterator feeIter = customerFeeBean.getList(feeCriteria).iterator();
		if (feeIter.hasNext()) {
			Invoice invoice = createInvoice(group, counter, params);
			counter++;
			detailLine = 0;
			getInvoicingDAO().insertInvoice(invoice);
			getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());
			while (feeIter.hasNext()) {
				CustomerFee fee = (CustomerFee)feeIter.next();
				InvoiceDetail invoiceDetail = createInvoiceDetail(fee, invoice, params);
				getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
				getInvoicingDAO().updateSource(fee);
				getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());			
			}
			getInvoicingDAO().createFinances(invoice, null);
		}
	}

	private Invoice createInvoice(CustomerFee customerFee, int counter, InvoicingParameters params) {
		Invoice invoice = new Invoice();
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		Registry registry = customerFee.getCustomer().getRegistry();
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryName((registry.getName() == null?"":registry.getName()) + " " + (registry.getSurname()==null?"":registry.getSurname()));
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getId());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(params.getSecurityLevel());
		return invoice;
	}
	
	private Invoice createInvoice(InvoicingGroup group, int counter, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setRegistry(group.getParent());
		invoice.setRegistryDocument(group.getParent().getDocument());
		invoice.setRegistryName((group.getParent().getName() == null?"":group.getParent().getName()) + " " + (group.getParent().getSurname()==null?"":group.getParent().getSurname()));
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getId());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(params.getSecurityLevel());
		return invoice;
	}

	private InvoiceDetail createInvoiceDetail(CustomerFee customerFee, Invoice invoice, InvoicingParameters params) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setSourceId(null);
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setLine(++detailLine);
		invoiceDetail.setItem(customerFee.getItem());
        invoiceDetail.setDescription(obtainFeeDescription(customerFee, invoice, params));
        invoiceDetail.setDiscountExpression(customerFee.getDiscountExpression());
		invoiceDetail.setPrice(CommonUtil.round(customerFee.getPrice() * calculateCorrectionFactor(customerFee, params), 2));
		invoiceDetail.setQuantity(customerFee.getQuantity());
		invoiceDetail.setSource(InvoiceSource.FEE);
		invoiceDetail.setSourceId((customerFee.getBillingDateYear() * 100) + customerFee.getBillingDateMonth().ordinal() + 1);
		invoiceDetail.setTaxes(0.0);
		invoiceDetail.setWorkPlace(customerFee.getWorkPlace());
		return invoiceDetail;
	}

	private String obtainFeeDescription(CustomerFee customerFee, Invoice invoice, InvoicingParameters params) {
		String description = customerFee.getDescription();
		if (description.indexOf("${MONTH}") > 0) {
			description = description.replace("${MONTH}", params.getMonth().getName(Locale.getDefault()).toUpperCase());
		}
		if (description.indexOf("${YEAR}") > 0) {
			description = description.replace("${YEAR}", Integer.toString(params.getYear()));
		}
		if (!invoice.getRegistry().getId().equals(customerFee.getCustomer().getId())) {
        	description += " - " + customerFee.getCustomer().getRegistry().getName() + " " + customerFee.getCustomer().getRegistry().getSurname();
        }
		return (description.length()>64)?description.substring(0, 64):description;
	}

	private int calculateNextNumber(Series series, int number) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		number = (number == 0 ? 1 : number);
		while (true) {
			Criteria criteria = new Criteria();
			if (series == null) {
				criteria.addNullExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES));
			} else {
				criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series.getId());
			}
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER), number);
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
			if (invoiceBean.getCount(criteria) == 0) {
				break;
			}
			number++;
		}
		return number;
	}

	private double calculateCorrectionFactor(CustomerFee customerFee, InvoicingParameters params) {
		if (customerFee.getPeriod().getValue() == 0) {
			return 1;
		} 
		Calendar calendar = new GregorianCalendar(params.getYear(), params.getMonth().getValue(), 1, 0, 0, 0);
		Date fromInv = calendar.getTime();
		calendar.add(Calendar.MONTH, customerFee.getPeriod().getValue());
		calendar.add(Calendar.DATE, -1);
		Date toInv = calendar.getTime();

		Date iniFee = (customerFee.getInitialDate().before(fromInv)) ? fromInv : customerFee.getInitialDate();
		Date endFee = (customerFee.getFinalDate() == null || customerFee.getFinalDate().after(toInv)) ? toInv : customerFee.getFinalDate();

		return (double)daysBetween(iniFee, endFee) / (double)daysBetween(fromInv, toInv);
	}

	private long daysBetween(Date from, Date to) {
		return ((to.getTime() - from.getTime()) / ((60 * 60 * 1000) * 24)) + 1;
	}

}