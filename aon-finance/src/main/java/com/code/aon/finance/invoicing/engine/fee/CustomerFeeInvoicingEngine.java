package com.code.aon.finance.invoicing.engine.fee;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;
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
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFeeInvoicingEngine implements IInvoicingEngine {
	
	private IInvoicingDAO invoicingDAO;
	private IInvoicingFeedBack invoicingFeedBack;
	private Session session;
	
	public IInvoicingDAO getInvoicingDAO() {
		return invoicingDAO;
	}

	public void setInvoicingDAO(IInvoicingDAO invoicingDAO) {
		this.invoicingDAO = invoicingDAO;
	}

	public IInvoicingFeedBack getInvoicingFeedBack() {
		return invoicingFeedBack;
	}

	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack) {
		this.invoicingFeedBack = invoicingFeedBack; 
	}

	public Session getHibernateSession() {
		return session;
	}

	public void setHibernateSession(Session session) {
		this.session = session; 
	}

	public void invoice(InvoicingParameters params) throws ManagerBeanException {
		List<ITransferObject> feeList = obtainFeeList(createInvoicingCriteria(params), params);
		invoiceFees(feeList, createInvoicingCriteria(params), params);
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		Expression dateExpression = createFromToExpression(params.getMonth(), params.getYear());
		criteria.addExpression(dateExpression);
		criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_SECURITY_LEVEL), getSecurityLevel(params.isConfidential()));
		if (params.getItem() != null && params.getItem().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID), params.getItem().getId());
		}
		if (params.getCategory() != null && params.getCategory().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_PRODUCT_CATEGORY_ID), params.getCategory().getId());
		}
		if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_WORK_PLACE_ID), params.getWorkPlace().getId());
		}
		criteria.addOrder(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		criteria.addOrder(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_LINE));
		return criteria;
	}

	private Expression createFromToExpression(Month month, int year) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month.getValue(), 1, 0, 0, 0);
		Date from = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		Date to = calendar.getTime();
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Expression billingExpr = ExpressionUtilities.getBetweenExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_BILLING_DATE), from, to);
		Expression initialExpr = ExpressionUtilities.getLessThanOrEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INITIAL_DATE), to);
        Expression finalExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE), from);
        Expression finalNullExpr = ExpressionUtilities.getNullExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE));
        return ExpressionUtilities.getAndExpression(ExpressionUtilities.getAndExpression(billingExpr, initialExpr), ExpressionUtilities.getOrExpression(finalExpr, finalNullExpr));
	}

	private Criteria completeCriteriaWithCustomerData(Criteria criteria, Customer customer) throws ManagerBeanException {
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		if (customer != null && customer.getId() != null) {
			Expression expression = ExpressionUtilities.getEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());

			InvoicingGroup group = getInvoicingGroupByParent(customer.getRegistry());
			if (group != null) {
				IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
				Criteria groupCriteria = new Criteria();
				groupCriteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IEntityAlias.INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID), group.getId());
				for (ITransferObject ito : invoicingGroupDetailBean.getList(groupCriteria)) {
					InvoicingGroupDetail detail = (InvoicingGroupDetail)ito;
					Expression groupExp = ExpressionUtilities.getEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), detail.getChild().getId());
					expression = ExpressionUtilities.getOrExpression(expression, groupExp);
				}
			}
			criteria.addExpression(expression);
		}
		return criteria;
	}

	private InvoicingGroup getInvoicingGroupByParent(Registry registry) throws ManagerBeanException {
		IManagerBean invoicingGroupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupBean.getFieldName(IEntityAlias.INVOICING_GROUP_PARENT_ID), registry.getId());
		for (ITransferObject ito : invoicingGroupBean.getList(criteria)) {
			return (InvoicingGroup)ito;
		}
		return null;
	}

	private InvoicingGroup getInvoicingGroupByChild(Registry registry) throws ManagerBeanException {
		IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IEntityAlias.INVOICING_GROUP_DETAIL_CHILD_ID), registry.getId());
		for (ITransferObject ito : invoicingGroupDetailBean.getList(criteria)) {
			return ((InvoicingGroupDetail)ito).getInvoicingGroup();
		}
		return null;
	}

	private List<ITransferObject> obtainFeeList(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria feeCriteria = new Criteria();
		feeCriteria.addExpression(criteria.getExpression());
		feeCriteria.setOrderByList(criteria.getOrderByList());
		feeCriteria = completeCriteriaWithCustomerData(feeCriteria, params.getCustomer());
		List<ITransferObject> feeList = feeBean.getList(feeCriteria);
		for (ITransferObject ito : feeList) {
			CustomerFee customerFee = (CustomerFee)ito;
			InvoicingGroup invoicingGroup = getInvoicingGroupByChild(customerFee.getCustomer().getRegistry());
			customerFee.setInvoicingDescription(obtainFeeDescription(customerFee, invoicingGroup, params));
			if (invoicingGroup != null) {
				customerFee.setInvoicingCustomer((Customer)BeanManager.getManagerBean(Customer.class).get(invoicingGroup.getParent().getId()));
			}
		}
		return orderFeeList(feeList);
	}

	private String obtainFeeDescription(CustomerFee customerFee, InvoicingGroup group, InvoicingParameters params) {
		String description = customerFee.getDescription();
		if (description.indexOf("${MONTH}") > 0) {
			description = description.replace("${MONTH}", params.getMonth().getName(Locale.getDefault()).toUpperCase());
		}
		if (description.indexOf("${YEAR}") > 0) {
			description = description.replace("${YEAR}", Integer.toString(params.getYear()));
		}
		if (group != null && !group.getParent().getId().equals(customerFee.getCustomer().getId())) {
        	description += " - " + customerFee.getCustomer().getRegistry().getName() ;
        }
		return (description.length()>1024)?description.substring(0, 1024):description;
	}

	private List<ITransferObject> orderFeeList(List<ITransferObject> feeList) {
		class FeeComparator implements Comparator<ITransferObject> {
			public int compare(ITransferObject o1, ITransferObject o2) {
				if (o1 instanceof CustomerFee && o2 instanceof CustomerFee) {
					CustomerFee fee1 = (CustomerFee)o1;
					String name1 = fee1.getInvoicingCustomer().getRegistry().getFullName();
					Integer line1 = new Integer(fee1.getLine());
					CustomerFee fee2 = (CustomerFee)o2;
					String name2 = fee2.getInvoicingCustomer().getRegistry().getFullName();
					Integer line2 = new Integer(fee2.getLine());
					return (name1.compareTo(name2) == 0) ? line1.compareTo(line2) : name1.compareTo(name2);
				}
				return 0;
			}
		}

		Collections.sort(feeList, new FeeComparator());
		return feeList;
	}

	private void invoiceFees(List<ITransferObject> feeList, Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		int size = feeList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int number = params.getInvoiceNumber();
		int detailLine = 0;
		Integer previousCustomerId = new Integer(Integer.MIN_VALUE);
		Invoice invoice = null;
		InvoiceDetail invoiceDetail = null;
		int i = 0;
		for (ITransferObject ito : feeList) {
			CustomerFee customerFee = (CustomerFee)ito;
			if (!previousCustomerId.equals(customerFee.getInvoicingCustomer().getId())) {
				if (invoice != null) {
					invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);
					invoiceDetail.getInvoice().setUpdateEnabled(true);
					BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);

					invoice = (Invoice)getHibernateSession().merge(invoice);
					getInvoicingDAO().createFinances(invoice, null);

					if (getInvoicingDAO().getCollection().size() % 10 == 0) {
						getHibernateSession().flush();
						getHibernateSession().clear();
					}
				}

				invoice = createInvoice(customerFee, number, params);
				getInvoicingDAO().insertInvoice(invoice);
				getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());

				number++;
				detailLine = 0;
				previousCustomerId = customerFee.getInvoicingCustomer().getId();
			}

			invoiceDetail = createInvoiceDetail(customerFee, invoice, ++detailLine, params);
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);

			customerFee = (CustomerFee)getHibernateSession().merge(customerFee);
			getInvoicingDAO().updateSource(customerFee);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
			
			i++;
			if (i < size) {
				getInvoicingFeedBack().setCurrentRow(i);	
			} 
		}

		if (invoice != null) {
			invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);
			invoiceDetail.getInvoice().setUpdateEnabled(true);
			BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);

			invoice = (Invoice)getHibernateSession().merge(invoice);
			getInvoicingDAO().createFinances(invoice, null);

			getHibernateSession().flush();
			getHibernateSession().clear();
		}
		getInvoicingFeedBack().setCurrentRow(size);
	}

	private Invoice createInvoice(CustomerFee customerFee, int number, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setNumber(calculateNextNumber(params.getInvoiceSeries(), number));
		invoice.setIssueDate(params.getInvoiceDate());
		Registry registry = customerFee.getInvoicingCustomer().getRegistry();
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryDocumentType(registry.getDocumentType());
		invoice.setRegistryDocumentCountry(registry.getDocumentCountry());
		invoice.setRegistryName((registry.getName()==null) ? "" : registry.getName());
		invoice.setSeries((params.getInvoiceSeries()==null) ? null : params.getInvoiceSeries().getCode());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		invoice.setComments(params.getInvoiceComments());
		return invoice;
	}
	
	private int calculateNextNumber(Series series, int number) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		number = (number == 0 ? 1 : number);
		while (true) {
			Criteria criteria = new Criteria();
			String seriesAlias = invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES);
			if (series == null || StringUtils.isEmpty(series.getCode())) {
				Expression nullExpr = ExpressionUtilities.getNullExpression(seriesAlias);
				Expression blankExpr = ExpressionUtilities.getEqualExpression(seriesAlias, "");
				criteria.addExpression(ExpressionUtilities.getOrExpression(nullExpr, blankExpr));
			} else {
				criteria.addEqualExpression(seriesAlias, series.getCode());
			}
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER), number);
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
			if (invoiceBean.getCount(criteria) == 0) {
				break;
			}
			number++;
		}
		return number;
	}

	private SecurityLevel getSecurityLevel(boolean confidential) {
		return (confidential) ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
	}

	private InvoiceDetail createInvoiceDetail(CustomerFee customerFee, Invoice invoice, int detailLine, InvoicingParameters params) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setSourceId(null);
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setLine(detailLine);
		invoiceDetail.setItem(customerFee.getItem());
        invoiceDetail.setDescription(customerFee.getInvoicingDescription());
        invoiceDetail.setDiscountExpression(customerFee.getDiscountExpression());
		invoiceDetail.setPrice(CommonUtil.round(customerFee.getPrice() * calculateCorrectionFactor(customerFee, params), 4));
		invoiceDetail.setQuantity(customerFee.getQuantity());
		invoiceDetail.setSource(InvoiceSource.FEE);
		invoiceDetail.setSourceId((customerFee.getBillingDateYear() * 100) + customerFee.getBillingDateMonth().ordinal() + 1);
		invoiceDetail.setTaxes(0.0);
		invoiceDetail.setWorkPlace(customerFee.getWorkPlace());
		invoiceDetail.getInvoice().setUpdateEnabled(false);
		return invoiceDetail;
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