package com.code.aon.finance.invoicing.engine.delivery;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
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
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryInvoicingEngine implements IInvoicingEngine {
	
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
		List<ITransferObject> deliveryList = obtainDeliveryList(createInvoicingCriteria(params), params);
		invoiceDeliveries(deliveryList, createInvoicingCriteria(params), params);
	}

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
		criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SECURITY_LEVEL), getSecurityLevel(params.isConfidential()));
		if (params.getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME), params.getFromDate());
		}
		if (params.getToDate() != null) {
			criteria.addLessThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME), params.getToDate());
		}
		if (params.getSeries() != null && !StringUtils.isEmpty(params.getSeries().getCode())) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SERIES), params.getSeries().getCode());
		}
		if (params.getFromNumber() != null) {
			criteria.addGreaterThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER), params.getFromNumber());
		}
		if (params.getToNumber() != null) {
			criteria.addLessThanOrEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER), params.getToNumber());
		}
		if (params.getWorkPlace() != null && params.getWorkPlace().getId() != null) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_WORK_PLACE_ID), params.getWorkPlace().getId());
		}
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_PROJECT));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_REGISTRY_ADDRESS));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_PAY_METHOD));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_BANK));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_BANK_ACCOUNT));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER_OF_PAYMENTS));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_DAYS_TO_FIRST_PAYMENT));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_DAYS_BETWEEN_PAYMENTS));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_PAYMENT_DAYS));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SERIES));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER));
		return criteria;
	}

	private Criteria completeCriteriaWithCustomerData(Criteria criteria, Customer customer) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		if (customer != null && customer.getId() != null) {
			Expression expression = ExpressionUtilities.getEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), customer.getId());

			InvoicingGroup group = getInvoicingGroupByParent(customer.getRegistry());
			if (group != null) {
				IManagerBean invoicingGroupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
				Criteria groupCriteria = new Criteria();
				groupCriteria.addEqualExpression(invoicingGroupDetailBean.getFieldName(IEntityAlias.INVOICING_GROUP_DETAIL_INVOICING_GROUP_ID), group.getId());
				for (ITransferObject ito : invoicingGroupDetailBean.getList(groupCriteria)) {
					InvoicingGroupDetail detail = (InvoicingGroupDetail)ito;
					Expression groupExp = ExpressionUtilities.getEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), detail.getChild().getId());
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

	private List<ITransferObject> obtainDeliveryList(Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		Map<Integer, ITransferObject> parentMap = new HashMap<Integer, ITransferObject>();
		List<ITransferObject> parentDeliveryList = new LinkedList<ITransferObject>();
		List<ITransferObject> childDeliveryList = new LinkedList<ITransferObject>();

		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria deliveryCriteria = new Criteria();
		deliveryCriteria.addExpression(criteria.getExpression());
		deliveryCriteria.setOrderByList(criteria.getOrderByList());
		deliveryCriteria = completeCriteriaWithCustomerData(deliveryCriteria, params.getCustomer());
		List<ITransferObject> deliveryList = deliveryBean.getList(deliveryCriteria);
		for (ITransferObject ito : deliveryList) {
			Delivery delivery = (Delivery)ito;
			InvoicingGroup invoicingGroup = getInvoicingGroupByChild(delivery.getCustomer().getRegistry());
			if (invoicingGroup != null) {
				childDeliveryList.add(delivery);
				if (!parentMap.containsKey(invoicingGroup.getParent().getId())) {
					Criteria parentCriteria = new Criteria();
					parentCriteria.addExpression(deliveryCriteria.getExpression());
					parentCriteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), invoicingGroup.getParent().getId());
					if (deliveryBean.getCount(parentCriteria) == 0) {
						Customer groupCustomer = new Customer();
						groupCustomer.setId(invoicingGroup.getParent().getId());
						groupCustomer.setRegistry(invoicingGroup.getParent());
						Delivery groupDelivery = new Delivery();
						groupDelivery.setCustomer(groupCustomer);
						parentDeliveryList.add(groupDelivery);
						parentMap.put(invoicingGroup.getParent().getId(), groupDelivery);
					}
				}
			}
		}

		for (ITransferObject ito : childDeliveryList) {
			Delivery delivery = (Delivery)ito;
			deliveryList.remove(delivery);
		}
		deliveryList.addAll(parentDeliveryList);

		return orderDeliveryList(deliveryList);
	}

	private List<ITransferObject> orderDeliveryList(List<ITransferObject> deliveryList) {
		class DeliveryComparator implements Comparator<ITransferObject> {
			public int compare(ITransferObject o1, ITransferObject o2) {
				if (o1 instanceof Delivery && o2 instanceof Delivery) {
					Delivery delivery1 = (Delivery)o1;
					String name1 = delivery1.getCustomer().getRegistry().getFullName();
					Delivery delivery2 = (Delivery)o2;
					String name2 = delivery2.getCustomer().getRegistry().getFullName();
					return name1.compareTo(name2);
				}
				return 0;
			}
		}

		Collections.sort(deliveryList, new DeliveryComparator());
		return deliveryList;
	}

	private void invoiceDeliveries(List<ITransferObject> deliveryList, Criteria criteria, InvoicingParameters params) throws ManagerBeanException {
		int size = deliveryList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int counter = params.getInvoiceNumber();
		Invoice invoice = null;
		Delivery previousDelivery = null;
		List<Delivery> customerDeliveryList = new LinkedList<Delivery>();
		int i = 0;
		for (ITransferObject ito : deliveryList) {
			Delivery delivery = (Delivery)ito;
			counter = calculateNextNumber(params.getInvoiceSeries(), counter);

			InvoicingGroup group = getInvoicingGroupByParent(delivery.getCustomer().getRegistry());
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
					customerDeliveryList = new LinkedList<Delivery>();
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
		deliveryCriteria = completeCriteriaWithCustomerData(deliveryCriteria, tmpParams.getCustomer());
		if (deliveryBean.getCount(deliveryCriteria) > 0) {
			Invoice invoice = null;
			Delivery previousDelivery = null;
			List<Delivery> customerDeliveryList = new LinkedList<Delivery>();
			for (ITransferObject ito : deliveryBean.getList(deliveryCriteria)) {
				Delivery delivery = (Delivery)ito;
				counter = calculateNextNumber(params.getInvoiceSeries(), counter);
				if (!isDeliveryGrouped(delivery, previousDelivery) || !delivery.getCustomer().isDeliveryGrouped()) {
					// Se crea finance asociado al invoice anterior, que ya no tiene más detalles
					if (invoice != null) {
						invoiceDeliveryList(invoice, customerDeliveryList);
						getInvoicingDAO().createFinances(invoice, previousDelivery);
					}
					invoice = createInvoice(group, delivery, counter, params);
					counter++;
					customerDeliveryList = new LinkedList<Delivery>();
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
		/*if (!ObjectUtils.equals(delivery.getProject(), previousDelivery.getProject())) {
			return false;
		}*/
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
		invoice.setProject(delivery.getProject());
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setRegistry(group.getParent());
		invoice.setRegistryDocument(group.getParent().getDocument());
		invoice.setRegistryDocumentType(group.getParent().getDocumentType());
		invoice.setRegistryDocumentCountry(group.getParent().getDocumentCountry());
		invoice.setRegistryName((group.getParent().getName() == null?"":group.getParent().getName()) );
		if (group.getParent().getId().equals(delivery.getCustomer().getId())) {
			invoice.setRegistryAddress(delivery.getRegistryAddress());
		}
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getCode());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		invoice.setComments(params.getInvoiceComments());
		return invoice;
	}

	private Invoice createInvoice(Delivery delivery, int counter, InvoicingParameters params) {
		Invoice invoice = new Invoice();
		invoice.setProject(delivery.getProject());
		invoice.setNumber(counter);
		invoice.setIssueDate(params.getInvoiceDate());
		Registry registry = delivery.getCustomer().getRegistry();
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(registry.getDocument());
		invoice.setRegistryDocumentType(registry.getDocumentType());
		invoice.setRegistryDocumentCountry(registry.getDocumentCountry());
		invoice.setRegistryName((registry.getName() == null?"":registry.getName()) );
		invoice.setRegistryAddress(delivery.getRegistryAddress());
		invoice.setSeries(params.getInvoiceSeries()==null?null:params.getInvoiceSeries().getCode());
		invoice.setType(InvoiceType.SALES);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		invoice.setComments(params.getInvoiceComments());
		return invoice;
	}

	private SecurityLevel getSecurityLevel(boolean confidential) {
		return confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
	}

	public void invoiceDeliveryList(Invoice invoice, List<Delivery> deliveryList) throws ManagerBeanException {
		for (Delivery delivery : deliveryList) {
			boolean lastDelivery = deliveryList.indexOf(delivery) == (deliveryList.size() - 1);
			createInvoiceDetails(invoice, delivery, lastDelivery);
			getInvoicingDAO().updateSource(delivery);
		}
	}

	private void createInvoiceDetails(Invoice invoice, Delivery delivery, boolean lastDelivery) throws ManagerBeanException {
		int line = calculateMaxLine(invoice);

		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
		List<ITransferObject> deliveryDetailList = deliveryDetailBean.getList(criteria);
		for (ITransferObject ito : deliveryDetailList) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(deliveryDetail.getDelivery().getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(deliveryDetail.getItem());
			invoiceDetail.setDescription(deliveryDetail.getDescription());
			invoiceDetail.setQuantity(deliveryDetail.getQuantity());
			invoiceDetail.setPrice(deliveryDetail.getPrice());
			invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(delivery.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.DELIVERY);
			invoiceDetail.setSourceId(deliveryDetail.getId());
			invoiceDetail.getInvoice().setUpdateEnabled(lastDelivery && ((deliveryDetailList.lastIndexOf(deliveryDetail) + 1) == deliveryDetailList.size()));
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
	}

	private	Integer calculateMaxLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) : 0;
	}

}