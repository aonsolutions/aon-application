package com.code.aon.finance.invoicing.engine.fee;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
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
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFeeInvoicingEngine implements IInvoicingEngine, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IInvoicingDAO invoicingDAO;
	private IInvoicingFeedBack invoicingFeedBack;
	private Session session;
	private int detailLine;
	
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
		invoiceFees(feeList, params);
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean feeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_SECURITY_LEVEL), getSecurityLevel(params.isConfidential()));
		criteria.addExpression(createFromToExpression(params.getMonth(), params.getYear()));
		if (params.getInvoicingGroup() != null && params.getInvoicingGroup().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INVOICING_GROUP_ID), params.getInvoicingGroup().getId());
		}
		if (params.getCustomer() != null && params.getCustomer().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), params.getCustomer().getId());
		} else if (params.getScope() != null && params.getScope().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_SCOPE_ID), params.getScope().getId());
		} else {
			criteria.addInExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_SCOPE_ID), params.getScopeIds());
		}
		if (params.getItem() != null && params.getItem().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID), params.getItem().getId());
		}
		if (params.getCategory() != null && params.getCategory().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_PRODUCT_CATEGORY_ID), params.getCategory().getId());
		}
		if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null) {
			criteria.addEqualExpression(feeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_WORK_PLACE_ID), params.getWorkPlace().getId());
		}
		return criteria;
	}

	private Expression createFromToExpression(Month month, int year) throws ManagerBeanException {
		Calendar calendar = Calendar.getInstance();
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

	private List<ITransferObject> obtainFeeList(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		List<ITransferObject> feeList = BeanManager.getManagerBean(CustomerFee.class).getList(criteria);
		for (ITransferObject ito : feeList) {
			CustomerFee customerFee = (CustomerFee)ito;
			customerFee.setInvoicingDescription(obtainFeeDescription(customerFee, params));
		}
		return orderFeeList(feeList);
	}

	private String obtainFeeDescription(CustomerFee customerFee, InvoicingParameters params) {
		String description = customerFee.getDescription();
		if (description.indexOf("${MONTH}") > 0) {
			description = description.replace("${MONTH}", params.getMonth().getName(Locale.ROOT).toUpperCase());
		}
		if (description.indexOf("${YEAR}") > 0) {
			description = description.replace("${YEAR}", Integer.toString(params.getYear()));
		}
		if (!customerFee.getInvoicingCustomer().getId().equals(customerFee.getCustomer().getId())) {
        	description += " - " + customerFee.getCustomer().getRegistry().getName();
        }
		return (description.length()>1024)?description.substring(0, 1024):description;
	}

	private List<ITransferObject> orderFeeList(List<ITransferObject> feeList) {
		class FeeComparator implements Comparator<ITransferObject> {
			public int compare(ITransferObject o1, ITransferObject o2) {
				int retValue = 0;
				if (o1 instanceof CustomerFee && o2 instanceof CustomerFee) {
					CustomerFee fee1 = (CustomerFee)o1;
					CustomerFee fee2 = (CustomerFee)o2;
					retValue = fee1.getInvoicingCustomer().getRegistry().getFullName().compareTo(fee2.getInvoicingCustomer().getRegistry().getFullName());
					if (retValue == 0) {
						retValue = fee1.getInvoicingCustomer().getId().compareTo(fee2.getInvoicingCustomer().getId());
						if (retValue == 0) {
							if (fee1.getInvoicingGroup() == null || fee2.getInvoicingGroup() == null) {
								retValue = (fee1.getInvoicingGroup() != null) ? 1 : (fee2.getInvoicingGroup() != null) ? -1 : 0;
							} else {
								retValue =  fee1.getInvoicingGroup().getId().compareTo(fee2.getInvoicingGroup().getId());
							}
							if (retValue == 0) {
								boolean parent1 = fee1.getInvoicingCustomer().getRegistry().getId().equals(fee1.getCustomer().getRegistry().getId());
								boolean parent2 = fee2.getInvoicingCustomer().getRegistry().getId().equals(fee2.getCustomer().getRegistry().getId());
								retValue = !(parent1 ^ parent2) ? 0 : ((parent1) ? -1 : 1);
								if (retValue == 0) {
									retValue = fee1.getCustomer().getRegistry().getFullName().compareTo(fee2.getCustomer().getRegistry().getFullName());
									if (retValue == 0) {
										retValue = fee1.getCustomer().getId().compareTo(fee2.getCustomer().getId());
										if (retValue == 0) {
											if (!fee1.getInvoicingCustomer().isProjectGrouped()) {
												if (fee1.getProject() == null || fee2.getProject() == null) {
													retValue = (fee1.getProject() != null) ? 1 : (fee2.getProject() != null) ? -1 : 0;
												} else {
													retValue =  fee1.getProject().getId().compareTo(fee2.getProject().getId());
												}
											}
										
											if (retValue == 0) {
												retValue = (fee1.getLine() < fee2.getLine()) ? -1 : 1;
											}
										}
									}
								}
							}
						}
					}
				}
				return retValue;
			}
		}

		Collections.sort(feeList, new FeeComparator());
		return feeList;
	}

	private void invoiceFees(List<ITransferObject> feeList, InvoicingParameters params) throws ManagerBeanException {
		int size = feeList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int number = params.getInvoiceNumber();
		Invoice invoice = null;
		InvoiceDetail invoiceDetail = null;
		CustomerFee previousFee = null;
		int i = 0;
		for (ITransferObject ito : feeList) {
			CustomerFee fee = (CustomerFee)ito;
			if (breakInvoice(fee, previousFee)) {
				if (invoice != null) {
					if (!isReadOnly()) {
						invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);	
						invoiceDetail.getInvoice().setUpdateEnabled(true);
						BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);
						invoice = (Invoice)getHibernateSession().merge(invoice);
					}
					getInvoicingDAO().createFinances(invoice, null);
					
					if (!isReadOnly() && getInvoicingDAO().getCollection().size() % 10 == 0) {
						getHibernateSession().flush();
						getHibernateSession().clear();
					}
				}

				invoice = createInvoice(fee, number, params);
				invoice = getInvoicingDAO().insertInvoice(invoice);
				getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());

				number = invoice.getNumber() + 1;
				detailLine = 0;
				previousFee = fee;
			}

			invoiceDetail = createInvoiceDetail(fee, invoice, params);
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);

			if (!isReadOnly()) {
				fee = (CustomerFee)getHibernateSession().merge(fee);
			}
			getInvoicingDAO().updateSource(fee, invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
			
			i++;
			if (i < size) {
				getInvoicingFeedBack().setCurrentRow(i);	
			} 
		}

		if (invoice != null) {
			if (!isReadOnly()) {
				invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);
				invoiceDetail.getInvoice().setUpdateEnabled(true);
				BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);
				invoice = (Invoice)getHibernateSession().merge(invoice);
			}
			getInvoicingDAO().createFinances(invoice, null);
			if (!isReadOnly()) {
				getHibernateSession().flush();
				getHibernateSession().clear();
			}
		}
		getInvoicingFeedBack().setCurrentRow(size);
	}

	private boolean breakInvoice(CustomerFee fee, CustomerFee previousFee) {
		if (previousFee == null) {
			return true;
		}
		if (!ObjectUtils.equals(fee.getInvoicingCustomer(), previousFee.getInvoicingCustomer())) {
			return true;
		}
		if (fee.getInvoicingGroup() != null && !fee.getInvoicingGroup().isCustomerGrouped() && !ObjectUtils.equals(fee.getCustomer(), previousFee.getCustomer())) {
			return true;
		}
		if (!fee.getInvoicingCustomer().isProjectGrouped() && !ObjectUtils.equals(fee.getProject(), previousFee.getProject())) {
			return true;
		}

		return false;
	}

	private boolean isReadOnly() {
		return (getInvoicingDAO() instanceof CustomerFeePreInvoicingDAO);
	}

	private Invoice createInvoice(CustomerFee customerFee, int number, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(customerFee.getProject());
		invoice.setSeries((params.getInvoiceSeries()==null) ? null : params.getInvoiceSeries().getCode());
		invoice.setNumber(calculateNextNumber(params.getInvoiceSeries(), number));
		invoice.setRegistry(customerFee.getInvoicingCustomer().getRegistry());
		invoice.setRegistryDocument(customerFee.getInvoicingCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(customerFee.getInvoicingCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(customerFee.getInvoicingCustomer().getRegistry().getDocumentCountry());
		invoice.setRegistryName(customerFee.getInvoicingCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(customerFee.getInvoicingCustomer().getRegistry().getDefaultAddress());
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setComments(params.getInvoiceComments());
		invoice.setSeller(customerFee.getSeller());
		return invoice;
	}
	
	private int calculateNextNumber(Series series, int number) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		number = (number == 0 ? 1 : number);
		while (true) {
			Criteria criteria = new Criteria();
			String seriesAlias = invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES);
			if (series == null || StringUtils.isBlank(series.getCode())) {
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

	private InvoiceDetail createInvoiceDetail(CustomerFee customerFee, Invoice invoice, InvoicingParameters params) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setProject(customerFee.getProject());
		invoiceDetail.setLine(++detailLine);
		invoiceDetail.setItem(customerFee.getItem());
        invoiceDetail.setDescription(customerFee.getInvoicingDescription());
		invoiceDetail.setQuantity(customerFee.getQuantity());
		invoiceDetail.setPrice(CommonUtil.round(customerFee.getPrice() * calculateCorrectionFactor(customerFee, params), 4));
        invoiceDetail.setDiscountExpression(customerFee.getDiscountExpression());
		invoiceDetail.setSource(InvoiceSource.FEE);
		invoiceDetail.setSourceId((customerFee.getBillingDateYear() * 100) + customerFee.getBillingDateMonth().ordinal() + 1);
		invoiceDetail.setSeller(customerFee.getSeller());
		invoiceDetail.setWorkPlace(customerFee.getWorkPlace());
		invoiceDetail.getInvoice().setUpdateEnabled(false);
		return invoiceDetail;
	}

	private double calculateCorrectionFactor(CustomerFee customerFee, InvoicingParameters params) {
		if (customerFee.getPeriod().getValue() == 0) {
			return 1;
		} 
		Date fromDate = DateUtils.truncate(new Date(), Calendar.YEAR);
		fromDate = DateUtils.setYears(fromDate, params.getYear());
		fromDate = DateUtils.setMonths(fromDate, params.getMonth().getValue());
		Date toDate = DateUtils.addDays(DateUtils.addMonths(fromDate, customerFee.getPeriod().getValue()), -1);

		Date iniFee = (customerFee.getInitialDate().before(fromDate)) ? fromDate : customerFee.getInitialDate();
		Date endFee = (customerFee.getFinalDate() == null || customerFee.getFinalDate().after(toDate)) ? toDate : customerFee.getFinalDate();

		return (double)daysBetween(iniFee, endFee) / (double)daysBetween(fromDate, toDate);
	}

	private long daysBetween(Date from, Date to) {
		return ((to.getTime() - from.getTime()) / ((60 * 60 * 1000) * 24)) + 1;
	}

}