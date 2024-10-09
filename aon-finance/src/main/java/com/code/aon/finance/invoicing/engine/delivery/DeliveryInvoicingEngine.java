package com.code.aon.finance.invoicing.engine.delivery;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.customer.InvoicingGroup;
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
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;

public class DeliveryInvoicingEngine implements IInvoicingEngine, Serializable {
	
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
		checkSegments( params );
		List<ITransferObject> deliveryList = obtainDeliveryList(createInvoicingCriteria(params));
		invoiceDeliveries(deliveryList, params);
	}
	
	public void checkSegments(InvoicingParameters params) throws ManagerBeanException {
		if (params.getSegments() != null) {
			Collection<Integer> segments =  Arrays.stream(params.getSegments())
				.filter( s -> s != null)
				.filter( s -> s.getId() != null)
				.map( s -> s.getId() )
				.collect(Collectors.toCollection(LinkedList::new));
			if (segments != null && !segments.isEmpty()) {
				String ids = segments.stream()
					.map(Object::toString)
					.collect(Collectors.joining(", "));
				String select = "select registry.name customerName "
					+ ", segment.name segmentName "
					+ ", count(*) segments "
					+"from rsegment "
					+"inner join registry on rsegment.registry = registry.id "
					+"inner join customer on registry.id = customer.registry "
					+"inner join segment on rsegment.segment = segment.id "
					+"where " + DomainManager.getSQLWhereClause("rsegment.domain")
					+"and rsegment.segment in ("+ ids +") "
					+"group by registry.name, segment.name "
					+"having count(*) > 1 "; 
				SQLQuery query = HibernateUtil
					.getSession(HibernateUtil.getSessionFactoryName())
					.createSQLQuery(select);
				List<?> list = query
					.addScalar("customerName", Hibernate.STRING)
					.addScalar("segmentName", Hibernate.STRING)
					.addScalar("segments", Hibernate.INTEGER)
					.list();
				if (!list.isEmpty()) {
					StringBuilder buf = new StringBuilder();
					buf.append("Existen clientes con segmentos definidos dos veces. Corrija la situación para poder continuar.");
					for ( int i = 0; i < list.size(); i++ ) {
						Object[] arr = (Object[])list.get(i);
						buf.append(" [Cliente: " + arr[0] + " - Segmento: " + arr[1] + " - " + arr[2] + " veces]");
					}
					throw new ManagerBeanException(buf.toString());
				}
			}
		}
	}
	

	private Criteria createInvoicingCriteria(InvoicingParameters params) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_STATUS), DeliveryStatus.PENDING);
		criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SECURITY_LEVEL), getSecurityLevel(params.isConfidential()));
		if (params.getInvoicingGroup() != null && params.getInvoicingGroup().getId() != null) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_INVOICING_GROUP_ID), params.getInvoicingGroup().getId());
		}
		if (params.getCustomer() != null && params.getCustomer().getId() != null) {
			criteria.addEqualExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), params.getCustomer().getId());
		}
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
		if (params.getSegments() != null) {
			Collection<Integer> segments =  Arrays.stream(params.getSegments())
					.filter( s -> s != null)
					.filter( s -> s.getId() != null)
					.map( s -> s.getId() )
					.collect(Collectors.toCollection(LinkedList::new));
			if (segments != null && !segments.isEmpty()) {
				String alias = resolveAlias(deliveryBean,"Delivery_customer_registry_segments_segment_id");
				criteria.addInExpression(alias,  segments );
			}
		}
		return criteria;
	}

	private String resolveAlias(IManagerBean feeBean,String alias) {
		String fieldName = StringUtils.substringBefore(alias, "-");
		try {
			fieldName = feeBean.getFieldName(fieldName);
		} catch (ManagerBeanException e) {
			fieldName = fieldName.replace('_', '.');
		}
		return fieldName;
	}
	
	private List<ITransferObject> obtainDeliveryList(Criteria criteria) throws ManagerBeanException {
		List<ITransferObject> deliveryList = BeanManager.getManagerBean(Delivery.class).getList(criteria);
		return orderDeliveryList(deliveryList);
	}

	private List<ITransferObject> orderDeliveryList(List<ITransferObject> deliveryList) {
		class DeliveryComparator implements Comparator<ITransferObject> {
			public int compare(ITransferObject o1, ITransferObject o2) {
				int retValue = 0;
				if (o1 instanceof Delivery && o2 instanceof Delivery) {
					Delivery delivery1 = (Delivery)o1;
					Delivery delivery2 = (Delivery)o2;
					retValue = delivery1.getInvoicingCustomer().getRegistry().getFullName().compareTo(delivery2.getInvoicingCustomer().getRegistry().getFullName());
					if (retValue == 0) {
						retValue = delivery1.getInvoicingCustomer().getId().compareTo(delivery2.getInvoicingCustomer().getId());
						if (retValue == 0) {
							if (delivery1.getCustomer().getInvoicingGroup() == null || delivery2.getCustomer().getInvoicingGroup() == null) {
								retValue = (delivery1.getCustomer().getInvoicingGroup() != null) ? 1 : (delivery2.getCustomer().getInvoicingGroup() != null) ? -1 : 0;
							} else {
								retValue =  delivery1.getCustomer().getInvoicingGroup().getId().compareTo(delivery2.getCustomer().getInvoicingGroup().getId());
							}
							if (retValue == 0) {
								retValue = delivery1.getCustomer().getRegistry().getFullName().compareTo(delivery2.getCustomer().getRegistry().getFullName());
								if (retValue == 0) {
									retValue = delivery1.getCustomer().getId().compareTo(delivery2.getCustomer().getId());
									if (retValue == 0) {
										if (!delivery1.getCustomer().isProjectGrouped()) {
											if (delivery1.getProject() == null || delivery2.getProject() == null) {
												retValue = (delivery1.getProject() != null) ? 1 : (delivery2.getProject() != null) ? -1 : 0;
											} else {
												retValue =  delivery1.getProject().getId().compareTo(delivery2.getProject().getId());
											}
										}
										if (retValue == 0) {
											if (delivery1.getPayMethod() == null || delivery2.getPayMethod() == null) {
												retValue = (delivery1.getPayMethod() != null) ? 1 : (delivery2.getPayMethod() != null) ? -1 : 0;
											} else {
												retValue =  delivery1.getPayMethod().getId().compareTo(delivery2.getPayMethod().getId());
											}
										}
										if (retValue == 0) {
											if (delivery1.getBankAccount() == null || delivery2.getBankAccount() == null) {
												retValue = (delivery1.getBankAccount() != null) ? 1 : (delivery2.getBankAccount() != null) ? -1 : 0;
											} else {
												retValue =  delivery1.getBankAccount().toString().compareTo(delivery2.getBankAccount().toString());
											}
										}
										if (retValue == 0 && delivery1.getNumberOfPayments() != delivery2.getNumberOfPayments()) {
											retValue = (delivery1.getNumberOfPayments() < delivery2.getNumberOfPayments()) ? -1 : 1;
										}
										if (retValue == 0 && delivery1.getDaysToFirstPayment() != delivery2.getDaysToFirstPayment()) {
											retValue = (delivery1.getDaysToFirstPayment() < delivery2.getDaysToFirstPayment()) ? -1 : 1;
										}
										if (retValue == 0 && delivery1.getDaysBetweenPayments() != delivery2.getDaysBetweenPayments()) {
											retValue = (delivery1.getDaysBetweenPayments() < delivery2.getDaysBetweenPayments()) ? -1 : 1;
										}
										if (retValue == 0) {
											retValue = delivery1.getPaymentDays().compareTo(delivery2.getPaymentDays());
										}
										if (retValue == 0) {
											retValue = delivery1.getIssueTime().compareTo(delivery2.getIssueTime());
										}
										if (retValue == 0) {
											if (delivery1.getSeries() == null || delivery2.getSeries() == null) {
												retValue = (delivery1.getSeries() != null) ? 1 : (delivery2.getSeries() != null) ? -1 : 0;
											} else {
												retValue =  delivery1.getSeries().compareTo(delivery2.getSeries());
											}
										}
										if (retValue == 0) {
											retValue = (delivery1.getNumber() < delivery2.getNumber()) ? -1 : 1;
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

		Collections.sort(deliveryList, new DeliveryComparator());
		return deliveryList;
	}

	private void invoiceDeliveries(List<ITransferObject> deliveryList, InvoicingParameters params) throws ManagerBeanException {
		int size = deliveryList.size();
		getInvoicingFeedBack().setRowCount(size);
		getInvoicingFeedBack().setCurrentRow(0);
		int number = params.getInvoiceNumber();
		Invoice invoice = null;
		InvoiceDetail invoiceDetail = null;
		Delivery previousDelivery = null;
		int i = 0;
		for (ITransferObject ito : deliveryList) {
			Delivery delivery = (Delivery)ito;
			if (breakInvoice(delivery, previousDelivery)) {
				if (invoice != null) {
					if (invoiceDetail != null) {
						invoiceDetail.getInvoice().setUpdateEnabled(true);
						invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);
						BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);
					}
					invoice = (Invoice)getHibernateSession().merge(invoice);
					if (detailLine > 0) {
						getInvoicingDAO().createFinances(invoice, previousDelivery);
					} else {
						getInvoicingDAO().getCollection().remove(invoice);
						BeanManager.getManagerBean(Invoice.class).remove(invoice);
						if(params.isTbai()) number++;
						else number--;
					}
					
					if (getInvoicingDAO().getCollection().size() % 10 == 0) {
						getHibernateSession().flush();
						getHibernateSession().clear();
					}
				}

				invoice = createInvoice(delivery, number, params);
				invoice = getInvoicingDAO().insertInvoice(invoice);
				getInvoicingFeedBack().addMessage("\t" + "Invoice: " + invoice.getReferenceCode());

				number = params.isTbai() 
					? invoice.getNumber() - 1
					: invoice.getNumber() + 1;
				detailLine = 0;
				previousDelivery = delivery;
			}

			List<?> deliveryDetailList = delivery.getOrderedDetailList();
			if (deliveryDetailList.size() > 0) {
				invoiceDetail = createInvoiceDetails(deliveryDetailList, invoice, false);
				if (invoiceDetail != null) {
					delivery = (Delivery)getHibernateSession().merge(delivery);
					getInvoicingDAO().updateSource(delivery, null);
				}
			}
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
			
			i++;
			if (i < size) {
				getInvoicingFeedBack().setCurrentRow(i);	
			} 
		}

		if (invoice != null) {
			if (invoiceDetail != null) {
				invoiceDetail.getInvoice().setUpdateEnabled(true);
				invoiceDetail = (InvoiceDetail)getHibernateSession().merge(invoiceDetail);
				BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);
			}
			invoice = (Invoice)getHibernateSession().merge(invoice);
			if (detailLine > 0) {
				getInvoicingDAO().createFinances(invoice, previousDelivery);
			} else {
				getInvoicingDAO().getCollection().remove(invoice);
				BeanManager.getManagerBean(Invoice.class).remove(invoice);
			}

			getHibernateSession().flush();
			getHibernateSession().clear();
		}
		getInvoicingFeedBack().setCurrentRow(size);
	}

	private boolean breakInvoice(Delivery delivery, Delivery previousDelivery) {
		if (previousDelivery == null) {
			return true;
		}
		if (!delivery.getCustomer().isDeliveryGrouped()) {
			return true;
		}
		if (!ObjectUtils.equals(delivery.getInvoicingCustomer(), previousDelivery.getInvoicingCustomer())) {
			return true;
		}
		InvoicingGroup invoicingGroup = delivery.getCustomer().getInvoicingGroup();
		if (invoicingGroup != null && !invoicingGroup.isCustomerGrouped() && !ObjectUtils.equals(delivery.getCustomer(), previousDelivery.getCustomer())) {
			return true;
		}
		if (!delivery.getCustomer().isProjectGrouped() && !ObjectUtils.equals(delivery.getProject(), previousDelivery.getProject())) {
			return true;
		}
		if (delivery.getPayMethod() != null || previousDelivery.getPayMethod() != null) {
			if (!ObjectUtils.equals(delivery.getPayMethod(), previousDelivery.getPayMethod())) {
				return true;
			}
			if (!ObjectUtils.equals(delivery.getBankAccount(), previousDelivery.getBankAccount())) {
				return true;
			}
			if (!ObjectUtils.equals(delivery.getNumberOfPayments(), previousDelivery.getNumberOfPayments())) {
				return true;
			}
			if (!ObjectUtils.equals(delivery.getDaysToFirstPayment(), previousDelivery.getDaysToFirstPayment())) {
				return true;
			}
			if (!ObjectUtils.equals(delivery.getDaysBetweenPayments(), previousDelivery.getDaysBetweenPayments())) {
				return true;
			}
			if (!ObjectUtils.equals(delivery.getPaymentDays(), previousDelivery.getPaymentDays())) {
				return true;
			}
		}

		return false;
	}

	private Invoice createInvoice(Delivery delivery, int number, InvoicingParameters params) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(delivery.getProject());
		invoice.setSeries((params.getInvoiceSeries()==null) ? null : params.getInvoiceSeries().getCode());
		invoice.setNumber(params.isTbai()
				? AON.getInvoiceMinNumber(params.getDomainName(), params.getDomainId(), params.getLogin(), com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, params.getInvoiceSeries().getCode())
				: calculateNextNumber(params.getInvoiceSeries(), number));
		invoice.setRegistry(delivery.getInvoicingCustomer().getRegistry());
		invoice.setRegistryDocument(delivery.getInvoicingCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(delivery.getInvoicingCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(delivery.getInvoicingCustomer().getRegistry().getDocumentCountry());
		invoice.setRegistryName(delivery.getInvoicingCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(delivery.getInvoicingCustomer().getRegistry().getDefaultAddress());
		invoice.setIssueDate(params.getInvoiceDate());
		invoice.setSecurityLevel(getSecurityLevel(params.isConfidential()));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setComments(params.getInvoiceComments());
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
		return confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
	}

	private InvoiceDetail createInvoiceDetails(List<?> deliveryDetailList, Invoice invoice, boolean updateEnabled) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = null;
		for (Object obj : deliveryDetailList) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)obj;
			invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(deliveryDetail.getDelivery().getProject());
			invoiceDetail.setLine(++detailLine);
			invoiceDetail.setItem(deliveryDetail.getItem());
			invoiceDetail.setDescription(deliveryDetail.getDescription());
			invoiceDetail.setQuantity(deliveryDetail.getQuantity());
			invoiceDetail.setPrice(deliveryDetail.getPrice());
			invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
			invoiceDetail.setSource(InvoiceSource.DELIVERY);
			invoiceDetail.setSourceId(deliveryDetail.getId());
			invoiceDetail.setSeller((deliveryDetail.getSalesDetail() != null) ? deliveryDetail.getSalesDetail().getSales().getSeller() : null);
			invoiceDetail.setWorkPlace(deliveryDetail.getDelivery().getWorkPlace());
			invoiceDetail.getInvoice().setUpdateEnabled(updateEnabled && (deliveryDetailList.indexOf(deliveryDetail) == (deliveryDetailList.size() - 1)));

			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
		return invoiceDetail;
	}

	public void invoiceDeliveryList(Invoice invoice, List<Delivery> deliveryList) throws ManagerBeanException {
		detailLine = calculateMaxLine(invoice);
		for (Delivery delivery : deliveryList) {
			List<?> deliveryDetailList = delivery.getOrderedDetailList();
			if (deliveryDetailList.size() > 0) {
				InvoiceDetail invoiceDetail = createInvoiceDetails(deliveryDetailList, invoice, true);
				if (invoiceDetail != null) {
					delivery = (Delivery)getHibernateSession().merge(delivery);
					getInvoicingDAO().updateSource(delivery, null);
				}
			}
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