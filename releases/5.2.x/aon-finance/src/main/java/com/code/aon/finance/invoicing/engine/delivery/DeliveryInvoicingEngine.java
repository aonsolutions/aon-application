package com.code.aon.finance.invoicing.engine.delivery;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
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
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoicingEngine implements IInvoicingEngine {
	
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
		List deliveryList = obtainDeliveryList(createInvoicingCriteria(params), params);
		invoiceDeliveries(deliveryList, createInvoicingCriteria(params), params);
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SECURITY_LEVEL), getSecurityLevel(params.isConfidential()));
		if (params.getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), params.getFromDate());
		}
		if (params.getToDate() != null) {
			criteria.addLessThanOrEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), params.getToDate());
		}
		if (params.getSeries() != null && !StringUtils.isEmpty(params.getSeries().getId())) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SERIES), params.getSeries().getId());
		}
		if (params.getFromNumber() != null) {
			criteria.addGreaterThanOrEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER), params.getFromNumber());
		}
		if (params.getToNumber() != null) {
			criteria.addLessThanOrEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER), params.getToNumber());
		}
		if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_WORK_PLACE_ID), params.getWorkPlace().getId());
		}
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_REGISTRY_ADDRESS));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_PAY_METHOD));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_BANK));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_BANK_ACCOUNT));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER_OF_PAYMENTS));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_DAYS_TO_FIRST_PAYMENT));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_DAYS_BETWEEN_PAYMENTS));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_PAYMENT_DAYS));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SERIES));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	private Criteria completeCriteriaWithCustomerData(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		if (params.getCustomer() != null && params.getCustomer().getId() != null) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), params.getCustomer().getId());
			InvoicingGroup group = getInvoicingGroup(params.getCustomer().getRegistry());
			if (group != null) {
				IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
				Expression expr = null;
				Criteria parentCriteria = new Criteria();
				parentCriteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IFinanceAlias.INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID), group.getId());
				Iterator iter = invoicingGroupDetailBean.getList(parentCriteria).iterator();
				while (iter.hasNext()) {
					InvoicingGroupDetail detail = (InvoicingGroupDetail)iter.next();
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), detail.getChild().getId()));
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
	private List obtainDeliveryList(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		Map parentMap = new HashMap();
		List parentDeliveryList = new LinkedList();
		List childDeliveryList = new LinkedList();

		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria deliveryCriteria = new Criteria();
		deliveryCriteria.addExpression(criteria.getExpression());
		deliveryCriteria.setOrderByList(criteria.getOrderByList());
		deliveryCriteria = completeCriteriaWithCustomerData(deliveryCriteria, params);
		List deliveryList = deliveryBean.getList(deliveryCriteria);
		Iterator iterator = deliveryList.iterator();
		while (iterator.hasNext()) {
			Delivery delivery = (Delivery)iterator.next();
			InvoicingGroupDetail invoicingGroupDetail = getInvoicingGroupDetail(delivery.getCustomer().getRegistry());
			if (invoicingGroupDetail != null) {
				childDeliveryList.add(delivery);
				if (!parentMap.containsKey(invoicingGroupDetail.getInvoicingGroup().getParent().getId())) {
					Criteria parentCriteria = new Criteria();
					parentCriteria.addExpression(deliveryCriteria.getExpression());
					parentCriteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), invoicingGroupDetail.getInvoicingGroup().getParent().getId());
					if (deliveryBean.getCount(parentCriteria) == 0) {
						Customer groupCustomer = new Customer();
						groupCustomer.setId(invoicingGroupDetail.getInvoicingGroup().getParent().getId());
						groupCustomer.setRegistry(invoicingGroupDetail.getInvoicingGroup().getParent());
						Delivery groupDelivery = new Delivery();
						groupDelivery.setCustomer(groupCustomer);
						parentDeliveryList.add(groupDelivery);
						parentMap.put(invoicingGroupDetail.getInvoicingGroup().getParent().getId(), groupDelivery);
					}
				}
			}
		}

		iterator = childDeliveryList.iterator();
		while (iterator.hasNext()) {
			Delivery delivery = (Delivery)iterator.next();
			deliveryList.remove(delivery);
		}
		deliveryList.addAll(parentDeliveryList);

		return orderDeliveryList(deliveryList);
	}

	@SuppressWarnings("unchecked")
	private List orderDeliveryList(List deliveryList) {
		class DeliveryComparator implements Comparator {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof Delivery && o2 instanceof Delivery) {
					Delivery delivery1 = (Delivery)o1;
					String surname1 = (delivery1.getCustomer().getRegistry().getSurname() != null) ? delivery1.getCustomer().getRegistry().getSurname() : "";
					String name1 = (delivery1.getCustomer().getRegistry().getName() != null) ? delivery1.getCustomer().getRegistry().getName() : "";
					Delivery delivery2 = (Delivery)o2;
					String surname2 = (delivery2.getCustomer().getRegistry().getSurname() != null) ? delivery2.getCustomer().getRegistry().getSurname() : "";
					String name2 = (delivery2.getCustomer().getRegistry().getName() != null) ? delivery2.getCustomer().getRegistry().getName() : "";
					return (surname1.compareTo(surname2) == 0) ? name1.compareTo(name2) : surname1.compareTo(surname2);
				}
				return 0;
			}
		}

		Collections.sort(deliveryList, new DeliveryComparator());
		return deliveryList;
	}

	@SuppressWarnings("unchecked")
	private void invoiceDeliveries(List deliveryList, Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		int size = deliveryList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int counter = params.getInvoiceNumber();
		Invoice invoice = null;
		Delivery previousDelivery = null;
		List customerDeliveryList = new LinkedList();
		Iterator iter = deliveryList.iterator();
		int i = 0;
		while(iter.hasNext()){
			Delivery delivery = (Delivery)iter.next();
			counter = calculateNextNumber(params.getInvoiceSeries(), counter);

			InvoicingGroup group = getInvoicingGroup(delivery.getCustomer().getRegistry());
			if (group != null) {
				if (previousDelivery == null || !previousDelivery.getCustomer().getId().equals(delivery.getCustomer().getId())) {
					invoiceGroup(group, criteria, counter, params);
				}
			} else {
				if (!isDeliveryGrouped(delivery, previousDelivery) || !delivery.getCustomer().isDeliveryGrouped()) {
					// Se crea finance asociado al invoice anterior, que ya no tiene más detalles
					if (invoice != null) {
						invoiceDeliveryList(invoice, customerDeliveryList);
						getInvoicingDAO().createFinances(invoice, previousDelivery);
					}
					invoice = createInvoice(delivery, counter, params);
					counter++;
					detailLine = 0;
					customerDeliveryList = new LinkedList();
					getInvoicingDAO().insertInvoice(invoice);
					getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());
				}
				customerDeliveryList.add(delivery);
			}
			previousDelivery = delivery;

			i++;
			if (i < size) {
				// La última vuelta se ignora para que el progreso se quede incompleto porque falta el calculo de vencimientos. 
				getInvoicingFeedBack().setCurrentRow(i);	
			} 
		}
		// Se crea finance asociado al ultimo invoice, que ya no tiene más detalles
		if (invoice != null) {
			invoiceDeliveryList(invoice, customerDeliveryList);
			getInvoicingDAO().createFinances(invoice, previousDelivery);
		}
		// Finalizado el calculo de vtos. se fija el progreso.
		getInvoicingFeedBack().setCurrentRow(size);
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

		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria deliveryCriteria = new Criteria();
		deliveryCriteria.addExpression(criteria.getExpression());
		deliveryCriteria.setOrderByList(criteria.getOrderByList());
		deliveryCriteria = completeCriteriaWithCustomerData(deliveryCriteria, tmpParams);
		Iterator deliveryIter = deliveryBean.getList(deliveryCriteria).iterator();
		if (deliveryIter.hasNext()) {
			Invoice invoice = null;
			Delivery previousDelivery = null;
			List customerDeliveryList = new LinkedList();
			while (deliveryIter.hasNext()) {
				Delivery delivery = (Delivery)deliveryIter.next();
				counter = calculateNextNumber(params.getInvoiceSeries(), counter);
				if (!isDeliveryGrouped(delivery, previousDelivery) || !delivery.getCustomer().isDeliveryGrouped()) {
					// Se crea finance asociado al invoice anterior, que ya no tiene más detalles
					if (invoice != null) {
						invoiceDeliveryList(invoice, customerDeliveryList);
						getInvoicingDAO().createFinances(invoice, previousDelivery);
					}
					invoice = createInvoice(group, delivery, counter, params);
					counter++;
					detailLine = 0;
					customerDeliveryList = new LinkedList();
					getInvoicingDAO().insertInvoice(invoice);
					getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());
				}
				customerDeliveryList.add(delivery);
				previousDelivery = delivery;
			}
			invoiceDeliveryList(invoice, customerDeliveryList);
			getInvoicingDAO().createFinances(invoice, previousDelivery);
		}
	}

	private boolean isDeliveryGrouped(Delivery delivery, Delivery previousDelivery) {
		if (previousDelivery == null) {
			return false;
		}
		if (!ObjectUtils.equals(delivery.getCustomer(), previousDelivery.getCustomer())) {
			return false;
		}
		if (!ObjectUtils.equals(delivery.getRegistryAddress(), previousDelivery.getRegistryAddress())) {
			return false;
		}
		if (delivery.getPayMethod() == null && previousDelivery.getPayMethod() == null) {
			return true;
		} else {
			if (!ObjectUtils.equals(delivery.getPayMethod(), previousDelivery.getPayMethod())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getBank(), previousDelivery.getBank())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getBankAccount(), previousDelivery.getBankAccount())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getNumberOfPayments(), previousDelivery.getNumberOfPayments())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getDaysToFirstPayment(), previousDelivery.getDaysToFirstPayment())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getDaysBetweenPayments(), previousDelivery.getDaysBetweenPayments())) {
				return false;
			}
			if (!ObjectUtils.equals(delivery.getPaymentDays(), previousDelivery.getPaymentDays())) {
				return false;
			}
		}

		return true;
	}

	private Invoice createInvoice(InvoicingGroup group, Delivery delivery, int counter, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setRegistry(group.getParent());
		invoice.setRegistryDocument(group.getParent().getDocument());
		invoice.setRegistryName((group.getParent().getName() == null?"":group.getParent().getName()) + " " + (group.getParent().getSurname()==null?"":group.getParent().getSurname()));
		if (group.getParent().getId().equals(delivery.getCustomer().getId())) {
			invoice.setRegistryAddress(delivery.getRegistryAddress());
		}
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getId());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		return invoice;
	}

	private Invoice createInvoice(Delivery delivery, int counter, InvoicingParameters params) {
		Invoice invoice = new Invoice();
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		Registry registry = delivery.getCustomer().getRegistry();
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryName((registry.getName() == null?"":registry.getName()) + " " + (registry.getSurname()==null?"":registry.getSurname()));
		invoice.setRegistryAddress(delivery.getRegistryAddress());
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getId());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		return invoice;
	}

	private SecurityLevel getSecurityLevel(boolean confidential) {
		return confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
	}

	public void invoiceDeliveryList(Invoice invoice, List<Delivery> deliveryList) throws ManagerBeanException {
		for (Delivery delivery : deliveryList) {
			createInvoiceDetails(invoice, delivery);
			getInvoicingDAO().updateSource(delivery);
		}
	}

	private void createInvoiceDetails(Invoice invoice, Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setLine(++detailLine);
			invoiceDetail.setItem(deliveryDetail.getItem());
			invoiceDetail.setDescription(deliveryDetail.getDescription());
			invoiceDetail.setQuantity(deliveryDetail.getQuantity());
			invoiceDetail.setPrice(deliveryDetail.getPrice());
			invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(delivery.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.DELIVERY);
			invoiceDetail.setSourceId(deliveryDetail.getId());
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
	}

}